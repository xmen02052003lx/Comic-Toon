package com.example.btvn1;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class UpStoryFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;
    private EditText etTitle;
    private EditText etDescription;
    private Spinner spinnerCategory;
    private ImageView btnUploadImage;
    private Button btnPostStory;
    private Uri selectedImageUri;
    private DatabaseReference databaseReference;
    private ImageView ima;
    private String imageUrl;
    private String username;

    public UpStoryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("articles");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_up_story_fragment, container, false);
        SharedPreferences sharedPreferences1 = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        username = sharedPreferences1.getString("username", "");
        Log.d(TAG, "User ID from ViewModel: " + username);
        etTitle = view.findViewById(R.id.etTitle);
        etDescription = view.findViewById(R.id.etDescription);
        spinnerCategory = view.findViewById(R.id.spinnerCategory);
        btnUploadImage = view.findViewById(R.id.btnUploadImage);
        btnPostStory = view.findViewById(R.id.btnPostStory);
        ima = view.findViewById(R.id.imageanhmau);
        UserData userData = new UserData(requireContext());
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


        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(requireContext(), R.array.categories_array1, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);

        btnUploadImage.setOnClickListener(v -> openImageGallery());

        btnPostStory.setOnClickListener(v -> {
            postNewStory();
            Intent intent = new Intent(requireContext(), MainLayout.class);
            startActivity(intent);
        });

        return view;
    }

    private void openImageGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // Trong phương thức onActivityResult:
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == getActivity().RESULT_OK && data != null) {
            this.selectedImageUri = data.getData();

            if (this.selectedImageUri != null) {
                try {
                    // Truy cập ContentResolver từ đối tượng hoạt động
                    ContentResolver contentResolver = getActivity().getContentResolver();

                    // Mở luồng đọc từ ContentResolver
                    InputStream inputStream = contentResolver.openInputStream(selectedImageUri);
                    String uniqueFileName1 = "image-up_" + username + "_" + System.currentTimeMillis() + ".jpg";

                    // Tạo một tệp trong bộ nhớ của ứng dụng
                    File imageFile = new File(getActivity().getFilesDir(), uniqueFileName1);

                    // Sao chép dữ liệu từ luồng đầu vào tệp
                    OutputStream outputStream = new FileOutputStream(imageFile);
                    byte[] buffer = new byte[4 * 1024];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                    outputStream.close();
                    inputStream.close();

                    // Bây giờ, bạn đã lưu hình ảnh trong bộ nhớ của ứng dụng và có thể lấy URL:
                    imageUrl = Uri.fromFile(imageFile).toString();
                    Picasso.get().load(imageUrl).into(ima);
                    // imageUrl chứa URL của hình ảnh sau khi lưu vào bộ nhớ của ứng dụng.
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(requireContext(), "Failed to save image.", Toast.LENGTH_SHORT).show();
                }
            }
        }

    }



    private void postNewStory() {
        String title = etTitle.getText().toString();
        String description = etDescription.getText().toString();
        String category = spinnerCategory.getSelectedItem().toString();

        if (selectedImageUri != null) {
            // Create a new Article object with the entered information
            Article newArticle = new Article(getNextArticleId(), imageUrl, title, description, category, 0,username);

            // Save the new Article to Firebase Database
            databaseReference.child(String.valueOf(newArticle.getArticle_id())).setValue(newArticle);

            Toast.makeText(requireContext(), "Story posted successfully!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(requireContext(), "Please select an image.", Toast.LENGTH_SHORT).show();
        }
    }

    private long getNextArticleId() {
        long maxId = -1;
        if (ArticleData.data != null && ArticleData.data.getArticles() != null) {
            for (Article article : ArticleData.data.getArticles()) {
                if (article.getArticle_id() > maxId) {
                    maxId = article.getArticle_id();
                }
            }
        }
        return maxId + 1;
    }
}
