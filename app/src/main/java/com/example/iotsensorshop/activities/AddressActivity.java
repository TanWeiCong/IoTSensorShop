package com.example.iotsensorshop.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.widget.Toolbar;

import com.example.iotsensorshop.R;
import com.example.iotsensorshop.adapters.AddressAdapter;
import com.example.iotsensorshop.adapters.MyCartAdapter;
import com.example.iotsensorshop.models.AddressModel;
import com.example.iotsensorshop.models.MyCartModel;
import com.example.iotsensorshop.models.NewProductsModel;
import com.example.iotsensorshop.models.PopularProductsModel;
import com.example.iotsensorshop.models.PurchaseHistoryModel;
import com.example.iotsensorshop.models.ShowAllModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AddressActivity extends AppCompatActivity implements AddressAdapter.SelectedAddress {

    Button addAddress;
    RecyclerView recyclerView;
    ConstraintLayout constraintLayout;
    private List<AddressModel> addressModelList;
    private AddressAdapter addressAdapter;
    FirebaseFirestore firestore;
    FirebaseAuth auth;
    Button paymentBtn;
    Toolbar toolbar;
    String mAddress = "";
    //Double totalAmount;
    //Double amount = getIntent().getDoubleExtra("amount", totalAmount);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);

        toolbar = findViewById(R.id.address_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firestore.collection("orderAddress")
                        .document(auth.getCurrentUser().getUid())
                        .collection("User")
                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot documentSnapshot : task.getResult()) {
                                String documentId = documentSnapshot.getId();
                                DocumentReference documentReferencePay = firestore.collection("orderAddress")
                                        .document(auth.getCurrentUser().getUid())
                                        .collection("User")
                                        .document(documentId);

                                documentReferencePay.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            firestore.collection("orderAddress").document(auth.getCurrentUser().getUid())
                                                    .collection("User")
                                                    .document(documentId)
                                                    .delete();
                                        }
                                    }
                                });
                            }
                        }
                    }
                });

                startActivity(new Intent(AddressActivity.this, CartActivity.class));
            }
        });

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();


        constraintLayout = findViewById(R.id.constraint_address);
        recyclerView = findViewById(R.id.address_recycler);
        paymentBtn = findViewById(R.id.payment_btn);
        addAddress = findViewById(R.id.add_address_btn);

        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        addressModelList = new ArrayList<>();
        addressAdapter = new AddressAdapter(getApplicationContext(), addressModelList, this);
        recyclerView.setAdapter(addressAdapter);

        firestore.collection("CurrentUser").document(auth.getCurrentUser().getUid())
                .collection("Address").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (DocumentSnapshot doc :task.getResult().getDocuments()) {
                        AddressModel addressModel = doc.toObject(AddressModel.class);
                        addressModelList.add(addressModel);
                        addressAdapter.notifyDataSetChanged();
                    }

                    if (addressAdapter.getItemCount() == 0) {
                        constraintLayout.setVisibility(View.VISIBLE);
                        paymentBtn.setVisibility(View.GONE);
                    }

                    if(addressAdapter.getItemCount() > 0) {
                        constraintLayout.setVisibility(View.GONE);
                        paymentBtn.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        paymentBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(AddressActivity.this, PaymentActivity.class);
                //intent.putExtra("amount", amount);
                startActivity(intent);
            }
        });

        addAddress.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddressActivity.this, AddAddressActivity.class);
                //intent.putExtra("amount", amount);
                startActivity(intent);
            }
        });
    }

    @Override
    public void setAddress(String address) {
        mAddress = address;
    }
}