package com.example.btvn1;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import java.util.ArrayList;
import java.util.List;

public class DeleteStoryFragment extends Fragment {

    private Spinner spinnerDeleteStory;
    private Button buttonDeleteStory;
    private String username;
    private UserData userData;
    private ArticleData articleData;
    private List<Article> articles;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_delete_story_fragment, container, false);

        // Get the username from SharedPreferences
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        username = sharedPreferences.getString("username", "");

        spinnerDeleteStory = view.findViewById(R.id.spinnerDeleteStory);
        buttonDeleteStory = view.findViewById(R.id.buttonDeleteStory);

        // Initialize UserData and ArticleData instances
        userData = new UserData(requireContext());
        articleData = new ArticleData(requireContext());

        // Fetch user's display name using the username
        userData.getUserByUsername(username, new UserData.DataRetrieveListener() {
            @Override
            public void onDataRetrieved(User user) {
                if (user != null) {
                    String displayName = user.getDisplayName();

                    // Fetch articles by the user's display name
                    fetchArticlesByAuthor(displayName);
                } else {
                    // Handle when the user is not found
                }
            }

            @Override
            public void onDataNotExists() {
                // Handle when the user is not found
            }

            @Override
            public void onDataRetrieveError(String errorMessage) {
                // Handle data retrieval error
            }
        });

        // Set a listener for the Spinner item selection
        spinnerDeleteStory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Handle Spinner item selection, if needed
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle when nothing is selected, if needed
            }
        });

        // Set a listener for the button to delete the story
        buttonDeleteStory.setOnClickListener(view1 -> confirmDeleteArticle());

        return view;
    }

    private void fetchArticlesByAuthor(String author) {
        // Fetch articles by the given author's display name
        articles = new ArrayList<>();
        articleData.fetchDataAndListenForChanges(articleList -> {
            for (Article article : articleList) {
                if (article.getArticle_author().equals(author)) {
                    articles.add(article);
                }
            }
            // Create a list of article titles from the articles list
            List<String> articleTitles = new ArrayList<>();
            for (Article article : articles) {
                articleTitles.add(article.getArticle_title());
            }
            // Populate the Spinner with the article titles
            populateSpinner(articleTitles);
        });
    }

    private void populateSpinner(List<String> articleTitles) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, articleTitles);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDeleteStory.setAdapter(adapter);
    }

    private void confirmDeleteArticle() {
        // Get the selected article title from the Spinner
        String selectedTitle = (String) spinnerDeleteStory.getSelectedItem();

        if (selectedTitle != null) {
            // Show a confirmation dialog to confirm the deletion
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setTitle("Xác nhận xóa bài viết");
            builder.setMessage("Bạn có chắc chắn muốn xóa bài viết này?");
            builder.setPositiveButton("Có", (dialog, which) -> deleteArticle());
            builder.setNegativeButton("Không", (dialog, which) -> {
                // Cancel the deletion, do nothing
            });
            builder.show();
        }
    }

    private void deleteArticle() {
        // Get the selected article from the Spinner
        final Article selectedArticle = findArticleByTitle((String) spinnerDeleteStory.getSelectedItem());

        if (selectedArticle != null) {
            // Remove the article from the articles list
            articles.remove(selectedArticle);

            // Delete the article from Firebase Realtime Database using the article ID
            articleData.deleteArticle(selectedArticle.getArticle_id(), new ArticleData.DeleteArticleListener() {
                @Override
                public void onArticleDeleted() {
                    // Article deletion is successful
                    // Provide feedback to the user or navigate to another screen if needed
                    Toast.makeText(requireContext(), "Xóa Câu Chuyện Thành Công", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(requireContext(), MainLayout.class);
                    startActivity(intent);
                }

                @Override
                public void onArticleDeleteError(String errorMessage) {
                    // Handle any error during the article deletion
                    Toast.makeText(requireContext(), "Lỗi Xóa Câu Chuyện", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private Article findArticleByTitle(String title) {
        for (Article article : articles) {
            if (article.getArticle_title().equals(title)) {
                return article;
            }
        }
        return null; // Return null if no article is found with the selected title
    }
}
