package com.ELayang.Desa.Surat;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageButton; // Tambahkan import untuk ImageButton

// Tambahkan untuk membaca file
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

// Retrofit
import com.ELayang.Desa.API.APIRequestData;
import com.ELayang.Desa.API.RetroServer;
import com.ELayang.Desa.DataModel.Surat.ResponSkm;

// HTTP Multipart
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

// Retrofit Callback
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.ELayang.Desa.Menu.permintaan_surat;
import com.ELayang.Desa.R;


public class SKM extends AppCompatActivity {

    private static final String TAG = "SKM_ACTIVITY";

    private ImageButton btnBack; // Deklarasi Tombol Kembali
    private EditText etNama, etTTL, etJenisKelamin, etPekerjaan, etAgama, etAlamat, etKewarganegaraan, etKeterangan;
    private Button btnKirim, btnChooseFile;
    private TextView tFileName;
    private Uri fileUri;

    private static final int FILE_SELECT_CODE = 100;

    // Tambahkan SharedPref untuk mendapatkan username
    private String usernameUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Pastikan menggunakan layout modern yang baru: activity_form_kematian
        setContentView(R.layout.surat_skm);

        Log.d(TAG, "onCreate: Activity Surat Keterangan Kematian dimulai");

        // Ambil username dari SharedPreferences
        usernameUser = getSharedPreferences("prefLogin", MODE_PRIVATE).getString("username", "");
        Log.d(TAG, "Username dari SharedPref: " + usernameUser);

        // Inisialisasi Komponen Header
        btnBack = findViewById(R.id.btnBack); // Inisialisasi Tombol Kembali

        // Inisialisasi EditText
        etNama = findViewById(R.id.etNama);
        etTTL = findViewById(R.id.etTTL);
        etJenisKelamin = findViewById(R.id.etJenisKelamin);
        etPekerjaan = findViewById(R.id.etPekerjaan);
        etAgama = findViewById(R.id.etAgama);
        etAlamat = findViewById(R.id.etAlamat);
        etKewarganegaraan = findViewById(R.id.etkewarganegaraan); // ID ini dari layout lama/sebelumnya
        etKeterangan = findViewById(R.id.etKeterangan);

        // Inisialisasi Button dan TextView
        btnKirim = findViewById(R.id.btnKirim);
        btnChooseFile = findViewById(R.id.btn_choose_file);
        tFileName = findViewById(R.id.t_file_name);

        Log.d(TAG, "onCreate: Semua komponen view berhasil di-inisialisasi");

        // Listener
        btnBack.setOnClickListener(v -> onBackPressed()); // 🟢 Logika Tombol Kembali
        btnChooseFile.setOnClickListener(v -> chooseFile());
        btnKirim.setOnClickListener(v -> kirimData());

