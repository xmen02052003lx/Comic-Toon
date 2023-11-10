package com.example.btvn1;

public class User {
    private String userid;
    private String username;
    private String password;
    private String displayName;
    private String image;

    public User() {
        // Required empty public constructor
    }

    public User(String userid, String username, String password, String displayName, String image) {
        this.userid = userid;
        this.username = username;
        this.password = password;
        this.displayName = displayName;
        this.image = image;
    }
    public String getUserId() {
        return userid;
    }

    public void setUserId(String userid) {
        this.userid = userid;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
