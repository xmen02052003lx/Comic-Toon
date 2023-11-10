package com.example.btvn1;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.MenuItem;
import android.widget.TextView;

public class XoaBai extends AppCompatActivity {
    private TextView tvStory;
    private TextView tvChapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dang_bai);
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Ánh xạ TextView cho lựa chọn "Story" và "Chapter"
        tvStory = findViewById(R.id.tvStory);
        tvChapter = findViewById(R.id.tvChapter);

        // Thiết lập sự kiện khi người dùng chọn "Story"
        tvStory.setOnClickListener(v -> {
            tvStory.setPaintFlags(tvStory.getPaintFlags() | android.graphics.Paint.UNDERLINE_TEXT_FLAG);
            tvChapter.setPaintFlags(tvChapter.getPaintFlags() & (~android.graphics.Paint.UNDERLINE_TEXT_FLAG));
            // Hiển thị Fragment "Story"
            loadFragment(new DeleteStoryFragment());
        });

        // Thiết lập sự kiện khi người dùng chọn "Chapter"
        tvChapter.setOnClickListener(v -> {
            tvChapter.setPaintFlags(tvChapter.getPaintFlags() | android.graphics.Paint.UNDERLINE_TEXT_FLAG);
            tvStory.setPaintFlags(tvStory.getPaintFlags() & (~android.graphics.Paint.UNDERLINE_TEXT_FLAG));
            // Hiển thị Fragment "Chapter"
            loadFragment(new DeleteChapterFragment());
        });
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.frameContainer, fragment)
                .commit();
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
