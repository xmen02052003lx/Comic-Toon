package com.example.btvn1;
import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.squareup.picasso.Picasso;

import java.util.List;

public class CommentListAdapter extends ArrayAdapter<Comment> {
    private List<Comment> comments;
    private Context context; // Add this line to declare the context
    String displayName;


    public CommentListAdapter(Context context, List<Comment> comments, String displayName) {
        super(context, 0, comments);
        this.context = context; // Initialize the context
        this.comments = comments;
        this.displayName = displayName;
        Log.d(TAG, "User ID from ViewModel: " + displayName);
    }
    // Thêm phương thức setComments để cập nhật dữ liệu mới vào adapter
    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }
    @Override
    public int getCount() {
        return comments.size(); // Change 'commentList' to 'comments'
    }

    @Override
    public Comment getItem(int position) {
        return comments.get(position); // Change 'commentList' to 'comments'
    }

    @Override
    public long getItemId(int position) {
        // Thường sử dụng ID của mục dữ liệu nếu có
        return position;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_comment, parent, false);
        }

        TextView usernameTextView = convertView.findViewById(R.id.commentUsernameTextView);
        TextView commentTextTextView = convertView.findViewById(R.id.commentTextTextView);
        ImageView imageView = convertView.findViewById(R.id.bookCoverImageView);
        TextView textView = convertView.findViewById(R.id.bookTitleTextView);
        ImageView deleteIcon = convertView.findViewById(R.id.deleteCommentIcon);

        Comment comment = getItem(position);

        usernameTextView.setText(comment.getUserName());
        commentTextTextView.setText(comment.getCommentText());
        textView.setText(comment.getArticleTitle());
        Picasso.get()
                .load(comment.getArticleImage())
                .into(imageView);
        deleteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (displayName != null && !displayName.isEmpty()) {
                    if (comment.getUserName().equals(displayName)) {
                        // Nếu là bình luận của người dùng hiện tại, hiển thị xác nhận xóa
                        showDeleteConfirmationDialog(comment);
                    } else {
                        // Nếu không phải bình luận của người dùng hiện tại, hiển thị thông báo
                        Toast.makeText(context, "Đây không phải bình luận của bạn", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        return convertView;
    }

    private void showDeleteConfirmationDialog(final Comment comment) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Xác nhận xóa");
        builder.setMessage("Bạn chắc chắn muốn xóa bình luận này không?");

        builder.setPositiveButton("Có", (dialog, which) -> {
                    CommentData commentData = new CommentData();
                    commentData.deleteComment(comment.getCommentId());
                    notifyDataSetChanged(); // Cập nhật ListView sau khi xóa
        });

        builder.setNegativeButton("Không", (dialog, which) -> {
            // Hủy xóa bình luận
            dialog.dismiss();
        });
        builder.show();
    }
}
