package com.example.iotsensorshop.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.iotsensorshop.R;
import com.example.iotsensorshop.models.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;
import java.util.Map;

public class EditProfileFragment extends Fragment {

    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    FirebaseUser fUser = fAuth.getCurrentUser();

    String name;
    String email;
    String password;
    String newPassword;
    String userType;
    String userId = fUser.getUid();
    DocumentReference documentReference = fStore.collection("User").document(userId);
    Bundle args = new Bundle();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        EditText et_name = view.findViewById(R.id.name_profile);
        EditText et_password = view.findViewById(R.id.password_profile);
        EditText et_new_password = view.findViewById(R.id.new_password_profile);
        Button mUpdateProfileBtn = view.findViewById(R.id.update_profile);

        mUpdateProfileBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                name = et_name.getText().toString();
                password = et_password.getText().toString();
                newPassword = et_new_password.getText().toString();

                if(TextUtils.isEmpty(name)){
                    Toast.makeText(getActivity(), "Please enter name!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(TextUtils.isEmpty(password)){
                    Toast.makeText(getActivity(), "Please enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isDigitsOnly(password) || password.matches("[a-zA-Z]+")) {
                    Toast.makeText(getActivity(), "The password should contain digit and letter!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(password.length() < 6){
                    Toast.makeText(getActivity(), "The password should be more than 5 characters!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(TextUtils.isEmpty(newPassword)){
                    Toast.makeText(getActivity(), "Please enter new password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isDigitsOnly(newPassword) || newPassword.matches("[a-zA-Z]+")) {
                    Toast.makeText(getActivity(), "The new password should contain digit and letter!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (newPassword.length() < 6){
                    Toast.makeText(getActivity(), "The new password should be more than 5 characters!", Toast.LENGTH_SHORT).show();
                    return;
                }

                documentReference = fStore.collection("users").document(userId);

                Map<String, Object> userName = new HashMap<>();
                userName.put("Name", name);
                documentReference.update(userName);

                AuthCredential authCredential = EmailAuthProvider.getCredential(fUser.getEmail(), password);
                fUser.reauthenticate(authCredential)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                fUser.updatePassword(newPassword)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.home_container, new ProfileFragment()).commit();
                                                Toast.makeText(getActivity(), "Password Updated...", Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(getActivity(), "Failed to update password...", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getActivity(), "Failed to update password...", Toast.LENGTH_SHORT).show();
                            }
                        });

                /*
                fUser.updatePassword(newPassword).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.home_container, new ProfileFragment()).commit();
                        Toast.makeText(getActivity(), "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "Failed to update the profile...", Toast.LENGTH_SHORT).show();
                    }
                });

                 */

            }
        });

        return view;
    }
}