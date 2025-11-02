package com.ELayang.Desa.DataModel;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class AspirasiResponse {

    // menyesuaikan dengan output JSON PHP
    @SerializedName("kode")
    private int kode;

    @SerializedName("pesan")
    private String pesan;

    @SerializedName("aspirasi")
    private List<Aspirasi> aspirasi;

    // Getter
    public int getKode() {
        return kode;
    }

    public String getPesan() {
        return pesan;
    }

    public List<Aspirasi> getAspirasi() {
        return aspirasi;
    }
}
