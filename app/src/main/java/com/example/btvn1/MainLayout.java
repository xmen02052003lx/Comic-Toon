package com.example.btvn1;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.PreferenceManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.squareup.picasso.Picasso;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainLayout extends AppCompatActivity {

    private Fragment fragment_home;
    private Fragment searchFragment;
    private Fragment libraryFragment;
    private Fragment settingsFragment;
    private Fragment commentFragment;
    private TextView headerTextView;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trang_chu);
        // Đọc trạng thái chế độ ban đêm từ SharedPreferences
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isNightModeOn = sharedPreferences.getBoolean("night_mode", false);
        // Lấy userId từ SharedPreferences
        SharedPreferences sharedPreferences1 = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        username = sharedPreferences1.getString("username", "");
        Log.d(TAG, "User ID from ViewModel: " + username);

        // Áp dụng chế độ ban đêm ngay sau khi ứng dụng khởi động
        AppCompatDelegate.setDefaultNightMode(isNightModeOn ?
                AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);

        headerTextView = findViewById(R.id.toolbarText1); // Lấy tham chiếu đến TextView

        // Tạo Fragment mặc định và hiển thị
        fragment_home = new HomeFragment();
        searchFragment = new SearchFragment();
        libraryFragment = new LibraryFragment();
        settingsFragment = new SettingsFragment();
        commentFragment = new CommentFragment();
        setFragment(fragment_home);
        headerTextView.setText("Home");
        NavigationView navigationView = findViewById(R.id.navigationView);
        DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);
        View headerView = navigationView.getHeaderView(0);
        ImageView menuItemImage = headerView.findViewById(R.id.menu_item_image);
        TextView menuItemText = headerView.findViewById(R.id.menu_item_text);
        UserData userData = new UserData(this);
        userData.getUserByUsername(username, new UserData.DataRetrieveListener() {
            @Override
            public void onDataRetrieved(User user) {
                if (user != null) {
                    username = user.getDisplayName();
                    String image = user.getImage();
                    Picasso.get().load(image).into(menuItemImage);
                    menuItemText.setText(username);
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

        // Xử lý sự kiện khi nút hamburger được nhấn
        ImageView btnMenu = findViewById(R.id.btnMenu1);
        btnMenu.setOnClickListener(view -> drawerLayout.openDrawer(GravityCompat.START));

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.menu_info) {
                startActivity(new Intent(this, SuaBai.class));
            } else if (id == R.id.menu_nofi) {
                startActivity(new Intent(this, XoaBai.class));
            } else if (id == R.id.menu_upbai) {
                startActivity(new Intent(this, DangBai.class));
            } else if (id == R.id.menu_share) {
                // Mở hộp thoại Share Intent
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, "App đọc truyện này rất hay");
                startActivity(shareIntent);
                return true;
            } else if (id == R.id.menu_send) {
                // Mở hộp thoại gửi email
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", "luanle.31211027594@st.ueh.edu.vn", null));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Chủ đề email");
                emailIntent.putExtra(Intent.EXTRA_TEXT, "Nội dung email");
                startActivity(Intent.createChooser(emailIntent, "Gửi Email"));
                return true;
            } else if (id == R.id.menu_rate_us) {
                // Mở trang đánh giá ứng dụng trên Google Play Store
                Uri uri = Uri.parse("market://details?id=" + getPackageName());
                Intent rateUsIntent = new Intent(Intent.ACTION_VIEW, uri);
                if (rateUsIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(rateUsIntent);
                } else {
                    Toast.makeText(this, "Hiện chưa được phát hành trên google play !", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
            // Đóng Navigation Drawer sau khi chọn mục
            drawerLayout.closeDrawer(GravityCompat.START);

            return true;
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            String headerText = ""; // Chuỗi tên header tương ứng với mục được chọn

            if (item.getItemId() == R.id.home) {
                selectedFragment = fragment_home;
                headerText = "Home"; // Tên tương ứng với mục Home
            } else if (item.getItemId() == R.id.search) {
                selectedFragment = searchFragment;
                headerText = "Search"; // Tên tương ứng với mục Search
            } else if (item.getItemId() == R.id.library) {
                selectedFragment = libraryFragment;
                headerText = "Library"; // Tên tương ứng với mục Library
            } else if (item.getItemId() == R.id.comment){
                selectedFragment = commentFragment;
                headerText = "Comment";
            } else if (item.getItemId() == R.id.settings) {
                selectedFragment = settingsFragment;
                headerText = "Settings"; // Tên tương ứng với mục Settings
            }

            setFragment(selectedFragment);

            // Cập nhật nội dung của TextView header
            headerTextView.setText(headerText);

            return true;
        });
    }

    private void setFragment(Fragment fragment) {
        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.fragment_container, fragment);
            transaction.commit();
        }
    }
}
