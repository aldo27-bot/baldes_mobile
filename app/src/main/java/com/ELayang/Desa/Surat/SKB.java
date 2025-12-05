package com.ELayang.Desa.Surat;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
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
import com.ELayang.Desa.DataModel.Surat.ResponSkb;
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

public class SKB extends AppCompatActivity {

    private ImageButton btnBack;
    private EditText etNama, etNik, etTTL, etPendidikan, etAlamat, etKeperluan;
    private Spinner spinnerAgama;
    private Button btnKirim;
    private Button btnPilihFile;
    private ImageView imgPreview;

    private Uri uriFile;
    private MultipartBody.Part filePart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_surat_berkelakuan_baik);

        // Init EditText
        etNama = findViewById(R.id.etNama);
        etNik = findViewById(R.id.etNik);
        etTTL = findViewById(R.id.etTTL);
        etPendidikan = findViewById(R.id.etPendidikan);
        etAlamat = findViewById(R.id.etAlamat);
        etKeperluan = findViewById(R.id.etKeperluan);

        spinnerAgama = findViewById(R.id.spinnerAgama);

        // SET DROPDOWN AGAMA
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.agama_resmi_array,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAgama.setAdapter(adapter);

        // Init View
        btnKirim = findViewById(R.id.btnKirim);
        btnPilihFile = findViewById(R.id.btnPilihFile);
        btnBack = findViewById(R.id.btnBack);
        imgPreview = findViewById(R.id.imgPreview);

        btnBack.setOnClickListener(v -> onBackPressed());
        btnPilihFile.setOnClickListener(v -> pilihFile());

        btnKirim.setOnClickListener(v -> {

            if (!validateForm()) return;

            new android.app.AlertDialog.Builder(this)
                    .setTitle("Konfirmasi Pengajuan")
                    .setMessage("Kirim Pengajuan Surat Keterangan Berkelakuan Baik?")
                    .setPositiveButton("KIRIM", (dialog, which) -> kirimData())
                    .setNegativeButton("BATAL", (dialog, which) -> dialog.dismiss())
                    .show();
        });
    }

    // =============================
    // PILIH FILE
    // =============================
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
                while ((nRead = is.read(temp)) != -1) buffer.write(temp, 0, nRead);

                byte[] fileBytes = buffer.toByteArray();
                is.close();

                RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), fileBytes);
                filePart = MultipartBody.Part.createFormData(
                        "file",
                        "skb_" + System.currentTimeMillis() + ".jpg",
                        reqFile
                );

            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Gagal membaca file", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // =============================
    // DETEKSI EMOJI
    // =============================
    private boolean containsEmoji(String text) {
        for (int i = 0; i < text.length(); i++) {
            int type = Character.getType(text.charAt(i));
            if (type == Character.SURROGATE || type == Character.OTHER_SYMBOL) return true;
        }
        return false;
    }

    // =============================
    // VALIDASI FORM
    // =============================
    private boolean validateForm() {

        String nama = etNama.getText().toString().trim();
        String nik = etNik.getText().toString().trim();

        // VALIDASI NAMA → hanya huruf dan spasi
        if (nama.isEmpty()) {
            etNama.setError("Nama tidak boleh kosong");
            return false;
        }
        if (!nama.matches("^[a-zA-Z ]+$")) {
            etNama.setError("Nama hanya boleh berisi huruf dan spasi");
            return false;
        }
        if (containsEmoji(nama)) {
            etNama.setError("Tidak boleh mengandung emoji");
            return false;
        }

        // VALIDASI NIK → wajib 16 digit angka
        if (nik.isEmpty()) {
            etNik.setError("NIK tidak boleh kosong");
            return false;
        }
        if (!nik.matches("\\d{16}")) {
            etNik.setError("NIK harus 16 digit angka");
            return false;
        }
        if (containsEmoji(nik)) {
            etNik.setError("Tidak boleh mengandung emoji");
            return false;
        }

        // AGAMA dari dropdown → tidak perlu cek manual emoji

        if (etTTL.getText().toString().trim().isEmpty()) {
            etTTL.setError("TTL tidak boleh kosong");
            return false;
        }

        if (etPendidikan.getText().toString().trim().isEmpty()) {
            etPendidikan.setError("Pendidikan tidak boleh kosong");
            return false;
        }

        if (etAlamat.getText().toString().trim().isEmpty()) {
            etAlamat.setError("Alamat tidak boleh kosong");
            return false;
        }

        if (etKeperluan.getText().toString().trim().isEmpty()) {
            etKeperluan.setError("Keperluan harus diisi");
            return false;
        }

        return true;
    }

    // =============================
    // KIRIM DATA
    // =============================
    private void kirimData() {

        SharedPreferences pref = getSharedPreferences("prefLogin", MODE_PRIVATE);
        String username = pref.getString("username", "");

        if (username.isEmpty()) {
            Toast.makeText(this, "Akun belum login!", Toast.LENGTH_SHORT).show();
            return;
        }

        RequestBody rbNama = rb(etNama.getText().toString().trim());
        RequestBody rbNik = rb(etNik.getText().toString().trim());
        RequestBody rbAgama = rb(spinnerAgama.getSelectedItem().toString());
        RequestBody rbTTL = rb(etTTL.getText().toString().trim());
        RequestBody rbPendidikan = rb(etPendidikan.getText().toString().trim());
        RequestBody rbAlamat = rb(etAlamat.getText().toString().trim());
        RequestBody rbKeperluan = rb(etKeperluan.getText().toString().trim());
        RequestBody rbKodeSurat = rb("SKBB");
        RequestBody rbIdPejabat = rb("");
        RequestBody rbUser = rb(username);

        MultipartBody.Part fileFix = (filePart != null)
                ? filePart
                : MultipartBody.Part.createFormData("file", "");

        APIRequestData api = RetroServer.konekRetrofit().create(APIRequestData.class);

        Call<ResponSkb> call = api.kirimskb(
                rbNama, rbNik, rbAgama, rbTTL, rbPendidikan,
                rbAlamat, fileFix, rbKeperluan,
                rbKodeSurat, rbIdPejabat, rbUser
        );

        call.enqueue(new Callback<ResponSkb>() {
            @Override
            public void onResponse(Call<ResponSkb> call, Response<ResponSkb> response) {
                if (response.isSuccessful() && response.body() != null) {

                    Toast.makeText(SKB.this,
                            response.body().getMessage(),
                            Toast.LENGTH_SHORT).show();

                    clearForm();

                    Intent intent = new Intent(SKB.this, permintaan_surat.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();

                } else {
                    Toast.makeText(SKB.this, "Gagal mengirim data ke server",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponSkb> call, Throwable t) {
                Toast.makeText(SKB.this,
                        "Kesalahan koneksi: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private RequestBody rb(String value) {
        return RequestBody.create(MediaType.parse("text/plain"), value);
    }

    private void clearForm() {
        etNama.setText("");
        etNik.setText("");
        spinnerAgama.setSelection(0);
        etTTL.setText("");
        etPendidikan.setText("");
        etAlamat.setText("");
        etKeperluan.setText("");

        imgPreview.setImageURI(null);
        imgPreview.setVisibility(ImageView.GONE);

        filePart = null;
        uriFile = null;
    }
}
