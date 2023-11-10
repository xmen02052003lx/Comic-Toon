package com.example.btvn1;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.squareup.picasso.Picasso;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class ChangeStoryFragment extends Fragment {

    private Spinner spinnerArticle;
    private EditText editTextTitle;
    private EditText editTextCategory;
    private EditText editTextDescription;
    private ImageView imageViewArticleImage;
    private Button buttonSelectImage;
    private Button buttonUpdateArticle;
    private String username;
    private UserData userData;
    private ArticleData articleData;
    private List<Article> articles;
    private String imageUrl2;
    private Context context;
    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_change_story_fragmet, container, false);

        // Get the username from SharedPreferences
        SharedPreferences sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        username = sharedPreferences.getString("username", "");

        spinnerArticle = view.findViewById(R.id.spinnerArticle);
        editTextTitle = view.findViewById(R.id.editTextTitle);
        editTextCategory = view.findViewById(R.id.editTextCategory);
        editTextDescription = view.findViewById(R.id.editTextDescription);
        imageViewArticleImage = view.findViewById(R.id.imageViewArticleImage);
        buttonSelectImage = view.findViewById(R.id.buttonSelectImage);
        buttonUpdateArticle = view.findViewById(R.id.buttonUpdateArticle);

        // Initialize UserData and ArticleData instances
        userData = new UserData(context);
        articleData = new ArticleData(context);

        // Fetch user's display name using the username
        userData.getUserByUsername(username, new UserData.DataRetrieveListener() {
            @Override
            public void onDataRetrieved(User user) {
                if (user != null) {
                    String displayName = user.getDisplayName();

                    // Fetch articles by the user's display name
                    fetchArticlesByAuthor(displayName);
                } else {
                    // Handle when the user is not found
                }
            }

            @Override
            public void onDataNotExists() {
                // Handle when the user is not found
            }

            @Override
            public void onDataRetrieveError(String errorMessage) {
                // Handle data retrieval error
            }
        });

        // Set a listener for the Spinner item selection
        spinnerArticle.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            // Trong phương thức onItemSelected của Spinner
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedTitle = (String) parent.getItemAtPosition(position);
                Article selectedArticle = findArticleByTitle(selectedTitle);
                if (selectedArticle != null) {
                    // Tiếp theo, bạn có thể làm việc với đối tượng selectedArticle
                    populateArticleDetails(selectedArticle);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Điều này là nơi bạn đặt xử lý khi không có mục nào được chọn
            }
        });


        // Set a listener for the button to select a different image
        buttonSelectImage.setOnClickListener(view1 -> openImageGallery());

        // Set a listener for the button to update the article
        buttonUpdateArticle.setOnClickListener(view12 -> {
            updateArticle();
            Intent intent = new Intent(requireContext(), MainLayout.class);
            startActivity(intent);
        });

        return view;
    }

    private void fetchArticlesByAuthor(String author) {
        // Fetch articles by the given author's display name
        articles = new ArrayList<>();
        articleData.fetchDataAndListenForChanges(articleList -> {
            for (Article article : articleList) {
                if (article.getArticle_author().equals(author)) {
                    articles.add(article);
                }
            }
            // Create a list of article titles from the articles list
            List<String> articleTitles = new ArrayList<>();
            for (Article article : articles) {
                articleTitles.add(article.getArticle_title());
            }
            // Populate the Spinner with the article titles
            populateSpinner(articleTitles);
        });
    }

    private void populateSpinner(List<String> articleTitles) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, articleTitles);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerArticle.setAdapter(adapter);
    }


    private void populateArticleDetails(Article selectedArticle) {
        editTextTitle.setText(selectedArticle.getArticle_title());
        editTextCategory.setText(selectedArticle.getArticle_category());
        editTextDescription.setText(selectedArticle.getArticle_description());
    }

    private void openImageGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == getActivity().RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();

            if (selectedImageUri != null) {
                try {
                    ContentResolver contentResolver = context.getContentResolver();
                    InputStream inputStream = contentResolver.openInputStream(selectedImageUri);
                    // Tạo tên file duy nhất dựa trên thời gian và ngày
                    String uniqueFileName = "image-change_" + username + "_" + System.currentTimeMillis() + ".jpg";
                    File imageFile = new File(context.getFilesDir(), uniqueFileName);
                    OutputStream outputStream = new FileOutputStream(imageFile);
                    byte[] buffer = new byte[4 * 1024];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                    outputStream.close();
                    inputStream.close();

                    imageUrl2 = Uri.fromFile(imageFile).toString();
                    Picasso.get().load(imageUrl2).into(imageViewArticleImage);
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(context, "Failed to save image.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void updateArticle() {
        // Get the selected article from the Spinner
        String selectedTitle = (String) spinnerArticle.getSelectedItem();
        Article selectedArticle = findArticleByTitle(selectedTitle);

        // Get the updated values from the EditText fields
        final String updatedTitle = editTextTitle.getText().toString();
        final String updatedCategory = editTextCategory.getText().toString();
        final String updatedDescription = editTextDescription.getText().toString();

        // Update the selected article with the updated values
        selectedArticle.setArticle_title(updatedTitle);
        selectedArticle.setArticle_category(updatedCategory);
        selectedArticle.setArticle_description(updatedDescription);

        // Check if an image has been selected
        if (imageUrl2 != null) {
            selectedArticle.setArticle_image(imageUrl2); // Set the updated image URL
        }

        // Update the article in Firebase Realtime Database using the article ID
        articleData.updateArticle(selectedArticle.getArticle_id(), selectedArticle, new ArticleData.UpdateArticleListener() {
            @Override
            public void onArticleUpdated() {
                // Article update is successful
                // Provide feedback to the user or navigate to another screen if needed
                Toast.makeText(requireContext(), "Update Successful", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onArticleUpdateError(String errorMessage) {
                // Handle any error during the article update
                Toast.makeText(requireContext(), "Update Error", Toast.LENGTH_SHORT).show();
            }
        });
    }
    // Hàm tìm Article từ tiêu đề
    private Article findArticleByTitle(String title) {
        for (Article article : articles) {
            if (article.getArticle_title().equals(title)) {
                return article;
            }
        }
        return null; // Trả về null nếu không tìm thấy bài viết nào với tiêu đề đã chọn
    }
}
