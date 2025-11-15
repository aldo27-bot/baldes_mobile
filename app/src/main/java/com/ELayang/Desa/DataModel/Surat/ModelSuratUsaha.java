package com.ELayang.Desa.DataModel.Surat;

import com.google.gson.annotations.SerializedName;

public class ModelSuratUsaha {

    @SerializedName("id_surat")
    private String idSurat;

    @SerializedName("username")
    private String username;

    @SerializedName("nama")
    private String nama;

    @SerializedName("nik")
    private String nik;

    @SerializedName("lokasi_usaha")
    private String lokasiUsaha;

    @SerializedName("nama_usaha")
    private String namaUsaha;

    @SerializedName("jenis_usaha")
    private String jenisUsaha;

    @SerializedName("tahun_berdiri")
    private String tahunBerdiri;

    public String getIdSurat() {
        return idSurat;
    }

    public void setIdSurat(String idSurat) {
        this.idSurat = idSurat;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getNik() {
        return nik;
    }

    public void setNik(String nik) {
        this.nik = nik;
    }

    public String getLokasiUsaha() {
        return lokasiUsaha;
    }

    public void setLokasiUsaha(String lokasiUsaha) {
        this.lokasiUsaha = lokasiUsaha;
    }

    public String getNamaUsaha() {
        return namaUsaha;
    }

    public void setNamaUsaha(String namaUsaha) {
        this.namaUsaha = namaUsaha;
    }

    public String getJenisUsaha() {
        return jenisUsaha;
    }

    public void setJenisUsaha(String jenisUsaha) {
        this.jenisUsaha = jenisUsaha;
    }

    public String getTahunBerdiri() {
        return tahunBerdiri;
    }

    public void setTahunBerdiri(String tahunBerdiri) {
        this.tahunBerdiri = tahunBerdiri;
    }
}
