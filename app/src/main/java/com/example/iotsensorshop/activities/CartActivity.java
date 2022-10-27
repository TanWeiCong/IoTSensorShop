package com.example.iotsensorshop.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.iotsensorshop.R;
import com.example.iotsensorshop.adapters.MyCartAdapter;
import com.example.iotsensorshop.models.MyCartModel;
import com.example.iotsensorshop.models.NewProductsModel;
import com.example.iotsensorshop.models.PopularProductsModel;
import com.example.iotsensorshop.models.ShowAllModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.protobuf.StringValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CartActivity extends AppCompatActivity {

    double overallTotalAmount;
    TextView overAllAmount;
    ImageView detailedImg;
    TextView name, description, price, quantity;
    ConstraintLayout constraintLayout;
    Button buyNow;
    Toolbar toolbar;
    RecyclerView recyclerView;
    List<MyCartModel> cartModelList;
    MyCartAdapter cartAdapter;
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;

    int totalQuantity = 1;
    int totalPrice = 0;

    //New Products
    NewProductsModel newProductsModel = null;

    //Popular Products
    PopularProductsModel popularProductsModel = null;

    //Show All
    ShowAllModel showAllModel = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        final Object obj = getIntent().getSerializableExtra("detailed");

        if (obj instanceof NewProductsModel) {
            newProductsModel = (NewProductsModel) obj;
        }else if (obj instanceof PopularProductsModel) {
            popularProductsModel = (PopularProductsModel) obj;
        }else if (obj instanceof ShowAllModel) {
            showAllModel = (ShowAllModel) obj;
        }

        constraintLayout = findViewById(R.id.constraint_cart);
        detailedImg = findViewById(R.id.detailed_img);
        quantity = findViewById(R.id.quantity);
        name = findViewById(R.id.detailed_name);
        description = findViewById(R.id.detailed_desc);
        price = findViewById(R.id.detailed_price);

        toolbar = findViewById(R.id.my_cart_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firestore.collection("payment")
                        .document(auth.getCurrentUser().getUid())
                        .collection("User")
                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot documentSnapshot : task.getResult()) {
                                String documentId = documentSnapshot.getId();
                                DocumentReference documentReferencePay = firestore.collection("payment")
                                        .document(auth.getCurrentUser().getUid())
                                        .collection("User")
                                        .document(documentId);

                                documentReferencePay.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            firestore.collection("payment").document(auth.getCurrentUser().getUid())
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

                startActivity(new Intent(CartActivity.this, MainActivity.class));
            }
        });

        //get data from my cart adapter
        /*
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(mMessageReceiver,new IntentFilter("MyTotalAmount"));

         */

        overAllAmount = findViewById(R.id.over_all_amount);
        recyclerView = findViewById(R.id.cart_rec);
        buyNow = findViewById(R.id.buy_now);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        cartModelList = new ArrayList<>();
        cartAdapter = new MyCartAdapter(this, cartModelList);
        recyclerView.setAdapter(cartAdapter);

        firestore.collection("AddToCart").document(auth.getCurrentUser().getUid())
                .collection("User")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (DocumentSnapshot doc :task.getResult().getDocuments()) {
                        String documentId = doc.getId();
                        MyCartModel myCartModel = doc.toObject(MyCartModel.class);

                        myCartModel.setDocumentId(documentId);

                        cartModelList.add(myCartModel);
                        cartAdapter.notifyDataSetChanged();

                    }

                    calculateTotalAmount(cartModelList);

                    //overallTotalAmount = totalAmount;
                }
            }
        });

        /*
        if ( cartModelList == null || cartModelList.isEmpty() || cartModelList.get(0) == null ) {
            constraintLayout.setVisibility(View.VISIBLE);
        }

        if (cartModelList != null && !cartModelList.isEmpty()) {
            constraintLayout.setVisibility(View.INVISIBLE);
        }

        if (cartAdapter!=null) {
                            if (cartAdapter.getItemCount() > 0) {
                                constraintLayout.setVisibility(View.GONE);
                            } else {
                                constraintLayout.setVisibility(View.VISIBLE);
                            }
                        }

         */

        //New Products
        if (newProductsModel != null){
            Glide.with(getApplicationContext()).load(newProductsModel.getImg_url()).into(detailedImg);
            name.setText(newProductsModel.getName());
            description.setText(newProductsModel.getDescription());
            price.setText(String.valueOf(newProductsModel.getPrice()));
            name.setText(newProductsModel.getName());

            totalPrice = newProductsModel.getPrice() * totalQuantity;
        }

        //Popular Products
        if (popularProductsModel != null){
            Glide.with(getApplicationContext()).load(popularProductsModel.getImg_url()).into(detailedImg);
            name.setText(popularProductsModel.getName());
            description.setText(popularProductsModel.getDescription());
            price.setText(String.valueOf(popularProductsModel.getPrice()));
            name.setText(popularProductsModel.getName());

            totalPrice = popularProductsModel.getPrice() * totalQuantity;
        }

        //Show All Products
        if (showAllModel != null){
            Glide.with(getApplicationContext()).load(showAllModel.getImg_url()).into(detailedImg);
            name.setText(showAllModel.getName());
            description.setText(showAllModel.getDescription());
            price.setText(String.valueOf(showAllModel.getPrice()));
            name.setText(showAllModel.getName());

            totalPrice = showAllModel.getPrice() * totalQuantity;
        }

        //Buy Now
        buyNow.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CartActivity.this, AddressActivity.class);
                //intent.putExtra("amount", overallTotalAmount);

                if(newProductsModel != null){
                    intent.putExtra("item", newProductsModel);
                }
                if(popularProductsModel != null){
                    intent.putExtra("item", popularProductsModel);
                }
                if(showAllModel != null){
                    intent.putExtra("item", showAllModel);
                }

                startActivity(intent);
            }
        });
    }

    private void calculateTotalAmount(List<MyCartModel> cartModelList) {
        int totalAmount = 0;
        for (MyCartModel myCartModel : cartModelList){
            totalAmount += myCartModel.getTotalPrice();
        }

        if (totalAmount == 0) {
            constraintLayout.setVisibility(View.VISIBLE);
            buyNow.setVisibility(View.GONE);
        } else {
            constraintLayout.setVisibility(View.GONE);
            buyNow.setVisibility(View.VISIBLE);
        }

        overAllAmount.setText("Total Amount: RM" + totalAmount);

        final HashMap<String, Object> cartMap = new HashMap<>();

        cartMap.put("totalAmount", Integer.toString(totalAmount));

        firestore.collection("payment").document(auth.getCurrentUser().getUid())
                .collection("User")
                .add(cartMap);

    }



    /*
    public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int totalBill = intent.getIntExtra("totalAmount", 0);
            overAllAmount.setText("Total Amount : " + totalBill + "$");
        }
    };

     */
}