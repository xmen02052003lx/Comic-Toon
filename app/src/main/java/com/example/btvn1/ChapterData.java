package com.example.btvn1;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class ChapterData {
    private DatabaseReference databaseReference;

    public ChapterData() {
        // Khởi tạo DatabaseReference
        databaseReference = FirebaseDatabase.getInstance().getReference("chapters");
    }
    // Add a new chapter with a generated chapterId

    // Thêm một chapter mới với chapterId được tự động sinh ra bằng tổng số nút + 1
    public void addChapterWithGeneratedId(String articleId, String chapterName, String chapterStory) {
        // Đầu tiên, bạn cần lấy số lượng hiện tại của các nút con trong node "chapters"
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long totalChapters = dataSnapshot.getChildrenCount();

                // Tạo một đối tượng Chapter với chapterId là tổng số nút + 1
                Chapter newChapter = new Chapter(String.valueOf(totalChapters + 1), articleId, chapterName, chapterStory);

                // Thêm chapter mới vào Firebase Realtime Database
                databaseReference.child(String.valueOf(totalChapters + 1)).setValue(newChapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Xử lý lỗi nếu cần
            }
        });
    }


    // Truy vấn Chapter theo articleId
    public void getChapterByArticleId(long articleId, ChapterCallback callback) {
        databaseReference.child(String.valueOf(articleId)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Chapter chapter = dataSnapshot.getValue(Chapter.class);
                if (chapter != null) {
                    callback.onChapterLoaded(chapter);
                } else {
                    callback.onError("Chapter not found");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onError(databaseError.getMessage());
            }
        });
    }

    // Truy vấn Chapter theo chapterId
    // Phương thức để lấy thông tin Chapter bằng ChapterId
    public void getChapterByChapterId(String chapterId, ChapterCallback callback) {
        databaseReference.child(chapterId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Chapter chapter = dataSnapshot.getValue(Chapter.class);
                if (chapter != null) {
                    callback.onChapterLoaded(chapter);
                } else {
                    callback.onError("Chapter not found");
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onError(databaseError.getMessage());
            }
        });
    }


    // Cập nhật thông tin của một Chapter
    public void updateChapter(String chapterId, String chapterName, String chapterStory) {
        DatabaseReference chapterRef = databaseReference.child(chapterId);
        chapterRef.child("chapterName").setValue(chapterName);
        chapterRef.child("chapterStory").setValue(chapterStory);
    }

    public void addInitialData() {
        // Thêm dữ liệu ban đầu vào Firebase
        // Tạo và đẩy các đối tượng Chapter vào Firebase ở đây

        Chapter chapter1 = new Chapter("1", "0", "Chương 1: Đêm", "Lúc này đã là đầu hạ, bên ngoài trời vừa mới hoàn toàn tối xuống, bóng đêm mờ ảo bao phủ khuôn mặt của thiếu nữ, mượn ánh nến trên bàn, lờ mờ có thể thấy rõ diện mạo của thiếu nữ trong trường.");
        Chapter chapter2 = new Chapter("2", "0", "Chương 2: Tai nghe là giả", "Trước đó không lâu nàng phân phó A Man uống rượu cùng bà tử quản chìa khóa nhị môn, đợi khi bà tử uống đã nhiều, thì thừa cơ tìm chìa khoá rồi ấn mấy cái lên mấy cục xà bông thơm đã chuẩn bị sẵn, cầm ra bên ngoài đánh mấy cái chìa khoá mới.");
        Chapter chapter3 = new Chapter("3","0","Chương 3: Cứu người", "Khương Tự cũng không dám trì hoãn, xách theo tay nải chạy đến ô đình cỏ tranh ( Cái đình giống cái ô / dù lợp bằng cỏ tranh , giống cái chòi tranh ý ) cách đó không xa, lấy ra túi nước mở nắp ra rồi hắt lên mái cỏ, sau đó lui về sau, châm lửa rồi ném lên mái cỏ một cái, cỏ tranh tẩm dầu cải lập tức bị bén lửa, rất nhanh toàn bộ ô đình cỏ tranh liền bị ngọn lửa nuốt trọn.");

        databaseReference.child("1").setValue(chapter1);
        databaseReference.child("2").setValue(chapter2);
        databaseReference.child("3").setValue(chapter3);
    }
    public void deleteChapter(String chapterId, DeleteChapterListener listener) {
        // Create a DatabaseReference to the chapter you want to delete
        DatabaseReference chapterRef = FirebaseDatabase.getInstance().getReference("chapters").child(chapterId);

        // Check if the chapter exists
        chapterRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Chapter exists, proceed with deletion
                    chapterRef.removeValue().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Chapter deleted successfully
                            if (listener != null) {
                                listener.onChapterDeleted();
                            }
                        } else {
                            // Error occurred during deletion
                            if (listener != null) {
                                listener.onChapterDeleteError("Failed to delete the chapter.");
                            }
                        }
                    });
                } else {
                    // Chapter does not exist, handle accordingly
                    if (listener != null) {
                        listener.onChapterDeleteError("Chapter not found or already deleted.");
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle database error
                if (listener != null) {
                    listener.onChapterDeleteError("Database error: " + databaseError.getMessage());
                }
            }
        });
    }

    public interface DeleteChapterListener {
        void onChapterDeleted();

        void onChapterDeleteError(String errorMessage);
    }
    public interface ChapterCallback {
        void onChaptersLoaded(List<Chapter> chapterList);
        void onChapterLoaded(Chapter chapter);
        void onError(String errorMessage);
    }
}
