package com.ELayang.Desa.DataModel.Surat;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ResponSuratUsaha {

    @SerializedName("kode")
    private boolean kode;

    @SerializedName("pesan")
    private String pesan;

    @SerializedName("data")
    private List<ModelSuratUsaha> data;

    public boolean isKode() {
        return kode;
    }

    public void setKode(boolean kode) {
        this.kode = kode;
    }

    public String getPesan() {
        return pesan;
    }

    public void setPesan(String pesan) {
        this.pesan = pesan;
    }

    public List<ModelSuratUsaha> getData() {
        return data;
    }

    public void setData(List<ModelSuratUsaha> data) {
        this.data = data;
    }
}
