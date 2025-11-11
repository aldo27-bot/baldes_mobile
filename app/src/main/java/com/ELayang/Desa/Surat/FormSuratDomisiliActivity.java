package com.ELayang.Desa.Surat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ELayang.Desa.API.APIRequestData;
import com.ELayang.Desa.API.RetroServer;
import com.ELayang.Desa.DataModel.Surat.ModelDomisili;
import com.ELayang.Desa.DataModel.Surat.ResponDomisili;
import com.ELayang.Desa.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FormSuratDomisiliActivity extends AppCompatActivity {

    private EditText etNama, etNik, etTempatTanggalLahir, etAlamat,
            etJenisKelamin, etPekerjaan, etAgama, etStatusPerkawinan, etKeterangan;
    private Button btnKirim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_surat_domisili);

        etNama = findViewById(R.id.etNama);
        etNik = findViewById(R.id.etNik);
        etTempatTanggalLahir = findViewById(R.id.etTTL);
        etAlamat = findViewById(R.id.etAlamat);
        etJenisKelamin = findViewById(R.id.etJK);
        etPekerjaan = findViewById(R.id.etPekerjaan);
        etAgama = findViewById(R.id.etAgama);
        etStatusPerkawinan = findViewById(R.id.etStatusPerkawinan);
        etKeterangan = findViewById(R.id.etKeterangan);
        btnKirim = findViewById(R.id.btnKirim);

        btnKirim.setOnClickListener(v -> kirimData());
    }

    private void kirimData() {
        // Ambil username dari SharedPreferences login
        SharedPreferences pref = getSharedPreferences("prefLogin", MODE_PRIVATE);
        String username = pref.getString("username", "");

        // Ambil data dari EditText
        String nama = etNama.getText().toString().trim();
        String nik = etNik.getText().toString().trim();
        String ttl = etTempatTanggalLahir.getText().toString().trim();
        String alamat = etAlamat.getText().toString().trim();
        String jk = etJenisKelamin.getText().toString().trim();
        String pekerjaan = etPekerjaan.getText().toString().trim();
        String agama = etAgama.getText().toString().trim();
        String status = etStatusPerkawinan.getText().toString().trim();
        String keterangan = etKeterangan.getText().toString().trim();

        if (nama.isEmpty() || nik.isEmpty()) {
            Toast.makeText(this, "Nama dan NIK harus diisi", Toast.LENGTH_SHORT).show();
            return;
        }

        if (username.isEmpty()) {
            Toast.makeText(this, "Akun belum login!", Toast.LENGTH_SHORT).show();
            return;
        }

        APIRequestData api = RetroServer.konekRetrofit().create(APIRequestData.class);
        Call<ResponDomisili> kirim = api.kirimSuratDomisili(
                nama, nik, ttl, alamat, jk, pekerjaan, agama, status, keterangan, username
        );

        kirim.enqueue(new Callback<ResponDomisili>() {
            @Override
            public void onResponse(Call<ResponDomisili> call, Response<ResponDomisili> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(FormSuratDomisiliActivity.this, response.body().getPesan(), Toast.LENGTH_SHORT).show();
                    clearForm();
                } else {
                    Toast.makeText(FormSuratDomisiliActivity.this, "Gagal mengirim data ke server", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponDomisili> call, Throwable t) {
                Toast.makeText(FormSuratDomisiliActivity.this, "Kesalahan koneksi: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void clearForm() {
        etNama.setText("");
        etNik.setText("");
        etTempatTanggalLahir.setText("");
        etAlamat.setText("");
        etJenisKelamin.setText("");
        etPekerjaan.setText("");
        etAgama.setText("");
        etStatusPerkawinan.setText("");
        etKeterangan.setText("");
    }
}
