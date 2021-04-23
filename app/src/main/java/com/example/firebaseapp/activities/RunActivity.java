package com.example.firebaseapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.firebaseapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class RunActivity extends AppCompatActivity {
    private TextView id, dateTxtView;
    private ImageButton chat;
    private ListView listview;
    private ArrayList<String> arrayList = new ArrayList<>();
    private Button participateButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run);
        chat = (ImageButton)findViewById(R.id.chatButton);
        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ChatActivity.class);
                intent.putExtra("id", String.valueOf(getIntent().getStringExtra("id")));
                intent.putExtra("activity", "Running");
                startActivity(intent);
            }
        });
        dateTxtView = findViewById(R.id.dateTxtView);
        participateButton = findViewById(R.id.button2);
        participateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase.getInstance().getReference("Sports").child("Running").child(getIntent().getStringExtra("id")).child("teammateID").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ArrayList<String> list = (ArrayList<String>)snapshot.getValue();
                        if(!list.get(0).equals(FirebaseAuth.getInstance().getUid().toString()) && list.size() < 2){
                            list.add(FirebaseAuth.getInstance().getUid());
                            FirebaseDatabase.getInstance().getReference("Sports").child("Running").child(getIntent().getStringExtra("id")).child("teammateID").setValue(list);
                        }
                        else{
                            Toast.makeText(v.getContext(), "You are already signed!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
        listview = findViewById(R.id.listView);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.item_list,  arrayList);
        listview.setAdapter(arrayAdapter);
        id = findViewById(R.id.matchID);
        id.setText(id.getText()+ " "+getIntent().getStringExtra("id"));
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Sports").child("Running").child(getIntent().getStringExtra("id"));
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                    arrayList.clear();
                    Log.d("TAG", "changed");
                    ArrayList<String> teammate = (ArrayList<String>)snapshot.child("teammateID").getValue();
                    dateTxtView.setText(snapshot.child("date").getValue().toString());
                    boolean found = false;
                    for(int i=0;i<teammate.size();i++){
                        if(teammate.get(i).equals(FirebaseAuth.getInstance().getUid())){
                            if(found==false)
                            {
                                found = true;
                            }
                        }
                        FirebaseDatabase.getInstance().getReference("Users").child(teammate.get(i)).child("userName").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                arrayList.add(snapshot.getValue(String.class));
                                arrayAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                    if(found==true){
                        chat.setClickable(true);
                        Log.d("TAG", "TRUE");
                    }
                    else{
                        chat.setClickable(false);
                        Log.d("TAG", "FALSE");
                    }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}