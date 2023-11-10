package com.example.btvn1;

public class Comment {
    private String commentId;
    private String username;
    private String commentText;
    private String articleImage;
    private String articleTitle;

    public Comment() {
        // Default constructor required for Firebase
    }

    public Comment(String commentId, String username, String commentText, String articleImage, String articleTitle) {
        this.commentId = commentId;
        this.username = username;
        this.commentText = commentText;
        this.articleImage = articleImage;
        this.articleTitle = articleTitle;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getUserName() {
        return username;
    }

    public void setUserName(String username) {
        this.username = username;
    }

    public String getCommentText() {
        return commentText;
    }
    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    public void setArticleImage(String articleImage) {
        this.articleImage = articleImage;
    }

    public String getArticleImage() {
        return articleImage;
    }

    public void setArticleTitle(String articleTitle) {
        this.articleTitle = articleTitle;
    }

    public String getArticleTitle() {
        return articleTitle;
    }
}


