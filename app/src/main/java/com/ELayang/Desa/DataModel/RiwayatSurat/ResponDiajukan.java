package com.ELayang.Desa.DataModel.RiwayatSurat;

import com.ELayang.Desa.DataModel.RiwayatSurat.ModelDiajukan;
import java.util.List;

public class ResponDiajukan {
    private int kode;
    private String pesan;
    private List<ModelDiajukan> data;

    public int getKode() { return kode; }
    public String getPesan() { return pesan; }
    public List<ModelDiajukan> getData() { return data; }
}
