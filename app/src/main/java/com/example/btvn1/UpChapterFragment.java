package com.example.btvn1;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import com.google.firebase.database.*;

import java.util.List;

public class UpChapterFragment extends Fragment {

    private EditText etContent, etContent1;
    private Spinner spinnerTitles;
    private Button btnPostChapter;
    private DatabaseReference databaseReference;
    private List<Article> articleList;
    private String username, username1;

    public UpChapterFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_up_chapter_fragment, container, false);
        SharedPreferences sharedPreferences1 = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        username = sharedPreferences1.getString("username", "");
        UserData userData = new UserData(requireContext());
        userData.getUserByUsername(username, new UserData.DataRetrieveListener() {
            @Override
            public void onDataRetrieved(User user) {
                if (user != null) {
                    username1 = user.getDisplayName();
                    Log.d(TAG, "User ID from ViewModel: " + username1);
                    // Initialize Firebase Database reference
                    databaseReference = FirebaseDatabase.getInstance().getReference("chapters");
                    ArticleData articleData = new ArticleData(requireContext());
                    articleData.fetchDataAndListenForChanges(articles -> {
                        // Lưu danh sách bài viết từ ArticleData
                        articleList = articles;
                        Log.d(TAG, "User ID from ViewModel1: " + username1);
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        for (Article article : articleList) {
                            if (article.getArticle_author().equals(username1)) {
                                adapter.add(article.getArticle_title());
                            }
                        }
                        spinnerTitles.setAdapter(adapter);
                    });
                    // Ở đây bạn có thông tin displayName, bạn có thể sử dụng nó theo nhu cầu.
                } else {
                    // Xử lý khi không tìm thấy thông tin người dùng
                }
            }
            @Override
            public void onDataNotExists() {
                // Xử lý khi người dùng không tồn tại
            }

            @Override
            public void onDataRetrieveError(String errorMessage) {
                // Xử lý lỗi nếu có
            }
        });

        etContent = view.findViewById(R.id.etContent);
        spinnerTitles = view.findViewById(R.id.spinnerTitles);
        btnPostChapter = view.findViewById(R.id.btnPostChapter);
        etContent1 = view.findViewById(R.id.etChapterTitle);

        btnPostChapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postNewChapter();
                Intent intent = new Intent(requireContext(), MainLayout.class);
                startActivity(intent);
            }
        });

        return view;
    }

    private void postNewChapter() {
        String content = etContent.getText().toString();
        String content1 = etContent1.getText().toString();
        String selectedTitle = spinnerTitles.getSelectedItem().toString();

        if (!content.isEmpty() && !selectedTitle.isEmpty()) {
            ChapterData chapterData = new ChapterData();
            chapterData.addChapterWithGeneratedId(getArticleIdFromSelectedTitle(selectedTitle), content1, content);
            Toast.makeText(requireContext(), "Up chapter successful !", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(requireContext(), "Please enter content and select a title.", Toast.LENGTH_SHORT).show();
        }
    }

    private String getArticleIdFromSelectedTitle(String selectedTitle) {
        for (Article article : articleList) {
            if (article.getArticle_title().equals(selectedTitle)) {
                return String.valueOf(article.getArticle_id());
            }
        }
        return null;
    }

}