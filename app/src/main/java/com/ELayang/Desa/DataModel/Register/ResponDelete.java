package com.ELayang.Desa.DataModel.Register;

import java.util.ArrayList;

public class ResponDelete {
    public int kode;
    private String status;
    private String message;

    public int getKode() {
        return kode;
    }

    public void setKode(int kode) {
        this.kode = kode;
    }
    // Constructor
    public ResponDelete(String status, String message) {
        this.status = status;
        this.message = message;
    }

    // Getter dan Setter untuk status
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // Getter dan Setter untuk message
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    public ArrayList<ModelDelete> getData() {
        return data;
    }
    ArrayList<ModelDelete> data;

}
