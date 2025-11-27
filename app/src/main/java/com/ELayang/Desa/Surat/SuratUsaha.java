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

        btnBack = findViewById(R.id.btnBack);

        etNama = findViewById(R.id.etNama);
        etAlamat = findViewById(R.id.etAlamat);
        etKapanUsaha = findViewById(R.id.etKapanUsaha);
        etLokasiUsaha = findViewById(R.id.etLokasiUsaha);
        etKeterangan = findViewById(R.id.etKeterangan);
        etTTL = findViewById(R.id.etTTL);

        btnKirim = findViewById(R.id.btnKirim);
        btnPilihFile = findViewById(R.id.btnPilihFile);
        tvNamaFile = findViewById(R.id.tvNamaFile);

        btnBack.setOnClickListener(v -> onBackPressed());
        btnPilihFile.setOnClickListener(v -> pilihFile());
        btnKirim.setOnClickListener(v -> validasiDanKonfirmasi());

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

                Toast.makeText(this, "File dipilih: " + fileName, Toast.LENGTH_SHORT).show();

            } catch (IOException e) {
                e.printStackTrace();
                filePart = null;
                tvNamaFile.setText("Gagal membaca file");
                Toast.makeText(this, "Gagal membaca file: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    // ================================
    // VALIDASI + POPUP KONFIRMASI
    // ================================

    private boolean cek(EditText et, String pesan) {
        if (et.getText().toString().trim().isEmpty()) {
            et.setError(pesan);
            et.requestFocus();
            return true;
        }
        return false;
    }

    private void validasiDanKonfirmasi() {

        if (cek(etNama, "Nama wajib diisi")) return;
        if (cek(etAlamat, "Alamat wajib diisi")) return;
        if (cek(etKapanUsaha, "Waktu usaha wajib diisi")) return;
        if (cek(etLokasiUsaha, "Lokasi usaha wajib diisi")) return;
        if (cek(etKeterangan, "Keterangan wajib diisi")) return;
        if (cek(etTTL, "TTL wajib diisi")) return;

        // POPUP KONFIRMASI
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Konfirmasi")
                .setMessage("Kirim Surat Usaha?")
                .setPositiveButton("Ya", (dialog, which) -> kirimData())
                .setNegativeButton("Tidak", (dialog, which) -> dialog.dismiss())
                .show();
    }

    // ================================
    // KIRIM DATA KE SERVER
    // ================================

    private void kirimData() {
        SharedPreferences pref = getSharedPreferences("prefLogin", MODE_PRIVATE);
        String username = pref.getString("username", "").trim();

        String namaText = etNama.getText().toString().trim();
        String alamatText = etAlamat.getText().toString().trim();
        String kapanUsahaText = etKapanUsaha.getText().toString().trim();
        String lokasiUsahaText = etLokasiUsaha.getText().toString().trim();
        String keteranganUsaha = etKeterangan.getText().toString().trim();
        String ttlText = etTTL.getText().toString().trim();

        RequestBody nama = rb(namaText);
        RequestBody alamat = rb(alamatText);
        RequestBody kapanUsaha = rb(kapanUsahaText);
        RequestBody lokasiUsaha = rb(lokasiUsahaText);
        RequestBody keterangan = rb(keteranganUsaha);
        RequestBody ttl = rb(ttlText);
        RequestBody user = rb(username);
        RequestBody kodeSurat = rb("SKU");

        MultipartBody.Part fileFix = (filePart != null)
                ? filePart
                : MultipartBody.Part.createFormData("file", "");

        APIRequestData api = RetroServer.konekRetrofit().create(APIRequestData.class);
        Call<ResponSuratUsaha> call = api.suratUsaha(
                user, nama, alamat, ttl,
                kapanUsaha, lokasiUsaha, keterangan,
                kodeSurat, fileFix
        );

        call.enqueue(new Callback<ResponSuratUsaha>() {
            @Override
            public void onResponse(Call<ResponSuratUsaha> call, Response<ResponSuratUsaha> response) {
                if (response.isSuccessful() && response.body() != null) {

                    String pesan = response.body().getMessage();
                    if (pesan == null || pesan.isEmpty()) pesan = "Pengajuan Surat Usaha berhasil dikirim.";

                    Toast.makeText(SuratUsaha.this, pesan, Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(SuratUsaha.this, permintaan_surat.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(SuratUsaha.this, "Gagal mengirim! Kode: " + response.code(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponSuratUsaha> call, Throwable t) {
                Toast.makeText(SuratUsaha.this, "Kesalahan koneksi: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("SuratUsaha", "Error: " + t.getMessage());
            }
        });
    }

    private RequestBody rb(String value) {
        return RequestBody.create(MediaType.parse("text/plain"), value);
    }
}
