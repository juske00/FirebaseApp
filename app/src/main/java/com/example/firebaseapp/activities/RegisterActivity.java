package com.example.firebaseapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.firebaseapp.R;
import com.example.firebaseapp.classes.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.FirebaseDatabase;


public class RegisterActivity extends AppCompatActivity {

    private EditText passwordEdtTxt, emailEdtTxt, userNameEdtTxt, ageEdtTxt, nameEdtTxt;
    private TextView loginTxtView;
    private FirebaseAuth firebaseAuth;
    private Button registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        loginTxtView = findViewById(R.id.loginTxtView);
        loginTxtView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(v.getContext(), LoginActivity.class));
            }
        });
        passwordEdtTxt = findViewById(R.id.passwordEdtTxt);
        emailEdtTxt = findViewById(R.id.emailEdtTxt);
        userNameEdtTxt = findViewById(R.id.userNameEdtTxt);
        nameEdtTxt = findViewById(R.id.nameEdtTxt);
        ageEdtTxt = findViewById(R.id.ageEdtTxt);
        firebaseAuth = FirebaseAuth.getInstance();
        registerButton = findViewById(R.id.button);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = userNameEdtTxt.getText().toString().trim();
                String password = passwordEdtTxt.getText().toString().trim();
                String email = emailEdtTxt.getText().toString().trim();
                String name = nameEdtTxt.getText().toString().trim();
                String age = ageEdtTxt.getText().toString().trim();
                Log.d("username", username);
                Log.d("password", password);
                Log.d("emailedt", email);
                Log.d("age", age);
                Log.d("name", name);
                if (username.isEmpty()) {
                    userNameEdtTxt.setError("Cannot be empty!");
                    userNameEdtTxt.requestFocus();
                    return;
                }
                if (email.isEmpty()) {
                    emailEdtTxt.setError("Cannot be empty!");
                    emailEdtTxt.requestFocus();
                    return;
                }
                if (password.isEmpty()) {
                    passwordEdtTxt.setError("Cannot be empty!");
                    passwordEdtTxt.requestFocus();
                    return;
                }
                if(name.isEmpty()){
                    nameEdtTxt.setError("Cannot be empty!");
                    nameEdtTxt.requestFocus();
                    return;
                }
                if(age.isEmpty()){
                    ageEdtTxt.setError("Cannot be empty!");
                    ageEdtTxt.requestFocus();
                    return;
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    emailEdtTxt.setError("Provide valid email!");
                    emailEdtTxt.requestFocus();
                    return;
                }
                firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            User user = new User(username, email, name, Integer.parseInt(age));
                            Log.d("USER", user.toString());
                            FirebaseAuth.getInstance().getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                UserProfileChangeRequest userprof = new UserProfileChangeRequest.Builder().setDisplayName(username).build();
                                                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                                            } else {
                                                Toast.makeText(RegisterActivity.this, "Verification email couldn't be sent!", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }

                                public void onFailure(@NonNull Task<Void> task) {
                                    Toast.makeText(RegisterActivity.this, "Verification email couldn't be sent!", Toast.LENGTH_SHORT).show();
                                }
                            });


                        } else {
                            Toast.makeText(RegisterActivity.this, "Failed to register!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}