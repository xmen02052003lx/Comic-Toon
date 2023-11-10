package com.example.btvn1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ArticleAdapter extends BaseAdapter {

    private ArrayList<Article> articleList;
    private Context context;

    public ArticleAdapter(Context context, ArrayList<Article> articleList) {
        this.context = context;
        this.articleList = articleList;
    }


    @Override
    public int getCount() {
        return articleList.size();
    }

    @Override
    public Object getItem(int position) {
        return articleList.get(position);
    }

    @Override
    public long getItemId(int position) {
        // Chú ý: Điều này không cần thiết nếu bạn không sử dụng ID cho mục
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.article_disp_tpl, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.ivPhoto = convertView.findViewById(R.id.imv_photo);
            viewHolder.tvCaption = convertView.findViewById(R.id.tv_title);
            viewHolder.tvCategory = convertView.findViewById(R.id.tv_category);
            viewHolder.tvView = convertView.findViewById(R.id.tv_views);
            viewHolder.tvAuthor = convertView.findViewById(R.id.tv_author);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Article article = articleList.get(position);

        // Sử dụng Picasso để tải ảnh và hiển thị
        Picasso.get()
                .load(article.getArticle_image())
                .resize(300, 400)
                .centerCrop()
                .into(viewHolder.ivPhoto);

        viewHolder.tvCaption.setText(article.getArticle_title());
        viewHolder.tvCategory.setText("Thể loại:" + " " + article.getArticle_category());
        viewHolder.tvView.setText(article.getArticle_views() + " Views");
        viewHolder.tvAuthor.setText("Tác giả:" + " " + article.getArticle_author());

        return convertView;
    }

    private static class ViewHolder {
        ImageView ivPhoto;
        TextView tvCaption;
        TextView tvCategory;
        TextView tvView;
        TextView tvAuthor;
    }
}
