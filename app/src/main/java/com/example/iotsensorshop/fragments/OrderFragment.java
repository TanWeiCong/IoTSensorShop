package com.example.iotsensorshop.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.iotsensorshop.R;
import com.example.iotsensorshop.adapters.OrderAdapter;
import com.example.iotsensorshop.models.OrderModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class OrderFragment extends Fragment {

    Toolbar toolbar;
    RecyclerView recyclerView;
    ConstraintLayout constraintLayout;
    List<OrderModel> list;
    OrderAdapter orderAdapter;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    public OrderFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_order, container, false);

        constraintLayout = view.findViewById(R.id.constraint_order);
        recyclerView = view.findViewById(R.id.order_rec);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        list = new ArrayList<>();
        orderAdapter = new OrderAdapter(getContext(), list);
        recyclerView.setAdapter(orderAdapter);

        firestore.collection("Order")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot doc : task.getResult().getDocuments()) {
                        String documentId = doc.getId();
                        OrderModel orderModel = doc.toObject(OrderModel.class);

                        orderModel.setDocumentId(documentId);

                        list.add(orderModel);
                        orderAdapter.notifyDataSetChanged();

                    }

                    if (orderAdapter.getItemCount() == 0) {
                        constraintLayout.setVisibility(View.VISIBLE);
                    }

                    if(orderAdapter.getItemCount() > 0) {
                        constraintLayout.setVisibility(View.GONE);
                    }
                }
            }
        });

        if ( list == null || list.isEmpty() || list.get(0) == null ) {
            constraintLayout.setVisibility(View.VISIBLE);
        }else {
            constraintLayout.setVisibility(View.GONE);
        }

        return view;

    }
}