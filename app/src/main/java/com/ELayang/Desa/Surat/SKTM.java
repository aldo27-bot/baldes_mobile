package com.ELayang.Desa.Surat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.ELayang.Desa.API.APIRequestData;
import com.ELayang.Desa.API.RetroServer;
import com.ELayang.Desa.DataModel.Surat.ResponSktm;
import com.ELayang.Desa.Menu.permintaan_surat;
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

public class SKTM extends AppCompatActivity {

    EditText etNama, etTtl, etAsalSekolah, etKeperluan,
            etNamaOrtu, etNikOrtu, etAlamatOrtu, etTtlOrtu, etPekerjaanOrtu;

    Button btnPilihFoto, btnKirim;
    ImageView imgPreview;

    Uri uriFoto;
    MultipartBody.Part filePart;

    String usernameUser;
    String kodeSurat = "SKTM";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.surat_sktm);

        SharedPreferences pref = getSharedPreferences("prefLogin", MODE_PRIVATE);
        usernameUser = pref.getString("username", "");

        Log.d("SKTM_PREF", "Username dari SharedPref: " + usernameUser);

        etNama = findViewById(R.id.etNama);
        etTtl = findViewById(R.id.etTtl);
        etAsalSekolah = findViewById(R.id.etAsalSekolah);
        etKeperluan = findViewById(R.id.etKeperluan);
        etNamaOrtu = findViewById(R.id.etNamaOrtu);
        etNikOrtu = findViewById(R.id.etNikOrtu);
        etAlamatOrtu = findViewById(R.id.etAlamatOrtu);
        etTtlOrtu = findViewById(R.id.etTtlOrtu);
        etPekerjaanOrtu = findViewById(R.id.etPekerjaanOrtu);

        btnPilihFoto = findViewById(R.id.btnPilihFoto);
        btnKirim = findViewById(R.id.btnKirim);
        imgPreview = findViewById(R.id.imgPreview);

        btnPilihFoto.setOnClickListener(v -> pilihFoto());
        btnKirim.setOnClickListener(v -> kirimData());
    }

    private void pilihFoto() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            uriFoto = data.getData();
            imgPreview.setImageURI(uriFoto);

            try {
                InputStream is = getContentResolver().openInputStream(uriFoto);
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                int nRead;
                byte[] temp = new byte[4096];
                while ((nRead = is.read(temp, 0, temp.length)) != -1) {
                    buffer.write(temp, 0, nRead);
                }
                byte[] fileBytes = buffer.toByteArray();
                is.close();

                RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), fileBytes);
                filePart = MultipartBody.Part.createFormData(
                        "file",
                        "sktm_" + System.currentTimeMillis() + ".jpg",
                        reqFile
                );

                Log.d("SKTM_DEBUG", "Foto berhasil dibaca & siap upload");
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Gagal membaca file: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void kirimData() {
        // Cek username
        if (usernameUser == null || usernameUser.isEmpty()) {
            Toast.makeText(this, "Akun belum login! Username kosong", Toast.LENGTH_LONG).show();
            Log.e("SKTM_DEBUG", "USERNAME KOSONG! Tidak bisa kirim data");
            return;
        }

        // Cek field wajib
        if (etNama.getText().toString().isEmpty() ||
                etTtl.getText().toString().isEmpty() ||
                etAsalSekolah.getText().toString().isEmpty() ||
                etKeperluan.getText().toString().isEmpty() ||
                etNamaOrtu.getText().toString().isEmpty() ||
                etNikOrtu.getText().toString().isEmpty() ||
                etAlamatOrtu.getText().toString().isEmpty() ||
                etTtlOrtu.getText().toString().isEmpty() ||
                etPekerjaanOrtu.getText().toString().isEmpty()) {
            Toast.makeText(this, "Semua field wajib diisi!", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d("SKTM_DEBUG", "Mulai kirim data SKTM oleh user: " + usernameUser);

        APIRequestData api = RetroServer.konekRetrofit().create(APIRequestData.class);

        MultipartBody.Part fotoPartFix =
                (filePart != null) ? filePart : MultipartBody.Part.createFormData("file", "");

        Call<ResponSktm> call = api.sktm(
                rb(etNama.getText().toString()),
                rb(etTtl.getText().toString()),
                rb(etAsalSekolah.getText().toString()),
                rb(etKeperluan.getText().toString()),
                rb(etNamaOrtu.getText().toString()),
                rb(etNikOrtu.getText().toString()),
                rb(etAlamatOrtu.getText().toString()),
                rb(etTtlOrtu.getText().toString()),
                rb(etPekerjaanOrtu.getText().toString()),
                rb(kodeSurat),
                rb(usernameUser),
                fotoPartFix
        );

        call.enqueue(new Callback<ResponSktm>() {
            @Override
            public void onResponse(Call<ResponSktm> call, Response<ResponSktm> response) {
                Log.d("SKTM_DEBUG", "Response code: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(SKTM.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    Log.d("SKTM_DEBUG", "Pesan server: " + response.body().getMessage());

                    // Kembali ke menu Pengajuan Surat
                    Intent intent = new Intent(SKTM.this, permintaan_surat.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish(); // Tutup activity SKTM
                } else {
                    Toast.makeText(SKTM.this, "Response tidak lengkap", Toast.LENGTH_SHORT).show();
                    Log.e("SKTM_DEBUG", "Response tidak lengkap");
                }
            }

            @Override
            public void onFailure(Call<ResponSktm> call, Throwable t) {
                Toast.makeText(SKTM.this, "Gagal: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("SKTM_DEBUG", "Gagal kirim SKTM: " + t.getMessage());
            }
        });
    }

    private RequestBody rb(String value) {
        return RequestBody.create(MediaType.parse("text/plain"), value);
    }
}
