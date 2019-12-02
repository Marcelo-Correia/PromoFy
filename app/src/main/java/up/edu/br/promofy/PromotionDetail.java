package up.edu.br.promofy;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuView;
import androidx.appcompat.widget.Toolbar;

import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import up.edu.br.promofy.models.Promotion;

public class PromotionDetail extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promotion_detail);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final TextView txtDescription    = findViewById(R.id.txtDescription);
        final TextView txtLocation       = findViewById(R.id.txtLocation);
        final TextView txtOriginalPrice  = findViewById(R.id.txtOriginalPrice);
        final TextView txtPromotionPrice = findViewById(R.id.txtPromotionalPrice);

        final ImageView imagePromotion   = findViewById(R.id.PromotionView);

        DatabaseReference promotionRef = FirebaseDatabase.getInstance().getReference().
                child("promotions").child(getIntent().getStringExtra("promotion"));

        promotionRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Promotion pro = dataSnapshot.getValue(Promotion.class);

                txtDescription.setText(pro.description);
                txtLocation.setText(pro.getLocationName());
                txtOriginalPrice.setText(pro.promotionalPrice + "");
                txtPromotionPrice.setText(pro.promotionalPrice + "");

                StorageReference mgref = FirebaseStorage.getInstance().getReference().child(pro.getImagePath());
                final long ONE_MEGABYTE = 1024 * 1024;
                mgref.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        imagePromotion.setImageBitmap(bitmap);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle any errors
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
