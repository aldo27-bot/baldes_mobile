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
import android.widget.TextView;
import android.widget.Toast;

import com.ELayang.Desa.API.APIRequestData;
import com.ELayang.Desa.API.RetroServer;
import com.ELayang.Desa.DataModel.Surat.ResponSkb;
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

    private EditText etNama, etNik, etAgama, etTTL, etPendidikan, etAlamat;
    private Button btnPilihFile, btnKirim;
    private TextView tvNamaFile;
    private Uri fileUri = null;

    private final ActivityResultLauncher<Intent> filePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    fileUri = result.getData().getData();
                    tvNamaFile.setText(getFileName(fileUri));
                }
            });

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
        btnPilihFile = findViewById(R.id.btnPilihFile);
        btnKirim = findViewById(R.id.btnKirim);
        tvNamaFile = findViewById(R.id.tvNamaFile);

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
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int idx = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (idx >= 0) result = cursor.getString(idx);
                }
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
            String mimeType = resolver.getType(uri);
            InputStream is = resolver.openInputStream(uri);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                bos.write(buffer, 0, bytesRead);
            }
            is.close();

            RequestBody requestFile = RequestBody.create(MediaType.parse(mimeType), bos.toByteArray());
            return MultipartBody.Part.createFormData("file", getFileName(uri), requestFile);

        } catch (Exception e) {
            e.printStackTrace();
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

        if (nama.isEmpty() || nik.isEmpty()) {
            Toast.makeText(this, "Nama dan NIK harus diisi", Toast.LENGTH_SHORT).show();
            return;
        }
        if (username.isEmpty()) {
            Toast.makeText(this, "Akun belum login!", Toast.LENGTH_SHORT).show();
            return;
        }

        RequestBody rbNama = RequestBody.create(MediaType.parse("text/plain"), nama);
        RequestBody rbNik = RequestBody.create(MediaType.parse("text/plain"), nik);
        RequestBody rbAgama = RequestBody.create(MediaType.parse("text/plain"), agama);
        RequestBody rbTTL = RequestBody.create(MediaType.parse("text/plain"), ttl);
        RequestBody rbPendidikan = RequestBody.create(MediaType.parse("text/plain"), pendidikan);
        RequestBody rbAlamat = RequestBody.create(MediaType.parse("text/plain"), alamat);
        RequestBody rbKodeSurat = RequestBody.create(MediaType.parse("text/plain"), "SKBB");
        RequestBody rbIdPejabat = RequestBody.create(MediaType.parse("text/plain"), "");
        RequestBody rbUsername = RequestBody.create(MediaType.parse("text/plain"), username);

        MultipartBody.Part filePart = null;
        if (fileUri != null) {
            filePart = prepareFilePart(fileUri);
        }

        APIRequestData api = RetroServer.konekRetrofit().create(APIRequestData.class);
        Call<ResponSkb> kirim = api.kirimskb(
                rbNama, rbNik, rbAgama, rbTTL, rbPendidikan, rbAlamat,
                filePart,
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
    }

    private void clearForm() {
        etNama.setText("");
        etNik.setText("");
        etAgama.setText("");
        etTTL.setText("");
        etPendidikan.setText("");
        etAlamat.setText("");
        tvNamaFile.setText("Belum ada file dipilih");
        fileUri = null;
    }
}
