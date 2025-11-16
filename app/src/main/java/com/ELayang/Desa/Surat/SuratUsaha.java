package com.ELayang.Desa.Surat;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.widget.*;

import com.ELayang.Desa.API.APIRequestData;
import com.ELayang.Desa.API.RetroServer;
import com.ELayang.Desa.DataModel.Surat.ResponSuratUsaha;
import com.ELayang.Desa.R;

import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;  // <-- yang benar
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SuratUsaha extends AppCompatActivity {

    EditText etNama, etTTL, etAlamat, etLokasi, etNamaUsaha, etJenis, etTahun;
    Button btnKirim;
    ImageView btnBack;
    TextView btnChooseFile, tvNamaFile;

    Uri fileUri = null;
    MultipartBody.Part filePart = null;

    String username;
    ProgressDialog progressDialog;

    // Pilih file
    ActivityResultLauncher<String> filePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    fileUri = uri;
                    String fileName = getFileName(uri);
                    tvNamaFile.setText(fileName);
                    createFilePart(uri);
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_surat_usaha);

        SharedPreferences sp = getSharedPreferences("prefLogin", Context.MODE_PRIVATE);
        username = sp.getString("username", "").trim();

        // Inisialisasi
        etNama = findViewById(R.id.etNama);
        etTTL = findViewById(R.id.etTTL);
        etAlamat = findViewById(R.id.etAlamat);
        etLokasi = findViewById(R.id.etLokasi);
        etNamaUsaha = findViewById(R.id.etNamaUsaha);
        etJenis = findViewById(R.id.etJenis);
        etTahun = findViewById(R.id.etTahun);
        btnKirim = findViewById(R.id.btnKirim);
        btnBack = findViewById(R.id.btnBack);
        btnChooseFile = findViewById(R.id.btnChooseFile);
        tvNamaFile = findViewById(R.id.tvNamaFile);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Mengirim data...");
        progressDialog.setCancelable(false);

        btnBack.setOnClickListener(v -> onBackPressed());

        btnChooseFile.setOnClickListener(v -> filePickerLauncher.launch("*/*"));

        btnKirim.setOnClickListener(v -> {
            if (isValid()) konfirmasiKirim();
        });
    }

    // Ambil nama file dari URI
    private String getFileName(Uri uri) {
        String result = null;
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst())
                result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
            cursor.close();
        }
        return result != null ? result : "file_upload";
    }

    // Convert file jadi Multipart
    private void createFilePart(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            byte[] fileBytes = new byte[inputStream.available()];
            inputStream.read(fileBytes);

            RequestBody requestBody = RequestBody.create(
                    MediaType.parse("application/octet-stream"),
                    fileBytes
            );

            filePart = MultipartBody.Part.createFormData(
                    "file",
                    getFileName(uri),
                    requestBody
            );

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Gagal memuat file", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isValid() {
        if (etNama.getText().toString().trim().isEmpty()) { etNama.setError("Wajib diisi"); return false; }
        if (etTTL.getText().toString().trim().isEmpty()) { etTTL.setError("Wajib diisi"); return false; }
        if (etAlamat.getText().toString().trim().isEmpty()) { etAlamat.setError("Wajib diisi"); return false; }
        if (etLokasi.getText().toString().trim().isEmpty()) { etLokasi.setError("Wajib diisi"); return false; }
        if (etNamaUsaha.getText().toString().trim().isEmpty()) { etNamaUsaha.setError("Wajib diisi"); return false; }
        if (etJenis.getText().toString().trim().isEmpty()) { etJenis.setError("Wajib diisi"); return false; }
        if (etTahun.getText().toString().trim().isEmpty()) { etTahun.setError("Wajib diisi"); return false; }
        if (filePart == null) {
            Toast.makeText(this, "File wajib diunggah", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void konfirmasiKirim() {
        new AlertDialog.Builder(this)
                .setTitle("Konfirmasi")
                .setMessage("Apakah Anda yakin ingin mengirim surat usaha?")
                .setPositiveButton("Kirim", (dialog, which) -> kirim())
                .setNegativeButton("Batal", null)
                .show();
    }

    private void kirim() {
        progressDialog.show();

        APIRequestData api = RetroServer.konekRetrofit().create(APIRequestData.class);

        Call<ResponSuratUsaha> call = api.kirimSuratUsaha(
                rb(username),
                rb("Surat Usaha"),
                rb(etNama.getText().toString()),
                rb(etTTL.getText().toString()),
                rb(etAlamat.getText().toString()),
                rb(etLokasi.getText().toString()),
                rb(etNamaUsaha.getText().toString()),
                rb(etJenis.getText().toString()),
                rb(etTahun.getText().toString()),
                filePart
        );

        call.enqueue(new Callback<ResponSuratUsaha>() {
            @Override
            public void onResponse(Call<ResponSuratUsaha> call, Response<ResponSuratUsaha> response) {
                progressDialog.dismiss();
                if (response.body() != null && response.body().isKode()) {
                    Toast.makeText(SuratUsaha.this, "Surat usaha berhasil dikirim", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(SuratUsaha.this, "Gagal mengirim surat", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponSuratUsaha> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(SuratUsaha.this, "Kesalahan: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private RequestBody rb(String value) {
        return RequestBody.create(MediaType.parse("text/plain"), value == null ? "" : value);
    }
}
