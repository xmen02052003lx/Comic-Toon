package com.example.btvn1;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;
import androidx.appcompat.app.AppCompatDelegate;

public class SettingsFragment extends PreferenceFragmentCompat {

    private SwitchPreferenceCompat nightModeSwitch;
    private SharedPreferences sharedPreferences;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.activity_settings_fragment, rootKey);

        sharedPreferences = getActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

        nightModeSwitch = findPreference("night_mode");

        sharedPreferences = getPreferenceManager().getSharedPreferences();

        nightModeSwitch.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                boolean isNightModeOn = (boolean) newValue;

                // Lưu trạng thái chế độ ban đêm vào SharedPreferences
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("night_mode", isNightModeOn);
                editor.apply();

                AppCompatDelegate.setDefaultNightMode(isNightModeOn ?
                        AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);

                return true;
            }
        });

        // Khôi phục trạng thái chế độ ban đêm từ SharedPreferences khi ứng dụng bật lại
        if (sharedPreferences != null) {
            boolean isNightModeOn = sharedPreferences.getBoolean("night_mode", false);
            AppCompatDelegate.setDefaultNightMode(isNightModeOn ?
                    AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
        }
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Preference logoutPreference = findPreference("logout");
        logoutPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                // Hiển thị hộp thoại xác nhận trước khi đăng xuất
                showLogoutConfirmationDialog();
                return true;
            }
        });
        // Bắt sự kiện khi người dùng bấm vào "Đổi mật khẩu"
        Preference changePasswordPreference = findPreference("change_password");
        changePasswordPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                // Xử lý khi người dùng bấm vào "Đổi mật khẩu"
                // Thực hiện đổi mật khẩu dựa trên userid
                changePassword();
                return true;
            }
        });
    }

    private void showLogoutConfirmationDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Xác nhận đăng xuất")
                .setMessage("Bạn có chắc chắn muốn đăng xuất?")
                .setPositiveButton("Có", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Xử lý đăng xuất ở đây, ví dụ chuyển về màn hình đăng nhập
                        logout();
                    }
                })
                .setNegativeButton("Không", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    private void logout() {
        // Xử lý đăng xuất ở đây, ví dụ chuyển về màn hình đăng nhập
        Intent intent = new Intent(requireContext(), LoginActivity.class);
        startActivity(intent);
        requireActivity().finish();

        // Xóa thông tin đăng nhập từ SharedPreferences hoặc bất kỳ trạng thái đăng nhập nào khác
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("userId"); // Xóa ID người dùng đã lưu
        editor.remove("username");
        editor.apply();
    }
    private void changePassword() {
        Intent intent = new Intent(getActivity(), ChangePasswordActivity.class);
        startActivity(intent);
    }
}
