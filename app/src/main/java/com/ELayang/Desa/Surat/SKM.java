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

// Tambahkan untuk membaca file
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

    private EditText etNama, etTTL, etJenisKelamin, etAgama, etAlamat, etKewarganegaraan, etKeterangan;
    private Button btnKirim, btnChooseFile;
    private TextView tFileName;
    private Uri fileUri;

    private static final int FILE_SELECT_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.surat_skm);

        Log.d(TAG, "onCreate: Activity Surat Keterangan Kematian dimulai");

        etNama = findViewById(R.id.etNama);
        etTTL = findViewById(R.id.etTTL);
        etJenisKelamin = findViewById(R.id.etJenisKelamin);
        etAgama = findViewById(R.id.etAgama);
        etAlamat = findViewById(R.id.etAlamat);
        etKewarganegaraan = findViewById(R.id.etkewarganegaraan);
        etKeterangan = findViewById(R.id.etKeterangan);

        btnKirim = findViewById(R.id.btnKirim);
        btnChooseFile = findViewById(R.id.btn_choose_file);
        tFileName = findViewById(R.id.t_file_name);

        Log.d(TAG, "onCreate: Semua komponen view berhasil di-inisialisasi");

        btnChooseFile.setOnClickListener(v -> chooseFile());
        btnKirim.setOnClickListener(v -> kirimData());
    }

    private void chooseFile() {
        Log.d(TAG, "chooseFile: Membuka file chooser...");

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        startActivityForResult(Intent.createChooser(intent, "Pilih File"), FILE_SELECT_CODE);

        Log.d(TAG, "chooseFile: File chooser dibuka");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(TAG, "onActivityResult: RequestCode = " + requestCode + ", ResultCode = " + resultCode);

        if (requestCode == FILE_SELECT_CODE && resultCode == RESULT_OK && data != null) {
            fileUri = data.getData();
            String fileName = fileUri.getLastPathSegment();

            tFileName.setText(fileName);

            Log.d(TAG, "onActivityResult: File berhasil dipilih -> " + fileName);
            Toast.makeText(this, "File dipilih: " + fileName, Toast.LENGTH_SHORT).show();
        } else {
            Log.e(TAG, "onActivityResult: Tidak ada file dipilih atau gagal");
        }
    }

    private void kirimData() {

        Log.d(TAG, "kirimData: === PROSES VALIDASI DIMULAI ===");

        String nama = etNama.getText().toString().trim();
        String ttl = etTTL.getText().toString().trim();
        String jk = etJenisKelamin.getText().toString().trim();
        String agama = etAgama.getText().toString().trim();
        String alamat = etAlamat.getText().toString().trim();
        String kewarganegaraan = etKewarganegaraan.getText().toString().trim();
        String keterangan = etKeterangan.getText().toString().trim();
        String username = "aldo_pandu"; // ganti pakai session / sharedpref kamu

        Log.d(TAG, "Input user:"
                + "\nNama=" + nama
                + "\nTTL=" + ttl
                + "\nJK=" + jk
                + "\nAgama=" + agama
                + "\nAlamat=" + alamat
                + "\nKewarganegaraan=" + kewarganegaraan
                + "\nKeterangan=" + keterangan
                + "\nUsername=" + username);

        if (nama.isEmpty() || ttl.isEmpty() || jk.isEmpty() || agama.isEmpty() ||
                alamat.isEmpty() || kewarganegaraan.isEmpty() || keterangan.isEmpty()) {

            Log.e(TAG, "kirimData: Validasi gagal -> Ada kolom kosong");
            Toast.makeText(this, "Harap isi semua kolom", Toast.LENGTH_SHORT).show();
            return;
        }

        // ============================
        //   PERSIAPAN REQUEST BODY
        // ============================

        RequestBody rNama = RequestBody.create(MediaType.parse("text/plain"), nama);
        RequestBody rAlamat = RequestBody.create(MediaType.parse("text/plain"), alamat);
        RequestBody rJK = RequestBody.create(MediaType.parse("text/plain"), jk);
        RequestBody rTTL = RequestBody.create(MediaType.parse("text/plain"), ttl);
        RequestBody rAgama = RequestBody.create(MediaType.parse("text/plain"), agama);
        RequestBody rKewarganegaraan = RequestBody.create(MediaType.parse("text/plain"), kewarganegaraan);
        RequestBody rKeterangan = RequestBody.create(MediaType.parse("text/plain"), keterangan);
        RequestBody rUsername = RequestBody.create(MediaType.parse("text/plain"), username);

        // ============================
        //   HANDLE FILE (JIKA ADA)
        // ============================

        MultipartBody.Part filePart = null;

        if (fileUri != null) {
            Log.d(TAG, "kirimData: File ditemukan -> " + fileUri.toString());

            try {
                InputStream inputStream = getContentResolver().openInputStream(fileUri);
                byte[] fileBytes = new byte[inputStream.available()];
                inputStream.read(fileBytes);

                RequestBody requestFile = RequestBody.create(
                        MediaType.parse("application/octet-stream"),
                        fileBytes
                );

                filePart = MultipartBody.Part.createFormData(
                        "file",
                        tFileName.getText().toString(),
                        requestFile
                );

            } catch (Exception e) {
                Log.e(TAG, "kirimData: ERROR membaca file -> " + e.getMessage());
            }

        } else {
            Log.w(TAG, "kirimData: Tidak ada file dipilih");
        }

        Log.i(TAG, "kirimData: === MULAI KIRIM DATA SEBENARNYA KE SERVER ===");

        APIRequestData api = RetroServer.konekRetrofit().create(APIRequestData.class);
        Call<ResponSkm> call = api.uploadSKM(
                rNama, rAlamat, rJK, rTTL, rAgama,
                rKewarganegaraan, rKeterangan, rUsername, filePart
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
                        + "\nPesan = " + respon.getMessage()
                        + "\nData = " + respon.getData());

                Toast.makeText(SKM.this, respon.getMessage(), Toast.LENGTH_SHORT).show();


                // Pindah activity
                Intent intent = new Intent(SKM.this, permintaan_surat.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(Call<ResponSkm> call, Throwable t) {
                Log.e(TAG, "onFailure: ERROR Retrofit -> " + t.getMessage());
                Toast.makeText(SKM.this, "Koneksi gagal: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}