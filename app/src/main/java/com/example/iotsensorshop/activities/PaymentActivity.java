package com.example.iotsensorshop.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.example.iotsensorshop.R;
import com.example.iotsensorshop.adapters.PurchaseHistoryAdapter;
import com.example.iotsensorshop.fragments.PurchaseHistoryFragment;
import com.example.iotsensorshop.models.PurchaseHistoryModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PaymentActivity extends AppCompatActivity implements PaymentResultListener {

    double amount = 0.0;
    String userId;
    String name;
    String email;
    String userType;
    Toolbar toolbar;
    TextView subTotal, discount, shipping, total;
    Button paymentBtn;
    List<PurchaseHistoryModel> list;
    PurchaseHistoryAdapter purchaseHistoryAdapter;

    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        //Toolbar
        toolbar = findViewById(R.id.payment_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PaymentActivity.this, AddressActivity.class));
            }
        });

        subTotal = findViewById(R.id.sub_total);
        discount = findViewById(R.id.discount);
        shipping = findViewById(R.id.shipping);
        total = findViewById(R.id.total_amt);
        paymentBtn = findViewById(R.id.pay_btn);

        userId = auth.getCurrentUser().getUid();

        subTotal.setText(amount + "$");
        
        paymentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //paymentMethod();

                firestore.collection("AddToCart")
                        .document(auth.getCurrentUser().getUid())
                        .collection("User")
                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for (DocumentSnapshot doc :task.getResult()) {
                                String documentId = doc.getId();
                                DocumentReference documentReference = firestore.collection("AddToCart")
                                        .document(auth.getCurrentUser().getUid())
                                        .collection("User")
                                        .document(documentId);

                                documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            DocumentSnapshot documentSnapshot = task.getResult();

                                            Map<String, Object> productDetail = new HashMap<>();
                                            productDetail.put("currentDate", documentSnapshot.getString("currentDate"));
                                            productDetail.put("currentTime", documentSnapshot.getString("currentTime"));
                                            productDetail.put("productName", documentSnapshot.getString("productName"));
                                            productDetail.put("productPrice", documentSnapshot.getString("productPrice"));
                                            productDetail.put("totalPrice", documentSnapshot.getDouble("totalPrice").intValue());
                                            productDetail.put("totalQuantity", documentSnapshot.getString("totalQuantity"));

                                            firestore.collection("PurchaseHistory").document(auth.getCurrentUser().getUid())
                                                    .collection("User")
                                                    .add(productDetail);

                                            firestore.collection("AddToCart").document(auth.getCurrentUser().getUid())
                                                    .collection("User")
                                                    .document(documentId)
                                                    .delete();

                                            DocumentReference documentReferenceOrder = firestore.collection("users").document(userId);
                                            documentReferenceOrder.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                @Override
                                                public void onSuccess(DocumentSnapshot documentSnapshotOrder) {
                                                    if(documentSnapshotOrder.exists()) {
                                                        name = documentSnapshotOrder.getString("Name");
                                                        email = documentSnapshotOrder.getString("Email");

                                                        Map<String, Object> order = new HashMap<>();
                                                        order.put("currentDate", documentSnapshot.getString("currentDate"));
                                                        order.put("currentTime", documentSnapshot.getString("currentTime"));
                                                        order.put("productName", documentSnapshot.getString("productName"));
                                                        order.put("productPrice", documentSnapshot.getString("productPrice"));
                                                        order.put("totalPrice", documentSnapshot.getDouble("totalPrice").intValue());
                                                        order.put("totalQuantity", documentSnapshot.getString("totalQuantity"));
                                                        order.put("name", name);
                                                        order.put("email", email);

                                                        firestore.collection("Order")
                                                                .add(order);
                                                    }
                                                }
                                            });
                                        }
                                    }
                                });
                            }
                        }
                    }
                });

                Toast.makeText(PaymentActivity.this, "Payment Successful", Toast.LENGTH_SHORT).show();

                startActivity(new Intent(PaymentActivity.this, MainActivity.class));

            }
        });
    }

    private void paymentMethod() {

        Checkout checkout = new Checkout();

        final Activity activity = PaymentActivity.this;

        try {
            JSONObject options = new JSONObject();
            //Set Company Name
            options.put("name", "My E-Commerce App");
            //Ref no
            options.put("description", "Reference No. #123456");
            //Image to be display
            options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.png");
            //options.put("order_id", "order_9A33XWu170gUtm");
            // Currency type
            options.put("currency", "USD");
            //double total = Double.parseDouble(mAmountText.getText().toString());
            //multiply with 100 to get exact amount in rupee
            amount = amount * 100;
            //amount
            options.put("amount", amount);
            JSONObject preFill = new JSONObject();
            //email
            preFill.put("email", "developer.kharag@gmail.com");
            //contact
            preFill.put("contact", "7489347378");

            options.put("prefill", preFill);

            checkout.open(activity, options);
        } catch (Exception e) {
            Log.e("TAG", "Error in starting Razorpay Checkout", e);
        }
    }

    @Override
    public void onPaymentSuccess(String s) {
        Toast.makeText(this, "Payment Successful", Toast.LENGTH_SHORT).show();


    }

    @Override
    public void onPaymentError(int i, String s) {

        Toast.makeText(this, "Payment Cancel", Toast.LENGTH_SHORT).show();
    }
}