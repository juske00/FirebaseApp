package com.example.firebaseapp.activities;
import com.example.firebaseapp.R;
import com.example.firebaseapp.classes.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private TextView appNameTextView, wwyTextView;
    private Button loginButton, registerButton;
    private Animation moveRightAnimation, moveLeftAnimation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            startActivity(new Intent(getApplicationContext(), UserPage.class));
        }

        appNameTextView = findViewById(R.id.appNameTxtView);
        wwyTextView = findViewById(R.id.wwyTxtView);
        loginButton = findViewById(R.id.loginButton);
        registerButton = findViewById(R.id.registerButton);

        moveRightAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.move_right_animation);
        moveLeftAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.move_left_animation);

        appNameTextView.startAnimation(moveRightAnimation);
        wwyTextView.startAnimation(moveLeftAnimation);
        loginButton.startAnimation(moveRightAnimation);
        registerButton.startAnimation(moveLeftAnimation);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
            }
        });


    }
}