package com.ELayang.Desa.Surat;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ELayang.Desa.API.APIRequestData;
import com.ELayang.Desa.API.RetroServer;
import com.ELayang.Desa.DataModel.Surat.ModelDomisili;
import com.ELayang.Desa.DataModel.Surat.ResponDomisili;

import com.ELayang.Desa.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FormSuratDomisiliActivity extends AppCompatActivity {

    private EditText etNama, etNik, etTempatTanggalLahir, etAlamat, etJenisKelamin,
            etPekerjaan, etAgama, etStatusPerkawinan, etKeterangan;
    private Button btnKirim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_surat_domisili);

        // --- Inisialisasi View ---
        etNama = findViewById(R.id.etNama);
        etNik = findViewById(R.id.etNik);
        etTempatTanggalLahir = findViewById(R.id.etTempatTanggalLahir);
        etAlamat = findViewById(R.id.etAlamat);
        etJenisKelamin = findViewById(R.id.etJenisKelamin);
        etPekerjaan = findViewById(R.id.etPekerjaan);
        etAgama = findViewById(R.id.etAgama);
        etStatusPerkawinan = findViewById(R.id.etStatusPerkawinan);
        etKeterangan = findViewById(R.id.etKeterangan);
        btnKirim = findViewById(R.id.btnKirim);

        btnKirim.setOnClickListener(v -> kirimData());
    }

    private void kirimData() {
        String nama = etNama.getText().toString().trim();
        String nik = etNik.getText().toString().trim();
        String ttl = etTempatTanggalLahir.getText().toString().trim();
        String alamat = etAlamat.getText().toString().trim();
        String jk = etJenisKelamin.getText().toString().trim();
        String pekerjaan = etPekerjaan.getText().toString().trim();
        String agama = etAgama.getText().toString().trim();
        String status = etStatusPerkawinan.getText().toString().trim();
        String keterangan = etKeterangan.getText().toString().trim();

        if (nama.isEmpty() || nik.isEmpty() || ttl.isEmpty()) {
            Toast.makeText(this, "Mohon lengkapi data terlebih dahulu", Toast.LENGTH_SHORT).show();
            return;
        }

        APIRequestData api = RetroServer.konekRetrofit().create(APIRequestData.class);
        Call<ResponDomisili> kirim = api.kirimSuratDomisili(
                nama, nik, ttl, alamat, jk, pekerjaan, agama, status, keterangan
        );

        kirim.enqueue(new Callback<ResponDomisili>() {
            @Override
            public void onResponse(Call<ResponDomisili> call, Response<ResponDomisili> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(FormSuratDomisiliActivity.this,
                            "Pengajuan berhasil dikirim!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(FormSuratDomisiliActivity.this,
                            "Gagal mengirim data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponDomisili> call, Throwable t) {
                Toast.makeText(FormSuratDomisiliActivity.this,
                        "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
