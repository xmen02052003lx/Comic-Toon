package com.example.btvn1;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class LoginActivity extends AppCompatActivity {

    private EditText usernameEditText, passwordEditText;
    private Button loginButton;
    private TextView registerTextView;
    private ImageView bannerImageView;
    private String imageUrl = "https://tse3.mm.bing.net/th?id=OIP.iuERCDZYaZU_Gb3py1tAiQHaEK&pid=Api&P=0&h=180";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        bannerImageView = findViewById(R.id.bannerImageView);

        // Khởi chạy AsyncTask để tải hình ảnh từ URL và hiển thị nó trong ImageView.
        new DownloadImageTask().execute(imageUrl);
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... urls) {
            String imageUrl = urls[0];
            Bitmap bitmap = null;

            try {
                URL url = new URL(imageUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                bitmap = BitmapFactory.decodeStream(input);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            if (result != null) {
                bannerImageView.setImageBitmap(result);
            }


            usernameEditText = findViewById(R.id.usernameEditText);
            passwordEditText = findViewById(R.id.passwordEditText);
            loginButton = findViewById(R.id.loginButton);
            registerTextView = findViewById(R.id.registerTextView);
            loginButton.setOnClickListener(v -> {
                // Xử lý đăng nhập ở đây
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                isLoginSuccessful(username, password);
            });

            registerTextView.setOnClickListener(v -> {
                // Điều hướng đến màn hình đăng ký
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            });
        }
    }

    private boolean isLoginSuccessful(String username, String password) {
        UserData userData = new UserData(this);
        userData.getUserByUsername(username, new UserData.DataRetrieveListener() {
            @Override
            public void onDataRetrieved(User user) {
                if (user != null) {
                    Log.d("LoginActivity", "User retrieved from database: " + user.getUsername());

                    // Mã hóa mật khẩu nhập vào từ giao diện người dùng
                    String encryptedPassword = encryptPassword(password);

                    if (user.getPassword().equals(encryptedPassword)) {
                        // Đăng nhập thành công
                        Toast.makeText(LoginActivity.this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                        // Điều hướng đến màn hình chính ở đây
                        startActivity(new Intent(LoginActivity.this, MainLayout.class));
                        finish();
                        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                        sharedPreferences.edit().putString("userId", user.getUserId()).apply();
                        sharedPreferences.edit().putString("username", username).apply();
                    } else {
                        // Mật khẩu không đúng
                        Toast.makeText(LoginActivity.this, "Mật khẩu không đúng", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Tài khoản không tồn tại
                    Toast.makeText(LoginActivity.this, "Tài khoản không tồn tại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onDataNotExists() {
                // Tài khoản không tồn tại
                Toast.makeText(LoginActivity.this, "Tài khoản không tồn tại", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDataRetrieveError(String errorMessage) {
                // Xử lý lỗi và hiển thị thông báo nếu cần
                Toast.makeText(LoginActivity.this, "Lỗi: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });

        return false; // Giá trị mặc định, bạn cần điều hướng đến màn hình chính khi đăng nhập thành công.
    }
    private String encryptPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedPassword = md.digest(password.getBytes());

            // Chuyển đổi kết quả thành chuỗi hex
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedPassword) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
}
