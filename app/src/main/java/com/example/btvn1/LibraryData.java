package com.example.btvn1;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LibraryData {
    private final DatabaseReference libraryReference;

    public LibraryData() {
        // Khởi tạo tham chiếu đến cơ sở dữ liệu Firebase
        libraryReference = FirebaseDatabase.getInstance().getReference("library");
    }

    public DatabaseReference getLibraryReference() {
        return libraryReference;
    }

    public void addToLibrary(String article_id, String userid, String date) {
        // Trước khi thêm một bài viết vào thư viện, kiểm tra xem nó đã tồn tại hay chưa
        libraryReference.orderByChild("userid").equalTo(userid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean articleExists = false;
                for (DataSnapshot itemSnapshot : dataSnapshot.getChildren()) {
                    Library libraryItem = itemSnapshot.getValue(Library.class);
                    if (libraryItem != null && libraryItem.getArticle_id().equals(article_id) && libraryItem.getUserid().equals(userid)) {
                        articleExists = true;
                        break;
                    }
                }

                if (!articleExists) {
                    // Nếu bài viết chưa tồn tại trong thư viện của người dùng, thêm nó
                    Library libraryItem = new Library(article_id, userid, date);
                    libraryReference.push().setValue(libraryItem);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Xử lý lỗi nếu cần
            }
        });
    }

    public void getLibraryIdForUser(String userid, ValueEventListener listener) {
        // Truy vấn cơ sở dữ liệu thư viện dựa trên userId để lấy danh sách bài viết
        libraryReference.orderByChild("userid").equalTo(userid).addListenerForSingleValueEvent(listener);
    }

    // Xóa dữ liệu của một userId trong library
    public void removeUserFromLibrary(String userId) {
        DatabaseReference libraryRef = FirebaseDatabase.getInstance().getReference().child("library");

        libraryRef.orderByChild("userid").equalTo(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    // Xóa nút dữ liệu của userId khỏi library
                    userSnapshot.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Xử lý lỗi nếu cần
            }
        });
    }
}

