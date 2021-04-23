package com.example.firebaseapp.classes;

import java.util.ArrayList;
import java.util.List;

public class Football {
    public String date;
    public String place;
    public String numMates;
    public String time;
    public List<String> teammates;
    public ArrayList<String> chat;
    public Football(){

    };
    public Football(String date, String time, String place, String numMates){
        this.date = date;
        this.place = place;
        this.time = time;
        this.numMates = numMates;
        this.teammates = new ArrayList<>();
        teammates.add(" ");

        this.chat = new ArrayList<>();
        chat.add(" ");
    }

}
