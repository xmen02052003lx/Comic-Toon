package com.example.btvn1;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.squareup.picasso.Picasso;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class RegisterActivity extends AppCompatActivity {

    private EditText usernameEditText, passwordEditText, displayNameEditText;
    private Button registerButton;
    private ImageView bannerImageView, selectedImageView, pickImageButton;
    private String imageUrl1;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri selectedImageUri;
    String imageUrl = "https://tse3.mm.bing.net/th?id=OIP.iuERCDZYaZU_Gb3py1tAiQHaEK&pid=Api&P=0&h=180";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        displayNameEditText = findViewById(R.id.displayNameEditText);
        registerButton = findViewById(R.id.registerButton);
        bannerImageView = findViewById(R.id.bannerImageView);
        selectedImageView = findViewById(R.id.selectedImageView);
        pickImageButton = findViewById(R.id.uploadImageView);
        Picasso.get().load(imageUrl).into(bannerImageView);

        pickImageButton.setOnClickListener(v -> openImageGallery());

        registerButton.setOnClickListener(v -> {
            // Xử lý đăng ký ở đây
            String username = usernameEditText.getText().toString();
            String password = passwordEditText.getText().toString();
            String displayName = displayNameEditText.getText().toString();

            if (username.isEmpty() || password.isEmpty() || displayName.isEmpty() || (selectedImageUri == null)) {
                // Hiển thị thông báo nếu có bất kỳ trường nào bị để trống
                Toast.makeText(RegisterActivity.this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            } else {
                // Thực hiện đăng ký và xử lý kết quả
                registerUser(username, password, displayName, imageUrl1);
            }
        });
    }

    private void openImageGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();

            if (selectedImageUri != null) {
                try {
                    // Truy cập ContentResolver từ đối tượng hoạt động
                    ContentResolver contentResolver = getContentResolver();

                    // Mở luồng đọc từ ContentResolver
                    InputStream inputStream = contentResolver.openInputStream(selectedImageUri);
                    String uniqueFileName2 = "image-avatar" + "_" + System.currentTimeMillis() + ".jpg";

                    // Tạo một tệp trong bộ nhớ của ứng dụng
                    File imageFile = new File(getFilesDir(), uniqueFileName2);

                    // Sao chép dữ liệu từ luồng đầu vào tệp
                    OutputStream outputStream = new FileOutputStream(imageFile);
                    byte[] buffer = new byte[4 * 1024];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                    outputStream.close();
                    inputStream.close();

                    // Lưu URL của hình ảnh đã chọn
                    imageUrl1 = Uri.fromFile(imageFile).toString();
                    Picasso.get().load(imageUrl1).into(selectedImageView);
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Failed to save image.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
    public void goToLogin(View view) {
        // Xử lý chuyển đến màn hình đăng nhập ở đây
        Intent loginIntent = new Intent(this, LoginActivity.class);
        startActivity(loginIntent);
    }


    private void registerUser(String username, String password, String displayName, String imageUrl) {
        // Thực hiện đăng ký tài khoản và xử lý kết quả
        // Gọi lớp UserData hoặc DatabaseHelper tùy theo cơ sở dữ liệu bạn sử dụng.
        // Ví dụ:
        UserData userData = new UserData(this);
        User user = new User(null, username, password, displayName, imageUrl);
        userData.addUser(user, new UserData.DataAddListener() {
            @Override
            public void onDataAdded(User user) {
                // Đăng ký thành công, bạn có thể điều hướng hoặc hiển thị thông báo tùy ý.
                Toast.makeText(RegisterActivity.this, "Đăng kí thành công", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }

            @Override
            public void onDataAddError(String errorMessage) {
                // Đăng ký thất bại, hiển thị thông báo lỗi tên đăng nhập đã tồn tại.
                Toast.makeText(RegisterActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
