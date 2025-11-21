package com.ELayang.Desa.DataModel.Akun;

import com.google.gson.annotations.SerializedName;

public class ResponUpdate {

    @SerializedName("kode")
    private int kode;

    @SerializedName("pesan")
    private String pesan;

    @SerializedName("profile_image")
    private String profileImage;

    public ResponUpdate() {}

    public ResponUpdate(int kode, String pesan) {
        this.kode = kode;
        this.pesan = pesan;
    }

    public int getKode() {
        return kode;
    }

    public void setKode(int kode) {
        this.kode = kode;
    }

    public String getPesan() {
        return pesan;
    }

    public void setPesan(String pesan) {
        this.pesan = pesan;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    @Override
    public String toString() {
        return "ResponUpdate{" +
                "kode=" + kode +
                ", pesan='" + pesan + '\'' +
                ", profileImage='" + profileImage + '\'' +
                '}';
    }
}
