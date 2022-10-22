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
import com.example.iotsensorshop.activities.PopularProductsActivity;
import com.example.iotsensorshop.fragments.HomeFragment;
import com.example.iotsensorshop.models.PopularProductsModel;
import com.example.iotsensorshop.models.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

public class PopularProductsAdapter extends RecyclerView.Adapter<PopularProductsAdapter.ViewHolder> {

    private Context context;
    private List<PopularProductsModel> popularProductsModelList;
    FirebaseAuth auth;
    FirebaseFirestore fStore;
    String userId;
    DocumentReference documentReference;
    String userType;
    String name;
    String email;

    public PopularProductsAdapter(Context context, List<PopularProductsModel> popularProductsModelList) {
        this.context = context;
        this.popularProductsModelList = popularProductsModelList;
        auth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userId = auth.getCurrentUser().getUid();
        documentReference = fStore.collection("users").document(userId);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.popular_items, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Glide.with(context).load(popularProductsModelList.get(position).getImg_url()).into(holder.imageView);
        holder.name.setText(popularProductsModelList.get(position).getName());
        holder.price.setText(String.valueOf(popularProductsModelList.get(position).getPrice()));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, DetailedActivity.class);
                intent.putExtra("detailed", popularProductsModelList.get(holder.getAdapterPosition()));
                context.startActivity(intent);
            }
        });

        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    name = documentSnapshot.getString("Name");
                    email = documentSnapshot.getString("Email");
                    userType = documentSnapshot.getString("userType");
                    UserModel user = new UserModel(userId, name, email, userType);

                    if (user.getUserType().equals("Admin")) {
                        holder.mDeleteItem.setEnabled(true);
                    }

                }
            }
        });

        holder.mDeleteItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog alertDialog = new AlertDialog.Builder(v.getContext())
                        .setTitle("Delete Item")
                        .setMessage("Do you want to delete the item?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                CollectionReference collectionReference = fStore.collection("PopularProducts");
                                collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()){
                                            for(DocumentSnapshot documentSnapshot : task.getResult()) {
                                                if (documentSnapshot.getString("documentId").equals(popularProductsModelList.get(holder.getAdapterPosition()).getDocumentId())){
                                                    DocumentReference documentReference = fStore.collection("PopularProducts").document(documentSnapshot.getId());
                                                    documentReference.delete();
                                                    Toast.makeText(context, "Item Deleted", Toast.LENGTH_SHORT).show();
                                                    Intent intent = new Intent(context, MainActivity.class);
                                                    context.startActivity(intent);
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                });

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
    }

    @Override
    public int getItemCount() {
        return popularProductsModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView name, price;
        private ImageView mDeleteItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.all_img);
            name = itemView.findViewById(R.id.all_product_name);
            price = itemView.findViewById(R.id.all_price);
            mDeleteItem = itemView.findViewById(R.id.delete);

            mDeleteItem.setEnabled(false);
        }
    }
}
