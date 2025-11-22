package com.ELayang.Desa.DataModel.Akun;

import com.google.gson.annotations.SerializedName;

public class ResponFotoProfil {
    private int kode;
    private String username;
    private String nama;
    private String email;
    private String profile_image;
    private String url_gambar_profil; // tambahkan ini

    // getter
    public int getKode() { return kode; }
    public String getUsername() { return username; }
    public String getNama() { return nama; }
    public String getEmail() { return email; }
    public String getProfile_image() { return profile_image; }
    public String getUrl_gambar_profil() { return url_gambar_profil; } // tambahkan getter ini
}
