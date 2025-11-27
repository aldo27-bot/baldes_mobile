package com.ELayang.Desa.Surat;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ELayang.Desa.API.APIRequestData;
import com.ELayang.Desa.API.RetroServer;
import com.ELayang.Desa.DataModel.Surat.ResponSkm;
import com.ELayang.Desa.Menu.permintaan_surat;
import com.ELayang.Desa.R;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SKM extends AppCompatActivity {

    private static final String TAG = "SKM_ACTIVITY";

    private ImageButton btnBack;
    private EditText etNama, etTTL, etPekerjaan, etAgama, etAlamat, etKewarganegaraan, etKeterangan;
    private Spinner spinnerJK; // 📌 Dropdown untuk Jenis Kelamin
    private Button btnKirim, btnChooseFile;
    private TextView tFileName;
    private Uri fileUri;

    private static final int FILE_SELECT_CODE = 100;
    private String usernameUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.surat_skm);

        Log.d(TAG, "onCreate: Activity SKM dimulai");

        // Ambil username dari SharedPreferences
        usernameUser = getSharedPreferences("prefLogin", MODE_PRIVATE).getString("username", "");
        Log.d(TAG, "Username dari SharedPref: " + usernameUser);

        // Inisialisasi Komponen
        btnBack = findViewById(R.id.btnBack);

        etNama = findViewById(R.id.etNama);
        etTTL = findViewById(R.id.etTTL);
        etPekerjaan = findViewById(R.id.etPekerjaan);
        etAgama = findViewById(R.id.etAgama);
        etAlamat = findViewById(R.id.etAlamat);
        etKewarganegaraan = findViewById(R.id.etkewarganegaraan);
        etKeterangan = findViewById(R.id.etKeterangan);

        spinnerJK = findViewById(R.id.spinnerJK); // Spinner jenis kelamin
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.jenis_kelamin_array, // harus ada di res/values/strings.xml
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerJK.setAdapter(adapter);

        btnKirim = findViewById(R.id.btnKirim);
        btnChooseFile = findViewById(R.id.btn_choose_file);
        tFileName = findViewById(R.id.t_file_name);

        btnBack.setOnClickListener(v -> onBackPressed());
        btnChooseFile.setOnClickListener(v -> chooseFile());
        btnKirim.setOnClickListener(v -> kirimData());

        tFileName.setText("Tidak ada file yang dipilih");
    }

    private void chooseFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(intent, "Pilih Dokumen Pendukung"), FILE_SELECT_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == FILE_SELECT_CODE && resultCode == RESULT_OK && data != null) {
            fileUri = data.getData();
            String fileName = fileUri.getLastPathSegment();

            if (fileName.length() > 30) {
                fileName = fileName.substring(0, 15) + "..." + fileName.substring(fileName.lastIndexOf('.') - 4);
            }

            tFileName.setText(fileName);
            Toast.makeText(this, "File dipilih: " + fileName, Toast.LENGTH_SHORT).show();
        } else {
            fileUri = null;
            tFileName.setText("Tidak ada file yang dipilih");
        }
    }

    private void kirimData() {
        String nama = etNama.getText().toString().trim();
        String ttl = etTTL.getText().toString().trim();
        String jk = spinnerJK.getSelectedItem().toString(); // Ambil dari spinner
        String agama = etAgama.getText().toString().trim();
        String pekerjaan = etPekerjaan.getText().toString().trim();
        String alamat = etAlamat.getText().toString().trim();
        String kewarganegaraan = etKewarganegaraan.getText().toString().trim();
        String keterangan = etKeterangan.getText().toString().trim();
        String username = usernameUser;

        if (username.isEmpty()) {
            Toast.makeText(this, "Akun belum login!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (nama.isEmpty() || ttl.isEmpty() || jk.isEmpty() || agama.isEmpty() ||
                alamat.isEmpty() || kewarganegaraan.isEmpty() || keterangan.isEmpty()) {
            Toast.makeText(this, "Harap isi semua kolom", Toast.LENGTH_SHORT).show();
            return;
        }

        RequestBody rNama = RequestBody.create(MediaType.parse("text/plain"), nama);
        RequestBody rAlamat = RequestBody.create(MediaType.parse("text/plain"), alamat);
        RequestBody rJK = RequestBody.create(MediaType.parse("text/plain"), jk);
        RequestBody rTTL = RequestBody.create(MediaType.parse("text/plain"), ttl);
        RequestBody rPekerjaan = RequestBody.create(MediaType.parse("text/plain"), pekerjaan);
        RequestBody rAgama = RequestBody.create(MediaType.parse("text/plain"), agama);
        RequestBody rKewarganegaraan = RequestBody.create(MediaType.parse("text/plain"), kewarganegaraan);
        RequestBody rKeterangan = RequestBody.create(MediaType.parse("text/plain"), keterangan);
        RequestBody rUsername = RequestBody.create(MediaType.parse("text/plain"), username);

        MultipartBody.Part filePart = null;

        if (fileUri != null) {
            try {
                InputStream inputStream = getContentResolver().openInputStream(fileUri);
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    bos.write(buffer, 0, bytesRead);
                }
                String mimeType = getContentResolver().getType(fileUri);
                if (mimeType == null) mimeType = "application/octet-stream";
                RequestBody requestFile = RequestBody.create(MediaType.parse(mimeType), bos.toByteArray());
                String fileName = fileUri.getLastPathSegment();
                filePart = MultipartBody.Part.createFormData("file", fileName, requestFile);
                inputStream.close();
            } catch (Exception e) {
                Toast.makeText(this, "Gagal memproses file: " + e.getMessage(), Toast.LENGTH_LONG).show();
                filePart = null;
            }
        }

        MultipartBody.Part filePartFix = (filePart != null) ? filePart : MultipartBody.Part.createFormData("file", "");

        APIRequestData api = RetroServer.konekRetrofit().create(APIRequestData.class);
        Call<ResponSkm> call = api.uploadSKM(rNama, rAlamat, rJK, rTTL, rAgama, rPekerjaan,
                rKewarganegaraan, rKeterangan, rUsername, filePartFix);

        call.enqueue(new Callback<ResponSkm>() {
            @Override
            public void onResponse(Call<ResponSkm> call, Response<ResponSkm> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(SKM.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(SKM.this, permintaan_surat.class)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
                    finish();
                } else {
                    Toast.makeText(SKM.this, "Gagal mengirim data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponSkm> call, Throwable t) {
                Toast.makeText(SKM.this, "Koneksi gagal: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
