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
    private EditText etNama, etNik, etTTL, etAlamat, etPekerjaan, etKeterangan;
    private Spinner spinnerJK, spinnerAgama, spinnerStatus;
    private Button btnKirim;
    private TextView btnPilihFile;
    private ImageView imgPreview;

    private Uri uriFile;
    private MultipartBody.Part filePart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_surat_domisili);

        // EditText
        etNama = findViewById(R.id.etNama);
        etNik = findViewById(R.id.etNik);
        etTTL = findViewById(R.id.etTTL);
        etAlamat = findViewById(R.id.etAlamat);
        etPekerjaan = findViewById(R.id.etPekerjaan);
        etKeterangan = findViewById(R.id.etKeterangan);

        // Spinner Jenis Kelamin
        spinnerJK = findViewById(R.id.spinnerJK);
        ArrayAdapter<CharSequence> adapterJK = ArrayAdapter.createFromResource(
                this, R.array.jenis_kelamin_array, android.R.layout.simple_spinner_item
        );
        adapterJK.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerJK.setAdapter(adapterJK);

        // Spinner Agama (NEW)
        spinnerAgama = findViewById(R.id.spinnerAgama);
        ArrayAdapter<CharSequence> adapterAgama = ArrayAdapter.createFromResource(
                this, R.array.agama_resmi_array, android.R.layout.simple_spinner_item
        );
        adapterAgama.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAgama.setAdapter(adapterAgama);

        // Spinner Status Perkawinan (NEW)
        spinnerStatus = findViewById(R.id.spinnerStatusPerkawinan);
        ArrayAdapter<CharSequence> adapterStatus = ArrayAdapter.createFromResource(
                this, R.array.status_perkawinan_array, android.R.layout.simple_spinner_item
        );
        adapterStatus.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(adapterStatus);

        btnKirim = findViewById(R.id.btnKirim);
        btnPilihFile = findViewById(R.id.btnPilihFile);
        btnBack = findViewById(R.id.btnBack);
        imgPreview = findViewById(R.id.imgPreview);

        btnBack.setOnClickListener(v -> onBackPressed());

        btnPilihFile.setOnClickListener(v -> pilihFile());

        btnKirim.setOnClickListener(v -> {
            if (!validateForm()) return;

            new android.app.AlertDialog.Builder(FormSuratDomisiliActivity.this)
                    .setTitle("Konfirmasi Pengajuan")
                    .setMessage("Kirim Pengajuan Surat Domisili?")
                    .setPositiveButton("KIRIM", (dialog, which) -> kirimData())
                    .setNegativeButton("BATAL", null)
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

            } catch (IOException e) {
                Toast.makeText(this, "Gagal membaca file", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // ======================== VALIDASI ===========================

    // Deteksi emoji
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

        String nama = etNama.getText().toString().trim();
        String nik = etNik.getText().toString().trim();

        // Validasi Nama → huruf & spasi saja
        if (nama.isEmpty()) {
            etNama.setError("Nama tidak boleh kosong");
            return false;
        }
        if (!nama.matches("^[A-Za-z ]+$")) {
            etNama.setError("Nama hanya boleh berisi huruf dan spasi");
            return false;
        }
        if (containsEmoji(nama)) {
            etNama.setError("Nama tidak boleh mengandung emoji");
            return false;
        }

        // Validasi NIK → harus 16 digit angka
        if (nik.isEmpty()) {
            etNik.setError("NIK tidak boleh kosong");
            return false;
        }
        if (!nik.matches("^[0-9]{16}$")) {
            etNik.setError("NIK harus 16 digit angka");
            return false;
        }

        // Validasi input wajib lainnya
        if (etTTL.getText().toString().trim().isEmpty()) {
            etTTL.setError("TTL tidak boleh kosong");
            return false;
        }
        if (etAlamat.getText().toString().trim().isEmpty()) {
            etAlamat.setError("Alamat tidak boleh kosong");
            return false;
        }
        if (etPekerjaan.getText().toString().trim().isEmpty()) {
            etPekerjaan.setError("Pekerjaan tidak boleh kosong");
            return false;
        }
        if (etKeterangan.getText().toString().trim().isEmpty()) {
            etKeterangan.setError("Keterangan tidak boleh kosong");
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

        RequestBody nama = rb(etNama.getText().toString().trim());
        RequestBody nik = rb(etNik.getText().toString().trim());
        RequestBody ttl = rb(etTTL.getText().toString().trim());
        RequestBody alamat = rb(etAlamat.getText().toString().trim());
        RequestBody jk = rb(spinnerJK.getSelectedItem().toString());
        RequestBody pekerjaan = rb(etPekerjaan.getText().toString().trim());
        RequestBody agama = rb(spinnerAgama.getSelectedItem().toString());
        RequestBody status = rb(spinnerStatus.getSelectedItem().toString());
        RequestBody keterangan = rb(etKeterangan.getText().toString().trim());
        RequestBody user = rb(username);

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
                    Toast.makeText(FormSuratDomisiliActivity.this,
                            response.body().getPesan(), Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(FormSuratDomisiliActivity.this,
                            "Gagal mengirim data ke server", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponDomisili> call, Throwable t) {
                Toast.makeText(FormSuratDomisiliActivity.this,
                        "Kesalahan koneksi: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private RequestBody rb(String value) {
        return RequestBody.create(MediaType.parse("text/plain"), value);
    }
}
