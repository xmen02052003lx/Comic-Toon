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

public class RecycleViewAdapter extends RecyclerView.Adapter<RecycleViewAdapter.ArticleViewHolder> {

    private Context context;
    private List<Article> articles;
    private OnItemClickListener itemClickListener; // Thêm biến cho sự kiện nhấn vào mục

    public RecycleViewAdapter(Context context, List<Article> articles) {
        this.context = context;
        this.articles = articles;
    }

    // Giao diện để xử lý sự kiện nhấn vào mục
    public interface OnItemClickListener {
        void onItemClick(Article article);
    }

    // Phương thức để thiết lập sự kiện nhấn vào mục
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.itemClickListener = listener;
    }

    @NonNull
    @Override
    public ArticleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_article, parent, false);
        return new ArticleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArticleViewHolder holder, int position) {
        Article article = articles.get(position);

        // Hiển thị tiêu đề
        holder.textViewTitle.setText(article.getArticle_title());
        holder.textViewAuthor.setText("Tác giả:" + " " + article.getArticle_author());
        holder.textViewCategory.setText("Thể loại:" + " " +article.getArticle_category());
        // Hiển thị hình ảnh
        Picasso.get()
                .load(article.getArticle_image())
                .placeholder(R.drawable.placeholder_image) // Hình mặc định nếu không có hình ảnh
                .error(R.drawable.error_image) // Hình khi xảy ra lỗi tải hình ảnh
                .into(holder.imageViewArticle);

        // Xử lý sự kiện nhấn vào mục
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickListener != null) {
                    itemClickListener.onItemClick(article);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return articles.size();
    }

    public class ArticleViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewArticle;
        TextView textViewTitle;
        TextView textViewAuthor;
        TextView textViewCategory;

        public ArticleViewHolder(View itemView) {
            super(itemView);
            imageViewArticle = itemView.findViewById(R.id.imageViewArticle);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewAuthor = itemView.findViewById(R.id.textViewAuthor);
            textViewCategory = itemView.findViewById(R.id.textViewCategory);
        }
    }
}
