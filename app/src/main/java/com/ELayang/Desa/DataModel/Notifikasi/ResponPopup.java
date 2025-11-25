package com.ELayang.Desa.DataModel.Notifikasi;

public class ResponPopup {
    private int kode;
    private String nopengajuan;
    private String status;
    private String alasan;
    private String tanggal;
    private String jam;
    private String pesan;
    private String jenis;

    public int getKode() { return kode; }
    public String getNopengajuan() { return nopengajuan; }
    public String getStatus() { return status; }
    public String getAlasan() { return alasan; }
    public String getTanggal() { return tanggal; }
    public String getJam() { return jam; }
    public String getPesan() { return pesan; }

    public String getJenis() { return jenis; }
    public void setJenis(String jenis) { this.jenis = jenis; }
}

