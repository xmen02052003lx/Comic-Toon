package com.example.btvn1;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.firebase.database.*;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class UserData {

    private Context context;
    private DatabaseReference databaseReference;
    private static final String TAG = "UserData";

    public UserData(Context context) {
        this.context = context;
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("users");
    }

    public void addUser(final User user, final DataAddListener listener) {
        // Mã hóa mật khẩu trước khi thêm vào Firebase
        final String encryptedPassword = encryptPassword(user.getPassword());

        // Kiểm tra xem tên đăng nhập đã tồn tại trong cơ sở dữ liệu chưa
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean usernameExists = false;

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User existingUser = snapshot.getValue(User.class);

                    if (existingUser != null && existingUser.getUsername().equals(user.getUsername()) || existingUser.getDisplayName().equals(user.getDisplayName())) {
                        // Tên đăng nhập đã tồn tại
                        usernameExists = true;
                        break;
                    }
                }

                if (!usernameExists) {
                    // Tên đăng nhập không tồn tại, tiến hành thêm người dùng
                    int newUserId = (int) dataSnapshot.getChildrenCount() + 1;
                    user.setUserId(Integer.toString(newUserId));

                    // Sử dụng `user.getUserId()` làm tên nút
                    DatabaseReference newUserRef = databaseReference.child(Integer.toString(newUserId));

                    // Mã hóa mật khẩu trước khi lưu vào Firebase
                    user.setPassword(encryptedPassword);

                    newUserRef.setValue(user, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if (databaseError == null) {
                                listener.onDataAdded(user);
                            } else {
                                listener.onDataAddError(databaseError.getMessage());
                            }
                        }
                    });
                } else {
                    // Tên đăng nhập đã tồn tại, gửi thông báo lỗi cho người dùng
                    listener.onDataAddError("Tên đăng nhập hoặc tên người dùng đã tồn tại");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                listener.onDataAddError(databaseError.getMessage());
            }
        });
    }



    public void getUserByUsername(String username, final DataRetrieveListener listener) {
        // Lấy thông tin người dùng dựa trên tên đăng nhập
        databaseReference.orderByChild("username").equalTo(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        User user = userSnapshot.getValue(User.class);
                        listener.onDataRetrieved(user);
                        return;
                    }
                }
                listener.onDataNotExists();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                listener.onDataRetrieveError(databaseError.getMessage());
            }
        });
    }
    public void getUserByUserId(String userid, final DataRetrieveListener listener) {
        databaseReference.child("userId").equalTo(userid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    User user = dataSnapshot.getValue(User.class);
                    listener.onDataRetrieved(user);
                } else {
                    listener.onDataNotExists();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                listener.onDataRetrieveError(databaseError.getMessage());
            }
        });
    }


    public void updateUser(User user, final DataUpdateListener listener) {
        // Sử dụng username để xác định người dùng cần cập nhật
        final String username = user.getUsername();

        // Kiểm tra xem username đã tồn tại trong cơ sở dữ liệu chưa
        databaseReference.orderByChild("username").equalTo(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Lấy đường dẫn của người dùng cần cập nhật
                    DataSnapshot userSnapshot = dataSnapshot.getChildren().iterator().next();
                    String userKey = userSnapshot.getKey();

                    // Cập nhật dữ liệu của người dùng dựa trên userKey
                    databaseReference.child(userKey).setValue(user, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                            if (databaseError == null) {
                                listener.onDataUpdated(user);
                            } else {
                                listener.onDataUpdateError(databaseError.getMessage());
                            }
                        }
                    });
                } else {
                    // Người dùng không tồn tại
                    listener.onDataUpdateError("Người dùng không tồn tại");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                listener.onDataUpdateError(databaseError.getMessage());
            }
        });
    }
    // Phương thức để mã hóa mật khẩu
    private String encryptPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedPassword = md.digest(password.getBytes());

            // Chuyển đổi kết quả thành chuỗi hex
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedPassword) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }



    public interface DataAddListener {
        void onDataAdded(User user);
        void onDataAddError(String errorMessage);
    }

    public interface DataRetrieveListener {
        void onDataRetrieved(User user);
        void onDataNotExists();
        void onDataRetrieveError(String errorMessage);
    }

    public interface DataUpdateListener {
        void onDataUpdated(User user);
        void onDataUpdateError(String errorMessage);
    }
}
