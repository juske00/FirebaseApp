package com.example.firebaseapp.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.firebaseapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class ProfileActivity extends AppCompatActivity {
    private ImageView profilePic;
    public Uri imageUri;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private Button changeProfilePic, signOutButton;
    private BottomNavigationView bottomNavigationView;
    private TextView ageTxtView;
    private TextView nameTxtView;
    private TextView profileTxtView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ageTxtView = findViewById(R.id.ageTxtView);
        nameTxtView = findViewById(R.id.nameTxtView);
        profileTxtView = findViewById(R.id.profileTxtView);
        signOutButton = findViewById(R.id.signOutButton);
        profilePic = findViewById(R.id.profilePic);
        changeProfilePic = findViewById(R.id.changePic);
        String uid;
        if(getIntent().getExtras() != null){
            uid = getIntent().getStringExtra("id");
            if(!uid.equals(FirebaseAuth.getInstance().getUid())){
                changeProfilePic.setVisibility(View.INVISIBLE);
                signOutButton.setVisibility(View.INVISIBLE);
            }
        }
        else{
            uid = FirebaseAuth.getInstance().getUid();
        }
        bottomNavigationView = (BottomNavigationView)findViewById(R.id.bottom_nav);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.nav_home:
                        Intent intent = new Intent(getApplicationContext(),UserPage.class);
                        startActivity(intent);
                        break;
                    default: break;
                }
                return true;
            }
        });
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        storageReference.child("photos/" + uid +"/pic").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(getApplicationContext()).load(uri).into(profilePic);
            }

        });
        changeProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choosePicture();
            }
        });
        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float scale =  Resources.getSystem().getDisplayMetrics().widthPixels / profilePic.getWidth();
                Log.d("SCALE", String.valueOf(scale));
                Log.d("v.width", String.valueOf(profilePic.getWidth()));
                if(v.getScaleX() == 1) {
                    v.setScaleY(2);
                    v.setScaleX(2);
                }else{
                    v.setScaleY(1);
                    v.setScaleX(1);
                }
            }
        });
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.child("Users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                nameTxtView.setText(nameTxtView.getText().toString() + snapshot.child("name").getValue().toString().trim());
                ageTxtView.setText(ageTxtView.getText().toString() + snapshot.child("age").getValue().toString().trim());
                profileTxtView.setText(snapshot.child("userName").getValue().toString().trim());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void choosePicture(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null){
            imageUri = data.getData();
            profilePic.setImageURI(imageUri);
            uploadPicture();
        }
    }

    private void uploadPicture() {
        StorageReference ref = storageReference.child("photos/" + FirebaseAuth.getInstance().getUid()+"/pic");
        ref.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(getApplicationContext(), "Uploaded", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Couldn't upload photo", Toast.LENGTH_SHORT).show();
            }
        });
    }
}