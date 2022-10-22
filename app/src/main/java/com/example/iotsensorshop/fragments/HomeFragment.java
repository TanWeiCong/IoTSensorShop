package com.example.iotsensorshop.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.iotsensorshop.R;
import com.example.iotsensorshop.activities.NewProductActivity;
import com.example.iotsensorshop.activities.PopularProductsActivity;
import com.example.iotsensorshop.activities.ShowAllActivity;
import com.example.iotsensorshop.adapters.CategoryAdapter;
import com.example.iotsensorshop.adapters.NewProductsAdapter;
import com.example.iotsensorshop.adapters.ShowAllAdapter;
import com.example.iotsensorshop.models.NewProductsModel;
import com.example.iotsensorshop.adapters.PopularProductsAdapter;
import com.example.iotsensorshop.models.CategoryModel;
import com.example.iotsensorshop.models.PopularProductsModel;
import com.example.iotsensorshop.models.ShowAllModel;
import com.example.iotsensorshop.models.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    TextView catShowAll, popularShowAll, newProductShowAll;
    EditText searchBox;
    RecyclerView recyclerViewSearch;
    LinearLayout linearLayout;
    ProgressDialog progressDialog;
    RecyclerView catRecyclerview, newProductRecyclerview, popularRecyclerview;

    ShowAllAdapter showAllAdapter;
    List<ShowAllModel> showAllModelList;

    //Category recyclerview
    CategoryAdapter categoryAdapter;
    List<CategoryModel> categoryModelList;

    //New Product Recyclerview
    NewProductsAdapter newProductsAdapter;
    List<NewProductsModel> newProductsModelList;

    //Popular Products
    PopularProductsAdapter popularProductsAdapter;
    List<PopularProductsModel> popularProductsModelList;

    //Firestore
    FirebaseFirestore db;

    String userId;

    Toolbar toolbar;

    FirebaseAuth auth;
    DocumentReference documentReference;
    String userType;
    String name;
    String email;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        userId = auth.getCurrentUser().getUid();
        documentReference = db.collection("users").document(userId);

        progressDialog = new ProgressDialog(getActivity());
        catRecyclerview = root.findViewById(R.id.rec_category);
        newProductRecyclerview = root.findViewById(R.id.new_product_rec);
        popularRecyclerview = root.findViewById(R.id.popular_rec);

        catShowAll = root.findViewById(R.id.category_see_all);
        popularShowAll = root.findViewById(R.id.popular_see_all);
        newProductShowAll = root.findViewById(R.id.newProducts_see_all);

        catShowAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), ShowAllActivity.class);
                startActivity(intent);
            }
        });

        newProductShowAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), NewProductActivity.class);
                startActivity(intent);
            }
        });

        popularShowAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), PopularProductsActivity.class);
                startActivity(intent);
            }
        });

        linearLayout = root.findViewById(R.id.home_layout);
        linearLayout.setVisibility(View.GONE);
        //image slider
        ImageSlider imageSlider = root.findViewById(R.id.image_slider);
        List<SlideModel> slideModels = new ArrayList<>();

        slideModels.add(new SlideModel(R.drawable.banner1, "Fast Delivery!", ScaleTypes.CENTER_CROP));
        slideModels.add(new SlideModel(R.drawable.banner2, "Have a Nice Day!", ScaleTypes.CENTER_CROP));
        slideModels.add(new SlideModel(R.drawable.banner3, "Welcome to IoT Sensor Shop!", ScaleTypes.CENTER_CROP));

        imageSlider.setImageList(slideModels);

        progressDialog.setTitle("Welcome To My ECommerce App");
        progressDialog.setMessage("please wait....");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        //Category
        catRecyclerview.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false));
        categoryModelList = new ArrayList<>();
        categoryAdapter = new CategoryAdapter(getContext(), categoryModelList);
        catRecyclerview.setAdapter(categoryAdapter);

        db.collection("Category")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                CategoryModel categoryModel = document.toObject(CategoryModel.class);
                                categoryModelList.add(categoryModel);
                                categoryAdapter.notifyDataSetChanged();
                                linearLayout.setVisibility(View.VISIBLE);
                                progressDialog.dismiss();
                            }
                        } else {
                            Toast.makeText(getActivity(), "" + task.getException(), Toast.LENGTH_SHORT).show();
                        }
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

                    //New Products
                    newProductRecyclerview.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false));
                    newProductsModelList = new ArrayList<>();
                    newProductsAdapter = new NewProductsAdapter(getContext(), newProductsModelList);
                    newProductRecyclerview.setAdapter(newProductsAdapter);

                    //Popular Products
                    popularRecyclerview.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false));
                    popularProductsModelList = new ArrayList<>();
                    popularProductsAdapter = new PopularProductsAdapter(getContext(), popularProductsModelList);
                    popularRecyclerview.setAdapter(popularProductsAdapter);

                    if (user.getUserType().equals("Admin")) {
                        db.collection("NewProducts")
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                NewProductsModel newProductsModel = document.toObject(NewProductsModel.class);
                                                newProductsModelList.add(newProductsModel);
                                                newProductsAdapter.notifyDataSetChanged();
                                            }
                                        } else {
                                            Toast.makeText(getActivity(), "" + task.getException(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                        db.collection("PopularProducts")
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                PopularProductsModel popularProductsModel = document.toObject(PopularProductsModel.class);
                                                popularProductsModelList.add(popularProductsModel);
                                                popularProductsAdapter.notifyDataSetChanged();
                                            }
                                        } else {
                                            Toast.makeText(getActivity(), "" + task.getException(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    } else {
                        db.collection("NewProducts").whereGreaterThan("stock", 0)
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                NewProductsModel newProductsModel = document.toObject(NewProductsModel.class);
                                                newProductsModelList.add(newProductsModel);
                                                newProductsAdapter.notifyDataSetChanged();
                                            }
                                        } else {
                                            Toast.makeText(getActivity(), "" + task.getException(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                        db.collection("PopularProducts").whereGreaterThan("stock", 0)
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                PopularProductsModel popularProductsModel = document.toObject(PopularProductsModel.class);
                                                popularProductsModelList.add(popularProductsModel);
                                                popularProductsAdapter.notifyDataSetChanged();
                                            }
                                        } else {
                                            Toast.makeText(getActivity(), "" + task.getException(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                }
            }
        });

        recyclerViewSearch = root.findViewById(R.id.search_rec);
        searchBox = root.findViewById(R.id.search_box);
        showAllModelList = new ArrayList<>();
        showAllAdapter = new ShowAllAdapter(getContext(), showAllModelList);
        recyclerViewSearch.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewSearch.setAdapter(showAllAdapter);
        recyclerViewSearch.setHasFixedSize(true);
        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().isEmpty()) {
                    showAllModelList.clear();
                    showAllAdapter.notifyDataSetChanged();
                } else {
                    searchProduct(s.toString());
                }
            }

            private void searchProduct(String name) {
                if (!name.isEmpty()) {
                    db.collection("ShowAll").whereEqualTo("name", name).get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful() && task.getResult() != null) {
                                        showAllModelList.clear();
                                        showAllAdapter.notifyDataSetChanged();

                                        for (DocumentSnapshot doc : task.getResult().getDocuments()) {
                                            ShowAllModel showAllModel = doc.toObject(ShowAllModel.class);
                                            showAllModelList.add(showAllModel);
                                            showAllAdapter.notifyDataSetChanged();
                                        }
                                    }
                                }
                            });
                }
            }


        });

        return root;
    }
}