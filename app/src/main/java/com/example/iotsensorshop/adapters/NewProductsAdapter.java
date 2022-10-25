package com.example.iotsensorshop.adapters;

import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.iotsensorshop.R;
import com.example.iotsensorshop.activities.DetailedActivity;
import com.example.iotsensorshop.activities.MainActivity;
import com.example.iotsensorshop.activities.NewProductActivity;
import com.example.iotsensorshop.fragments.HomeFragment;
import com.example.iotsensorshop.models.NewProductsModel;
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
import android.content.Context;
import android.widget.Toast;

public class NewProductsAdapter extends RecyclerView.Adapter<NewProductsAdapter.ViewHolder> {

    private Context context;
    private List<NewProductsModel> list;
    FirebaseAuth auth;
    FirebaseFirestore fStore;
    String userId;
    DocumentReference documentReference;
    String userType;
    String name;
    String email;

    public NewProductsAdapter(Context context, List<NewProductsModel> list) {
        this.context = context;
        this.list = list;
        auth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userId = auth.getCurrentUser().getUid();
        documentReference = fStore.collection("users").document(userId);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.new_products, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Glide.with(context).load(list.get(position).getImg_url()).into(holder.newImg);
        holder.newName.setText(list.get(position).getName());
        holder.newPrice.setText(String.valueOf(list.get(position).getPrice()));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
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
                                CollectionReference collectionReference = fStore.collection("NewProducts");
                                collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()){
                                            for(DocumentSnapshot documentSnapshot : task.getResult()) {
                                                if (documentSnapshot.getString("documentId").equals(list.get(holder.getAdapterPosition()).getDocumentId())){
                                                    DocumentReference documentReference = fStore.collection("NewProducts").document(documentSnapshot.getId());
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
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView newImg;
        TextView newName, newPrice;
        private ImageView mDeleteItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            newImg = itemView.findViewById(R.id.new_img);
            newName = itemView.findViewById(R.id.new_product_name);
            newPrice = itemView.findViewById(R.id.new_price);

            mDeleteItem = itemView.findViewById(R.id.delete);
        }
    }
}
