package com.example.iotsensorshop.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import com.example.iotsensorshop.R;
import com.example.iotsensorshop.models.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class ProfileFragment extends Fragment {

    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    FirebaseUser fUser = fAuth.getCurrentUser();

    String name;
    String email;
    String userType;
    String userId = fUser.getUid();
    DocumentReference documentReference = fStore.collection("users").document(userId);
    Bundle args = new Bundle();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        TextView tv_userName = view.findViewById(R.id.tv_userName);
        TextView tv_email = view.findViewById(R.id.tv_email);
        Button mEditProfileBtn = view.findViewById(R.id.edit_profile);

        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot != null) {
                        name = documentSnapshot.getString("Name");
                        email = documentSnapshot.getString("Email");
                        UserModel user = new UserModel(userId, name, email, userType);

                        tv_userName.setText(user.getName());
                        tv_email.setText(user.getEmail());
                    }
                }
            }
        });

        mEditProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.home_container, new EditProfileFragment()).commit();
            }
        });

        return view;
    }
}
