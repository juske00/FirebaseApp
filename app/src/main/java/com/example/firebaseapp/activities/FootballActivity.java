package com.example.firebaseapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.firebaseapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FootballActivity extends AppCompatActivity {
    private TextView id, dateTxtView;
    private ImageButton chat;
    private ListView listview;
    private ArrayList<String> arrayList = new ArrayList<>();
    private ArrayList<String> uidList = new ArrayList<>();
    private Button participateButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_football);
        chat = (ImageButton)findViewById(R.id.chatButton);
        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ChatActivity.class);
                intent.putExtra("id", String.valueOf(getIntent().getStringExtra("id")));
                intent.putExtra("activity", "Football");
                startActivity(intent);
            }
        });
        dateTxtView = findViewById(R.id.dateTxtView);
        participateButton = findViewById(R.id.button2);
        participateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference database = FirebaseDatabase.getInstance().getReference("Sports").child("Football").child(getIntent().getStringExtra("id"));
                database.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ArrayList<String> list = (ArrayList<String>)snapshot.child("teammates").getValue();
                        boolean found = false;
                        for(int i=0; i<list.size(); i++){
                            if(list.get(i).equals(FirebaseAuth.getInstance().getUid())){
                                found = true;
                                break;
                            }
                        }
                        if(!found && list.size() < Integer.parseInt(snapshot.child("numMates").getValue().toString())){
                            list.add(FirebaseAuth.getInstance().getUid());
                            FirebaseDatabase.getInstance().getReference("Sports").child("Football").child(getIntent().getStringExtra("id")).child("teammates").setValue(list);
                        }
                        else if (found){
                            Toast.makeText(v.getContext(), "You are already signed!", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(v.getContext(), "Too many people have already signed in!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        listview = findViewById(R.id.listView);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.item_list,  arrayList);
        listview.setAdapter(arrayAdapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(view.getContext(), ProfileActivity.class);
                intent.putExtra("id", uidList.get(position));
                startActivity(intent);
            }
        });
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        id = findViewById(R.id.matchID);
        id.setText(id.getText()+ " "+getIntent().getStringExtra("id"));
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Sports").child("Football").child(getIntent().getStringExtra("id"));
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrayList.clear();
                uidList.clear();
                ArrayList<String> teammate = (ArrayList<String>)snapshot.child("teammates").getValue();
                dateTxtView.setText(snapshot.child("date").getValue().toString());
                boolean found = false;
                for(int i=0;i<teammate.size();i++){
                    if(teammate.get(i).equals(FirebaseAuth.getInstance().getUid())){
                        if(found==false)
                        {
                            found = true;
                        }
                    }
                    FirebaseDatabase.getInstance().getReference("Users").child(teammate.get(i)).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            arrayList.add(snapshot.child("userName").getValue(String.class));
                            uidList.add(snapshot.getKey().toString());
                            arrayAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}