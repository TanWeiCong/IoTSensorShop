package com.example.iotsensorshop.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.iotsensorshop.R;
import com.example.iotsensorshop.adapters.NewProductsAdapter;
import com.example.iotsensorshop.adapters.PopularProductsAdapter;
import com.example.iotsensorshop.models.NewProductsModel;
import com.example.iotsensorshop.models.PopularProductsModel;
import com.example.iotsensorshop.models.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class PopularProductsActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    PopularProductsAdapter popularProductsAdapter;
    List<PopularProductsModel> popularProductsModelList;

    String userId;

    Toolbar toolbar;

    FirebaseFirestore firestore;
    FirebaseAuth auth;
    DocumentReference documentReference;
    String userType;
    String name;
    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_all);

        auth = FirebaseAuth.getInstance();
        userId = auth.getCurrentUser().getUid();

        toolbar = findViewById(R.id.show_all_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PopularProductsActivity.this, MainActivity.class));
            }
        });

        firestore = FirebaseFirestore.getInstance();
        documentReference = firestore.collection("users").document(userId);

        recyclerView = findViewById(R.id.new_product_rec);
        recyclerView.setVisibility(View.VISIBLE);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        popularProductsModelList = new ArrayList<>();
        popularProductsAdapter = new PopularProductsAdapter(this, popularProductsModelList);
        recyclerView.setAdapter(popularProductsAdapter);

        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    name = documentSnapshot.getString("Name");
                    email = documentSnapshot.getString("Email");
                    userType = documentSnapshot.getString("userType");
                    UserModel user = new UserModel(userId, name, email, userType);

                    if (user.getUserType().equals("Admin")) {
                        firestore.collection("PopularProducts")
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            for (DocumentSnapshot doc : task.getResult().getDocuments()) {
                                                PopularProductsModel popularProductsModel = doc.toObject(PopularProductsModel.class);
                                                popularProductsModelList.add(popularProductsModel);
                                                popularProductsAdapter.notifyDataSetChanged();
                                            }
                                        }
                                    }
                                });
                    } else {
                        firestore.collection("PopularProducts").whereGreaterThan("stock", 0)
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            for (DocumentSnapshot doc : task.getResult().getDocuments()) {
                                                PopularProductsModel popularProductsModel = doc.toObject(PopularProductsModel.class);
                                                popularProductsModelList.add(popularProductsModel);
                                                popularProductsAdapter.notifyDataSetChanged();
                                            }
                                        }
                                    }
                                });
                    }
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    userType = documentSnapshot.getString("userType");

                    if (userType.equals("Admin")) {
                        getMenuInflater().inflate(R.menu.admin_main_menu, menu);
                    } else {
                        getMenuInflater().inflate(R.menu.main_menu, menu);
                    }
                }
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_logout) {
            auth.signOut();
            startActivity(new Intent(PopularProductsActivity.this, RegistrationActivity.class));
            finish();

        } else if (id == R.id.menu_my_cart){
            startActivity(new Intent(PopularProductsActivity.this, CartActivity.class));
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(PopularProductsActivity.this, MainActivity.class));
    }
}
