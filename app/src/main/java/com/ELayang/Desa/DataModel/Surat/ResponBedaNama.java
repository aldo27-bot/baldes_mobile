package com.ELayang.Desa.DataModel.Surat;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ResponBedaNama {

    @SerializedName("kode")
    private int kode;

    @SerializedName("pesan")
    private String pesan;

    @SerializedName("data")
    private List<ModelBedaNama> data;

    // --- GETTER ---

    public int getKode() {
        return kode;
    }

    public String getPesan() {
        return pesan;
    }

    public List<ModelBedaNama> getData() {
        return data;
    }

    public boolean isKode() {
        return kode == 1;
    }
}