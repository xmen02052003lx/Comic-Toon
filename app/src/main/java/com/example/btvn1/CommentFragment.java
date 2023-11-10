package com.example.btvn1;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class CommentFragment extends Fragment {
    private ListView listView;
    private CommentListAdapter adapter; // Sử dụng adapter tùy chỉnh
    private String displayName;
    private String userId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_comment_fragment, container, false);

        listView = view.findViewById(R.id.commentListView);

        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        userId = sharedPreferences.getString("userId", null);

        // Sử dụng userId để truy vấn database users và lấy displayName
        FirebaseDatabase.getInstance().getReference("users").child(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        if (user != null) {
                            displayName = user.getDisplayName();
                            // Ngay sau khi bạn có displayName, bạn có thể thiết lập nó trong adapter của bạn.
                            adapter = new CommentListAdapter(requireContext(), new ArrayList<>(), displayName);
                            listView.setAdapter(adapter);

                            // Gọi phương thức để tải toàn bộ dữ liệu của bình luận
                            loadAllComments();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Xử lý lỗi nếu cần
                    }
                });

        return view;
    }

    // Phương thức để tải toàn bộ dữ liệu của bình luận
    private void loadAllComments() {
        // Lắng nghe sự thay đổi trên nút "comments" trong Firebase Database
        FirebaseDatabase.getInstance().getReference("comments").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Comment> commentList = new ArrayList<>();
                for (DataSnapshot commentSnapshot : dataSnapshot.getChildren()) {
                    Comment comment = commentSnapshot.getValue(Comment.class);
                    if (comment != null) {
                        commentList.add(comment);
                    }
                }

                // Cập nhật dữ liệu mới vào adapter và thông báo cho nó cần cập nhật giao diện
                adapter.setComments(commentList);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Xử lý lỗi nếu cần
            }
        });
    }
}
