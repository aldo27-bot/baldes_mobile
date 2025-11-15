package com.ELayang.Desa.DataModel.Surat;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ResponSktm {
    @SerializedName("kode")
    private int kode;

    @SerializedName("pesan")
    private String pesan;

    @SerializedName("data")
    private List<ModelSktm> data;

    public int getKode() { return kode; }
    public String getPesan() { return pesan; }
    public List<ModelSktm> getData() { return data; }
}
