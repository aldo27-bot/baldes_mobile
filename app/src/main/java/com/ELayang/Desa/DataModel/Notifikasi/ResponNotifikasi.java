package com.ELayang.Desa.DataModel.Notifikasi;

import java.util.ArrayList;
import java.util.List;

public class ResponNotifikasi {

    private int kode;          // untuk cek 1 = sukses, 0 = gagal
    private String status;     // "Tolak" atau "Selesai"
    private String alasan;     // alasan penolakan / keterangan
    private String pesan;
    private String tanggal;
    private String jam;
    private String tanggapan;
    private String nopengajuan;

    private ArrayList<ModelNotifikasi> data;
    private List<ModelNotifikasi> notifikasi;

    // === Getter & Setter ===
    public int getKode() { return kode; }
    public void setKode(int kode) { this.kode = kode; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getAlasan() { return alasan; }
    public void setAlasan(String alasan) { this.alasan = alasan; }

    public String getPesan() { return pesan; }
    public void setPesan(String pesan) { this.pesan = pesan; }

    public String getTanggal() { return tanggal; }
    public void setTanggal(String tanggal) { this.tanggal = tanggal; }

    public String getJam() { return jam; }
    public void setJam(String jam) { this.jam = jam; }

    public String getTanggapan() { return tanggapan; }
    public void setTanggapan(String tanggapan) { this.tanggapan = tanggapan; }

    public String getNopengajuan() { return nopengajuan; }
    public void setNopengajuan(String nopengajuan) { this.nopengajuan = nopengajuan; }

    public ArrayList<ModelNotifikasi> getData() { return data; }
    public void setData(ArrayList<ModelNotifikasi> data) { this.data = data; }

    public List<ModelNotifikasi> getNotifikasi() {
        return notifikasi != null ? notifikasi : data;
    }

    public void setNotifikasi(List<ModelNotifikasi> notifikasi) {
        this.notifikasi = notifikasi;
    }
}
