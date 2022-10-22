package com.example.iotsensorshop.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.iotsensorshop.R;
import com.example.iotsensorshop.activities.MainActivity;
import com.example.iotsensorshop.adapters.PurchaseHistoryAdapter;
import com.example.iotsensorshop.models.PurchaseHistoryModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class PurchaseHistoryFragment extends Fragment {

    Toolbar toolbar;
    RecyclerView recyclerView;
    List<PurchaseHistoryModel> list;
    PurchaseHistoryAdapter purchaseHistoryAdapter;
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;

    public PurchaseHistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_purchase_history, container, false);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        recyclerView = view.findViewById(R.id.purchase_history_rec);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        list = new ArrayList<>();
        purchaseHistoryAdapter = new PurchaseHistoryAdapter(getContext(), list);
        recyclerView.setAdapter(purchaseHistoryAdapter);

        firestore.collection("PurchaseHistory").document(auth.getCurrentUser().getUid())
                .collection("User")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot doc : task.getResult().getDocuments()) {
                        String documentId = doc.getId();
                        PurchaseHistoryModel purchaseHistoryModel = doc.toObject(PurchaseHistoryModel.class);

                        purchaseHistoryModel.setDocumentId(documentId);

                        list.add(purchaseHistoryModel);
                        purchaseHistoryAdapter.notifyDataSetChanged();
                    }
                }
            }
        });
/*
            firestore.collection("AddToCart").document(auth.getCurrentUser().getUid())
                    .collection("User").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot doc : task.getResult().getDocuments()) {
                            String documentId = doc.getId();
                            PurchaseHistoryModel purchaseHistoryModel = doc.toObject(PurchaseHistoryModel.class);

                            purchaseHistoryModel.setDocumentId(documentId);

                            list.add(purchaseHistoryModel);
                            purchaseHistoryAdapter.notifyDataSetChanged();

                            DocumentReference documentReference = firestore.collection("ShowAll").document(auth.getCurrentUser().getUid())
                                    .collection("User").document(documentId);
                            documentReference.delete();
                        }
                    }
                }
            });


        }

 */
        return view;
    }

}
