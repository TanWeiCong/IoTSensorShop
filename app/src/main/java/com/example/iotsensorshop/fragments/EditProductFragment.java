package com.example.iotsensorshop.fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.iotsensorshop.R;
import com.example.iotsensorshop.activities.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class EditProductFragment extends Fragment {

    FirebaseFirestore firestore;
    ImageView imgProduct;
    EditText name, description, price, type, stock, existName, currentType;
    Button editProduct;
    String choices;
    Uri path;
    StorageReference storageReference;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_product, container, false);

        imgProduct = view.findViewById(R.id.img_product);
        name = view.findViewById(R.id.product_name);
        description = view.findViewById(R.id.product_description);
        price = view.findViewById(R.id.product_price);
        type = view.findViewById(R.id.product_type);
        editProduct = view.findViewById(R.id.btn_edit_product);
        stock = view.findViewById(R.id.product_stock);
        existName = view.findViewById(R.id.product_exist_name);
        currentType = view.findViewById(R.id.product_exist_type);

        firestore = FirebaseFirestore.getInstance();

        editProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String productName = name.getText().toString().trim();
                String productDescription = description.getText().toString().trim();
                String productPrice = price.getText().toString().trim();
                String productType = type.getText().toString().trim();
                String productStock = stock.getText().toString().trim();
                String productExistName = existName.getText().toString().trim();
                String productCurrentType = currentType.getText().toString().trim();

                if(TextUtils.isEmpty(productExistName)){
                    Toast.makeText(getContext(), "Please enter existed product name!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(TextUtils.isEmpty(productName)){
                    Toast.makeText(getContext(), "Please enter product name!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(TextUtils.isEmpty(productDescription)){
                    Toast.makeText(getContext(), "Please enter product description!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(TextUtils.isEmpty(productPrice)){
                    Toast.makeText(getContext(), "Please enter product price!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(TextUtils.isEmpty(productType)){
                    Toast.makeText(getContext(), "Please product type!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(TextUtils.isEmpty(productStock)){
                    Toast.makeText(getContext(), "Please enter product stock!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(TextUtils.isEmpty(productCurrentType)){
                    Toast.makeText(getContext(), "Please enter current product type!", Toast.LENGTH_SHORT).show();
                    return;
                }

                String documentId = productExistName + productCurrentType;

                int integerPrice = Integer.valueOf(productPrice);
                int integerStock = Integer.valueOf(productStock);

                DocumentReference documentReference = firestore.collection("ShowAll").document(documentId);
                documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Map<String, Object> productDetail = new HashMap<>();
                                productDetail.put("name", productName);
                                productDetail.put("type", productType);
                                productDetail.put("description", productDescription);
                                productDetail.put("price", integerPrice);
                                productDetail.put("img_url", path);
                                productDetail.put("documentId", documentId);
                                productDetail.put("stock", integerStock);
                                documentReference.set(productDetail);

                                firestore.collection("ShowAll").document(documentId)
                                        .update(productDetail)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Toast.makeText(getContext(), "Successfully Updated!", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(getActivity().getApplication(), MainActivity.class);
                                                startActivity(intent);
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getContext(), "Failed to Update!", Toast.LENGTH_SHORT).show();
                                    }
                                });

                            } else {
                                Toast.makeText(getContext(), "The Product Does Not Existed!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });

                storageReference = FirebaseStorage.getInstance().getReference("images/*");

                storageReference.putFile(path)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Toast.makeText(getContext(), "Successfully Updated", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (path == null) {
                            Toast.makeText(getContext(), "Failed to upload image", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        imgProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                activityResultLauncher.launch(intent);

            }
        });

        return view;
    }

    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        path = data.getData();
                        imgProduct.setImageURI(path);
                    }
                }
            });
}
