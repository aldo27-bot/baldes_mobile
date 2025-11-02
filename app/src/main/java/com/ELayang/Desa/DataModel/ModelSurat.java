package com.ELayang.Desa.DataModel;

public class ModelSurat {

    String kode_surat;
    String keterangan;

    // 🔹 Constructor kosong (Wajib ditambah)
    public ModelSurat() {
    }

    // 🔹 Constructor dengan parameter
    public ModelSurat(String kodeSurat, String keterangan) {
        this.kode_surat = kodeSurat;
        this.keterangan = keterangan;
    }

    public String getKode_surat() {
        return kode_surat;
    }

    public void setKode_surat(String kode_surat) {
        this.kode_surat = kode_surat;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }
}
