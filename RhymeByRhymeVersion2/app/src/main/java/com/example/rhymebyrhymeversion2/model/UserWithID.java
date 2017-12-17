package com.example.rhymebyrhymeversion2.model;

/**
 * Created by Amir on 17.12.2017.
 */

public class UserWithID {
    private User user;
    private String userID;

    public UserWithID() {
    }

    public UserWithID(User user, String userID) {
        this.user = user;
        this.userID = userID;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}
