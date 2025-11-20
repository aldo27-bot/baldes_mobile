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
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ImageButton;
import android.widget.TextView;

import com.ELayang.Desa.API.APIRequestData;
import com.ELayang.Desa.API.RetroServer;
import com.ELayang.Desa.DataModel.Surat.ResponSktm;
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

public class SKTM extends AppCompatActivity {

    ImageButton btnBack;

    EditText etNama, etTtl, etAsalSekolah, etKeperluan,
            etNamaOrtu, etNikOrtu, etAlamatOrtu, etTtlOrtu, etPekerjaanOrtu;

    TextView btnPilihFoto, tvNamaFoto;
    Button btnKirim;
    ImageView imgPreview;

    Uri uriFoto;
    MultipartBody.Part filePart;

    String usernameUser;
    String kodeSurat = "SKTM";

    private static final int PICK_IMAGE_REQUEST = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.surat_sktm);

        // Ambil username
        SharedPreferences pref = getSharedPreferences("prefLogin", MODE_PRIVATE);
        usernameUser = pref.getString("username", "");

        // Inisialisasi EditText
        etNama = findViewById(R.id.etNama);
        etTtl = findViewById(R.id.etTtl);
        etAsalSekolah = findViewById(R.id.etAsalSekolah);
        etKeperluan = findViewById(R.id.etKeperluan);
        etNamaOrtu = findViewById(R.id.etNamaOrtu);
        etNikOrtu = findViewById(R.id.etNikOrtu);
        etAlamatOrtu = findViewById(R.id.etAlamatOrtu);
        etTtlOrtu = findViewById(R.id.etTtlOrtu);
        etPekerjaanOrtu = findViewById(R.id.etPekerjaanOrtu);

        // Inisialisasi komponen aksi
        btnBack = findViewById(R.id.btnBack);
        btnPilihFoto = findViewById(R.id.btnPilihFoto);
        tvNamaFoto = findViewById(R.id.tvNamaFoto);
        btnKirim = findViewById(R.id.btnKirim);
        imgPreview = findViewById(R.id.imgPreview);

        // Tombol aksi
        btnBack.setOnClickListener(v -> onBackPressed());
        btnPilihFoto.setOnClickListener(v -> pilihFoto());
        btnKirim.setOnClickListener(v -> kirimData());

        // Default tampilan
        tvNamaFoto.setText("Tidak ada foto (opsional)");
        imgPreview.setVisibility(ImageView.GONE);
    }

    private void pilihFoto() {
        Intent i = new Intent(Intent.ACTION_PICK);
        i.setType("image/*");
        startActivityForResult(i, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            uriFoto = data.getData();
            imgPreview.setImageURI(uriFoto);
            imgPreview.setVisibility(ImageView.VISIBLE);

            // Coba baca file
            try {
                InputStream is = getContentResolver().openInputStream(uriFoto);
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                int n;
                byte[] temp = new byte[4096];
                while ((n = is.read(temp)) != -1) buffer.write(temp, 0, n);
                byte[] fileBytes = buffer.toByteArray();
                is.close();

                RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), fileBytes);

                filePart = MultipartBody.Part.createFormData(
                        "file",
                        "sktm_" + System.currentTimeMillis() + ".jpg",
                        reqFile
                );

                tvNamaFoto.setText("Foto dipilih");

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Gagal membaca foto", Toast.LENGTH_SHORT).show();
                filePart = null;
            }

        } else {
            // Reset jika batal
            uriFoto = null;
            filePart = null;
            tvNamaFoto.setText("Tidak ada foto (opsional)");
            imgPreview.setVisibility(ImageView.GONE);
        }
    }

    private void kirimData() {
        if (usernameUser.isEmpty()) {
            Toast.makeText(this, "Akun belum login!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validasi field wajib
        if (etNama.getText().toString().isEmpty() ||
                etTtl.getText().toString().isEmpty() ||
                etAsalSekolah.getText().toString().isEmpty() ||
                etKeperluan.getText().toString().isEmpty() ||
                etNamaOrtu.getText().toString().isEmpty() ||
                etNikOrtu.getText().toString().isEmpty() ||
                etAlamatOrtu.getText().toString().isEmpty() ||
                etTtlOrtu.getText().toString().isEmpty() ||
                etPekerjaanOrtu.getText().toString().isEmpty()) {

            Toast.makeText(this, "Lengkapi semua data!", Toast.LENGTH_SHORT).show();
            return;
        }

        // RequestBody
        RequestBody nama = rb(etNama.getText().toString());
        RequestBody ttl = rb(etTtl.getText().toString());
        RequestBody sekolah = rb(etAsalSekolah.getText().toString());
        RequestBody keperluan = rb(etKeperluan.getText().toString());
        RequestBody ortu = rb(etNamaOrtu.getText().toString());
        RequestBody nikOrtu = rb(etNikOrtu.getText().toString());
        RequestBody alamatOrtu = rb(etAlamatOrtu.getText().toString());
        RequestBody ttlOrtu = rb(etTtlOrtu.getText().toString());
        RequestBody kerjaOrtu = rb(etPekerjaanOrtu.getText().toString());
        RequestBody kode = rb(kodeSurat);
        RequestBody user = rb(usernameUser);

        MultipartBody.Part fotoFix = filePart; // biarkan null jika tidak ada file


        APIRequestData api = RetroServer.konekRetrofit().create(APIRequestData.class);

        Call<ResponSktm> call = api.sktm(
                nama, ttl, sekolah, keperluan,
                ortu, nikOrtu, alamatOrtu, ttlOrtu, kerjaOrtu,
                kode, user, fotoFix
        );

        call.enqueue(new Callback<ResponSktm>() {
            @Override
            public void onResponse(Call<ResponSktm> call, Response<ResponSktm> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(SKTM.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    clearForm();

                    Intent intent = new Intent(SKTM.this, permintaan_surat.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(SKTM.this, "Gagal mengirim data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponSktm> call, Throwable t) {
                Toast.makeText(SKTM.this, "Kesalahan koneksi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private RequestBody rb(String value) {
        return RequestBody.create(MediaType.parse("text/plain"), value);
    }

    private void clearForm() {
        etNama.setText("");
        etTtl.setText("");
        etAsalSekolah.setText("");
        etKeperluan.setText("");
        etNamaOrtu.setText("");
        etNikOrtu.setText("");
        etAlamatOrtu.setText("");
        etTtlOrtu.setText("");
        etPekerjaanOrtu.setText("");

        imgPreview.setImageURI(null);
        imgPreview.setVisibility(ImageView.GONE);
        filePart = null;
    }
}
