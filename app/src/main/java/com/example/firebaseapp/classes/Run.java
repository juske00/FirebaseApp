package com.example.firebaseapp.classes;

import java.util.ArrayList;
import java.util.List;

public class Run {
    public String date;
    public String place;
    public ArrayList<String> teammateID;
    public ArrayList<String> chat;

    public Run(){

    };
    public Run(String date, String place, String teammateID){
        this.date = date;
        this.place = place;
        this.teammateID = new ArrayList<String>();
        this.chat = new ArrayList<String>();
        this.chat.add(" ");
        this.teammateID.add(teammateID);
    }
}
