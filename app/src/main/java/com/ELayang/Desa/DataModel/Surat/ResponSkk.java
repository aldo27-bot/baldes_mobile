package com.ELayang.Desa.DataModel.Surat;

import java.util.ArrayList;

public class ResponSkk {

    private boolean kode;
    private String pesan;
    private ArrayList<ModelSkk> data;

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

    public ArrayList<ModelSkk> getData() {
        return data;
    }

    public void setData(ArrayList<ModelSkk> data) {
        this.data = data;
    }
}
