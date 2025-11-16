package com.ELayang.Desa.DataModel.Surat;

import java.util.ArrayList;

public class ResponSkm {
    private boolean status;
    private String message;
    private ArrayList<ModelSKM> data;

    public boolean isStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public ArrayList<ModelSKM> getData() {
        return data;
    }
}
