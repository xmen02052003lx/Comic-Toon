package com.example.btvn1;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CommentData {
    private DatabaseReference commentReference;

    public CommentData() {
        // Khởi tạo tham chiếu đến cơ sở dữ liệu Firebase cho bình luận
        commentReference = FirebaseDatabase.getInstance().getReference("comments");
    }

    public void addComment(String username, String commentText, String articleImage, String articleTittle) {
        // Tạo một commentId mới bằng cách lấy commentId lớn nhất + 1
        commentReference.orderByChild("commentId").limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int maxCommentId = 0;
                for (DataSnapshot itemSnapshot : dataSnapshot.getChildren()) {
                    Comment comment = itemSnapshot.getValue(Comment.class);
                    if (comment != null) {
                        int currentCommentId = Integer.parseInt(comment.getCommentId());
                        if (currentCommentId > maxCommentId) {
                            maxCommentId = currentCommentId;
                        }
                    }
                }

                // Tạo commentId mới
                String newCommentId = String.valueOf(maxCommentId + 1);

                // Tạo một đối tượng Comment mới với commentId
                Comment newComment = new Comment(newCommentId, username, commentText, articleImage, articleTittle);

                // Thêm bình luận mới vào Firebase
                commentReference.child(newCommentId).setValue(newComment);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Xử lý lỗi nếu cần
            }
        });
    }
    public void updateComment(String commentId, String newCommentText) {
        commentReference.child(commentId).child("commentText").setValue(newCommentText);
        // Cập nhật trường commentText của bình luận với commentId tương ứng
    }
    public void deleteComment(String commentId) {
        commentReference.child(commentId).removeValue();
        // Xóa bình luận với commentId tương ứng khỏi Firebase Realtime Database
    }
    // Các phương thức khác liên quan đến quản lý bình luận
}
