package com.ELayang.Desa.DataModel.Surat;

import com.google.gson.annotations.SerializedName;

public class ResponSktm {
    @SerializedName("kode")
    private int kode;

    @SerializedName("pesan")
    private String pesan;

    @SerializedName("file")
    private String file;

    public int getKode() { return kode; }
    public String getPesan() { return pesan; }
    public String getFile() { return file; }
}
