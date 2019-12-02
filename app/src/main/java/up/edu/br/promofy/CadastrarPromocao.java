package up.edu.br.promofy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import up.edu.br.promofy.helpers.ApplicationHelper;
import up.edu.br.promofy.models.Promotion;
import up.edu.br.promofy.services.AuthService;

public class CadastrarPromocao extends AppCompatActivity {

    private Bitmap bitmapPromition;
    private Location location;

    private ImageView imgPromotion;
    private EditText txtDescription;
    private EditText numberOriginalPrice;
    private EditText numberPromotionalPrice;

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    public static final int RC_SING_IN = 2000;
    public static final int HARDWARE_PERMISSION = 5000;

    private static final String LOG_TAG = "APP_LOG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar_promocao);

        imgPromotion = findViewById(R.id.imgPromotion);
        txtDescription = findViewById(R.id.txtDescription);
        numberOriginalPrice = findViewById(R.id.numberOriginalPrice);
        numberPromotionalPrice = findViewById(R.id.numberPromotionalPrice);

        ApplicationHelper.setContext(this);

        handlePermissions();

        if (!AuthService.isLogged()) {
            openAuthUI();
        } else {
           continueInit();
        }
    }

    private void continueInit()
    {
        dispatchTakePictureIntent();

        LocationServices.getFusedLocationProviderClient(this).getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                CadastrarPromocao.this.location = location;
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(LOG_TAG, e.getMessage(), e);
            }
        });
    }

    public void onCancelClick(View view)
    {
        finish();
    }

    public void onPublishClick(View view)
    {
        // Todo: implementar validação de campos

        if (this.location == null) {
            new AlertDialog.Builder(this)
                    .setTitle("Houve um problema")
                    .setMessage("Não foi possível obter sua localização, por favor tente novamente em alguns instantes")
                    .show();

            return;
        }

        uploadImage(this.bitmapPromition);
    }

    /**
     * 
     */
    private void openAuthUI() {
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build()
        );

        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SING_IN);
    }

    /**
     * Verifica se o app possui as permissoes
     */
    private void handlePermissions() {
        List<String> permissions = new ArrayList<>();

        // Permissao de camera
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
            permissions.add(Manifest.permission.CAMERA);
        }

        // Permissao de nao sei
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == getPackageManager().PERMISSION_DENIED){
//            permissions.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION);
//        }

        // Permissao de localizacao
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==  PackageManager.PERMISSION_DENIED) {
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }

        // Verificacao lixo das permissoes
        if (permissions.size() > 0) {
            ActivityCompat.requestPermissions(this, permissions.toArray(new String[0]), HARDWARE_PERMISSION);
        }
    }

    /**
     * Abre a camera
     */
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    private void uploadImage(Bitmap bitmap)
    {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] imageData = stream.toByteArray();

        StorageReference storageRef = FirebaseStorage.getInstance().getReference();

        // example: images/promo/663ed7ad-ba36-4ab2-9851-d4d52a07db74.jpg
        StorageReference imageRef = storageRef.child("images/promo/" + UUID.randomUUID().toString() + ".jpg");

        UploadTask uploadTask = imageRef.putBytes(imageData);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                CadastrarPromocao.this.showImageUploadError(e);
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                CadastrarPromocao.this.savePromoData(taskSnapshot.getMetadata().getPath());
            }
        });
    }

    public void showImageUploadError(Exception e)
    {
        Log.e(LOG_TAG, "Image Upload error: " + e.getMessage());

        new AlertDialog.Builder(this)
                .setTitle("Atenção")
                .setMessage("Houve um erro ao salvar a imagem nos nossos servidores")
                .show();
    }

    public void savePromoData(String promoImagePath)
    {
        // Save data in firebase database
        Promotion promotion = new Promotion(
                AuthService.getLogged().getUid(),
                promoImagePath,
                txtDescription.getText().toString(),
                Float.parseFloat(numberOriginalPrice.getText().toString()),
                Float.parseFloat(numberPromotionalPrice.getText().toString()),
                location
        );

        FirebaseDatabase.getInstance().getReference().child("promotions")
                .child(promotion.uid).setValue(promotion);

        // Todo: enviar para página de detalhes da promoçõa

        finish();
    }

    private void setBitmapPromotion(Bitmap bitmap)
    {
        this.bitmapPromition = bitmap;
        imgPromotion.setImageBitmap(this.bitmapPromition);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case RC_SING_IN:
                IdpResponse response = IdpResponse.fromResultIntent(data);

                if (resultCode == RESULT_OK) {
                    // Successfully signed in
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                    Log.i("Usuario:", user.getEmail());

                    continueInit();
                } else {
                    // Sign in failed. If response is null the user canceled the
                    // sign-in flow using the back button. Otherwise check
                    // response.getError().getErrorCode() and handle the error.
                    // ...
                    Log.i("Erro login", "deu muito ruim");
                }
                break;
            case HARDWARE_PERMISSION:
                // Hanlder permission
                if (requestCode == RESULT_OK) {
//                    dispatchTakePictureIntent();
                } else {
                    // Todo: exibir mensagem de aviso

                    finish();
                }

                break;
            case REQUEST_IMAGE_CAPTURE:
                // Handler camera result
                if (resultCode == RESULT_OK) {
                    // Get image from memory
                    Bundle extras = data.getExtras();
                    setBitmapPromotion((Bitmap) extras.get("data"));
                }
                break;
        }
    }
}
