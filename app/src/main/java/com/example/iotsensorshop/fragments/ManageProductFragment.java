package com.example.iotsensorshop.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.iotsensorshop.R;
import com.example.iotsensorshop.activities.ShowAllActivity;

public class ManageProductFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_manage_product, container, false);

        CardView addProduct = view.findViewById(R.id.cv_add);
        CardView editProduct = view.findViewById(R.id.cv_edit);
        CardView deleteProduct = view.findViewById(R.id.cv_delete);

        addProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.home_container, new AddProductFragment()).commit();
            }
        });

        editProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.home_container, new EditProductFragment()).commit();
            }
        });

        deleteProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity().getApplication(), ShowAllActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }

}