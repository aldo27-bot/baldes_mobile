package com.ELayang.Desa.DataModel.Surat;

import com.google.gson.annotations.SerializedName;

public class ResponDomisili {

    @SerializedName("status")
    private String status;

    @SerializedName("message")
    private String pesan;

    public String getStatus() {
        return status;
    }

    public String getPesan() {
        return pesan;
    }
}
