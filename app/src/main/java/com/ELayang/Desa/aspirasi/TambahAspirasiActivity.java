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
import android.widget.ImageButton;

import java.io.InputStream;
import java.io.ByteArrayOutputStream;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.ELayang.Desa.API.APIRequestData;
import com.ELayang.Desa.API.RetroServer;
import com.ELayang.Desa.DataModel.AspirasiResponse;
import com.ELayang.Desa.R;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.text.InputFilter;

public class TambahAspirasiActivity extends AppCompatActivity {

    private static final int PICK_IMAGE = 100;

    private ImageButton btnBack;
    private EditText etJudul, etDeskripsi;
    private Spinner spKategori;
    private Button btnKirim, btnPilihFoto;
    private ImageView ivPreview;
    private Uri uriFoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_aspirasi);

        btnBack = findViewById(R.id.btnBack);
        etJudul = findViewById(R.id.etJudul);
        etDeskripsi = findViewById(R.id.etDeskripsi);
        spKategori = findViewById(R.id.spKategori);
        ivPreview = findViewById(R.id.ivPreview);
        btnKirim = findViewById(R.id.btnKirim);
        btnPilihFoto = findViewById(R.id.btnPilihFoto);

        // ================================
        // 🔒 FILTER ANTI EMOJI
        // ================================
        InputFilter filterNoEmoji = (source, start, end, dest, dstart, dend) -> {
            for (int i = start; i < end; i++) {
                int type = Character.getType(source.charAt(i));
                if (type == Character.SURROGATE || type == Character.OTHER_SYMBOL) {
                    Toast.makeText(this, "Emoji tidak diperbolehkan", Toast.LENGTH_SHORT).show();
                    return "";
                }
            }
            return null;
        };

        etJudul.setFilters(new InputFilter[]{filterNoEmoji});
        etDeskripsi.setFilters(new InputFilter[]{filterNoEmoji});
        // ================================

        // Isi spinner kategori
        String[] kategoriList = {
                "Pilih Kategori",
                "Pembangunan", "Kesehatan", "Pendidikan", "Sosial",
                "Ekonomi", "Lingkungan", "Keamanan", "Lainnya"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                kategoriList
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spKategori.setAdapter(adapter);

        // Listener
        btnBack.setOnClickListener(v -> onBackPressed());
        btnPilihFoto.setOnClickListener(v -> pilihFoto());
        btnKirim.setOnClickListener(v -> uploadAspirasi());
    }

    private void pilihFoto() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(intent, "Pilih Foto Bukti"), PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            uriFoto = data.getData();
            ivPreview.setImageURI(uriFoto);
            Log.d("UPLOAD_ASPIRASI", "Foto berhasil dipilih: " + uriFoto.getLastPathSegment());
        } else if (requestCode == PICK_IMAGE && resultCode == RESULT_CANCELED) {
            uriFoto = null;
            ivPreview.setImageResource(R.drawable.placeholder);
            Log.d("UPLOAD_ASPIRASI", "Pemilihan foto dibatalkan.");
        }
    }

    private void uploadAspirasi() {
        String judul = etJudul.getText().toString().trim();
        String kategori = spKategori.getSelectedItem().toString().trim();
        String deskripsi = etDeskripsi.getText().toString().trim();

        if (judul.isEmpty() || kategori.equals("Pilih Kategori") || deskripsi.isEmpty()) {
            Toast.makeText(this, "Harap isi Judul, Kategori, dan Deskripsi", Toast.LENGTH_SHORT).show();
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
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int bytesRead;

                while ((bytesRead = is.read(buffer)) != -1) {
                    bos.write(buffer, 0, bytesRead);
                }
                is.close();

                String mimeType = getContentResolver().getType(uriFoto);
                if (mimeType == null) mimeType = "image/*";

                String fileName = uriFoto.getLastPathSegment();
                if (fileName == null) fileName = "upload.jpg";

                RequestBody reqFile = RequestBody.create(MediaType.parse(mimeType), bos.toByteArray());
                partFoto = MultipartBody.Part.createFormData("foto", fileName, reqFile);

                Log.d("UPLOAD_ASPIRASI", "Foto siap diupload: " + fileName);
            } else {
                RequestBody empty = RequestBody.create(MediaType.parse("text/plain"), "");
                partFoto = MultipartBody.Part.createFormData("foto", "", empty);
            }
        } catch (Exception e) {
            pd.dismiss();
            Log.e("UPLOAD_ASPIRASI", "Gagal memproses foto: " + e.getMessage(), e);
            Toast.makeText(this, "Gagal memproses foto", Toast.LENGTH_LONG).show();
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
                    if (response.body().getKode() == 1) {
                        finish();
                    }
                } else {
                    Toast.makeText(TambahAspirasiActivity.this, "Gagal mengirim aspirasi", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AspirasiResponse> call, Throwable t) {
                pd.dismiss();
                Toast.makeText(TambahAspirasiActivity.this, "Koneksi Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("UPLOAD_ASPIRASI", "Error upload: ", t);
            }
        });
    }
}
