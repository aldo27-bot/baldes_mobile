package com.ELayang.Desa.DataModel.Surat;

import com.google.gson.annotations.SerializedName;

public class ModelBedaNama {

    @SerializedName("no_pengajuan")
    private int no_pengajuan;

    @SerializedName("nama_lama")
    private String nama_lama;

    @SerializedName("nama_baru")
    private String nama_baru;

    @SerializedName("nik")
    private String nik;

    @SerializedName("alamat")
    private String alamat;

    @SerializedName("tempat_tanggal_lahir")
    private String tempat_tanggal_lahir;

    @SerializedName("pekerjaan")
    private String pekerjaan;

    @SerializedName("keterangan")
    private String keterangan;

    @SerializedName("file")
    private String file;

    @SerializedName("kode_surat")
    private String kode_surat;

    @SerializedName("username")
    private String username;

    public int getNo_pengajuan() { return no_pengajuan; }
    public void setNo_pengajuan(int no_pengajuan) { this.no_pengajuan = no_pengajuan; }

    public String getNama_lama() { return nama_lama; }
    public void setNama_lama(String nama_lama) { this.nama_lama = nama_lama; }

    public String getNama_baru() { return nama_baru; }
    public void setNama_baru(String nama_baru) { this.nama_baru = nama_baru; }

    public String getNik() { return nik; }
    public void setNik(String nik) { this.nik = nik; }

    public String getAlamat() { return alamat; }
    public void setAlamat(String alamat) { this.alamat = alamat; }

    public String getTempat_tanggal_lahir() { return tempat_tanggal_lahir; }
    public void setTempat_tanggal_lahir(String tempat_tanggal_lahir) { this.tempat_tanggal_lahir = tempat_tanggal_lahir; }

    public String getPekerjaan() { return pekerjaan; }
    public void setPekerjaan(String pekerjaan) { this.pekerjaan = pekerjaan; }

    public String getKeterangan() { return keterangan; }
    public void setKeterangan(String keterangan) { this.keterangan = keterangan; }

    public String getFile() { return file; }
    public void setFile(String file) { this.file = file; }

    public String getKode_surat() { return kode_surat; }
    public void setKode_surat(String kode_surat) { this.kode_surat = kode_surat; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
}