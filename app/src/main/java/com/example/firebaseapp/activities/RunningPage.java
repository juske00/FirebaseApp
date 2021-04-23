package com.example.firebaseapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.firebaseapp.R;
import com.example.firebaseapp.classes.Run;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class RunningPage extends AppCompatActivity {
    private Button addNewRun;
    private ArrayList<String> arrayList = new ArrayList<>();
    ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_running_page);
        listView = findViewById(R.id.listView);
        arrayList.clear();
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.item_list,  arrayList);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(view.getContext(), RunActivity.class);
                intent.putExtra("id", String.valueOf(position+1));
                startActivity(intent);


            }
        });
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Sports").child("Running");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrayList.clear();
                for(DataSnapshot snapshot1 : snapshot.getChildren()){
                    Run run = snapshot1.getValue(Run.class);
                    String text = run.date + " " + run.place;
                    arrayList.add(text);
                }
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Cannot connect to database", Toast.LENGTH_SHORT).show();
            }
        });
        addNewRun = findViewById(R.id.button);
        addNewRun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(v.getContext(), AddRun.class));
            }
        });
    }
}