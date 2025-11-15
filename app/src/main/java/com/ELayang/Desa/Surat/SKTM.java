package com.ELayang.Desa.Surat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import java.io.IOException;


import com.ELayang.Desa.API.APIRequestData;
import com.ELayang.Desa.API.RetroServer;
import com.ELayang.Desa.DataModel.Surat.ResponSktm;
import com.ELayang.Desa.R;
import com.ELayang.Desa.utils.FileUtils;


import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SKTM extends AppCompatActivity {

    EditText etNama, etTtl, etAsalSekolah, etKeperluan,
            etNamaOrtu, etNikOrtu, etAlamatOrtu, etTtlOrtu, etPekerjaanOrtu;

    Button btnPilihFoto, btnKirim;
    ImageView imgPreview;

    Uri uriFoto;
    MultipartBody.Part filePart;

    SharedPreferences sp;
    String usernameUser, idPejabatDesa, kodeSurat = "SKTM";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.surat_sktm);

        // Ambil SharedPreferences user login
        sp = getSharedPreferences("USER_DATA", MODE_PRIVATE);
        usernameUser = sp.getString("username", "");
        idPejabatDesa = sp.getString("id_pejabat_desa", "");

        etNama = findViewById(R.id.etNama);
        etTtl = findViewById(R.id.etTtl);
        etAsalSekolah = findViewById(R.id.etAsalSekolah);
        etKeperluan = findViewById(R.id.etKeperluan);
        etNamaOrtu = findViewById(R.id.etNamaOrtu);
        etNikOrtu = findViewById(R.id.etNikOrtu);
        etAlamatOrtu = findViewById(R.id.etAlamatOrtu);
        etTtlOrtu = findViewById(R.id.etTtlOrtu);
        etPekerjaanOrtu = findViewById(R.id.etPekerjaanOrtu);

        btnPilihFoto = findViewById(R.id.btnPilihFoto);
        btnKirim = findViewById(R.id.btnKirim);
        imgPreview = findViewById(R.id.imgPreview);

        btnPilihFoto.setOnClickListener(v -> pilihFoto());
        btnKirim.setOnClickListener(v -> kirimData());
    }

    private void pilihFoto() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            uriFoto = data.getData();
            imgPreview.setImageURI(uriFoto);

            try {
                // pakai helper FileUtils baru
                filePart = FileUtils.prepareFilePart(this, "file", uriFoto);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Gagal membaca file: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void kirimData() {

        if (filePart == null) {
            Toast.makeText(this, "Silakan pilih foto terlebih dahulu", Toast.LENGTH_SHORT).show();
            return;
        }

        APIRequestData api = RetroServer.konekRetrofit().create(APIRequestData.class);

        Call<ResponSktm> call = api.sktm(
                rb(etNama.getText().toString()),
                rb(etTtl.getText().toString()),
                rb(etAsalSekolah.getText().toString()),
                rb(etKeperluan.getText().toString()),
                rb(etNamaOrtu.getText().toString()),
                rb(etNikOrtu.getText().toString()),
                rb(etAlamatOrtu.getText().toString()),
                rb(etTtlOrtu.getText().toString()),
                rb(etPekerjaanOrtu.getText().toString()),
                rb(kodeSurat),         // FIX: Tidak dummy
                rb(idPejabatDesa),     // FIX: Ambil dari SharedPreferences
                rb(usernameUser),      // FIX: Ambil dari SharedPreferences
                filePart
        );

        call.enqueue(new Callback<ResponSktm>() {
            @Override
            public void onResponse(Call<ResponSktm> call, Response<ResponSktm> response) {

                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(SKTM.this, response.body().getPesan(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SKTM.this, "Response tidak lengkap", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponSktm> call, Throwable t) {
                Toast.makeText(SKTM.this, "Gagal: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private RequestBody rb(String value) {
        return RequestBody.create(MediaType.parse("text/plain"), value);
    }
}
