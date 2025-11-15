package com.ELayang.Desa.DataModel.Surat;

import com.google.gson.annotations.SerializedName;

public class ResponSktm {
    @SerializedName("status")
    private String status;

    @SerializedName("message")
    private String message;

    @SerializedName("file")
    private String file;

    public String getStatus() { return status; }
    public String getMessage() { return message; }
    public String getFile() { return file; }
}
