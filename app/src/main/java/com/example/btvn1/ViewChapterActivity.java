package com.example.btvn1;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ViewChapterActivity extends AppCompatActivity {
    private TextView chapterNameTextView;
    private TextView chapterStoryTextView;
    private EditText commentEditText;
    private Button toughPatchButton, previousButton, sendbutton;
    private long articleId;
    private String userId;
    private String username; // Declare username at the class level
    private String articleImage; // Declare articleImage at the class level
    private String articleTitle;
    private List<Chapter> chaptersList;
    private int currentChapterIndex;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_chapter);
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        articleId = getIntent().getLongExtra("article_id", -1);
        Log.d(TAG, "User ID from ViewModel: " + articleId);
        onStorySelected(articleId);
        SharedPreferences sharedPreferences = this.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        userId = sharedPreferences.getString("userId", null);
        // Lấy userId từ SharedPreferences
        SharedPreferences sharedPreferences1 = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        username = sharedPreferences1.getString("username", "");
        Log.d(TAG, "User ID from ViewModel: " + username);
        if (userId != null && articleId != -1) {
            // Lấy ngày và giờ hiện tại
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String currentDateAndTime = sdf.format(new Date());

            // Gọi phương thức addToLibrary để thêm dữ liệu vào Firebase
            LibraryData libraryData = new LibraryData();
            libraryData.addToLibrary(String.valueOf(articleId), userId, currentDateAndTime);
        }
        commentEditText = findViewById(R.id.editTextText);
        sendbutton = findViewById(R.id.button);
        UserData userData = new UserData(this);
        userData.getUserByUsername(username, new UserData.DataRetrieveListener() {
            @Override
            public void onDataRetrieved(User user) {
                if (user != null) {
                    username = user.getDisplayName();

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
        ArticleData articleData = new ArticleData(this);
        Article article = articleData.getArticleFromId(articleId);

        if (articleId != -1) {
            articleImage = article.getArticle_image();
            articleTitle = article.getArticle_title();

            // Sử dụng `articleImage` và `title` theo nhu cầu
        } else {
            // Xử lý trường hợp không tìm thấy bài viết
        }

        Log.d(TAG, "User ID from ViewModel: " + username);
        sendbutton.setOnClickListener(view -> {
            String commentText = commentEditText.getText().toString().trim();
            if (!username.isEmpty() && !articleImage.isEmpty() && !commentText.isEmpty()) {
                // Gọi phương thức add của CommentData để thêm bình luận vào Firebase
                CommentData commentData = new CommentData();
                commentData.addComment(username, commentText, articleImage, articleTitle);

                // Xóa văn bản trong ô nhập bình luận sau khi gửi
                commentEditText.setText("");
            }
        });

        chapterNameTextView = findViewById(R.id.chapterNameTextView);
        chapterStoryTextView = findViewById(R.id.chapterStoryTextView);
        toughPatchButton = findViewById(R.id.nextButton);
        previousButton = findViewById(R.id.previousButton);

        // Khởi tạo DatabaseReference và kiểm tra dữ liệu ban đầu trong Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference("chapters");
        checkInitialDataInFirebase();
        // Load the first chapter when the activity starts

        toughPatchButton.setOnClickListener(view -> loadNextChapter());

        previousButton.setOnClickListener(view -> loadPreviousChapter());
    }
    private void updateChapterList(long newArticleId) {
        // Xóa danh sách chương hiện tại
        if (chaptersList != null) {
            chaptersList.clear();
        } else {
            chaptersList = new ArrayList<>();
        }

        // Lấy thông tin chương từ Firebase liên quan đến articleId cụ thể
        FirebaseDatabase.getInstance().getReference("chapters")
                .orderByChild("articleId")
                .equalTo(String.valueOf(newArticleId))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Chapter chapter = snapshot.getValue(Chapter.class);
                            chaptersList.add(chapter);
                        }

                        // Kiểm tra xem danh sách chương có dữ liệu không
                        if (chaptersList.isEmpty()) {
                            // Xử lý khi không tìm thấy chương
                        } else {
                            currentChapterIndex = 0;
                            loadChapter(currentChapterIndex);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Xử lý lỗi nếu cần
                    }
                });
    }
    // Khi người dùng chọn một câu chuyện mới
    private void onStorySelected(long newArticleId) {
        // Cập nhật danh sách chương dựa trên articleId mới
        updateChapterList(newArticleId);

    }

    private void loadChapter(int index) {
        if (index >= 0 && index < chaptersList.size()) {
            Chapter chapter = chaptersList.get(index);
            chapterNameTextView.setText(chapter.getChapterName());
            chapterStoryTextView.setText(chapter.getChapterStory());
        } else {
            // Xử lý khi vị trí chương không hợp lệ
        }
    }

    private void loadNextChapter() {
        if (currentChapterIndex < chaptersList.size() - 1) {
            currentChapterIndex++;
            loadChapter(currentChapterIndex);
        } else {
            // Xử lý khi không còn chương kế tiếp
        }
    }

    private void loadPreviousChapter() {
        if (currentChapterIndex > 0) {
            currentChapterIndex--;
            loadChapter(currentChapterIndex);
        } else {
            // Xử lý khi không còn chương trước đó
        }
    }

    private void checkInitialDataInFirebase() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("chapters");
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
            }
        });
    }

    private void addInitialDataToFirebase() {
        ChapterData chapterData = new ChapterData();
        chapterData.addInitialData();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // Khi nút "Up" được bấm, quay lại parent activity
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
