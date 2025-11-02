package com.ELayang.Desa.aspirasi;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefManager {

    private static SharedPrefManager instance;
    private SharedPreferences sp;
    private static final String PREF_NAME = "MyPrefs";

    private SharedPrefManager(Context context) {
        sp = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized SharedPrefManager getInstance(Context context) {
        if (instance == null) {
            instance = new SharedPrefManager(context.getApplicationContext());
        }
        return instance;
    }

    // Simpan data user saat login
    public void saveUserData(String username, String nama, String email, String profileImage) {
        sp.edit()
                .putString("USERNAME", username)
                .putString("NAMA", nama)
                .putString("EMAIL", email)
                .putString("PROFILE_IMAGE", profileImage)
                .apply();
    }

    // Ambil username user
    public String getUsername() {
        return sp.getString("USERNAME", null);
    }

    // Ambil nama user
    public String getNama() {
        return sp.getString("NAMA", null);
    }

    // Ambil email user
    public String getEmail() {
        return sp.getString("EMAIL", null);
    }

    // Ambil profile image user
    public String getProfileImage() {
        return sp.getString("PROFILE_IMAGE", null);
    }

    // Cek apakah user sudah login
    public boolean isLoggedIn() {
        return getUsername() != null;
    }

    // Logout / hapus data user
    public void clear() {
        sp.edit().clear().apply();
    }
}
