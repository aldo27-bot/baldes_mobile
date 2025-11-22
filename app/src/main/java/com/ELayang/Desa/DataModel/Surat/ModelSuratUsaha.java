package com.ELayang.Desa.DataModel.Surat;

import com.google.gson.annotations.SerializedName;

public class ModelSuratUsaha {

    @SerializedName("id_surat")
    private String idSurat;

    @SerializedName("username")
    private String username;

    @SerializedName("nama")
    private String nama;

    // 🛠️ TAMBAHAN: Field Alamat Pemohon, sesuai input di Activity
    @SerializedName("alamat")
    private String alamat;

    // 🛠️ TAMBAHAN: Field Tempat/Tanggal Lahir (TTL), sesuai input di Activity
    @SerializedName("tempat_tanggal_lahir")
    private String tempatTanggalLahir;

    @SerializedName("nik")
    private String nik;

    @SerializedName("lokasi_usaha")
    private String lokasiUsaha;

    @SerializedName("keterangan_usaha")
    private String keterangan_usaha;

    @SerializedName("nama_usaha")
    private String namaUsaha;

    @SerializedName("jenis_usaha")
    private String jenisUsaha;

    @SerializedName("tahun_berdiri")
    private String tahunBerdiri;

    // --- Getter dan Setter ---

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

    // Getter dan Setter untuk Alamat
    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    // Getter dan Setter untuk Tempat/Tanggal Lahir
    public String getTempatTanggalLahir() {
        return tempatTanggalLahir;
    }

    public void setTempatTanggalLahir(String tempatTanggalLahir) {
        this.tempatTanggalLahir = tempatTanggalLahir;
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

    public String getKeteranganUsaha() {
        return keterangan_usaha;
    }

    public void setKeteranganUsaha(String keterangan_usaha) { this.keterangan_usaha = keterangan_usaha; }

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