package com.ELayang.Desa.DataModel.Surat;

import com.ELayang.Desa.DataModel.Surat.ModelSkb;
import java.util.List;

public class ResponSkb {

    private int status;
    private String message;
    private List<ModelSkb> data;

    public int getStatus() { return status; }
    public String getMessage() { return message; }
    public List<ModelSkb> getData() { return data; }
}
