package com.ELayang.Desa.Surat;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.*;
import android.database.Cursor;

import com.ELayang.Desa.API.APIRequestData;
import com.ELayang.Desa.API.RetroServer;
import com.ELayang.Desa.DataModel.Surat.ModelSkk;
import com.ELayang.Desa.DataModel.Surat.ResponSkk;
import com.ELayang.Desa.R;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

import okhttp3.*;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SKK extends AppCompatActivity {

    private static final int PICK_FILE_REQUEST = 1;

    Spinner spJenisKelamin;
    EditText eNama, eAgama, eTTL, eAlamat, eKewarganegaraan, eKeterangan;
    Button btnKirim, btnChooseFile;
    ImageButton btnBack;
    String username, fileNameSaved = "";
    String currentNoPengajuan = null;

    ProgressDialog progressDialog;
    DatePickerDialog picker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.surat_kehilangan);

        // Ambil username dari SharedPreferences
        SharedPreferences sp = getSharedPreferences("prefLogin", Context.MODE_PRIVATE);
        username = sp.getString("username", "").trim();

        eNama = findViewById(R.id.e_nama_pelapor);
        eAgama = findViewById(R.id.e_agama);
        spJenisKelamin = findViewById(R.id.e_jenis_kelamin);
        eTTL = findViewById(R.id.e_tempat_tanggal_lahir);
        eAlamat = findViewById(R.id.e_alamat_pelapor);
        eKewarganegaraan = findViewById(R.id.e_kewarganegaraan);
        eKeterangan = findViewById(R.id.e_keterangan);

        btnKirim = findViewById(R.id.kirim);
        btnChooseFile = findViewById(R.id.btn_choose_file);
        btnBack = findViewById(R.id.btnBack);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                new String[]{"Laki-laki", "Perempuan"}
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spJenisKelamin.setAdapter(adapter);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Memproses...");
        progressDialog.setCancelable(false);

        btnBack.setOnClickListener(view -> onBackPressed());
        btnChooseFile.setOnClickListener(v -> chooseFile());
        eTTL.setOnClickListener(v -> showDatePicker());

        btnKirim.setOnClickListener(v -> {
            if(isValid()) konfirmasiKirim();
        });
    }

    private boolean isValid() {
        if (eNama.getText().toString().trim().isEmpty()) {
            eNama.setError("Nama wajib diisi");
            return false;
        }
        if (eAlamat.getText().toString().trim().isEmpty()) {
            eAlamat.setError("Alamat wajib diisi");
            return false;
        }
        if (eKeterangan.getText().toString().trim().isEmpty()) {
            eKeterangan.setError("Keterangan kehilangan wajib diisi");
            return false;
        }
        return true;
    }

    private void showDatePicker() {
        Calendar cal = Calendar.getInstance();
        picker = new DatePickerDialog(SKK.this, (view, y, m, d) -> {
            cal.set(y, m, d);
            eTTL.setText(new SimpleDateFormat("dd MMMM yyyy", new Locale("id","ID")).format(cal.getTime()));
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
        picker.show();
    }

    private void konfirmasiKirim() {
        new AlertDialog.Builder(this)
                .setTitle("Konfirmasi")
                .setMessage("Kirim surat kehilangan?")
                .setPositiveButton("Kirim", (d,w)-> kirim())
                .setNegativeButton("Batal", null)
                .show();
    }

    private void kirim() {
        progressDialog.show();
        APIRequestData api = RetroServer.konekRetrofit().create(APIRequestData.class);

        Call<ResponSkk> call = api.kirimSkk(
                rb(username),
                rb(eNama.getText().toString()),
                rb(eAgama.getText().toString()),
                rb(spJenisKelamin.getSelectedItem().toString()),
                rb(eTTL.getText().toString()),
                rb(eAlamat.getText().toString()),
                rb(eKewarganegaraan.getText().toString()),
                rb(eKeterangan.getText().toString()),
                rb("SKK"), // ✅ KODE SURAT FIXED
                getFilePart()
        );

        call.enqueue(DefaultCallback("Berhasil mengirim surat"));
    }

    private void chooseFile() {
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.setType("*/*");
        i.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(i,"Pilih File"), PICK_FILE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode==PICK_FILE_REQUEST && resultCode==RESULT_OK && data!=null){
            Uri uri = data.getData();
            if (uri != null) {
                fileNameSaved = getFileName(uri);
                saveFile(uri);
                btnChooseFile.setText("File: " + fileNameSaved);
            }
        }
    }

    @SuppressLint("Range")
    private String getFileName(Uri uri){
        Cursor cursor = getContentResolver().query(uri,null,null,null,null);
        if(cursor!=null && cursor.moveToFirst()){
            try {
                return cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
            } finally {
                cursor.close();
            }
        }
        // fallback
        String path = uri.getPath();
        if (path == null) return "";
        int cut = path.lastIndexOf('/');
        return (cut != -1) ? path.substring(cut + 1) : path;
    }

    private void saveFile(Uri uri){
        if (uri == null || fileNameSaved == null || fileNameSaved.isEmpty()) return;
        try (InputStream is = getContentResolver().openInputStream(uri);
             OutputStream os = new FileOutputStream(new File(getFilesDir(), fileNameSaved))) {

            byte[] buf = new byte[1024];
            int len;
            while((len=is.read(buf))>0) os.write(buf,0,len);

        } catch(Exception ignored){
            ignored.printStackTrace();
        }
    }

    private MultipartBody.Part getFilePart(){
        if(fileNameSaved == null || fileNameSaved.isEmpty()) return null;
        File file = new File(getFilesDir(), fileNameSaved);
        if (!file.exists()) return null;
        RequestBody rb = RequestBody.create(MediaType.parse("application/octet-stream"), file);
        return MultipartBody.Part.createFormData("file", file.getName(), rb);
    }

    private Callback<ResponSkk> DefaultCallback(String successMessage){
        return new Callback<ResponSkk>() {
            @Override public void onResponse(Call<ResponSkk> call, Response<ResponSkk> response){
                progressDialog.dismiss();
                Toast.makeText(SKK.this, successMessage, Toast.LENGTH_SHORT).show();
                finish();
            }
            @Override public void onFailure(Call<ResponSkk> call, Throwable t){
                progressDialog.dismiss();
                Toast.makeText(SKK.this, "Gagal: "+t.getMessage(), Toast.LENGTH_LONG).show();
            }
        };
    }

    private RequestBody rb(String v){
        return RequestBody.create(MediaType.parse("text/plain"), v == null ? "" : v);
    }
}
