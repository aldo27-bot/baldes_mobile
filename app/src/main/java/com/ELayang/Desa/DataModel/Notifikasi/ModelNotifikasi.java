package com.ELayang.Desa.DataModel.Notifikasi;

public class ModelNotifikasi {
    private String id;
    private String username;
    private String judul;     // ✅ tambahkan ini
    private String pesan;
    private String status;
    private String tanggal;

    // Field tambahan (untuk notifikasi surat)
    private String nopengajuan;
    private String alasan;
    private String jam;
    private String tanggapan;
    private String jenis; // misal "aspirasi" atau "surat"
    private String kode;

    // ✅ Getter dan Setter Lengkap
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getJudul() { return judul; }  // ✅ baru ditambah
    public void setJudul(String judul) { this.judul = judul; }

    public String getPesan() { return pesan; }
    public void setPesan(String pesan) { this.pesan = pesan; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getTanggal() { return tanggal; }
    public void setTanggal(String tanggal) { this.tanggal = tanggal; }

    public String getNopengajuan() { return nopengajuan; }
    public void setNopengajuan(String nopengajuan) { this.nopengajuan = nopengajuan; }

    public String getAlasan() { return alasan; }
    public void setAlasan(String alasan) { this.alasan = alasan; }

    public String getJam() { return jam; }
    public void setJam(String jam) { this.jam = jam; }

    public String getTanggapan() { return tanggapan; }
    public void setTanggapan(String tanggapan) { this.tanggapan = tanggapan; }

    public String getJenis() { return jenis; }
    public void setJenis(String jenis) { this.jenis = jenis; }

    public String getKode() { return kode; }
    public void setKode(String kode) { this.kode = kode; }
}
