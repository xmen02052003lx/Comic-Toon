package com.example.btvn1;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ChangePasswordActivity extends AppCompatActivity {

    private EditText oldPasswordEditText, newPasswordEditText, confirmPasswordEditText;
    private Button saveButton;
    private String username;
    private UserData userData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        // Khởi tạo userData để truy cập dữ liệu người dùng từ Firebase
        userData = new UserData(this);

        // Lấy userId từ SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        username = sharedPreferences.getString("username", "");
        Log.d(TAG, "User ID from ViewModel: " + username);


        oldPasswordEditText = findViewById(R.id.oldPasswordEditText);
        newPasswordEditText = findViewById(R.id.newPasswordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmNewPasswordEditText);
        saveButton = findViewById(R.id.savePasswordButton);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePassword();
                Intent intent = new Intent(ChangePasswordActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void changePassword() {
        final String oldPassword = oldPasswordEditText.getText().toString();
        final String newPassword = newPasswordEditText.getText().toString();
        String confirmPassword = confirmPasswordEditText.getText().toString();

        // Kiểm tra xem mật khẩu cũ trùng khớp với mật khẩu trên cơ sở dữ liệu
        userData.getUserByUsername(username, new UserData.DataRetrieveListener() {
            @Override
            public void onDataRetrieved(User user) {
                if (user != null) {
                    if (user.getPassword().equals(encryptPassword(oldPassword))) {
                        // Mật khẩu cũ đúng, tiếp tục kiểm tra mật khẩu mới
                        if (newPassword.isEmpty()) {
                            Toast.makeText(ChangePasswordActivity.this, "Mật khẩu mới không được để trống", Toast.LENGTH_SHORT).show();
                        } else if (!newPassword.equals(confirmPassword)) {
                            Toast.makeText(ChangePasswordActivity.this, "Mật khẩu mới không trùng khớp với xác nhận mật khẩu", Toast.LENGTH_SHORT).show();
                        } else {
                            // Cập nhật mật khẩu mới đã được mã hóa vào cơ sở dữ liệu
                            user.setPassword(encryptPassword(newPassword));
                            userData.updateUser(user, new UserData.DataUpdateListener() {
                                @Override
                                public void onDataUpdated(User updatedUser) {
                                    Toast.makeText(ChangePasswordActivity.this, "Mật khẩu đã được thay đổi", Toast.LENGTH_SHORT).show();
                                    finish();
                                }

                                @Override
                                public void onDataUpdateError(String errorMessage) {
                                    Toast.makeText(ChangePasswordActivity.this, "Lỗi: " + errorMessage, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    } else {
                        // Mật khẩu cũ không trùng khớp
                        Toast.makeText(ChangePasswordActivity.this, "Mật khẩu cũ không đúng", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Lỗi khi tìm kiếm người dùng
                    Toast.makeText(ChangePasswordActivity.this, "Lỗi khi tìm kiếm người dùng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onDataNotExists() {
                // Người dùng không tồn tại
                Toast.makeText(ChangePasswordActivity.this, "Người dùng không tồn tại", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDataRetrieveError(String errorMessage) {
                // Lỗi khi tìm kiếm người dùng
                Toast.makeText(ChangePasswordActivity.this, "Lỗi: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
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
