package com.ELayang.Desa.DataModel;

import com.google.gson.annotations.SerializedName;

public class Aspirasi {
    @SerializedName("id_aspirasi")
    private String idAspirasi;

    @SerializedName("judul")
    private String judul;

    @SerializedName("kategori")
    private String kategori;

    @SerializedName("deskripsi")
    private String deskripsi;

    @SerializedName("foto")
    private String foto;

    @SerializedName("status")
    private String status;

    @SerializedName("tanggapan")
    private String tanggapan;

    @SerializedName("tanggal")
    private String tanggal;

    // Getter
    public String getIdAspirasi() { return idAspirasi; }
    public String getJudul() { return judul; }
    public String getKategori() { return kategori; }
    public String getDeskripsi() { return deskripsi; }
    public String getFoto() { return foto; }
    public String getStatus() { return status; }
    public String getTanggapan() { return tanggapan; }
    public String getTanggal() { return tanggal; }
}
