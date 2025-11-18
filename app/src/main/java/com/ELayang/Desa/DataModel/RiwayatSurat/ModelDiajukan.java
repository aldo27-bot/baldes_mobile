package com.ELayang.Desa.DataModel.RiwayatSurat;

import com.google.gson.annotations.SerializedName;

public class ModelDiajukan {

    @SerializedName("id_pengajuan_surat")
    private String idPengajuanSurat;

    @SerializedName("no_pengajuan")
    private String noPengajuan;

    @SerializedName("nama")
    private String nama;

    @SerializedName("nik")
    private String nik;

    @SerializedName("tanggal")
    private String tanggal;

    @SerializedName("kode_surat")
    private String kodeSurat;

    @SerializedName("status")
    private String status;

    // Getter
    public String getIdPengajuanSurat() { return idPengajuanSurat; }
    public String getNoPengajuan() { return noPengajuan; }
    public String getNama() { return nama; }
    public String getNik() { return nik; }
    public String getTanggal() { return tanggal; }
    public String getKodeSurat() { return kodeSurat; }
    public String getStatus() { return status; }
}
