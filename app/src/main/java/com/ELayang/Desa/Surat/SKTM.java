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
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ImageButton;
import android.widget.TextView;

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

    ImageButton btnBack;

    EditText etNama, etTtl, etAsalSekolah, etKeperluan,
            etNamaOrtu, etNikOrtu, etAlamatOrtu, etTtlOrtu, etPekerjaanOrtu;

    TextView btnPilihFoto, tvNamaFoto;
    Button btnKirim;
    ImageView imgPreview;

    Uri uriFoto;
    MultipartBody.Part filePart;

    String usernameUser;
    String kodeSurat = "SKTM";

    private static final int PICK_IMAGE_REQUEST = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.surat_sktm); // Asumsi ini adalah layout yang benar

        SharedPreferences pref = getSharedPreferences("prefLogin", MODE_PRIVATE);
        usernameUser = pref.getString("username", "");

        Log.d("SKTM_PREF", "Username dari SharedPref: " + usernameUser);

        // Inisialisasi EditText
        etNama = findViewById(R.id.etNama);
        etTtl = findViewById(R.id.etTtl);
        etAsalSekolah = findViewById(R.id.etAsalSekolah);
        etKeperluan = findViewById(R.id.etKeperluan);
        etNamaOrtu = findViewById(R.id.etNamaOrtu);
        etNikOrtu = findViewById(R.id.etNikOrtu);
        etAlamatOrtu = findViewById(R.id.etAlamatOrtu);
        etTtlOrtu = findViewById(R.id.etTtlOrtu);
        etPekerjaanOrtu = findViewById(R.id.etPekerjaanOrtu);

        // Inisialisasi Komponen Aksi
        btnBack = findViewById(R.id.btnBack);
        btnPilihFoto = findViewById(R.id.btnPilihFoto);
        tvNamaFoto = findViewById(R.id.tvNamaFoto);
        btnKirim = findViewById(R.id.btnKirim);
        imgPreview = findViewById(R.id.imgPreview);

        // Listener
        btnBack.setOnClickListener(view -> onBackPressed());
        btnPilihFoto.setOnClickListener(v -> pilihFoto());
        btnKirim.setOnClickListener(v -> kirimData());

        // Atur tampilan awal
        tvNamaFoto.setText("Tidak ada foto yang dipilih (Opsional)");
        imgPreview.setVisibility(ImageView.GONE);
    }

    private void pilihFoto() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            uriFoto = data.getData();
            imgPreview.setImageURI(uriFoto);
            imgPreview.setVisibility(ImageView.VISIBLE);

            // Tampilkan nama file
            if (uriFoto != null) {
                // Mendapatkan nama file sederhana dari URI
                String path = uriFoto.getPath();
                String fileName = (path != null && path.lastIndexOf('/') != -1) ?
                        path.substring(path.lastIndexOf('/') + 1) :
                        "Foto Terpilih";
                tvNamaFoto.setText(fileName);
            }

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
                tvNamaFoto.setText("Gagal membaca file");
                filePart = null; // Penting: reset filePart jika gagal dibaca
            }
        } else if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_CANCELED) {
            // Jika pengguna membatalkan pemilihan, reset file part
            uriFoto = null;
            filePart = null;
            tvNamaFoto.setText("Tidak ada foto yang dipilih (Opsional)");
            imgPreview.setVisibility(ImageView.GONE);
        }
    }

    private void kirimData() {
        // Cek username
        if (usernameUser == null || usernameUser.isEmpty()) {
            Toast.makeText(this, "Akun belum login! Username kosong", Toast.LENGTH_LONG).show();
            Log.e("SKTM_DEBUG", "USERNAME KOSONG! Tidak bisa kirim data");
            return;
        }

        // Cek field wajib (Data Teks)
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

        // 🔴 Hapus validasi wajib file di sini
        /*
        if (filePart == null) {
            Toast.makeText(this, "Mohon pilih file foto/dokumen pendukung", Toast.LENGTH_SHORT).show();
            return;
        }
        */

        Log.d("SKTM_DEBUG", "Mulai kirim data SKTM oleh user: " + usernameUser);

        APIRequestData api = RetroServer.konekRetrofit().create(APIRequestData.class);

        // 🟢 Penyesuaian untuk file opsional
        MultipartBody.Part fotoPartFix = (filePart != null)
                ? filePart
                : MultipartBody.Part.createFormData("file", ""); // Kirim Part kosong jika file tidak dipilih

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
                fotoPartFix // Menggunakan fotoPartFix yang bisa kosong
        );

        call.enqueue(new Callback<ResponSktm>() {
            @Override
            public void onResponse(Call<ResponSktm> call, Response<ResponSktm> response) {
                Log.d("SKTM_DEBUG", "Response code: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    String pesan = response.body().getMessage();
                    if (pesan == null || pesan.isEmpty()) {
                        pesan = "Pengajuan SKTM berhasil dikirim.";
                    }
                    Toast.makeText(SKTM.this, pesan, Toast.LENGTH_SHORT).show();
                    Log.d("SKTM_DEBUG", "Pesan server: " + response.body().getMessage());

                    Intent intent = new Intent(SKTM.this, permintaan_surat.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(SKTM.this, "Gagal mengirim data ke server", Toast.LENGTH_SHORT).show();
                    Log.e("SKTM_DEBUG", "Response tidak sukses atau body null. Code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ResponSktm> call, Throwable t) {
                Toast.makeText(SKTM.this, "Kesalahan koneksi: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("SKTM_DEBUG", "Gagal kirim SKTM: " + t.getMessage());
            }
        });
    }

    private RequestBody rb(String value) {
        return RequestBody.create(MediaType.parse("text/plain"), value);
    }
}