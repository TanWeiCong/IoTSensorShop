package com.example.iotsensorshop.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.iotsensorshop.R;
import com.example.iotsensorshop.activities.DetailedActivity;
import com.example.iotsensorshop.activities.MainActivity;
import com.example.iotsensorshop.activities.ShowAllActivity;
import com.example.iotsensorshop.fragments.HomeFragment;
import com.example.iotsensorshop.models.ShowAllModel;
import com.example.iotsensorshop.models.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.protobuf.StringValue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import static java.sql.DriverManager.println;

public class ShowAllAdapter extends RecyclerView.Adapter<ShowAllAdapter.ViewHolder> {

    private Context context;
    private List<ShowAllModel> list;
    FirebaseAuth auth;
    FirebaseFirestore fStore;
    String userId;
    DocumentReference documentReference;
    String userType;
    String name;
    String email;

    public ShowAllAdapter(Context context, List<ShowAllModel> list) {
        this.context = context;
        this.list = list;
        auth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userId = auth.getCurrentUser().getUid();
        documentReference = fStore.collection("users").document(userId);
    }

    public ShowAllAdapter() {
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.show_all_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Glide.with(context).load(list.get(position).getImg_url()).into(holder.mItemImage);
        holder.mCost.setText("RM" + list.get(position).getPrice());
        holder.mName.setText(list.get(position).getName());

        holder.itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, DetailedActivity.class);
                intent.putExtra("detailed", list.get(holder.getAdapterPosition()));
                context.startActivity(intent);
            }
        });

        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    userType = documentSnapshot.getString("userType");

                    if (userType.equals("Admin")) {
                        holder.mDeleteItem.setVisibility(View.VISIBLE);
                        holder.mMinusBtn.setVisibility(View.VISIBLE);
                        holder.mAddBtn.setVisibility(View.VISIBLE);
                        holder.mStock.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        String documentId = (list.get(holder.getAdapterPosition()).getDocumentId());
        String productName = (list.get(holder.getAdapterPosition()).getName());
        String productType = (list.get(holder.getAdapterPosition()).getType());
        String productDes = (list.get(holder.getAdapterPosition()).getDescription());
        String productImg = (list.get(holder.getAdapterPosition()).getImg_url());
        int productPrice = (list.get(holder.getAdapterPosition()).getPrice());

        holder.mDeleteItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog alertDialog = new AlertDialog.Builder(v.getContext())
                        .setTitle("Delete Item")
                        .setMessage("Do you want to delete the item?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                CollectionReference collectionReference = fStore.collection("ShowAll");
                                collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()){
                                            for(DocumentSnapshot documentSnapshot : task.getResult()) {
                                                if (documentSnapshot.getString("documentId").equals(list.get(holder.getAdapterPosition()).getDocumentId())){
                                                    DocumentReference documentReference = fStore.collection("ShowAll").document(documentSnapshot.getId());
                                                    documentReference.delete();

                                                    CollectionReference collectionReferenceNewProduct = fStore.collection("NewProducts");
                                                    collectionReferenceNewProduct.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                            if (task.isSuccessful()){
                                                                for(DocumentSnapshot documentSnapshot : task.getResult()) {
                                                                    if (documentSnapshot.getString("documentId").equals(list.get(holder.getAdapterPosition()).getDocumentId())){
                                                                        DocumentReference documentReference = fStore.collection("NewProducts").document(documentSnapshot.getId());
                                                                        documentReference.delete();

                                                                        CollectionReference collectionReferencePopularProduct = fStore.collection("PopularProducts");
                                                                        collectionReferencePopularProduct.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                                if (task.isSuccessful()){
                                                                                    for(DocumentSnapshot documentSnapshot : task.getResult()) {
                                                                                        if (documentSnapshot.getString("documentId").equals(list.get(holder.getAdapterPosition()).getDocumentId())){
                                                                                            DocumentReference documentReference = fStore.collection("PopularProducts").document(documentSnapshot.getId());
                                                                                            documentReference.delete();
                                                                                            Toast.makeText(context, "Item Deleted", Toast.LENGTH_SHORT).show();
                                                                                            Intent intent = new Intent(context, MainActivity.class);
                                                                                            context.startActivity(intent);
                                                                                        } else {
                                                                                            break;
                                                                                        }
                                                                                    }
                                                                                }
                                                                            }
                                                                        });
                                                                    } else {
                                                                        CollectionReference collectionReferencePopularProduct = fStore.collection("PopularProducts");
                                                                        collectionReferencePopularProduct.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                                if (task.isSuccessful()){
                                                                                    for(DocumentSnapshot documentSnapshot : task.getResult()) {
                                                                                        if (documentSnapshot.getString("documentId").equals(list.get(holder.getAdapterPosition()).getDocumentId())){
                                                                                            DocumentReference documentReference = fStore.collection("PopularProducts").document(documentSnapshot.getId());
                                                                                            documentReference.delete();
                                                                                            Toast.makeText(context, "Item Deleted", Toast.LENGTH_SHORT).show();
                                                                                            Intent intent = new Intent(context, MainActivity.class);
                                                                                            context.startActivity(intent);
                                                                                        } else {
                                                                                            break;
                                                                                        }
                                                                                    }
                                                                                }
                                                                            }
                                                                        });
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    });
                                                }
                                            }
                                        }
                                    }
                                });

                                Toast.makeText(context, "The product is deleted!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(context, MainActivity.class);
                                context.startActivity(intent);
                                /*
                                fStore.collection("ShowAll")
                                        .document(list.get(holder.getAdapterPosition()).getName())
                                        .delete()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    list.remove(list.get(holder.getAdapterPosition()));
                                                    notifyDataSetChanged();
                                                    Intent intent = new Intent(context, ShowAllActivity.class);
                                                    context.startActivity(intent);
                                                    Toast.makeText(context, "Item Deleted", Toast.LENGTH_SHORT).show();
                                                }else {
                                                    Toast.makeText(context, "Error" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });

                                 */
                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).show();


            }
        });

        holder.mStock.setText(String.valueOf(list.get(position).getStock()));

        holder.mAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((list.get(holder.getAdapterPosition()).getStock()) < 50){
                    int addStock = (list.get(holder.getAdapterPosition()).getStock());
                    addStock ++;
                    int finalAddStock = addStock;

                    holder.mStock.setText(String.valueOf(finalAddStock));

                    DocumentReference documentReference = fStore.collection("ShowAll").document(documentId);
                    documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                Map<String, Object> productDetail = new HashMap<>();
                                productDetail.put("stock", finalAddStock);
                                productDetail.put("name", productName);
                                productDetail.put("description", productDes);
                                productDetail.put("price", productPrice);
                                productDetail.put("img_url", productImg);
                                productDetail.put("type", productType);
                                productDetail.put("documentId", documentId);
                                documentReference.set(productDetail);

                                fStore.collection("ShowAll").document(documentId)
                                        .update(productDetail)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Toast.makeText(context, "Successfully Updated!", Toast.LENGTH_SHORT).show();

                                                fStore.collection("NewProducts").document(documentId)
                                                        .update(productDetail);

                                                fStore.collection("PopularProducts").document(documentId)
                                                        .update(productDetail);

                                                Intent intent = new Intent(context, ShowAllActivity.class);
                                                context.startActivity(intent);
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(context, "Failed to Update!", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    });
                }else {
                    Toast.makeText(context, "The Maximum Quantity is 50!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        holder.mMinusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((list.get(holder.getAdapterPosition()).getStock()) > 0){
                    int minusStock = (list.get(holder.getAdapterPosition()).getStock());
                    minusStock --;
                    int finalMinusStock = minusStock;

                    holder.mStock.setText(String.valueOf((list.get(holder.getAdapterPosition()).getStock())));

                    DocumentReference documentReference = fStore.collection("ShowAll").document(documentId);
                    documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                Map<String, Object> productDetail = new HashMap<>();
                                productDetail.put("stock", finalMinusStock);
                                productDetail.put("name", productName);
                                productDetail.put("description", productDes);
                                productDetail.put("price", productPrice);
                                productDetail.put("img_url", productImg);
                                productDetail.put("type", productType);
                                productDetail.put("documentId", documentId);
                                documentReference.set(productDetail);

                                fStore.collection("ShowAll").document(documentId)
                                        .update(productDetail)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Toast.makeText(context, "Successfully Updated!", Toast.LENGTH_SHORT).show();

                                                fStore.collection("NewProducts").document(documentId)
                                                        .update(productDetail);

                                                fStore.collection("PopularProducts").document(documentId)
                                                        .update(productDetail);

                                                Intent intent = new Intent(context, ShowAllActivity.class);
                                                context.startActivity(intent);
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(context, "Failed to Update!", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    });
                }else {
                    Toast.makeText(context, "The Minimum Quantity is 0!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView mItemImage;
        private ImageView mDeleteItem;
        private ImageView mAddBtn;
        private ImageView mMinusBtn;
        private TextView mCost;
        private TextView mName;
        private TextView mStock;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mItemImage = itemView.findViewById(R.id.item_image);
            mCost = itemView.findViewById(R.id.item_cost);
            mName = itemView.findViewById(R.id.item_name);
            mDeleteItem = itemView.findViewById(R.id.delete);
            mAddBtn = itemView.findViewById(R.id.add_btn);
            mMinusBtn = itemView.findViewById(R.id.minus_btn);
            mStock = itemView.findViewById(R.id.tv_stock);

        }
    }
}
