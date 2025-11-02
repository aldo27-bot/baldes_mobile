package com.ELayang.Desa.DataModel.Akun;

public class ResponFotoProfil {
    // Field sesuai dengan respons JSON dari API
    private boolean kode;       // Menyimpan nilai 'kode' (true/false)
    private String pesan;       // Menyimpan pesan kesalahan atau informasi
    private String image_url;   // Menyimpan URL gambar profil (jika ada)

    // Getter dan Setter
    public boolean isKode() {
        return kode;
    }
    public void setKode(boolean kode) {
        this.kode = kode;
    }
    public String getPesan() {
        return pesan;
    }
    public void setPesan(String pesan) {
        this.pesan = pesan;
    }
    public String getImageUrl() {
        return image_url;
    }
    public void setImageUrl(String image_url) {
        this.image_url = image_url;
    }
}
