package com.ELayang.Desa.DataModel.Notifikasi;

import java.util.ArrayList;

public class ModelNotifikasi {

    private String nopengajuan;
    private String kode;
    private String tanggal;
    private String status;
    private String alasan;
    private String tanggapan;

    private int id;
    private int id_aspirasi;
    private String pesan;
    private String judul;

    public int getId() { return id; }
    public int getId_aspirasi() { return id_aspirasi; }

    public String getPesan() { return pesan; }

    public String getTanggal() { return tanggal; }
    public String getJudul() { return judul; }
    public String getTanggapan() { return tanggapan; }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNopengajuan() {
        return nopengajuan;
    }

    public void setNopengajuan(String nopengajuan) {
        this.nopengajuan = nopengajuan;
    }

    public String getKode() {
        return kode;
    }

    public void setKode(String kode) {
        this.kode = kode;
    }


    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public String getAlasan() {
        return alasan;
    }

    public void setAlasan(String alasan) {
        this.alasan = alasan;
    }
}
