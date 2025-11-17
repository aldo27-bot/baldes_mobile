package com.ELayang.Desa.Surat;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ELayang.Desa.API.APIRequestData;
import com.ELayang.Desa.API.RetroServer;
import com.ELayang.Desa.DataModel.Surat.ResponSuratUsaha;
import com.ELayang.Desa.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SuratUsaha extends AppCompatActivity {

    private EditText etNama, etAlamat, etTTL;
    private Button btnKirim, btnPilihFile;

    private Uri uriFile;
    private MultipartBody.Part filePart;

    private static final int FILE_REQUEST_CODE = 77;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_surat_usaha);

        etNama = findViewById(R.id.etNama);
        etAlamat = findViewById(R.id.etAlamat);
        etTTL = findViewById(R.id.etTTL);

        btnKirim = findViewById(R.id.btnKirim);
        btnPilihFile = findViewById(R.id.btnPilihFile);

        btnPilihFile.setOnClickListener(v -> pilihFile());
        btnKirim.setOnClickListener(v -> kirimData());
    }

    private void pilihFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*"); // Bisa sesuaikan misal "application/pdf"
        startActivityForResult(intent, FILE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == FILE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            uriFile = data.getData();
            try {
                InputStream is = getContentResolver().openInputStream(uriFile);
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                int nRead;
                byte[] temp = new byte[4096];
                while ((nRead = is.read(temp)) != -1) buffer.write(temp, 0, nRead);
                byte[] fileBytes = buffer.toByteArray();
                is.close();

                RequestBody reqFile = RequestBody.create(MediaType.parse("application/pdf"), fileBytes);
                filePart = MultipartBody.Part.createFormData(
                        "file",
                        "surat_usaha_" + System.currentTimeMillis() + ".pdf",
                        reqFile
                );

                Log.d("SuratUsaha", "File siap diupload");
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Gagal membaca file", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void kirimData() {
        SharedPreferences pref = getSharedPreferences("prefLogin", MODE_PRIVATE);
        String username = pref.getString("username", "");

        if (username.isEmpty()) {
            Toast.makeText(this, "Akun belum login!", Toast.LENGTH_SHORT).show();
            return;
        }

        RequestBody nama = rb(etNama.getText().toString().trim());
        RequestBody alamat = rb(etAlamat.getText().toString().trim());
        RequestBody ttl = rb(etTTL.getText().toString().trim());
        RequestBody user = rb(username);
        RequestBody kodeSurat = rb("SKU"); // sesuaikan kode surat

        MultipartBody.Part fileFix = (filePart != null)
                ? filePart
                : MultipartBody.Part.createFormData("file", "");

        APIRequestData api = RetroServer.konekRetrofit().create(APIRequestData.class);
        Call<ResponSuratUsaha> call = api.suratUsaha(user, nama, alamat, ttl, kodeSurat, fileFix);

        call.enqueue(new Callback<ResponSuratUsaha>() {
            @Override
            public void onResponse(Call<ResponSuratUsaha> call, Response<ResponSuratUsaha> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(SuratUsaha.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    clearForm();
                    finish();
                } else {
                    Toast.makeText(SuratUsaha.this, "Gagal mengirim data ke server", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponSuratUsaha> call, Throwable t) {
                Toast.makeText(SuratUsaha.this, "Kesalahan koneksi: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private RequestBody rb(String value) {
        return RequestBody.create(MediaType.parse("text/plain"), value);
    }

    private void clearForm() {
        etNama.setText("");
        etAlamat.setText("");
        etTTL.setText("");
        uriFile = null;
        filePart = null;
    }
}
