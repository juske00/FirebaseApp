package com.example.firebaseapp.classes;

public class ChatMessage {
    public String user;
    public String date;
    public String time;
    public String message;

    public ChatMessage(String user, String date, String time, String message){
        this.user = user;
        this.date = date;
        this.time = time;
        this.message = message;
    }

    @Override
    public String toString() {
        return this.date + " " + this.time + " " + this.user + " " + this.message;
    }
}
