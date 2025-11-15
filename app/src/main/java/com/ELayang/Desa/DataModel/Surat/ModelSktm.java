package com.ELayang.Desa.DataModel.Surat;

import com.google.gson.annotations.SerializedName;

public class ModelSktm {

    @SerializedName("nama")
    private String nama;

    @SerializedName("tempat_tanggal_lahir")
    private String tempat_tanggal_lahir;

    @SerializedName("asal_sekolah")
    private String asal_sekolah;

    @SerializedName("keperluan")
    private String keperluan;

    @SerializedName("nama_orangtua")
    private String nama_orangtua;

    @SerializedName("nik_orangtua")
    private String nik_orangtua;

    @SerializedName("alamat_orangtua")
    private String alamat_orangtua;

    @SerializedName("tempat_tanggal_lahir_orangtua")
    private String tempat_tanggal_lahir_orangtua;

    @SerializedName("pekerjaan_orangtua")
    private String pekerjaan_orangtua;

    @SerializedName("file")
    private String file;

    @SerializedName("kode_surat")
    private String kode_surat;

    @SerializedName("id_pejabat_desa")
    private String id_pejabat_desa;

    @SerializedName("username")
    private String username;

    // Getter tetap sama
    public String getNama() { return nama; }
    public String getTempat_tanggal_lahir() { return tempat_tanggal_lahir; }
    public String getAsal_sekolah() { return asal_sekolah; }
    public String getKeperluan() { return keperluan; }
    public String getNama_orangtua() { return nama_orangtua; }
    public String getNik_orangtua() { return nik_orangtua; }
    public String getAlamat_orangtua() { return alamat_orangtua; }
    public String getTempat_tanggal_lahir_orangtua() { return tempat_tanggal_lahir_orangtua; }
    public String getPekerjaan_orangtua() { return pekerjaan_orangtua; }
    public String getFile() { return file; }
    public String getKode_surat() { return kode_surat; }
    public String getId_pejabat_desa() { return id_pejabat_desa; }
    public String getUsername() { return username; }
}
