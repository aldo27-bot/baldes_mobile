package com.ELayang.Desa.aspirasi;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import android.util.Log;
import java.io.File;
import java.io.InputStream; // <-- tambahkan ini



import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.ELayang.Desa.API.APIRequestData;
import com.ELayang.Desa.API.RetroServer;
import com.ELayang.Desa.DataModel.AspirasiResponse;
import com.ELayang.Desa.R;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TambahAspirasiActivity extends AppCompatActivity {

    private static final int PICK_IMAGE = 100;

    private EditText etJudul, etDeskripsi;
    private Spinner spKategori;
    private Button btnKirim, btnPilihFoto;
    private ImageView ivPreview;
    private Uri uriFoto;
    private String imagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_aspirasi);

        etJudul = findViewById(R.id.etJudul);
        etDeskripsi = findViewById(R.id.etDeskripsi);
        spKategori = findViewById(R.id.spKategori);
        ivPreview = findViewById(R.id.ivPreview);
        btnKirim = findViewById(R.id.btnKirim);
        btnPilihFoto = findViewById(R.id.btnPilihFoto);

        // Isi spinner kategori
        String[] kategoriList = {
                "Pembangunan", "Kesehatan", "Pendidikan", "Sosial",
                "Ekonomi", "Lingkungan", "Keamanan", "Lainnya"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, kategoriList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spKategori.setAdapter(adapter);

        btnPilihFoto.setOnClickListener(v -> pilihFoto());
        btnKirim.setOnClickListener(v -> uploadAspirasi());
    }

    // Pilih foto dari galeri
    private void pilihFoto() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            uriFoto = data.getData();
            ivPreview.setImageURI(uriFoto);
            imagePath = FileUtils.getPath(this, uriFoto); // Helper path (pastikan ada FileUtils)
        }
    }

    // Upload aspirasi
    private void uploadAspirasi() {
        String judul = etJudul.getText().toString().trim();
        String kategori = spKategori.getSelectedItem().toString().trim();
        String deskripsi = etDeskripsi.getText().toString().trim();

        if (judul.isEmpty() || kategori.isEmpty() || deskripsi.isEmpty()) {
            Toast.makeText(this, "Semua field wajib diisi", Toast.LENGTH_SHORT).show();
            return;
        }

        ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Mengirim aspirasi...");
        pd.setCancelable(false);
        pd.show();

        SharedPreferences prefs = getSharedPreferences("prefLogin", MODE_PRIVATE);
        String username = prefs.getString("username", "");
        if (username.isEmpty()) {
            pd.dismiss();
            Toast.makeText(this, "Username tidak ditemukan. Silakan login ulang.", Toast.LENGTH_SHORT).show();
            return;
        }

        RequestBody rbUsername = RequestBody.create(MediaType.parse("text/plain"), username);
        RequestBody rbJudul = RequestBody.create(MediaType.parse("text/plain"), judul);
        RequestBody rbKategori = RequestBody.create(MediaType.parse("text/plain"), kategori);
        RequestBody rbDeskripsi = RequestBody.create(MediaType.parse("text/plain"), deskripsi);

        MultipartBody.Part partFoto = null;

        try {
            if (uriFoto != null) {
                InputStream is = getContentResolver().openInputStream(uriFoto);
                byte[] bytes = new byte[is.available()];
                is.read(bytes);
                is.close();

                RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), bytes);
                partFoto = MultipartBody.Part.createFormData("foto", "upload.jpg", reqFile);

                Log.d("UPLOAD_ASPIRASI", "Foto siap diupload dari URI: " + uriFoto);
            } else {
                RequestBody empty = RequestBody.create(MediaType.parse("text/plain"), "");
                partFoto = MultipartBody.Part.createFormData("foto", "", empty);
                Log.d("UPLOAD_ASPIRASI", "Tidak ada foto yang diunggah");
            }
        } catch (Exception e) {
            pd.dismiss();
            Log.e("UPLOAD_ASPIRASI", "Gagal membaca foto: " + e.getMessage(), e);
            Toast.makeText(this, "Gagal membaca foto", Toast.LENGTH_SHORT).show();
            return;
        }

        APIRequestData api = RetroServer.konekRetrofit().create(APIRequestData.class);
        Call<AspirasiResponse> call = api.kirimAspirasi(rbUsername, rbJudul, rbKategori, rbDeskripsi, partFoto);

        call.enqueue(new Callback<AspirasiResponse>() {
            @Override
            public void onResponse(Call<AspirasiResponse> call, Response<AspirasiResponse> response) {
                pd.dismiss();
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(TambahAspirasiActivity.this, response.body().getPesan(), Toast.LENGTH_SHORT).show();
                    if (response.body().getKode() == 1) finish();
                } else {
                    Toast.makeText(TambahAspirasiActivity.this, "Gagal mengirim aspirasi", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AspirasiResponse> call, Throwable t) {
                pd.dismiss();
                Toast.makeText(TambahAspirasiActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("UPLOAD_ASPIRASI", "Error upload: ", t);
            }
        });
    }
}