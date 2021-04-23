package com.example.firebaseapp.classes;

import com.google.firebase.auth.FirebaseAuth;

public class User {
    public String userName, email, name;
    public int age;

    public User() {

    }

    public User(String userName, String email, String name, int age) {
        this.userName = userName;
        this.email = email;
        this.name = name;
        this.age = age;
    }


    public String getUserName() {
        return userName;
    }
}
