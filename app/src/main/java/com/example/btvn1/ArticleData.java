package com.example.btvn1;

import android.content.Context;
import android.util.Log;
import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.*;
import java.util.ArrayList;
import java.util.List;

public class ArticleData {

    private Context context;
    private DatabaseReference databaseReference;
    public static ArticleList data;
    private static final String TAG = "ArticleData"; // Đặt tag cho Log

    public ArticleData(Context context) {
        this.context = context;
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("articles");
    }

    public static Article getArticleFromId(long id) {
        if (data != null && data.getArticles() != null) {
            for (int i = 0; i < data.getArticles().size(); i++) {
                if (data.getArticles().get(i).getArticle_id() == id) {
                    return data.getArticles().get(i);
                }
            }
        }
        return null;
    }

    public void addInitialData() {
        // Thêm dữ liệu ban đầu vào Firebase
        // Tạo và đẩy các đối tượng Article vào Firebase ở đây

        databaseReference.child("0").setValue(new Article(0, "https://lh3.googleusercontent.com/pw/AIL4fc-BV58s_mFbXosakwhGx7lqwtD4g9zQ7_TLjLkSvx5zejJ2hzpbr6oWKD8LPs0NLJCwZpdLy_Rr5e7aDX6Q0PCAruHj8V6oh7J8K7v5AxQKbdkvANhGuafTrUUsS8hNSeHjX3rHXUF4drVko4g2rWBq=w215-h322-s-no?authuser=0", "Tự Cẩm", "","Ngôn Tình",0,"Nguyên An"));
        databaseReference.child("1").setValue(new Article(1, "https://static.8cache.com/cover/eJzLyTDW163MTDcPSAsrt4z3qUwrSIwqs3CqqDQusTCryPbNcwtKqnANNy1ONTPPKQz0yvIJMSgKz4ysSjP2TknKdAwPDw9Nt3APq8wKMUxKCg7wMwmzLTcyNNXNMDYyAgD-FB8x/ngao-the-dan-than.jpg", "Ngạo Thế Đan Thần", "A cherry is the fruit of many plants of the genus Prunus, and is a fleshy","Tiên Hiệp",0,"Tạ Văn Thành"));
        databaseReference.child("2").setValue(new Article(2, "https://imgtruyentr.staticscdn.net/2020/11/than-dao-dan-ton-1-215x322.jpg?1", "Thần Đạo Đan Tôn", "","Tiên Hiệp",0,"Lê Đan Phương"));
        databaseReference.child("3").setValue(new Article(3, "https://img.8cache.com/hot-9.jpg", "Linh Vũ Thiên Hạ", "","Tiên Hiệp",0,"Nguyên An"));
        databaseReference.child("4").setValue(new Article(4, "https://static.8cache.com/cover/eJwFwbsOQDAUANAvanApNRjQYPBoRG7F1qi3GAzo3zvnXF1r8Rsi8JO8aS81Z7d51B7U0hZbjuyoRpP0oAULtaymsrgV9MBnm_J4oq0RQ3elCA6y7sj9r8CQePBGLziUrC7AD_z0Hfw=/anh-dao-ho-phach.jpg", "Anh Đào Hổ Phách", "","Ngôn Tình",0,"Lê Đan Phương"));
        databaseReference.child("5").setValue(new Article(5, "https://lh3.googleusercontent.com/Rk6VtuGyofT8tMcl3xr1CtrTT6VkgDKmVmD7yHPKcojDf9QpFqo6DPJqMNRxrdGtMSIRUez3DJGO24sdmg=w215-h322-rw-no", "Mê Đắm", "","Ngôn Tình",0,"Nguyễn Việt Hoàng"));
        databaseReference.child("6").setValue(new Article(6, "https://static.8cache.com/cover/eJzLyTDWN3SrKg53L8nIC9HVDQ4O86o0NDILK7N0dQrNNTNwKsvIMjMwC9A1N_bMTcvzyXf1rXIpNfPzSI0PcsvNLw0Mdk4MiUiMTC2oCDKMz6zwLPH3dLQtNzI01c0wNjICAM9tHpk=/khong-phu-the-duyen.jpg", "Không Phụ Duyên", "","Ngôn Tình",0,"Tạ Văn Thành"));
        databaseReference.child("7").setValue(new Article(7, "https://lh3.googleusercontent.com/pw/AJFCJaVjuIBFnTKQ5soBnZlRWVtCxD3sg1ILwmCHYgnNBJtHdpQlmtRRAJm28EmxtxPtR3UE8bxLUMLsf_PCPFivFFj_YYYnnkXgbjPUyBdCzx1TaicW3dK17dpLz7pSoYMq0muNmrmYxWjTwey3ThHPBdgt=w215-h322-s-no?authuser=0", "Nàng Không Muốn Làm Hoàng Hậu", "","Cổ Trang",0,"Nguyễn Viết Phương"));
        databaseReference.child("8").setValue(new Article(8, "https://static.8cache.com/cover/o/eJzLyTDW1413DDYJDCosqDLM1w_LKgpLMoovjo_y1HeEAmeDcn1jt_iCkjK_iEQTC_1yQyNL3QxDSyNdz2QTIwDDkxPv/nam-an-thai-phi-truyen-ky.jpg", "Nam An Thái Phi Truyền Kỳ", "","Cổ Trang",0,"Nguyên An"));

    }

