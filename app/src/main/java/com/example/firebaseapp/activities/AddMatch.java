package com.example.firebaseapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.firebaseapp.R;
import com.example.firebaseapp.classes.Football;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Locale;

public class AddMatch extends AppCompatActivity {
    private EditText numEdtTxt, placeEdtTxt;
    private Button button, dateButton, timeButton;
    private DatePickerDialog datePickerDialog;
    int hour, minutes;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_match);
        numEdtTxt = findViewById(R.id.numEdtTxt);
        dateButton = findViewById(R.id.dateButton);
        dateButton.setText(getTodayDate());
        initDatePicker();
        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDatePicker();
            }
        });
        placeEdtTxt = findViewById(R.id.placeEdtTxt);
        timeButton = findViewById(R.id.timeButton);
        timeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener(){
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        hour = hourOfDay;
                        minutes = minute;
                        timeButton.setText(String.format(Locale.getDefault(), "%02d:%02d", hour, minutes));
                    }
                };
                TimePickerDialog timePickerDialog = new TimePickerDialog(v.getContext(), AlertDialog.THEME_HOLO_DARK, onTimeSetListener, hour, minutes, true);
                timePickerDialog.setTitle("Pick time");
                timePickerDialog.show();
            }
        });
        button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String num = numEdtTxt.getText().toString().trim();
                String place = placeEdtTxt.getText().toString().trim();
                String date = dateButton.getText().toString().trim();
                String time = timeButton.getText().toString().trim();
                Football match = new Football(date,time, place,num);
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Ids").child("FootballID");
                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot){
                        int id = Integer.parseInt(snapshot.getValue().toString());
                        id++;
                        if(match.teammates.get(0).equals(" ")){
                            match.teammates.set(0, (String)FirebaseAuth.getInstance().getUid());
                        }
                        String ids = String.valueOf(id);
                        reference.setValue(ids);
                        FirebaseDatabase.getInstance().getReference("Sports").child("Football").child(ids).setValue(match).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    startActivity(new Intent(v.getContext(), FootballPage.class));
                                    return;
                                }
                                else{
                                    Toast.makeText(v.getContext(), "Failed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        return;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });

    }

    private void openDatePicker() {
        datePickerDialog.show();
    }

    private void initDatePicker() {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month + 1;
                String date = makeDateString(dayOfMonth, month, year);
                dateButton.setText(date);
            }
        };
        Calendar cal = Calendar.getInstance();
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int month = cal.get(Calendar.MONTH);
        int year = cal.get(Calendar.YEAR);
        int style = AlertDialog.THEME_HOLO_DARK;
        datePickerDialog = new DatePickerDialog(this, style, dateSetListener, year, month, day);
    }

    private String getTodayDate() {
        Calendar cal = Calendar.getInstance();
        return cal.get(Calendar.DAY_OF_MONTH) + " " + (cal.get(Calendar.MONTH)+1) + " " + cal.get(Calendar.YEAR);
    }

    private String makeDateString(int day, int month, int year){
        return day + " " + month + " " + year;
    }
}