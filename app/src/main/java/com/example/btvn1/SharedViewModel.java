package com.example.btvn1;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class SharedViewModel extends ViewModel {
    private MutableLiveData<List<Long>> selectedArticleIds = new MutableLiveData<>(new ArrayList<>());


    public LiveData<List<Long>> getSelectedArticles() {
        return selectedArticleIds;
    }
    
    public void selectArticle(long articleId) {
        List<Long> selectedIds = selectedArticleIds.getValue();
        if (selectedIds == null) {
            selectedIds = new ArrayList<>();
        }
        selectedIds.add(articleId);
        selectedArticleIds.setValue(selectedIds);
    }

    public void deselectArticle(long articleId) {
        List<Long> selectedIds = selectedArticleIds.getValue();
        if (selectedIds != null) {
            selectedIds.remove(articleId);
            selectedArticleIds.setValue(selectedIds);
        }

    }

    public void resetData() {
        selectedArticleIds.setValue(new ArrayList<>());
    }
}