    public void updateArticleViewsInFirebase(long articleId) {
        DatabaseReference articleRef = databaseReference.child(String.valueOf(articleId));
        articleRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Article article = dataSnapshot.getValue(Article.class);
                if (article != null) {
                    long updatedViews = article.getArticle_views() + 1;
                    articleRef.child("article_views").setValue(updatedViews);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Xử lý lỗi nếu có
                Log.e(TAG, "Firebase Database Error: " + databaseError.getMessage());
            }
        });
    }



    public void fetchDataAndListenForChanges(final DataChangeListener listener) {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Duyệt qua các children của DataSnapshot và chuyển thành Article
                ArrayList<Article> articles = new ArrayList<>();
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    Article article = childSnapshot.getValue(Article.class);
                    articles.add(article);
                }

                // Sử dụng lớp trung gian để chuyển đổi thành ArticleList
                data = ArticleListConverter.convertFromArrayList(articles);

                // Gọi phương thức onDataChange của người nghe
                if (listener != null) {
                    listener.onDataChange(articles);
                }
            }

            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Xử lý lỗi nếu có
                Log.e(TAG, "Firebase Database Error: " + databaseError.getMessage());
            }
        });
    }
    public void updateArticle(long articleId, Article updatedArticle, final UpdateArticleListener listener) {
        // Tạo một DatabaseReference đến bài viết cần cập nhật
        DatabaseReference articleRef = databaseReference.child(String.valueOf(articleId));

        // Cập nhật dữ liệu của bài viết
        articleRef.setValue(updatedArticle).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    // Gọi callback onSuccess khi cập nhật thành công
                    if (listener != null) {
                        listener.onArticleUpdated();
                    }
                } else {
                    // Gọi callback onError khi xảy ra lỗi
                    if (listener != null) {
                        listener.onArticleUpdateError("Failed to update the article.");
                    }
                }
            }
        });
    }
    public void fetchArticlesByAuthor(String author, ArticleCallback callback) {
        Query query = databaseReference.orderByChild("article_author").equalTo(author);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<Article> articles = new ArrayList<>();
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    Article article = childSnapshot.getValue(Article.class);
                    articles.add(article);
                }

                if (callback != null) {
                    callback.onArticlesLoaded(articles);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                if (callback != null) {
                    callback.onError("Failed to fetch articles by author: " + databaseError.getMessage());
                }
            }
        });
    }
    public void deleteArticle(long articleId, DeleteArticleListener listener) {
        // Tạo một DatabaseReference đến bài viết cần xóa
        DatabaseReference articleRef = databaseReference.child(String.valueOf(articleId));

        // Xóa bài viết
        articleRef.removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Gọi callback onArticleDeleted khi xóa thành công
                if (listener != null) {
                    listener.onArticleDeleted();
                }
            } else {
                // Gọi callback onArticleDeleteError khi xảy ra lỗi
                if (listener != null) {
                    listener.onArticleDeleteError("Lỗi xóa bài viết");
                }
            }
        });
    }


    public interface UpdateArticleListener {
        void onArticleUpdated();

        void onArticleUpdateError(String errorMessage);
    }

    public interface DataChangeListener {
        void onDataChange(List<Article> articles);
    }
    public interface ArticleCallback {
        void onArticlesLoaded(List<Article> articleList);
        void onError(String errorMessage);
    }
    public interface DeleteArticleListener {
        void onArticleDeleted();
        void onArticleDeleteError(String errorMessage);
    }
}
