package com.ELayang.Desa.DataModel.Akun;

import com.google.gson.annotations.SerializedName;

public class ResponUpdate {
    private int kode;
    private String pesan;
    private Data data;

    public int getKode() { return kode; }
    public String getPesan() { return pesan; }
    public Data getData() { return data; }

    public static class Data {
        private String username;
        private String email;
        private String nama;
        private String profile_image;
        private String url_gambar_profil;

        public String getUsername() { return username; }
        public String getEmail() { return email; }
        public String getNama() { return nama; }
        public String getProfile_image() { return profile_image; }
        public String getUrl_gambar_profil() { return url_gambar_profil; }
    }
}
