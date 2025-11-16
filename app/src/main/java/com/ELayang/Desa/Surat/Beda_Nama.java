package com.ELayang.Desa.Surat;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

// Import yang diperlukan untuk API dan File Handling
import com.ELayang.Desa.API.APIRequestData;
import com.ELayang.Desa.API.RetroServer;
import com.ELayang.Desa.DataModel.Surat.ResponBedaNama;
import com.ELayang.Desa.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Beda_Nama extends AppCompatActivity {

    // Konstanta untuk request file
    private static final int PICK_FILE_REQUEST = 1;

    // Komponen UI
    EditText etNamaLama, etNamaBaru, etNIK, etTTL, etPekerjaan, etAlamat, etKeterangan;
    Button btnKirim;
    ImageButton btnBack;
    TextView btnChooseFile, tvNamaFile;

    // Variabel data
    String username;
    String fileNameSaved = ""; // Nama file yang disimpan di internal storage
    Uri fileUri = null; // URI file yang dipilih

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.surat_beda_nama); // Pastikan ini layout yang benar

        // Ambil username dari SharedPreferences
        SharedPreferences sp = getSharedPreferences("prefLogin", Context.MODE_PRIVATE);
        username = sp.getString("username", "").trim();

        // Inisialisasi UI
        etNamaLama = findViewById(R.id.etNamaLama);
        etNamaBaru = findViewById(R.id.etNamaBaru);
        etNIK = findViewById(R.id.etNIK);
        etTTL = findViewById(R.id.etTTL);
        etPekerjaan = findViewById(R.id.etPekerjaan);
        etAlamat = findViewById(R.id.etAlamat);
        etKeterangan = findViewById(R.id.etKeterangan);
        btnKirim = findViewById(R.id.btnKirim);
        btnBack = findViewById(R.id.btnBack);

        // Komponen File
        btnChooseFile = findViewById(R.id.btnChooseFile);
        tvNamaFile = findViewById(R.id.tvNamaFile);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Memproses...");
        progressDialog.setCancelable(false);

        // Listener
        btnBack.setOnClickListener(view -> onBackPressed());

        btnChooseFile.setOnClickListener(v -> chooseFile());

        btnKirim.setOnClickListener(v -> {
            if(isValid()) konfirmasiKirim();
        });
    }

    private boolean isValid(){
        // Validasi Username (Wajib)
        if(TextUtils.isEmpty(username)){
            Toast.makeText(this, "Username tidak ditemukan. Harap login ulang.", Toast.LENGTH_LONG).show();
            return false;
        }

        // Validasi Semua Field Text Wajib
        if(etNamaLama.getText().toString().trim().isEmpty()){
            etNamaLama.setError("Nama Lama wajib diisi");
            return false;
        }
        if(etNamaBaru.getText().toString().trim().isEmpty()){
            etNamaBaru.setError("Nama Baru wajib diisi");
            return false;
        }
        if(etNIK.getText().toString().trim().isEmpty()){
            etNIK.setError("NIK wajib diisi");
            return false;
        }
        if(etAlamat.getText().toString().trim().isEmpty()){
            etAlamat.setError("Alamat wajib diisi");
            return false;
        }
        if(etTTL.getText().toString().trim().isEmpty()){
            etTTL.setError("Tempat/Tanggal Lahir wajib diisi");
            return false;
        }
        if(etPekerjaan.getText().toString().trim().isEmpty()){
            etPekerjaan.setError("Pekerjaan wajib diisi");
            return false;
        }
        if(etKeterangan.getText().toString().trim().isEmpty()){
            etKeterangan.setError("Keterangan wajib diisi");
            return false;
        }
        // File bersifat opsional, tidak perlu validasi
        return true;
    }

    private void konfirmasiKirim(){
        new AlertDialog.Builder(this)
                .setTitle("Konfirmasi")
                .setMessage("Kirim pengajuan Surat Beda Nama?")
                .setPositiveButton("Kirim", (d,w)-> kirimDataPengajuan())
                .setNegativeButton("Batal",null)
                .show();
    }

    private void kirimDataPengajuan(){
        progressDialog.show();
        APIRequestData api = RetroServer.konekRetrofit().create(APIRequestData.class);

        // Ambil data teks
        String namaLama = etNamaLama.getText().toString().trim();
        String namaBaru = etNamaBaru.getText().toString().trim();
        String nik = etNIK.getText().toString().trim();
        // Ganti nama variabel lokal 'ttl' menjadi 'tempat_tanggal_lahir' agar konsisten
        String tempat_tanggal_lahir = etTTL.getText().toString().trim();
        String pekerjaan = etPekerjaan.getText().toString().trim();
        String alamat = etAlamat.getText().toString().trim();
        String keterangan = etKeterangan.getText().toString().trim();
        String kodeSurat = "SBN";

        Call<ResponBedaNama> call = api.bedaNama(
                rb(username),
                rb(kodeSurat),
                rb(namaLama),
                rb(namaBaru),
                rb(nik),
                rb(alamat),
                rb(tempat_tanggal_lahir), // Kirim data TTL dengan key yang benar
                rb(pekerjaan),
                rb(keterangan),
                getFilePart()
        );

        call.enqueue(DefaultCallback("Pengajuan Surat Beda Nama berhasil dikirim!"));
    }

    private void chooseFile(){
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.setType("*/*");
        i.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(i,"Pilih File"), PICK_FILE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        if(requestCode==PICK_FILE_REQUEST && resultCode==RESULT_OK && data!=null){
            fileUri = data.getData();
            if(fileUri != null) {
                fileNameSaved = getFileName(fileUri);
                // 1. Simpan salinan file ke internal storage
                saveFile(fileUri);
                // 2. Update UI
                tvNamaFile.setText(fileNameSaved);
                Toast.makeText(this, "File dipilih: " + fileNameSaved, Toast.LENGTH_SHORT).show();
            }
        } else {
            // Jika user membatalkan, reset
            fileUri = null;
            fileNameSaved = "";
            tvNamaFile.setText("Tidak ada file yang dipilih");
        }
        super.onActivityResult(requestCode,resultCode,data);
    }

    @SuppressLint("Range")
    private String getFileName(Uri uri){
        Cursor cursor = getContentResolver().query(uri,null,null,null,null);
        if(cursor!=null && cursor.moveToFirst()){
            return cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
        }
        return null;
    }

    private void saveFile(Uri uri){
        // Menjaga agar file bisa diakses oleh OkHttp/Retrofit
        try(InputStream is = getContentResolver().openInputStream(uri);
            OutputStream os = new FileOutputStream(new File(getFilesDir(), fileNameSaved))){

            byte[] buf = new byte[1024];
            int len;
            while((len=is.read(buf))>0) os.write(buf,0,len);

        } catch(Exception ignored){
            Toast.makeText(this, "Gagal menyimpan salinan file: " + ignored.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private MultipartBody.Part getFilePart(){
        // File OPSIONAL: Jika tidak ada file, kirim NULL
        if(fileNameSaved.isEmpty() || fileUri == null) {
            return null;
        }
        File file = new File(getFilesDir(), fileNameSaved);

        // Pastikan file tersebut ada
        if (!file.exists()) {
            Toast.makeText(this, "Error: File tidak ditemukan di penyimpanan internal.", Toast.LENGTH_LONG).show();
            return null;
        }

        // Key "file" HARUS SAMA dengan $_FILES['file'] di PHP
        RequestBody rb = RequestBody.create(MediaType.parse("application/octet-stream"), file);
        return MultipartBody.Part.createFormData("file", file.getName(), rb);
    }

    // --- UTILITY METHODS ---
    private Callback<ResponBedaNama> DefaultCallback(String successMessage){
        return new Callback<ResponBedaNama>() {
            @Override public void onResponse(Call<ResponBedaNama> call, Response<ResponBedaNama> response){
                progressDialog.dismiss();
                // Opsional: Hapus file lokal setelah berhasil dikirim
                if(fileNameSaved!=null && !fileNameSaved.isEmpty()) {
                    new File(getFilesDir(), fileNameSaved).delete();
                }

                if(response.isSuccessful() && response.body() != null && response.body().isKode()){
                    Toast.makeText(Beda_Nama.this, successMessage, Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    String errorMsg = response.body() != null ? response.body().getPesan() : "Respon server tidak valid.";
                    Toast.makeText(Beda_Nama.this, "Gagal: " + errorMsg, Toast.LENGTH_LONG).show();
                }
            }
            @Override public void onFailure(Call<ResponBedaNama> call, Throwable t){
                progressDialog.dismiss();
                // Opsional: Hapus file lokal jika terjadi kegagalan koneksi
                if(fileNameSaved!=null && !fileNameSaved.isEmpty()) {
                    new File(getFilesDir(), fileNameSaved).delete();
                }
                // Kemungkinan besar error dari IllegalStateException karena output bocor dari PHP/Koneksi.php
                Toast.makeText(Beda_Nama.this, "Gagal Koneksi: "+t.getMessage(), Toast.LENGTH_LONG).show();
            }
        };
    }

    private RequestBody rb(String v){
        return RequestBody.create(MediaType.parse("text/plain"), v == null ? "" : v);
    }
}