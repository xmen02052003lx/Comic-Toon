package com.example.btvn1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class LibraryAdapter extends RecyclerView.Adapter<LibraryAdapter.ViewHolder> {
    private Context context;
    private List<Article> libraryItemList;

    public LibraryAdapter(Context context, List<Article> libraryItemList) {
        this.context = context;
        this.libraryItemList = libraryItemList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_article, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Article libraryItem = libraryItemList.get(position);

        // Customize this part to display the library item's details
        holder.titleTextView.setText(libraryItem.getArticle_title());
        // Hiển thị hình ảnh
        Picasso.get()
                .load(libraryItem.getArticle_image())
                .placeholder(R.drawable.placeholder_image) // Hình mặc định nếu không có hình ảnh
                .error(R.drawable.error_image) // Hình khi xảy ra lỗi tải hình ảnh
                .into(holder.imageViewArticle);
    }

    @Override
    public int getItemCount() {
        return libraryItemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        ImageView imageViewArticle;

        public ViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.textViewTitle);
            imageViewArticle = itemView.findViewById(R.id.imageViewArticle);
        }
    }
}
