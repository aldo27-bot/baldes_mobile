package com.ELayang.Desa.Surat;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton; // Import ImageButton
import android.widget.TextView;
import android.widget.Toast;

import com.ELayang.Desa.API.APIRequestData;
import com.ELayang.Desa.API.RetroServer;
import com.ELayang.Desa.DataModel.Surat.ResponSkb;
import com.ELayang.Desa.Menu.permintaan_surat; // Import activity tujuan
import com.ELayang.Desa.R;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SKB extends AppCompatActivity {

    private ImageButton btnBack; // Deklarasi Tombol Kembali
    private EditText etNama, etNik, etAgama, etTTL, etPendidikan, etAlamat, etKeperluan;
    private Button btnPilihFile, btnKirim;
    private TextView tvNamaFile;
    private Uri fileUri = null;

    private final ActivityResultLauncher<Intent> filePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    fileUri = result.getData().getData();
                    // Pastikan fileUri tidak null sebelum memanggil getFileName
                    if (fileUri != null) {
                        tvNamaFile.setText(getFileName(fileUri));
                    }
                } else if (result.getResultCode() == RESULT_CANCELED) {
                    // Jika pemilihan dibatalkan
                    fileUri = null;
                    tvNamaFile.setText("Belum ada file dipilih");
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Menggunakan layout modern yang baru: activity_form_skbb
        setContentView(R.layout.activity_surat_berkelakuan_baik);

        // Inisialisasi Tombol Kembali
        btnBack = findViewById(R.id.btnBack);

        etNama = findViewById(R.id.etNama);
        etNik = findViewById(R.id.etNik);
        etAgama = findViewById(R.id.etAgama);
        etTTL = findViewById(R.id.etTTL);
        etPendidikan = findViewById(R.id.etPendidikan);
        etAlamat = findViewById(R.id.etAlamat);
        etKeperluan = findViewById(R.id.etKeperluan);
        btnPilihFile = findViewById(R.id.btnPilihFile);
        btnKirim = findViewById(R.id.btnKirim);
        tvNamaFile = findViewById(R.id.tvNamaFile);

        // Listener
        btnBack.setOnClickListener(v -> onBackPressed()); // Implementasi Tombol Kembali
        btnPilihFile.setOnClickListener(v -> pilihFile());
        btnKirim.setOnClickListener(v -> kirimData());
    }

    private void pilihFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        filePickerLauncher.launch(intent);
    }

    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            // Gunakan try-with-resources untuk memastikan Cursor tertutup
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int idx = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (idx >= 0) result = cursor.getString(idx);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (result == null) {
            result = uri.getLastPathSegment();
        }
        return result;
    }

    private MultipartBody.Part prepareFilePart(Uri uri) {
        try {
            ContentResolver resolver = getContentResolver();
            // Fallback mimeType jika resolver.getType(uri) mengembalikan null
            String mimeType = resolver.getType(uri);
            if (mimeType == null) {
                mimeType = "application/octet-stream";
            }

            InputStream is = resolver.openInputStream(uri);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int bytesRead;

            if (is == null) {
                throw new Exception("Input stream is null");
            }

            while ((bytesRead = is.read(buffer)) != -1) {
                bos.write(buffer, 0, bytesRead);
            }
            is.close();

            RequestBody requestFile = RequestBody.create(MediaType.parse(mimeType), bos.toByteArray());
            return MultipartBody.Part.createFormData("file", getFileName(uri), requestFile);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Gagal memproses file: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    private void kirimData() {
        SharedPreferences pref = getSharedPreferences("prefLogin", MODE_PRIVATE);
        String username = pref.getString("username", "").trim();

        String nama = etNama.getText().toString().trim();
        String nik = etNik.getText().toString().trim();
        String agama = etAgama.getText().toString().trim();
        String ttl = etTTL.getText().toString().trim();
        String pendidikan = etPendidikan.getText().toString().trim();
        String alamat = etAlamat.getText().toString().trim();
        String keperluan = etKeperluan.getText().toString().trim();

        // Validasi field wajib
        if (nama.isEmpty() || nik.isEmpty() || agama.isEmpty() || ttl.isEmpty()
                || pendidikan.isEmpty() || alamat.isEmpty() || keperluan.isEmpty()) {
            Toast.makeText(this, "Semua field wajib diisi", Toast.LENGTH_LONG).show();
            return;
        }
        if (username.isEmpty()) {
            Toast.makeText(this, "Akun belum login!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Siapkan RequestBody untuk data teks
        RequestBody rbNama = RequestBody.create(MediaType.parse("text/plain"), nama);
        RequestBody rbNik = RequestBody.create(MediaType.parse("text/plain"), nik);
        RequestBody rbAgama = RequestBody.create(MediaType.parse("text/plain"), agama);
        RequestBody rbTTL = RequestBody.create(MediaType.parse("text/plain"), ttl);
        RequestBody rbPendidikan = RequestBody.create(MediaType.parse("text/plain"), pendidikan);
        RequestBody rbAlamat = RequestBody.create(MediaType.parse("text/plain"), alamat);
        RequestBody rbKeperluan = RequestBody.create(MediaType.parse("text/plain"), keperluan);
        RequestBody rbKodeSurat = RequestBody.create(MediaType.parse("text/plain"), "SKBB");
        RequestBody rbIdPejabat = RequestBody.create(MediaType.parse("text/plain"), "");
        RequestBody rbUsername = RequestBody.create(MediaType.parse("text/plain"), username);

        // Siapkan file part (Wajib jika diminta, tapi di sini diasumsikan opsional seperti SKTM)
        // Jika file wajib, tambahkan validasi: if (fileUri == null) { Toast...; return; }

        MultipartBody.Part filePart = null;
        if (fileUri != null) {
            filePart = prepareFilePart(fileUri);
        }

        // Buat filePart placeholder jika opsional dan kosong
        MultipartBody.Part fotoPartFix = (filePart != null)
                ? filePart
                : MultipartBody.Part.createFormData("file", ""); // Part kosong

        APIRequestData api = RetroServer.konekRetrofit().create(APIRequestData.class);
        Call<ResponSkb> kirim = api.kirimskb(
                rbNama, rbNik, rbAgama, rbTTL, rbPendidikan, rbAlamat,
                fotoPartFix,
                rbKeperluan,// Menggunakan fotoPartFix
                rbKodeSurat, rbIdPejabat, rbUsername
        );

        kirim.enqueue(new Callback<ResponSkb>() {
            @Override
            public void onResponse(Call<ResponSkb> call, Response<ResponSkb> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String pesan = response.body().getMessage();
                    if (pesan == null || pesan.isEmpty()) {
                        pesan = "Pengajuan SKBB berhasil dikirim.";
                    }
                    Toast.makeText(SKB.this, pesan, Toast.LENGTH_SHORT).show();

                    // 🟢 LOGIKA PINDAH ACTIVITY
                    Intent intent = new Intent(SKB.this, permintaan_surat.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish(); // Tutup activity SKB
                } else {
                    Toast.makeText(SKB.this, "Gagal mengirim data ke server", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponSkb> call, Throwable t) {
                Toast.makeText(SKB.this, "Kesalahan koneksi: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void clearForm() {
        etNama.setText("");
        etNik.setText("");
        etAgama.setText("");
        etTTL.setText("");
        etPendidikan.setText("");
        etAlamat.setText("");
        etKeperluan.setText("");
        tvNamaFile.setText("Belum ada file dipilih");
        fileUri = null;
    }
}