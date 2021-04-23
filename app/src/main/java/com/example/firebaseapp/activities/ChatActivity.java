package com.example.firebaseapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Debug;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.firebaseapp.R;
import com.example.firebaseapp.classes.ChatMessage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

public class ChatActivity extends AppCompatActivity {
    private ListView listview;
    private ArrayList<String> arrayList = new ArrayList<>();
    private Button sendButton;
    private EditText message;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        listview = findViewById(R.id.chatListView);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.item_list,  arrayList);
        listview.setAdapter(arrayAdapter);
        message = findViewById(R.id.messageEdtTxt);
        sendButton = findViewById(R.id.button);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageString = message.getText().toString();
                String date = getTodayDate();
                String time = getTodayTime();
                if(!messageString.equals("")){
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Sports").child(getIntent().getStringExtra("activity")).child(getIntent().getStringExtra("id")).child("chat");
                    ref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            ArrayList<String> chat = (ArrayList<String>)snapshot.getValue();
                            FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getUid()).child("userName").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    String user = snapshot.getValue(String.class);
                                    ChatMessage chatMessage = new ChatMessage(user, date, time, messageString);
                                    if(chat.get(0).equals(" ")){
                                        chat.set(0, chatMessage.toString());
                                        ref.setValue(chat);
                                        message.setText("");
                                    }
                                    else{
                                        chat.add(chatMessage.toString());
                                        ref.setValue(chat);
                                        message.setText("");
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
                else{
                    Toast.makeText(v.getContext(), "You can't send nothing!", Toast.LENGTH_SHORT).show();
                }


            }
        });
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Sports").child(getIntent().getStringExtra("activity")).child(getIntent().getStringExtra("id")).child("chat");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrayList.clear();
                ArrayList<String> chat = (ArrayList<String>)snapshot.getValue();
                for(int i=chat.size()-1;i>=0;i--){
                    arrayList.add(chat.get(i));
                }
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    private String getTodayDate() {
        Calendar cal = Calendar.getInstance();
        return cal.get(Calendar.DAY_OF_MONTH) + "." + (cal.get(Calendar.MONTH)+1) + "." + cal.get(Calendar.YEAR);
    }
    private String getTodayTime() {
        Calendar cal = Calendar.getInstance();
        return cal.get(Calendar.HOUR_OF_DAY) + ":" + cal.get(Calendar.MINUTE);
    }
}