package com.ELayang.Desa.DataModel.Register;

import java.util.ArrayList;

public class ResponRegister1 {
    public int kode;
    String Pesan;
    public String otp; // ← DITAMBAHKAN DI SINI

    ArrayList<ModelRegister1> data;

    public int getKode() {
        return kode;
    }

    public void setKode(int kode) {
        this.kode = kode;
    }

    public String getPesan() {
        return Pesan;
    }

    public void setPesan(String pesan) {
        Pesan = pesan;
    }

    public String getOtp() {     // ← Getter
        return otp;
    }

    public void setOtp(String otp) {     // ← Setter
        this.otp = otp;
    }

    public ArrayList<ModelRegister1> getData() {
        return data;
    }

    public void setData(ArrayList<ModelRegister1> data) {
        this.data = data;
    }
}
