package com.example.btvn1;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Article {
    @SerializedName("article_id")
    @Expose
    private long article_id;

    @SerializedName("article_title")
    @Expose
    private String article_title;

    @SerializedName("article_image")
    @Expose
    private String article_image;

    @SerializedName("article_description")
    @Expose
    private String article_description;

    @SerializedName("article_category")
    @Expose
    private String article_category;

    @SerializedName("article_views")
    @Expose
    private long article_views;
    @SerializedName("article_author")
    @Expose
    private String article_author;


    // Constructor có tham số
    public Article(long article_id,  String article_image,String article_title, String article_description, String article_category, long article_views, String article_author) {
        this.article_id = article_id;
        this.article_image = article_image;
        this.article_title = article_title;
        this.article_description = article_description;
        this.article_category = article_category;
        this.article_views = article_views;
        this.article_author = article_author;
    }

    // Constructor mặc định (không có tham số)
    public Article() {
        // Để đảm bảo rằng Firebase có thể tạo một đối tượng Article mà không cần thông tin cụ thể.
    }

    public long getArticle_id() {
        return article_id;
    }

    public void setArticle_id(long article_id) {
        this.article_id = article_id;
    }


    public String getArticle_image() {
        return article_image;
    }

    public void setArticle_image(String article_image) {
        this.article_image = article_image;
    }

    public String getArticle_title() {
        return article_title;
    }

    public void setArticle_title(String article_title) {
        this.article_title = article_title;
    }

    public String getArticle_description() {
        return article_description;
    }

    public void setArticle_description(String article_description) {
        this.article_description = article_description;
    }

    public String getArticle_category() {
        return article_category;
    }

    public void setArticle_category(String article_category) {
        this.article_category = article_category;
    }

    public long getArticle_views() {
        return article_views;
    }

    public void setArticle_views(long article_views) {
        this.article_views = article_views;
    }

    public String getArticle_author() {
        return article_author;
    }

    public void setArticle_author(String article_author) {
        this.article_author = article_author;
    }
}
