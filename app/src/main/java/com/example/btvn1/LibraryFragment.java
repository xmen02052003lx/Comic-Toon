package com.example.btvn1;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.*;
import java.util.ArrayList;
import java.util.List;

public class LibraryFragment extends Fragment {

    private RecyclerView recyclerView;
    private LibraryAdapter libraryAdapter;
    private List<Article> libraryItemList = new ArrayList<>();
    private String userId; // User ID obtained from the login
    private List<Long> selectedArticleIds; // List of selected article IDs from HomeFragment
    private ArticleData articleData;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize ViewModel and obtain user ID and selected article IDs
        SharedViewModel viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        selectedArticleIds = viewModel.getSelectedArticles().getValue();

        // Retrieve user ID from SharedPreferences
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        userId = sharedPreferences.getString("userId", null);
        Log.d(TAG, "User ID from ViewModel: " + selectedArticleIds + userId);


        // Initialize ArticleData for fetching article details
        articleData = new ArticleData(requireContext());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_library_fragment, container, false);
        // Initialize RecyclerView and its adapter
        recyclerView = view.findViewById(R.id.recyclerView);
        libraryAdapter = new LibraryAdapter(getActivity(), libraryItemList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(libraryAdapter);

        // Fetch user's library from Firebase
        fetchUserLibrary();
        // Find the reset button
        libraryAdapter.notifyDataSetChanged();
        Button resetButton = view.findViewById(R.id.button1);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetLibrary();
            }
        });
        return view;
    }

    private void resetLibrary() {
        if (userId != null) {
            LibraryData libraryData = new LibraryData();
            libraryData.removeUserFromLibrary(userId);

            // Xóa dữ liệu của userId trong ViewModel
            SharedViewModel viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
            viewModel.resetData();
            libraryItemList.clear();
            libraryAdapter.notifyDataSetChanged();
        }
    }


    private void fetchUserLibrary() {
        LibraryData libraryData = new LibraryData();

        libraryData.getLibraryIdForUser(userId, new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                libraryItemList.clear();

                for (DataSnapshot itemSnapshot : dataSnapshot.getChildren()) {
                    Library libraryItem = itemSnapshot.getValue(Library.class);
                    if (libraryItem != null) {
                        long articleId = Long.parseLong(libraryItem.getArticle_id());
                        // Use ArticleData to get article details
                        Article article = articleData.getArticleFromId(articleId);
                        if (article != null) {
                            libraryItemList.add(article);
                        }
                    }
                }

                libraryAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error if needed
            }
        });
    }
}
