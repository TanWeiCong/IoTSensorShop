package com.example.iotsensorshop.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.iotsensorshop.R;
import com.example.iotsensorshop.activities.CartActivity;
import com.example.iotsensorshop.activities.DetailedActivity;
import com.example.iotsensorshop.activities.MainActivity;
import com.example.iotsensorshop.models.MyCartModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

public class MyCartAdapter extends RecyclerView.Adapter<MyCartAdapter.ViewHolder> {
    Context context;
    List<MyCartModel> list;
    int totalAmount = 0;
    FirebaseFirestore firestore;
    FirebaseAuth auth;

    public MyCartAdapter(Context context, List<MyCartModel> list) {
        this.context = context;
        this.list = list;
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.my_cart_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.date.setText(list.get(position).getCurrentDate());
        holder.time.setText(list.get(position).getCurrentTime());
        holder.price.setText("RM" + list.get(position).getProductPrice());
        holder.name.setText(list.get(position).getProductName());
        holder.totalPrice.setText("RM" + String.valueOf(list.get(position).getTotalPrice()));
        holder.totalQuantity.setText(list.get(position).getTotalQuantity());

        holder.deleteItem.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                firestore.collection("AddToCart").document(auth.getCurrentUser().getUid())
                        .collection("User")
                        .document(list.get(holder.getAdapterPosition()).getDocumentId())
                        .delete()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    list.remove(list.get(holder.getAdapterPosition()));
                                    notifyDataSetChanged();
                                    Intent intent = new Intent(context, CartActivity.class);
                                    context.startActivity(intent);
                                    Toast.makeText(context, "Item Deleted", Toast.LENGTH_SHORT).show();
                                }else {
                                    Toast.makeText(context, "Error" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
/*
        holder.editItem.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, DetailedActivity.class);
                intent.putExtra("detailed", list.get(holder.getAdapterPosition()));
                context.startActivity(intent);
            }
        });


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CharSequence options[] = new CharSequence[]
                        {
                                "Edit",
                                "Remove"
                        };
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Cart Options:");

                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (i == 0) {
                            Intent intent = new Intent(context, DetailedActivity.class);
                            intent.putExtra("pid", list.get(holder.getAdapterPosition()).getDocumentId());
                            context.startActivity(intent);
                        }else {
                            firestore.collection("CurrentUser").document(auth.getCurrentUser().getUid())
                                    .collection("AddToCart")
                                    .document(list.get(holder.getAdapterPosition()).getDocumentId())
                                    .delete()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                list.remove(list.get(holder.getAdapterPosition()));
                                                notifyDataSetChanged();
                                                Toast.makeText(context, "Item Deleted", Toast.LENGTH_SHORT).show();
                                            }else{
                                                Toast.makeText(context, "Failed to delete item" + task.getException(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }
                    }
                });
            }
        });

 */

        //Total amount pass to Cart Activity
        /*
        totalAmount = totalAmount + list.get(position).getTotalPrice();
        Intent intent = new Intent("MyTotalAmount");
        intent.putExtra("totalAmount", totalAmount);

        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

         */

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, price, date, time, totalQuantity, totalPrice;
        ImageView deleteItem;
        //ImageView editItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.product_name);
            price = itemView.findViewById(R.id.product_price);
            date = itemView.findViewById(R.id.current_date);
            time = itemView.findViewById(R.id.current_time);
            totalQuantity = itemView.findViewById(R.id.total_quantity);
            totalPrice = itemView.findViewById(R.id.total_price);
            deleteItem = itemView.findViewById(R.id.delete);
            //editItem = itemView.findViewById(R.id.edit);
        }
    }
}
