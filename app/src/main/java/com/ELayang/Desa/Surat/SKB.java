package com.ELayang.Desa.Surat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ELayang.Desa.API.APIRequestData;
import com.ELayang.Desa.API.RetroServer;
import com.ELayang.Desa.DataModel.Surat.ResponSkb;
import com.ELayang.Desa.R;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SKB extends AppCompatActivity {

    private EditText etNama, etNik, etAgama, etTTL, etPendidikan, etAlamat;
    private Button btnKirim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_surat_berkelakuan_baik);

        etNama = findViewById(R.id.etNama);
        etNik = findViewById(R.id.etNik);
        etAgama = findViewById(R.id.etAgama);
        etTTL = findViewById(R.id.etTTL);
        etPendidikan = findViewById(R.id.etPendidikan);
        etAlamat = findViewById(R.id.etAlamat);
        btnKirim = findViewById(R.id.btnKirim);

        btnKirim.setOnClickListener(v -> kirimData());
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

        if (nama.isEmpty() || nik.isEmpty()) {
            Toast.makeText(this, "Nama dan NIK harus diisi", Toast.LENGTH_SHORT).show();
            return;
        }

        if (username.isEmpty()) {
            Toast.makeText(this, "Akun belum login!", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            RequestBody rbNama = RequestBody.create(MediaType.parse("text/plain"), nama);
            RequestBody rbNik = RequestBody.create(MediaType.parse("text/plain"), nik);
            RequestBody rbAgama = RequestBody.create(MediaType.parse("text/plain"), agama);
            RequestBody rbTTL = RequestBody.create(MediaType.parse("text/plain"), ttl);
            RequestBody rbPendidikan = RequestBody.create(MediaType.parse("text/plain"), pendidikan);
            RequestBody rbAlamat = RequestBody.create(MediaType.parse("text/plain"), alamat);
            RequestBody rbKodeSurat = RequestBody.create(MediaType.parse("text/plain"), "SKBB"); // sesuai data_surat
            RequestBody rbIdPejabat = RequestBody.create(MediaType.parse("text/plain"), ""); // null / kosong
            RequestBody rbUsername = RequestBody.create(MediaType.parse("text/plain"), username);

            APIRequestData api = RetroServer.konekRetrofit().create(APIRequestData.class);
            Call<ResponSkb> kirim = api.kirimskb(
                    rbNama, rbNik, rbAgama, rbTTL, rbPendidikan, rbAlamat,
                    null, // file tidak dikirim
                    rbKodeSurat, rbIdPejabat, rbUsername
            );

            kirim.enqueue(new Callback<ResponSkb>() {
                @Override
                public void onResponse(Call<ResponSkb> call, Response<ResponSkb> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Toast.makeText(SKB.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        clearForm();
                    } else {
                        Toast.makeText(SKB.this, "Gagal mengirim data ke server", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponSkb> call, Throwable t) {
                    Toast.makeText(SKB.this, "Kesalahan koneksi: " + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });

        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void clearForm() {
        etNama.setText("");
        etNik.setText("");
        etAgama.setText("");
        etTTL.setText("");
        etPendidikan.setText("");
        etAlamat.setText("");
    }
}