        // Atur status awal file
        tFileName.setText("Tidak ada file yang dipilih");
    }

    private void chooseFile() {
        Log.d(TAG, "chooseFile: Membuka file chooser...");

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        startActivityForResult(Intent.createChooser(intent, "Pilih Dokumen Pendukung"), FILE_SELECT_CODE);

        Log.d(TAG, "chooseFile: File chooser dibuka");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(TAG, "onActivityResult: RequestCode = " + requestCode + ", ResultCode = " + resultCode);

        if (requestCode == FILE_SELECT_CODE && resultCode == RESULT_OK && data != null) {
            fileUri = data.getData();
            String fileName = fileUri.getLastPathSegment();

            // Sederhanakan nama file
            if (fileName.length() > 30) {
                fileName = fileName.substring(0, 15) + "..." + fileName.substring(fileName.lastIndexOf('.') - 4);
            }

            tFileName.setText(fileName);

            Log.d(TAG, "onActivityResult: File berhasil dipilih -> " + fileName);
            Toast.makeText(this, "File dipilih: " + fileName, Toast.LENGTH_SHORT).show();
        } else {
            // Jika dibatalkan atau gagal, reset status file
            fileUri = null;
            tFileName.setText("Tidak ada file yang dipilih");
            Log.e(TAG, "onActivityResult: Tidak ada file dipilih atau gagal");
        }
    }

    private void kirimData() {

        Log.d(TAG, "kirimData: === PROSES VALIDASI DIMULAI ===");

        String nama = etNama.getText().toString().trim();
        String ttl = etTTL.getText().toString().trim();
        String jk = etJenisKelamin.getText().toString().trim();
        String agama = etAgama.getText().toString().trim();
        String pekerjaan = etPekerjaan.getText().toString().trim();
        String alamat = etAlamat.getText().toString().trim();
        String kewarganegaraan = etKewarganegaraan.getText().toString().trim();
        String keterangan = etKeterangan.getText().toString().trim();
        String username = usernameUser; // Gunakan username dari SharedPref

        if (username.isEmpty()) {
            Toast.makeText(this, "Akun belum login! Username kosong", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "kirimData: Validasi gagal -> Username kosong");
            return;
        }

        if (nama.isEmpty() || ttl.isEmpty() || jk.isEmpty() || agama.isEmpty() ||
                alamat.isEmpty() || kewarganegaraan.isEmpty() || keterangan.isEmpty()) {

            Log.e(TAG, "kirimData: Validasi gagal -> Ada kolom kosong");
            Toast.makeText(this, "Harap isi semua kolom", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d(TAG, "kirimData: Validasi berhasil. Username: " + username);

        // ============================
        //   PERSIAPAN REQUEST BODY TEKS
        // ============================

        RequestBody rNama = RequestBody.create(MediaType.parse("text/plain"), nama);
        RequestBody rAlamat = RequestBody.create(MediaType.parse("text/plain"), alamat);
        RequestBody rJK = RequestBody.create(MediaType.parse("text/plain"), jk);
        RequestBody rTTL = RequestBody.create(MediaType.parse("text/plain"), ttl);
        RequestBody rPekerjaan = RequestBody.create(MediaType.parse("text/plain"), pekerjaan);
        RequestBody rAgama = RequestBody.create(MediaType.parse("text/plain"), agama);
        RequestBody rKewarganegaraan = RequestBody.create(MediaType.parse("text/plain"), kewarganegaraan);
        RequestBody rKeterangan = RequestBody.create(MediaType.parse("text/plain"), keterangan);
        RequestBody rUsername = RequestBody.create(MediaType.parse("text/plain"), username);

        // ============================
        //   HANDLE FILE (OPSIONAL)
        // ============================

        MultipartBody.Part filePart = null;

        if (fileUri != null) {
            Log.d(TAG, "kirimData: File ditemukan -> " + fileUri.toString());

            try {
                InputStream inputStream = getContentResolver().openInputStream(fileUri);

                // Gunakan ByteArrayOutputStream untuk membaca seluruh data
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int bytesRead;

                if (inputStream == null) {
                    throw new Exception("Input stream is null");
                }

                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    bos.write(buffer, 0, bytesRead);
                }

                // Dapatkan mime type atau gunakan default
                String mimeType = getContentResolver().getType(fileUri);
                if (mimeType == null) {
                    mimeType = "application/octet-stream";
                }

                RequestBody requestFile = RequestBody.create(
                        MediaType.parse(mimeType),
                        bos.toByteArray()
                );

                // Dapatkan nama file yang lengkap
                String fileName = fileUri.getLastPathSegment();

                filePart = MultipartBody.Part.createFormData(
                        "file",
                        fileName, // Kirim nama file asli ke server
                        requestFile
                );

                inputStream.close();

            } catch (Exception e) {
                Log.e(TAG, "kirimData: ERROR membaca/memproses file -> " + e.getMessage());
                Toast.makeText(SKM.this, "Gagal memproses file: " + e.getMessage(), Toast.LENGTH_LONG).show();
                filePart = null;
            }
        }

        // Buat filePart placeholder jika null (file opsional)
        MultipartBody.Part fotoPartFix = (filePart != null)
                ? filePart
                : MultipartBody.Part.createFormData("file", ""); // Part kosong

        Log.i(TAG, "kirimData: === MULAI KIRIM DATA SEBENARNYA KE SERVER ===");

        APIRequestData api = RetroServer.konekRetrofit().create(APIRequestData.class);
        Call<ResponSkm> call = api.uploadSKM(
                rNama, rAlamat, rJK, rTTL, rAgama, rPekerjaan,
                rKewarganegaraan, rKeterangan, rUsername, fotoPartFix // Menggunakan fotoPartFix
        );

        call.enqueue(new Callback<ResponSkm>() {
            @Override
            public void onResponse(Call<ResponSkm> call, Response<ResponSkm> response) {

                Log.d(TAG, "onResponse: Status HTTP = " + response.code());

                if (!response.isSuccessful()) {
                    Log.e(TAG, "onResponse: Gagal -> " + response.errorBody());
                    Toast.makeText(SKM.this, "Gagal mengirim: " + response.code(), Toast.LENGTH_SHORT).show();
                    return;
                }

                ResponSkm respon = response.body();

                Log.i(TAG, "onResponse: SERVER BALAS:"
                        + "\nStatus = " + respon.isStatus()
                        + "\nPesan = " + respon.getMessage());

                Toast.makeText(SKM.this, respon.getMessage(), Toast.LENGTH_SHORT).show();


                // Pindah activity ke halaman permintaan surat
                Intent intent = new Intent(SKM.this, permintaan_surat.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(Call<ResponSkm> call, Throwable t) {
                Log.e(TAG, "onFailure: ERROR Retrofit -> " + t.getMessage());
                Toast.makeText(SKM.this, "Koneksi gagal: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}