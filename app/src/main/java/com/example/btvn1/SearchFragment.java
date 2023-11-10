package com.example.btvn1;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {

    private AutoCompleteTextView autoCompleteTextView;
    private Button searchButton;
    private RecyclerView recyclerView;
    private RecycleViewAdapter recycleViewAdapter;
    private List<Article> articles;
    private DatabaseReference databaseReference;
    private CheckBox checkBoxTitle;
    private CheckBox checkBoxCategory;
    private CheckBox checkBoxAuthor;
    private CheckBox checkBoxDescription;

    private ArrayAdapter<String> currentAdapter; // Holds the currently selected adapter

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_search, container, false);
        autoCompleteTextView = view.findViewById(R.id.autoCompleteTextView);
        searchButton = view.findViewById(R.id.searchButton);
        recyclerView = view.findViewById(R.id.recyclerView);
        checkBoxTitle = view.findViewById(R.id.checkBoxTitle);
        checkBoxCategory = view.findViewById(R.id.checkBoxCategory);
        checkBoxAuthor = view.findViewById(R.id.checkBoxAuthor);
        checkBoxDescription = view.findViewById(R.id.checkBoxDiscription);

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        articles = new ArrayList();
        recycleViewAdapter = new RecycleViewAdapter(requireContext(), articles);
        recyclerView.setAdapter(recycleViewAdapter);
        databaseReference = FirebaseDatabase.getInstance().getReference("articles");

        setAutoCompleteTextViewSuggestions(); // Initialize suggestions based on title

        recycleViewAdapter.setOnItemClickListener(article -> {
            // Chuyển sang ViewArticleActivity với dữ liệu tương ứng
            Intent intent = new Intent(requireContext(), ViewArticleActivity.class);
            intent.putExtra("id", (long) article.getArticle_id());
            startActivity(intent);
            // Cập nhật số lượt xem trong Firebase
            updateArticleViewsInFirebase(article.getArticle_id());
        });

        searchButton.setOnClickListener(v -> {
            boolean searchByTitle = checkBoxTitle.isChecked();
            boolean searchByCategory = checkBoxCategory.isChecked();
            boolean searchByAuthor = checkBoxAuthor.isChecked();
            boolean searchByDescription = checkBoxDescription.isChecked();
            String searchTerm = autoCompleteTextView.getText().toString().trim();

            if (searchByTitle || searchByCategory || searchByAuthor || searchByDescription) {
                searchInFirebase(searchTerm, searchByTitle, searchByCategory, searchByAuthor, searchByDescription);
            } else {
                Toast.makeText(requireContext(), "Please select at least one search criteria.", Toast.LENGTH_SHORT).show();
            }
        });

        // Add listeners for the checkboxes to dynamically change the AutoCompleteTextView adapter
        checkBoxTitle.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                setAutoCompleteTextViewSuggestions();
            }
        });

        checkBoxCategory.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                setAutoCompleteTextViewSuggestions();
            }
        });

        checkBoxAuthor.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                setAutoCompleteTextViewSuggestions();
            }
        });

        checkBoxDescription.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                setAutoCompleteTextViewSuggestions();
            }
        });

        return view;
    }

    private void setAutoCompleteTextViewSuggestions() {
        currentAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, new ArrayList<>());
        autoCompleteTextView.setAdapter(currentAdapter);

        // Truy vấn cơ sở dữ liệu để lấy danh sách tương ứng với các checkbox đã chọn
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> suggestions = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Article article = dataSnapshot.getValue(Article.class);
                    if (article != null) {
                        if (checkBoxTitle.isChecked()) {
                            suggestions.add(article.getArticle_title());
                        }
                        if (checkBoxCategory.isChecked()) {
                            suggestions.add(article.getArticle_category());
                        }
                        if (checkBoxAuthor.isChecked()) {
                            suggestions.add(article.getArticle_author());
                        }
                        if (checkBoxDescription.isChecked()) {
                            suggestions.add(article.getArticle_description());
                        }
                    }
                }
                currentAdapter.clear();
                currentAdapter.addAll(suggestions);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(requireContext(), "Error loading suggestions.", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void searchInFirebase(String searchTerm, boolean searchByTitle, boolean searchByCategory, boolean searchByAuthor, boolean searchByDescription) {
        articles.clear();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Article article = dataSnapshot.getValue(Article.class);

                    // Kiểm tra các tiêu chí tìm kiếm và thêm bài viết phù hợp vào danh sách kết quả
                    if (article != null) {
                        boolean isMatch = searchByTitle && article.getArticle_title().contains(searchTerm);
                        if (searchByCategory && article.getArticle_category().contains(searchTerm)) {
                            isMatch = true;
                        }
                        if (searchByAuthor && article.getArticle_author().contains(searchTerm)) {
                            isMatch = true;
                        }
                        if (searchByDescription && article.getArticle_description().contains(searchTerm)) {
                            isMatch = true;
                        }

                        if (isMatch) {
                            articles.add(article);
                        }
                    }
                }
                recycleViewAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(requireContext(), "Error loading articles.", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void updateArticleViewsInFirebase(long articleId) {
        ArticleData articleData = new ArticleData(requireActivity());
        articleData.updateArticleViewsInFirebase(articleId);
    }
}
