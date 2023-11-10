package com.example.btvn1;

import java.util.ArrayList;

public class ArticleListConverter {
    public static ArticleList convertFromArrayList(ArrayList<Article> articles) {
        return new ArticleList(articles);
    }
}


