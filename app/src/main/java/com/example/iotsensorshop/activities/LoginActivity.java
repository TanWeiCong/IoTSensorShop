package com.example.iotsensorshop.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.iotsensorshop.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class LoginActivity extends AppCompatActivity {

    EditText email, password;
    //TextView tv_forgotPassword;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //getSupportActionBar().hide();

        auth = FirebaseAuth.getInstance();

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        //tv_forgotPassword = findViewById(R.id.tv_forgotPassword);

        /*
        tv_forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText mResetEmail = new EditText(v.getContext());
                AlertDialog alertDialog = new AlertDialog.Builder(v.getContext())
                        .setTitle("Reset Password")
                        .setMessage("Enter your email to receive reset password link")
                        .setView(mResetEmail)
                        .setPositiveButton("Send Email", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                String resetEmail = mResetEmail.getText().toString().trim();
                                boolean emailValid = Patterns.EMAIL_ADDRESS.matcher(resetEmail).matches();
                                boolean valid = true;

                                if (resetEmail.isEmpty())
                                {
                                    Toast.makeText(LoginActivity.this,"Please fill in your email!", Toast.LENGTH_SHORT).show();
                                    valid = false;
                                }

                                if (!resetEmail.isEmpty() && !emailValid)
                                {
                                    Toast.makeText(LoginActivity.this,"This is not a valid email!", Toast.LENGTH_SHORT).show();
                                    valid = false;
                                }

                                if (valid)
                                {
                                    auth.sendPasswordResetEmail(resetEmail).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(LoginActivity.this, "Reset link is sent to your email!", Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(LoginActivity.this, "The account does not exist!", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }

                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).show();
            }
        });

         */
    }

    public void signIn(View view) {

        String userEmail = email.getText().toString();
        String userPassword = password.getText().toString();
        boolean emailValid = Patterns.EMAIL_ADDRESS.matcher(userEmail).matches();

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

        auth.signInWithEmailAndPassword(userEmail, userPassword)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Login Successfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));

                        }else {
                            if (task.getException() instanceof FirebaseAuthInvalidUserException)
                            {
                                Toast.makeText(LoginActivity.this, "User account does not exist", Toast.LENGTH_SHORT).show();
                            }

                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException)
                            {
                                Toast.makeText(LoginActivity.this, "Incorrect password", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

    public void signUp(View view) {
        startActivity(new Intent(LoginActivity.this, RegistrationActivity.class));
    }

    public void forgotPasswword(View view) {
        EditText mResetEmail = new EditText(view.getContext());
        AlertDialog alertDialog = new AlertDialog.Builder(view.getContext())
                .setTitle("Reset Password")
                .setMessage("Enter your email to receive reset password link")
                .setView(mResetEmail)
                .setPositiveButton("Send Email", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String resetEmail = mResetEmail.getText().toString().trim();
                        boolean emailValid = Patterns.EMAIL_ADDRESS.matcher(resetEmail).matches();
                        boolean valid = true;

                        if (resetEmail.isEmpty())
                        {
                            Toast.makeText(LoginActivity.this,"Please fill in your email!", Toast.LENGTH_SHORT).show();
                            valid = false;
                        }

                        if (!resetEmail.isEmpty() && !emailValid)
                        {
                            Toast.makeText(LoginActivity.this,"This is not a valid email!", Toast.LENGTH_SHORT).show();
                            valid = false;
                        }

                        if (valid)
                        {
                            auth.sendPasswordResetEmail(resetEmail).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(LoginActivity.this, "Reset link is sent to your email!", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(LoginActivity.this, "The account does not exist!", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
    }
}