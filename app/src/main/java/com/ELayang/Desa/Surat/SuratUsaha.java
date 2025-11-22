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
import android.widget.ImageButton; // Import ImageButton
import android.widget.TextView;     // Import TextView
import android.widget.Toast;

import com.ELayang.Desa.API.APIRequestData;
import com.ELayang.Desa.API.RetroServer;
import com.ELayang.Desa.DataModel.Surat.ResponSuratUsaha;
import com.ELayang.Desa.Menu.permintaan_surat; // Import activity tujuan
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

    private ImageButton btnBack; // Deklarasi Tombol Kembali
    private EditText etNama, etAlamat, etKeterangan, etTTL;
    private Button btnKirim, btnPilihFile;
    private TextView tvNamaFile; // Deklarasi TextView untuk nama file

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
        etKeterangan = findViewById(R.id.etKeterangan);
        etTTL = findViewById(R.id.etTTL);
        btnKirim = findViewById(R.id.btnKirim);
        btnPilihFile = findViewById(R.id.btnPilihFile);
        tvNamaFile = findViewById(R.id.tvNamaFile); // Inisialisasi TextView nama file

        // Listener
        btnBack.setOnClickListener(v -> onBackPressed()); // 🟢 Logika Tombol Kembali
        btnPilihFile.setOnClickListener(v -> pilihFile());
        btnKirim.setOnClickListener(v -> kirimData());

        // Atur status awal file
        tvNamaFile.setText("Belum ada file dipilih (Opsional)");
    }

    private void pilihFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE); // Penting untuk Android modern
        startActivityForResult(Intent.createChooser(intent, "Pilih Dokumen"), FILE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == FILE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            uriFile = data.getData();
            String fileName = uriFile.getLastPathSegment();

            // Tampilkan nama file
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

                // Dapatkan mime type
                String mimeType = getContentResolver().getType(uriFile);
                if (mimeType == null) {
                    mimeType = "application/octet-stream";
                }

                RequestBody reqFile = RequestBody.create(MediaType.parse(mimeType), fileBytes);
                filePart = MultipartBody.Part.createFormData(
                        "file",
                        fileName, // Kirim nama file asli
                        reqFile
                );

                Log.d("SuratUsaha", "File siap diupload: " + fileName);
                Toast.makeText(this, "File dipilih: " + fileName, Toast.LENGTH_SHORT).show();

            } catch (IOException e) {
                e.printStackTrace();
                filePart = null; // Reset jika gagal
                tvNamaFile.setText("Gagal membaca file");
                Toast.makeText(this, "Gagal membaca file: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == FILE_REQUEST_CODE) {
            // Jika dibatalkan
            uriFile = null;
            filePart = null;
            tvNamaFile.setText("Belum ada file dipilih (Opsional)");
        }
    }

    private void kirimData() {
        SharedPreferences pref = getSharedPreferences("prefLogin", MODE_PRIVATE);
        String username = pref.getString("username", "").trim();

        // Validasi
        String namaText = etNama.getText().toString().trim();
        String alamatText = etAlamat.getText().toString().trim();
        String keteranganUsaha = etKeterangan.getText().toString().trim();
        String ttlText = etTTL.getText().toString().trim();

        if (username.isEmpty()) {
            Toast.makeText(this, "Akun belum login!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (namaText.isEmpty() || alamatText.isEmpty() || keteranganUsaha.isEmpty() || ttlText.isEmpty()) {
            Toast.makeText(this, "Semua field wajib diisi!", Toast.LENGTH_SHORT).show();
            return;
        }


        RequestBody nama = rb(namaText);
        RequestBody alamat = rb(alamatText);
        RequestBody keterangan_usaha = rb(keteranganUsaha);
        RequestBody ttl = rb(ttlText);
        RequestBody user = rb(username);
        RequestBody kodeSurat = rb("SKU"); // Kode surat ini harus sesuai dengan API Anda

        // Penanganan file opsional
        MultipartBody.Part fileFix = (filePart != null)
                ? filePart
                : MultipartBody.Part.createFormData("file", "");

        APIRequestData api = RetroServer.konekRetrofit().create(APIRequestData.class);
        Call<ResponSuratUsaha> call = api.suratUsaha(user, nama, alamat, keterangan_usaha, ttl, kodeSurat, fileFix);

        call.enqueue(new Callback<ResponSuratUsaha>() {
            @Override
            public void onResponse(Call<ResponSuratUsaha> call, Response<ResponSuratUsaha> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String pesan = response.body().getMessage();
                    if (pesan == null || pesan.isEmpty()) {
                        pesan = "Pengajuan Surat Usaha berhasil dikirim.";
                    }
                    Toast.makeText(SuratUsaha.this, pesan, Toast.LENGTH_SHORT).show();

                    // 🟢 Pindah activity
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
        etKeterangan.setText("");
        etTTL.setText("");
        uriFile = null;
        filePart = null;
        tvNamaFile.setText("Belum ada file dipilih (Opsional)");
    }
}