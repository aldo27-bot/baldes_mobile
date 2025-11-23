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
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.ELayang.Desa.API.APIRequestData;
import com.ELayang.Desa.API.RetroServer;
import com.ELayang.Desa.DataModel.Surat.ResponSuratUsaha;
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

public class SuratUsaha extends AppCompatActivity {

    private ImageButton btnBack;
    private EditText etNama, etAlamat, etKapanUsaha, etLokasiUsaha, etKeterangan, etTTL;
    private Button btnKirim, btnPilihFile;
    private TextView tvNamaFile;

    private Uri uriFile;
    private MultipartBody.Part filePart;
    private static final int FILE_REQUEST_CODE = 77;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_surat_usaha);

        // Inisialisasi Tombol Kembali
        btnBack = findViewById(R.id.btnBack);

        etNama = findViewById(R.id.etNama);
        etAlamat = findViewById(R.id.etAlamat);
        etKapanUsaha = findViewById(R.id.etKapanUsaha);   // Tambahan
        etLokasiUsaha = findViewById(R.id.etLokasiUsaha); // Tambahan
        etKeterangan = findViewById(R.id.etKeterangan);
        etTTL = findViewById(R.id.etTTL);

        btnKirim = findViewById(R.id.btnKirim);
        btnPilihFile = findViewById(R.id.btnPilihFile);
        tvNamaFile = findViewById(R.id.tvNamaFile);

        // Listener
        btnBack.setOnClickListener(v -> onBackPressed());
        btnPilihFile.setOnClickListener(v -> pilihFile());
        btnKirim.setOnClickListener(v -> kirimData());

        // Atur status awal file
        tvNamaFile.setText("Belum ada file dipilih (Opsional)");
    }

    private void pilihFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(intent, "Pilih Dokumen"), FILE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == FILE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            uriFile = data.getData();
            String fileName = uriFile.getLastPathSegment();
            tvNamaFile.setText(fileName);

            try {
                InputStream is = getContentResolver().openInputStream(uriFile);
                if (is == null) throw new IOException("Input stream is null");

                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                int nRead;
                byte[] temp = new byte[4096];

                while ((nRead = is.read(temp)) != -1) buffer.write(temp, 0, nRead);

                byte[] fileBytes = buffer.toByteArray();
                is.close();

                String mimeType = getContentResolver().getType(uriFile);
                if (mimeType == null) mimeType = "application/octet-stream";

                RequestBody reqFile = RequestBody.create(MediaType.parse(mimeType), fileBytes);
                filePart = MultipartBody.Part.createFormData("file", fileName, reqFile);

                Log.d("SuratUsaha", "File siap diupload: " + fileName);
                Toast.makeText(this, "File dipilih: " + fileName, Toast.LENGTH_SHORT).show();

            } catch (IOException e) {
                e.printStackTrace();
                filePart = null;
                tvNamaFile.setText("Gagal membaca file");
                Toast.makeText(this, "Gagal membaca file: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == FILE_REQUEST_CODE) {
            uriFile = null;
            filePart = null;
            tvNamaFile.setText("Belum ada file dipilih (Opsional)");
        }
    }

    private void kirimData() {
        SharedPreferences pref = getSharedPreferences("prefLogin", MODE_PRIVATE);
        String username = pref.getString("username", "").trim();

        // Ambil data dari form
        String namaText = etNama.getText().toString().trim();
        String alamatText = etAlamat.getText().toString().trim();
        String kapanUsahaText = etKapanUsaha.getText().toString().trim();   // Tambahan
        String lokasiUsahaText = etLokasiUsaha.getText().toString().trim(); // Tambahan
        String keteranganUsaha = etKeterangan.getText().toString().trim();
        String ttlText = etTTL.getText().toString().trim();

        if (username.isEmpty()) {
            Toast.makeText(this, "Akun belum login!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (namaText.isEmpty() || alamatText.isEmpty() || kapanUsahaText.isEmpty() || lokasiUsahaText.isEmpty() || keteranganUsaha.isEmpty() || ttlText.isEmpty()) {
            Toast.makeText(this, "Semua field wajib diisi!", Toast.LENGTH_SHORT).show();
            return;
        }

        // RequestBody
        RequestBody nama = rb(namaText);
        RequestBody alamat = rb(alamatText);
        RequestBody kapanUsaha = rb(kapanUsahaText);
        RequestBody lokasiUsaha = rb(lokasiUsahaText);
        RequestBody keterangan_usaha = rb(keteranganUsaha);
        RequestBody ttl = rb(ttlText);
        RequestBody user = rb(username);
        RequestBody kodeSurat = rb("SKU");

        MultipartBody.Part fileFix = (filePart != null)
                ? filePart
                : MultipartBody.Part.createFormData("file", "");

        // Panggil API
        APIRequestData api = RetroServer.konekRetrofit().create(APIRequestData.class);
        Call<ResponSuratUsaha> call = api.suratUsaha(user, nama, alamat, ttl, kapanUsaha, lokasiUsaha, keterangan_usaha, kodeSurat, fileFix);

        call.enqueue(new Callback<ResponSuratUsaha>() {
            @Override
            public void onResponse(Call<ResponSuratUsaha> call, Response<ResponSuratUsaha> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String pesan = response.body().getMessage();
                    if (pesan == null || pesan.isEmpty()) {
                        pesan = "Pengajuan Surat Usaha berhasil dikirim.";
                    }
                    Toast.makeText(SuratUsaha.this, pesan, Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(SuratUsaha.this, permintaan_surat.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(SuratUsaha.this, "Gagal mengirim data ke server. Kode: " + response.code(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponSuratUsaha> call, Throwable t) {
                Toast.makeText(SuratUsaha.this, "Kesalahan koneksi: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("SuratUsaha", "Gagal koneksi: " + t.getMessage());
            }
        });
    }

    private RequestBody rb(String value) {
        return RequestBody.create(MediaType.parse("text/plain"), value);
    }

    private void clearForm() {
        etNama.setText("");
        etAlamat.setText("");
        etKapanUsaha.setText("");   // Tambahan
        etLokasiUsaha.setText("");  // Tambahan
        etKeterangan.setText("");
        etTTL.setText("");
        uriFile = null;
        filePart = null;
        tvNamaFile.setText("Belum ada file dipilih (Opsional)");
    }
}
