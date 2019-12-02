package up.edu.br.promofy;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import up.edu.br.promofy.helpers.ApplicationHelper;
import up.edu.br.promofy.models.Promotion;
import up.edu.br.promofy.views.PromotionRecyclerAdapter;

public class MainActivity extends AppCompatActivity {

    private View btnPromoAdd;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter recyclerAdapter;
    private RecyclerView.LayoutManager recyclerLayoutManager;

    private ArrayList<Promotion> promotions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ApplicationHelper.setContext(this);

        promotions = new ArrayList<Promotion>();

        btnPromoAdd = findViewById(R.id.btnPromoAdd);

        btnPromoAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newPromocao = new Intent(MainActivity.this, CadastrarPromocao.class);
                startActivity(newPromocao);
            }
        });

        recyclerView = findViewById(R.id.recyclerPromotions);

        recyclerLayoutManager = new LinearLayoutManager(this);
        recyclerAdapter = new PromotionRecyclerAdapter(promotions);

        recyclerView.setLayoutManager(recyclerLayoutManager);
        recyclerView.setAdapter(recyclerAdapter);

        FirebaseDatabase.getInstance().getReference().child("promotions").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Promotion promotion = dataSnapshot.getValue(Promotion.class);

                promotions.add(promotion);
                recyclerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Promotion promotion = dataSnapshot.getValue(Promotion.class);

                promotions.remove(promotion.uid);
                promotions.add(promotion);
                recyclerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
