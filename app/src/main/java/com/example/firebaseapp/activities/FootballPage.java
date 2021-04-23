package com.example.firebaseapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.firebaseapp.R;
import com.example.firebaseapp.classes.Football;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FootballPage extends AppCompatActivity {
    private Button addNewMatch;
    private ArrayList<String> arrayList = new ArrayList<>();
    ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_football_page);
        listView = findViewById(R.id.listView);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.item_list,  arrayList);
        listView.setAdapter(arrayAdapter);
        arrayList.clear();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(view.getContext(), FootballActivity.class);
                intent.putExtra("id", String.valueOf(position+1));
                startActivity(intent);
            }
        });
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Sports").child("Football");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrayList.clear();
                for(DataSnapshot snapshot1 : snapshot.getChildren()){
                    Football match = snapshot1.getValue(Football.class);
                    String text = match.date + " " + match.time + " "+ match.place + " " + String.valueOf(Integer.parseInt(match.numMates) - match.teammates.size() + 1) + " people required";
                    arrayList.add(text);
                }
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        addNewMatch = findViewById(R.id.button);
        addNewMatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(v.getContext(), AddMatch.class));
            }
        });
    }
}