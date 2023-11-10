package com.example.btvn1;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;
import com.squareup.picasso.Picasso;

public class ViewArticleActivity extends AppCompatActivity {
    ImageView ivDetail;
    TextView tvDetailTitle, tvDetailDescription, tvCategory, tvView, tvAuthor;
    Button btnReadArticle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_article);
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        ivDetail = findViewById(R.id.iv_detail);
        tvDetailTitle = findViewById(R.id.tv_detail_title);
        tvDetailDescription = findViewById(R.id.tv_detail_description);
        tvCategory = findViewById(R.id.tv_detail_category);
        tvView = findViewById(R.id.tv_detail_views);
        tvAuthor = findViewById(R.id.tv_detail_author);

        btnReadArticle = findViewById(R.id.btn_read);
        btnReadArticle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long articleId = getIntent().getLongExtra("id", -1); // Get the article ID from the Intent
                if (articleId != -1) {
                    Intent intent = new Intent(ViewArticleActivity.this, ViewChapterActivity.class);
                    intent.putExtra("article_id", articleId); // Pass the article ID to ViewChapterActivity

                    startActivity(intent);
                } else {
                    Log.e("ViewArticleActivity", "Invalid id received.");
                }
            }
        });


        // Lấy dữ liệu từ Intent
        long id = getIntent().getLongExtra("id", -1);
        // Log the value of id
        Log.d("ViewArticleActivity", "id received: " + id);

        if (id != -1) {
            Article article = ArticleData.getArticleFromId(id);
            if (article != null) {
                // Load and display article data
            } else {
                Log.e("ViewArticleActivity", "Article not found in ArticleData.");
            }
        } else {
            Log.e("ViewArticleActivity", "Invalid id received.");
        }


        // Kiểm tra nếu có dữ liệu hợp lệ
        if (id != -1) {
            Article article = ArticleData.getArticleFromId(id);
            if (article != null) {
                // Load hình ảnh bằng Picasso
                Picasso.get().load(article.getArticle_image()).resize(420, 420).centerCrop().into(ivDetail);
                tvDetailTitle.setText(article.getArticle_title());
                tvDetailDescription.setText("Mô tả :" + article.getArticle_description());
                tvCategory.setText("Thể loại :" + article.getArticle_category());
                tvAuthor.setText("Tác giả: " + article.getArticle_author());
                tvView.setText("Lượt đọc :" + String.valueOf(article.getArticle_views()));
            } else {
                // Xử lý trường hợp không tìm thấy bài viết
                // Ví dụ: Hiển thị thông báo lỗi
                tvDetailTitle.setText("Article Not Found");
                tvDetailDescription.setText("This article does not exist.");
            }
        } else {
            // Xử lý trường hợp không có dữ liệu từ Intent
            // Ví dụ: Hiển thị thông báo lỗi
            tvDetailTitle.setText("Invalid Data");
            tvDetailDescription.setText("No valid data received.");
        }
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
