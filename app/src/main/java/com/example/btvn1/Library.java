package com.example.btvn1;

public class Library {
    private String article_id;
    private String userid;
    private String date;

    public Library() {
        // Default constructor required for Firebase
    }

    public Library(String article_id, String userid, String date) {
        this.article_id = article_id;
        this.userid = userid;
        this.date = date;
    }

    public String getArticle_id() {
        return article_id;
    }

    public String getUserid() {
        return userid;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}