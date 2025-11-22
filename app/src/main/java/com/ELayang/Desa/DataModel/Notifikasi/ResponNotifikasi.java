package com.ELayang.Desa.DataModel.Notifikasi;

import java.util.ArrayList;
import java.util.List;

public class ResponNotifikasi {
    private int kode;
    private String pesan;
    private ArrayList<ModelNotifikasi> data;

    public int getKode() { return kode; }
    public String getPesan() { return pesan; }
    public ArrayList<ModelNotifikasi> getData() { return data; }
}

