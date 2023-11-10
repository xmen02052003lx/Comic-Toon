package com.example.btvn1;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private GridView gridview;
    private ArticleAdapter articleAdapter;
    private final List<Article> articleList = new ArrayList<>();
    private SharedViewModel viewModel;
    private final AdapterView.OnItemClickListener onitemclick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Article selectedArticle = articleList.get(position);
            // Lấy articleId của bài viết đã chọn
            long articleId = selectedArticle.getArticle_id();

            // Cập nhật số lượt xem trong Firebase
            updateArticleViewsInFirebase(articleId);
            Intent intent = new Intent(getActivity(), ViewArticleActivity.class);
            intent.putExtra("id", (long)selectedArticle.getArticle_id()); // Truyền getArticles_Id
            startActivity(intent);
            viewModel.selectArticle(selectedArticle.getArticle_id()); // Cập nhật LiveData
        }
    };

    private Spinner spinner; // Spinner for selecting categories
    private ArrayAdapter<CharSequence> spinnerAdapter;
    private String selectedCategory = "Tất cả"; // Default to show all articles

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_main, container, false);
        viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        gridview = view.findViewById(R.id.gridview);
        gridview.setOnItemClickListener(onitemclick);

        spinner = view.findViewById(R.id.spinner);
        spinnerAdapter = ArrayAdapter.createFromResource(requireContext(), R.array.categories_array, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Get the selected category
                selectedCategory = (String) parentView.getItemAtPosition(position);

                // Update the GridView with articles of the selected category
                updateGridView(selectedCategory);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing
            }
        });

        // Kiểm tra xem dữ liệu ban đầu đã được thêm vào Firebase chưa
        checkInitialDataInFirebase();

        // Tạo adapter cho GridView và thiết lập nó
        articleAdapter = new ArticleAdapter(getActivity(), (ArrayList<Article>) articleList);
        gridview.setAdapter(articleAdapter);

        // Load all articles initially
        loadArticlesByCategory("Tất cả");

        return view;
    }

    private void checkInitialDataInFirebase() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("articles");
        // Kiểm tra xem có dữ liệu ban đầu trong Firebase chưa
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    // Thêm dữ liệu ban đầu vào Firebase
                    addInitialDataToFirebase();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Xử lý lỗi nếu cần
                Toast.makeText(getActivity(), "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addInitialDataToFirebase() {
        ArticleData articleData = new ArticleData(getActivity());
        articleData.addInitialData();
    }

    private void updateArticleViewsInFirebase(long articleId) {
        ArticleData articleData = new ArticleData(getActivity());
        articleData.updateArticleViewsInFirebase(articleId);
    }

    private void updateGridView(String category) {
        // Update the GridView based on the selected category
        loadArticlesByCategory(category);
    }

    private void loadArticlesByCategory(String category) {
        // Load articles from Firebase based on the selected category
        ArticleData articleData = new ArticleData(getActivity());
        articleData.fetchDataAndListenForChanges(articles -> {
            // Filter articles based on the selected category
            articleList.clear();
            if (category.equals("Tất cả")) {
                // Show all articles
                articleList.addAll(articles);
            } else {
                // Show articles of the selected category
                for (Article article : articles) {
                    if (article.getArticle_category().equals(category)) {
                        articleList.add(article);
                    }
                }
            }
            articleAdapter.notifyDataSetChanged();
        });
    }
}
