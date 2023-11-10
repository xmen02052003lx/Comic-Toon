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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class DeleteChapterFragment extends Fragment {

    private Spinner spinnerArticle;
    private Spinner spinnerChapter;
    private Button buttonDeleteChapter;
    private String username;
    private UserData userData;
    private ArticleData articleData;
    private ChapterData chapterData;
    private List<Article> articles;
    private List<Chapter> chapters;
    private String selectedChapterId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_delete_chapter_fragment, container, false);

        // Get the username from SharedPreferences
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        username = sharedPreferences.getString("username", "");

        spinnerArticle = view.findViewById(R.id.spinnerArticle);
        spinnerChapter = view.findViewById(R.id.spinnerChapter);
        buttonDeleteChapter = view.findViewById(R.id.buttonDeleteChapter);

        // Initialize UserData, ArticleData, and ChapterData instances
        userData = new UserData(requireContext());
        articleData = new ArticleData(requireContext());
        chapterData = new ChapterData();

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

        spinnerArticle.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedArticleTitle = (String) parent.getItemAtPosition(position);

                // Find the Article object based on the selected title
                Article selectedArticle = null;
                for (Article article : articles) {
                    if (article.getArticle_title().equals(selectedArticleTitle)) {
                        selectedArticle = article;
                        break; // Exit the loop when found
                    }
                }

                if (selectedArticle != null) {
                    // You now have the selected Article object
                    // You can use it for further processing
                    fetchChaptersByArticleId(selectedArticle.getArticle_id());
                } else {
                    // Handle the case when the selected article is not found
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle when nothing is selected
            }
        });

        spinnerChapter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Lấy chapter từ danh sách chapters bằng vị trí đã chọn
                Chapter selectedChapter = chapters.get(position);
                selectedChapterId = selectedChapter.getChapterId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Xử lý khi không có mục nào được chọn
            }
        });

        buttonDeleteChapter.setOnClickListener(view12 -> showDeleteConfirmationDialog());

        return view;
    }

    private void fetchArticlesByAuthor(String author) {
        articles = new ArrayList<>();
        articleData.fetchArticlesByAuthor(author, new ArticleData.ArticleCallback() {
            @Override
            public void onArticlesLoaded(List<Article> articleList) {
                articles = articleList;
                List<String> articleTitles = new ArrayList<>();
                for (Article article : articles) {
                    articleTitles.add(article.getArticle_title());
                }
                populateSpinner(spinnerArticle, articleTitles);
            }

            @Override
            public void onError(String errorMessage) {
                // Handle any error during article retrieval
            }
        });
    }

    private void fetchChaptersByArticleId(long articleId) {
        chapters = new ArrayList<>();
        // Fetch chapters for the selected article and populate the spinner
        FirebaseDatabase.getInstance().getReference("chapters")
                .orderByChild("articleId")
                .equalTo(String.valueOf(articleId))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        chapters = new ArrayList<>();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Chapter chapter = snapshot.getValue(Chapter.class);
                            chapters.add(chapter);
                        }

                        if (!chapters.isEmpty()) {
                            populateSpinner(spinnerChapter, getChapterNames(chapters));
                        } else {
                            // Handle the case when no chapters are found
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Handle any error during database query
                    }
                });
    }
    private List<String> getChapterNames(List<Chapter> chapterList) {
        List<String> chapterNames = new ArrayList<>();
        for (Chapter chapter : chapterList) {
            chapterNames.add(chapter.getChapterName());
        }
        return chapterNames;
    }

    private void populateSpinner(Spinner spinner, List<String> items) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private void showDeleteConfirmationDialog() {
        if (selectedChapterId != null) {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Confirm Deletion")
                    .setMessage("Are you sure you want to delete this chapter?")
                    .setPositiveButton("Yes", (dialog, which) -> deleteChapter())
                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                    .show();
        } else {
            Toast.makeText(requireContext(), "Please select a chapter to delete", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteChapter() {
        if (selectedChapterId != null) {
            // Delete the selected chapter
            chapterData.deleteChapter(selectedChapterId, new ChapterData.DeleteChapterListener() {
                @Override
                public void onChapterDeleted() {
                    Toast.makeText(requireContext(), "Chapter deleted", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(requireContext(), MainLayout.class);
                    startActivity(intent);
                }

                @Override
                public void onChapterDeleteError(String errorMessage) {
                    Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(requireContext(), "Please select a chapter to delete", Toast.LENGTH_SHORT).show();
        }
    }
}
