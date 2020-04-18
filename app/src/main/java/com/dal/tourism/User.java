package com.dal.tourism;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class User {

    public String name;
    public String mobile_number;
    public String email;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String name, String mobile_number, String email) {
        this.name = name;
        this.mobile_number = mobile_number;
        this.email = email;
    }

}