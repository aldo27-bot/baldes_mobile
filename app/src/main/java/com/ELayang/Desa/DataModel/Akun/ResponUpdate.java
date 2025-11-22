package com.ELayang.Desa.DataModel.Akun;

import com.google.gson.annotations.SerializedName;

public class ResponUpdate {
    private int kode;
    private String pesan;
    private Data data; // Data bisa berisi nama, email, profile_image, url_gambar_profil

    // getter
    public int getKode() { return kode; }
    public String getPesan() { return pesan; }
    public Data getData() { return data; }

    public class Data {
        private String nama;
        private String email;
        private String username;
        private String profile_image;
        private String url_gambar_profil;

        public String getNama() { return nama; }
        public String getEmail() { return email; }
        public String getUsername() { return username; }
        public String getProfileImage() { return profile_image; }
        public String getUrlGambarProfil() { return url_gambar_profil; }
    }
}
