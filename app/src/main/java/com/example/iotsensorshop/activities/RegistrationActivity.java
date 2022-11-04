package com.example.iotsensorshop.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.iotsensorshop.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegistrationActivity extends AppCompatActivity {

    EditText name, email, password;
    Button mRegisterBtn;
    //Button mChangeUserTypeBtn;
    private FirebaseAuth auth;
    FirebaseFirestore fStore;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    String userId;
    int clickcount;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        //getSupportActionBar().hide();
        auth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        if (auth.getCurrentUser() != null){
            startActivity(new Intent(RegistrationActivity.this, MainActivity.class));
            finish();
        }

        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        //mChangeUserTypeBtn = findViewById(R.id.btn_changeUserType);
        mRegisterBtn = findViewById(R.id.button);

        clickcount = 0;

        /*
        sharedPreferences = getSharedPreferences("onBoardingScreen", MODE_PRIVATE);

        boolean isFirstTime = sharedPreferences.getBoolean("firstTime", true);

        if (isFirstTime){

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("firstTime", false);
            editor.commit();

            Intent intent = new Intent(RegistrationActivity.this, OnBoardingActivity.class);
            startActivity(intent);
            finish();
        }

         */


/*
        mChangeUserTypeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mRegisterBtn.getText().toString().equals("Signup")) {
                    mRegisterBtn.setText("Signup as admin");
                } else {
                    mRegisterBtn.setText("Signup");
                }
            }
        });

 */

    }

    public void changeUserType(View view) {
        clickcount += 1;

        if(clickcount == 3 || clickcount > 3){
            if (mRegisterBtn.getText().toString().equals("Signup")) {
                mRegisterBtn.setText("Signup as admin");
            }
        }else {
            mRegisterBtn.setText("Signup");
        }
    }

    public void signup(View view) {

        String userName = name.getText().toString();
        String userEmail = email.getText().toString();
        String userPassword = password.getText().toString();
        boolean emailValid = Patterns.EMAIL_ADDRESS.matcher(userEmail).matches();

        if(TextUtils.isEmpty(userName)){
            Toast.makeText(this, "Please enter name!", Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(userEmail)){
            Toast.makeText(this, "Please enter email address!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!userEmail.isEmpty() && !emailValid)
        {
            Toast.makeText(this, "Please enter valid email!", Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(userPassword)){
            Toast.makeText(this, "Please enter password!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isDigitsOnly(userPassword) || userPassword.matches("[a-zA-Z]+")) {
            Toast.makeText(this, "The password should contain digit and letter!", Toast.LENGTH_SHORT).show();
            return;
        }

        if(userPassword.length() < 6){
            Toast.makeText(this, "The password should be more than 5 characters!", Toast.LENGTH_SHORT).show();
            return;
        }

        auth.createUserWithEmailAndPassword(userEmail, userPassword)
                .addOnCompleteListener(RegistrationActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                       if (task.isSuccessful()){
                           /*
                           userId = auth.getCurrentUser().getUid();
                           DocumentReference documentReference = fStore.collection("User").document(userId);
                           Map<String, Object> user = new HashMap<>();
                           user.put("Name", name);
                           user.put("Email", email);
                           if (mRegisterBtn.getText().toString().equals("Signup")) {
                               user.put("userType", "User");
                           } else {
                               user.put("userType", "Admin");
                           }

                           documentReference.set(user);

                           databaseReference.child("users").child(auth.getCurrentUser().getUid()).child("email").setValue(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                               @Override
                               public void onComplete(@NonNull Task<Void> task) {
                                   if (task.isSuccessful()) {

                                   }
                               }
                           }).addOnFailureListener(new OnFailureListener() {
                               @Override
                               public void onFailure(@NonNull Exception e) {
                                   Toast.makeText(RegistrationActivity.this, "Failure in saving data : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                               }
                           });

                            */

                           Toast.makeText(RegistrationActivity.this, "Successfully Registered!", Toast.LENGTH_SHORT).show();
                           userId = auth.getCurrentUser().getUid();
                           DocumentReference documentReference = fStore.collection("users").document(userId);
                           Map<String, Object> user = new HashMap<>();
                           user.put("Name", userName);
                           user.put("Email", userEmail);
                           if (mRegisterBtn.getText().toString().equals("Sign Up")) {
                               user.put("userType", "User");
                               databaseReference.child("users").child(auth.getCurrentUser().getUid()).child("user").setValue("User").addOnCompleteListener(new OnCompleteListener<Void>() {
                                   @Override
                                   public void onComplete(@NonNull Task<Void> task) {
                                       if (task.isSuccessful()) {
                                       }
                                   }
                               }).addOnFailureListener(new OnFailureListener() {
                                   @Override
                                   public void onFailure(@NonNull Exception e) {
                                       Toast.makeText(RegistrationActivity.this, "Failure in saving data : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                   }
                               });
                           } else {
                               user.put("userType", "Admin");
                               databaseReference.child("users").child(auth.getCurrentUser().getUid()).child("user").setValue("Admin").addOnCompleteListener(new OnCompleteListener<Void>() {
                                   @Override
                                   public void onComplete(@NonNull Task<Void> task) {
                                       if (task.isSuccessful()) {
                                       }
                                   }
                               }).addOnFailureListener(new OnFailureListener() {
                                   @Override
                                   public void onFailure(@NonNull Exception e) {
                                       Toast.makeText(RegistrationActivity.this, "Failure in saving data : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                   }
                               });
                           }
                           documentReference.set(user);

                           databaseReference.child("users").child(auth.getCurrentUser().getUid()).child("email").setValue(userEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                               @Override
                               public void onComplete(@NonNull Task<Void> task) {
                                   if (task.isSuccessful()) {
                                   }
                               }
                           }).addOnFailureListener(new OnFailureListener() {
                               @Override
                               public void onFailure(@NonNull Exception e) {
                                   Toast.makeText(RegistrationActivity.this, "Failure in saving data : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                               }
                           });

                           startActivity(new Intent(RegistrationActivity.this, MainActivity.class));

                       }else {

                           if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                               Toast.makeText(RegistrationActivity.this, "The account is already existed", Toast.LENGTH_SHORT).show();
                           }


                           Toast.makeText(RegistrationActivity.this, "Registration Failed!" + task.getException(), Toast.LENGTH_SHORT).show();
                       }
                    }
                });

        //startActivity(new Intent(RegistrationActivity.this, MainActivity.class));
    }

    public void signin(View view) {
        startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
    }
}