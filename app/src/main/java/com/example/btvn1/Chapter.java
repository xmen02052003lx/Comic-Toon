package com.example.btvn1;
public class Chapter {
    private String chapterId;
    private String articleId;
    private String chapterName;
    private String chapterStory;

    public Chapter() {
        // Default constructor required for Firebase
    }

    public Chapter(String chapterId, String articleId, String chapterName, String chapterStory) {
        this.chapterId = chapterId;
        this.articleId = articleId;
        this.chapterName = chapterName;
        this.chapterStory = chapterStory;
    }

    public String getChapterId() {
        return chapterId;
    }

    public void setChapterId(String chapterId) {
        this.chapterId = chapterId;
    }

    public String getArticleId() {
        return articleId;
    }

    public void setArticleId(String articleId) {
        this.articleId = articleId;
    }

    public String getChapterName() {
        return chapterName;
    }

    public void setChapterName(String chapterName) {
        this.chapterName = chapterName;
    }

    public String getChapterStory() {
        return chapterStory;
    }

    public void setChapterStory(String chapterStory) {
        this.chapterStory = chapterStory;
    }
}
