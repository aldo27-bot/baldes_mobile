package com.ELayang.Desa.DataModel.Notifikasi;

import java.util.ArrayList;

public class ResponNotifikasiAspirasi {
    private int kode;
    private String pesan;
    private ArrayList<ModelNotifikasiAspirasi> data;

    public int getKode() { return kode; }
    public void setKode(int kode) { this.kode = kode; }

    public String getPesan() { return pesan; }
    public void setPesan(String pesan) { this.pesan = pesan; }

    public ArrayList<ModelNotifikasiAspirasi> getData() { return data; }
    public void setData(ArrayList<ModelNotifikasiAspirasi> data) { this.data = data; }


}
