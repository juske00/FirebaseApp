package com.example.firebaseapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.firebaseapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {
    private final DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    private EditText passwordEdtTxt, emailEdtTxt;
    private TextView registerTxtView;
    private FirebaseAuth firebaseAuth;
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initializeLayoutElements();

        registerTxtView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(v.getContext(), RegisterActivity.class));
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEdtTxt.getText().toString().trim();
                String password = passwordEdtTxt.getText().toString().trim();
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
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    emailEdtTxt.setError("Provide valid email!");
                    emailEdtTxt.requestFocus();
                    return;
                }
                firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            if (FirebaseAuth.getInstance().getCurrentUser().isEmailVerified())
                                startActivity(new Intent(v.getContext(), UserPage.class));
                            else
                                Toast.makeText(v.getContext(), "Firstly, verify your email!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(v.getContext(), "Wrong login input!", Toast.LENGTH_SHORT).show();
                            emailEdtTxt.requestFocus();
                            return;
                        }
                    }
                });
            }
        });
    }

    private void initializeLayoutElements() {
        passwordEdtTxt = findViewById(R.id.passwordEdtTxt);
        emailEdtTxt = findViewById(R.id.emailEdtTxt);
        firebaseAuth = FirebaseAuth.getInstance();
        registerTxtView = findViewById(R.id.registerTxtView);
        loginButton = findViewById(R.id.button);
    }

}