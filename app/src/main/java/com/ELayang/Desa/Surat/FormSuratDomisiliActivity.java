package com.ELayang.Desa.Surat;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ELayang.Desa.API.APIRequestData;
import com.ELayang.Desa.API.RetroServer;
import com.ELayang.Desa.DataModel.Surat.ResponDomisili;
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

public class FormSuratDomisiliActivity extends AppCompatActivity {

    ImageButton btnBack;
    private EditText etNama, etNik, etTTL, etAlamat,
            etPekerjaan, etAgama, etStatusPerkawinan, etKeterangan;
    private Spinner spinnerJK;
    private Button btnKirim;
    private TextView btnPilihFile;
    private ImageView imgPreview;

    private Uri uriFile;
    private MultipartBody.Part filePart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_surat_domisili);

        // Inisialisasi EditText
        etNama = findViewById(R.id.etNama);
        etNik = findViewById(R.id.etNik);
        etTTL = findViewById(R.id.etTTL);
        etAlamat = findViewById(R.id.etAlamat);
        etPekerjaan = findViewById(R.id.etPekerjaan);
        etAgama = findViewById(R.id.etAgama);
        etStatusPerkawinan = findViewById(R.id.etStatusPerkawinan);
        etKeterangan = findViewById(R.id.etKeterangan);

        // Inisialisasi Spinner
        spinnerJK = findViewById(R.id.spinnerJK);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.jenis_kelamin_array,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerJK.setAdapter(adapter);

        // Inisialisasi komponen aksi
        btnKirim = findViewById(R.id.btnKirim);
        btnPilihFile = findViewById(R.id.btnPilihFile);
        btnBack = findViewById(R.id.btnBack);
        imgPreview = findViewById(R.id.imgPreview);

        // Tombol Kembali
        btnBack.setOnClickListener(view -> onBackPressed());

        // Pilih File
        btnPilihFile.setOnClickListener(v -> pilihFile());

        // Kirim Data (dengan validasi & konfirmasi)
        btnKirim.setOnClickListener(v -> {

            if (!validateForm()) return;

            new android.app.AlertDialog.Builder(FormSuratDomisiliActivity.this)
                    .setTitle("Konfirmasi Pengajuan")
                    .setMessage("Kirim Pengajuan Surat Domisili?")
                    .setPositiveButton("KIRIM", (dialog, which) -> kirimData())
                    .setNegativeButton("BATAL", (dialog, which) -> dialog.dismiss())
                    .show();
        });
    }

    private void pilihFile() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 101);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 101 && resultCode == RESULT_OK && data != null) {
            uriFile = data.getData();
            imgPreview.setImageURI(uriFile);
            imgPreview.setVisibility(ImageView.VISIBLE);

            try {
                InputStream is = getContentResolver().openInputStream(uriFile);
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                int nRead;
                byte[] temp = new byte[4096];
                while ((nRead = is.read(temp)) != -1)
                    buffer.write(temp, 0, nRead);
                byte[] fileBytes = buffer.toByteArray();
                is.close();

                RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), fileBytes);
                filePart = MultipartBody.Part.createFormData(
                        "file",
                        "domisili_" + System.currentTimeMillis() + ".jpg",
                        reqFile
                );

                Log.d("Domisili", "File siap diupload");
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Gagal membaca file", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // ======================== VALIDASI ===========================

    // Tambahan: deteksi emoji
    private boolean containsEmoji(String text) {
        for (int i = 0; i < text.length(); i++) {
            int type = Character.getType(text.charAt(i));
            if (type == Character.SURROGATE || type == Character.OTHER_SYMBOL) {
                return true;
            }
        }
        return false;
    }

    private boolean validateForm() {

        if (etNama.getText().toString().trim().isEmpty()) {
            etNama.setError("Nama tidak boleh kosong");
            etNama.requestFocus();
            return false;
        }

        if (etNik.getText().toString().trim().isEmpty()) {
            etNik.setError("NIK tidak boleh kosong");
            etNik.requestFocus();
            return false;
        }

        if (etTTL.getText().toString().trim().isEmpty()) {
            etTTL.setError("TTL tidak boleh kosong");
            etTTL.requestFocus();
            return false;
        }

        if (etAlamat.getText().toString().trim().isEmpty()) {
            etAlamat.setError("Alamat tidak boleh kosong");
            etAlamat.requestFocus();
            return false;
        }

        if (etPekerjaan.getText().toString().trim().isEmpty()) {
            etPekerjaan.setError("Pekerjaan tidak boleh kosong");
            etPekerjaan.requestFocus();
            return false;
        }

        if (etAgama.getText().toString().trim().isEmpty()) {
            etAgama.setError("Agama tidak boleh kosong");
            etAgama.requestFocus();
            return false;
        }

        if (etStatusPerkawinan.getText().toString().trim().isEmpty()) {
            etStatusPerkawinan.setError("Status Perkawinan tidak boleh kosong");
            etStatusPerkawinan.requestFocus();
            return false;
        }

        if (etKeterangan.getText().toString().trim().isEmpty()) {
            etKeterangan.setError("Keterangan tidak boleh kosong");
            etKeterangan.requestFocus();
            return false;
        }

        // ================= TAMBAHAN VALIDASI EMOJI =================
        if (containsEmoji(etNama.getText().toString())) {
            etNama.setError("Tidak boleh mengandung emoji");
            return false;
        }
        if (containsEmoji(etNik.getText().toString())) {
            etNik.setError("Tidak boleh mengandung emoji");
            return false;
        }
        if (containsEmoji(etTTL.getText().toString())) {
            etTTL.setError("Tidak boleh mengandung emoji");
            return false;
        }
        if (containsEmoji(etAlamat.getText().toString())) {
            etAlamat.setError("Tidak boleh mengandung emoji");
            return false;
        }
        if (containsEmoji(etPekerjaan.getText().toString())) {
            etPekerjaan.setError("Tidak boleh mengandung emoji");
            return false;
        }
        if (containsEmoji(etAgama.getText().toString())) {
            etAgama.setError("Tidak boleh mengandung emoji");
            return false;
        }
        if (containsEmoji(etStatusPerkawinan.getText().toString())) {
            etStatusPerkawinan.setError("Tidak boleh mengandung emoji");
            return false;
        }
        if (containsEmoji(etKeterangan.getText().toString())) {
            etKeterangan.setError("Tidak boleh mengandung emoji");
            return false;
        }

        return true;
    }

    // =============================================================
    private void kirimData() {
        SharedPreferences pref = getSharedPreferences("prefLogin", MODE_PRIVATE);
        String username = pref.getString("username", "");

        if (username.isEmpty()) {
            Toast.makeText(this, "Akun belum login!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Ambil data form
        RequestBody nama = rb(etNama.getText().toString().trim());
        RequestBody nik = rb(etNik.getText().toString().trim());
        RequestBody ttl = rb(etTTL.getText().toString().trim());
        RequestBody alamat = rb(etAlamat.getText().toString().trim());
        RequestBody jk = rb(spinnerJK.getSelectedItem().toString());
        RequestBody pekerjaan = rb(etPekerjaan.getText().toString().trim());
        RequestBody agama = rb(etAgama.getText().toString().trim());
        RequestBody status = rb(etStatusPerkawinan.getText().toString().trim());
        RequestBody keterangan = rb(etKeterangan.getText().toString().trim());
        RequestBody user = rb(username);

        // File opsional → kirim part kosong jika tidak ada
        MultipartBody.Part fileFix = (filePart != null)
                ? filePart
                : MultipartBody.Part.createFormData("file", "");

        APIRequestData api = RetroServer.konekRetrofit().create(APIRequestData.class);
        Call<ResponDomisili> call = api.kirimSuratDomisili(
                nama, nik, ttl, alamat, jk, pekerjaan, agama, status, keterangan, user, fileFix
        );

        call.enqueue(new Callback<ResponDomisili>() {
            @Override
            public void onResponse(Call<ResponDomisili> call, Response<ResponDomisili> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String pesan = response.body().getPesan();
                    if (pesan == null || pesan.isEmpty()) pesan = "Pengajuan selesai";

                    Toast.makeText(FormSuratDomisiliActivity.this, pesan, Toast.LENGTH_SHORT).show();
                    clearForm();
                    finish();
                } else {
                    Toast.makeText(FormSuratDomisiliActivity.this, "Gagal mengirim data ke server", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponDomisili> call, Throwable t) {
                String pesanError = t.getMessage() != null ? t.getMessage() : "Kesalahan tidak diketahui";
                Toast.makeText(FormSuratDomisiliActivity.this, "Kesalahan koneksi: " + pesanError, Toast.LENGTH_LONG).show();
            }
        });
    }

    private RequestBody rb(String value) {
        return RequestBody.create(MediaType.parse("text/plain"), value);
    }

    private void clearForm() {
        etNama.setText("");
        etNik.setText("");
        etTTL.setText("");
        etAlamat.setText("");
        spinnerJK.setSelection(0);
        etPekerjaan.setText("");
        etAgama.setText("");
        etStatusPerkawinan.setText("");
        etKeterangan.setText("");
        imgPreview.setImageURI(null);
        imgPreview.setVisibility(ImageView.GONE);
        filePart = null;
    }
}
