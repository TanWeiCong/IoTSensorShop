package com.example.iotsensorshop.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.iotsensorshop.R;
import com.example.iotsensorshop.adapters.NewProductsAdapter;
import com.example.iotsensorshop.adapters.ShowAllAdapter;
import com.example.iotsensorshop.models.NewProductsModel;
import com.example.iotsensorshop.models.ShowAllModel;
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

public class NewProductActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    NewProductsAdapter newProductsAdapter;
    List<NewProductsModel> newProductsModelList;

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
                startActivity(new Intent(NewProductActivity.this, MainActivity.class));
            }
        });

        firestore = FirebaseFirestore.getInstance();
        documentReference = firestore.collection("users").document(userId);

        recyclerView = findViewById(R.id.new_product_rec);
        recyclerView.setVisibility(View.VISIBLE);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        newProductsModelList = new ArrayList<>();
        newProductsAdapter = new NewProductsAdapter(this, newProductsModelList);
        recyclerView.setAdapter(newProductsAdapter);

        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    name = documentSnapshot.getString("Name");
                    email = documentSnapshot.getString("Email");
                    userType = documentSnapshot.getString("userType");
                    UserModel user = new UserModel(userId, name, email, userType);

                    if (user.getUserType().equals("Admin")) {
                        firestore.collection("NewProducts")
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            for (DocumentSnapshot doc : task.getResult().getDocuments()) {
                                                NewProductsModel newProductModel = doc.toObject(NewProductsModel.class);
                                                newProductsModelList.add(newProductModel);
                                                newProductsAdapter.notifyDataSetChanged();
                                            }
                                        }
                                    }
                                });
                    } else {
                        firestore.collection("NewProducts").whereGreaterThan("stock", 0)
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            for (DocumentSnapshot doc : task.getResult().getDocuments()) {
                                                NewProductsModel newProductModel = doc.toObject(NewProductsModel.class);
                                                newProductsModelList.add(newProductModel);
                                                newProductsAdapter.notifyDataSetChanged();
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
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_logout) {
            auth.signOut();
            startActivity(new Intent(NewProductActivity.this, RegistrationActivity.class));
            finish();

        } else if (id == R.id.menu_my_cart){
            startActivity(new Intent(NewProductActivity.this, CartActivity.class));
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(NewProductActivity.this, MainActivity.class));
    }

}
