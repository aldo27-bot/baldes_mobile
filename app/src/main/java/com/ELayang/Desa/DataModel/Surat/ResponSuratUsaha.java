package com.ELayang.Desa.DataModel.Surat;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ResponSuratUsaha {
    private boolean status;
    private String message;
    private String no_pengajuan;

    public boolean isStatus() { return status; }
    public String getMessage() { return message; }
    public String getNo_pengajuan() { return no_pengajuan; }
}

