package com.ELayang.Desa.DataModel;

import com.google.gson.annotations.SerializedName;

public class Aspirasi {

    @SerializedName("id_aspirasi")
    private String id;

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
    public String getId() { return id; }
    public String getJudul() { return judul; }
    public String getKategori() { return kategori; }
    public String getDeskripsi() { return deskripsi; }
    public String getFoto() { return foto; }
    public String getStatus() { return status; }
    public String getTanggapan() { return tanggapan; }
    public String getTanggal() { return tanggal; }

    // Setter (opsional)
    public void setId(String id) { this.id = id; }
    public void setJudul(String judul) { this.judul = judul; }
    public void setKategori(String kategori) { this.kategori = kategori; }
    public void setDeskripsi(String deskripsi) { this.deskripsi = deskripsi; }
    public void setFoto(String foto) { this.foto = foto; }
    public void setStatus(String status) { this.status = status; }
    public void setTanggapan(String tanggapan) { this.tanggapan = tanggapan; }
    public void setTanggal(String tanggal) { this.tanggal = tanggal; }
}
