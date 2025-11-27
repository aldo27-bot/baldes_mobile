package com.ELayang.Desa.Surat;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
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

    private ImageButton btnBack;
    private EditText etNama, etTTL, etPekerjaan, etAgama, etAlamat, etKewarganegaraan, etKeterangan;
    private Spinner spinnerJK;
    private Button btnKirim, btnChooseFile;
    private TextView tFileName;
    private Uri fileUri;

    private static final int FILE_SELECT_CODE = 100;
    private String usernameUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.surat_skm);

        usernameUser = getSharedPreferences("prefLogin", MODE_PRIVATE).getString("username", "");

        btnBack = findViewById(R.id.btnBack);
        etNama = findViewById(R.id.etNama);
        etTTL = findViewById(R.id.etTTL);
        etPekerjaan = findViewById(R.id.etPekerjaan);
        etAgama = findViewById(R.id.etAgama);
        etAlamat = findViewById(R.id.etAlamat);
        etKewarganegaraan = findViewById(R.id.etkewarganegaraan);
        etKeterangan = findViewById(R.id.etKeterangan);

        spinnerJK = findViewById(R.id.spinnerJK);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.jenis_kelamin_array,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerJK.setAdapter(adapter);

        btnKirim = findViewById(R.id.btnKirim);
        btnChooseFile = findViewById(R.id.btn_choose_file);
        tFileName = findViewById(R.id.t_file_name);

        tFileName.setText("Tidak ada file yang dipilih");

        btnBack.setOnClickListener(v -> onBackPressed());
        btnChooseFile.setOnClickListener(v -> chooseFile());
        btnKirim.setOnClickListener(v -> validasiSebelumKirim());
    }

    // ====================================================== //
    //                VALIDASI SEBELUM KIRIM
    // ====================================================== //
    private void validasiSebelumKirim() {
        if (!isValidForm()) return;  // Jika gagal validasi → stop

        // Popup konfirmasi
        new AlertDialog.Builder(this)
                .setTitle("Konfirmasi")
                .setMessage("Kirim Surat Kematian?")
                .setPositiveButton("Kirim", (d, w) -> kirimData())
                .setNegativeButton("Batal", null)
                .show();
    }

    // ====================================================== //
    //               VALIDASI INPUT FORM
    // ====================================================== //
    private boolean isValidForm() {

        if (etNama.getText().toString().trim().isEmpty()) {
            etNama.setError("Nama wajib diisi");
            etNama.requestFocus();
            return false;
        }
        if (etTTL.getText().toString().trim().isEmpty()) {
            etTTL.setError("Tempat/Tanggal Lahir wajib diisi");
            etTTL.requestFocus();
            return false;
        }
        if (spinnerJK.getSelectedItem().toString().equals("Pilih Jenis Kelamin")) {
            Toast.makeText(this, "Pilih jenis kelamin!", Toast.LENGTH_SHORT).show();
            spinnerJK.performClick();
            return false;
        }
        if (etAgama.getText().toString().trim().isEmpty()) {
            etAgama.setError("Agama wajib diisi");
            etAgama.requestFocus();
            return false;
        }
        if (etPekerjaan.getText().toString().trim().isEmpty()) {
            etPekerjaan.setError("Pekerjaan wajib diisi");
            etPekerjaan.requestFocus();
            return false;
        }
        if (etAlamat.getText().toString().trim().isEmpty()) {
            etAlamat.setError("Alamat wajib diisi");
            etAlamat.requestFocus();
            return false;
        }
        if (etKewarganegaraan.getText().toString().trim().isEmpty()) {
            etKewarganegaraan.setError("Kewarganegaraan wajib diisi");
            etKewarganegaraan.requestFocus();
            return false;
        }
        if (etKeterangan.getText().toString().trim().isEmpty()) {
            etKeterangan.setError("Keterangan wajib diisi");
            etKeterangan.requestFocus();
            return false;
        }

        return true;
    }

    // ====================================================== //
    //                       PILIH FILE
    // ====================================================== //
    private void chooseFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(intent, "Pilih File Pendukung"), FILE_SELECT_CODE);
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
        } else {
            fileUri = null;
            tFileName.setText("Tidak ada file yang dipilih");
        }
    }

    // ====================================================== //
    //                    KIRIM KE SERVER
    // ====================================================== //
    private void kirimData() {

        String nama = etNama.getText().toString().trim();
        String ttl = etTTL.getText().toString().trim();
        String jk = spinnerJK.getSelectedItem().toString();
        String agama = etAgama.getText().toString().trim();
        String pekerjaan = etPekerjaan.getText().toString().trim();
        String alamat = etAlamat.getText().toString().trim();
        String kewarganegaraan = etKewarganegaraan.getText().toString().trim();
        String keterangan = etKeterangan.getText().toString().trim();
        String username = usernameUser;

        RequestBody rNama = rb(nama);
        RequestBody rAlamat = rb(alamat);
        RequestBody rJK = rb(jk);
        RequestBody rTTL = rb(ttl);
        RequestBody rPekerjaan = rb(pekerjaan);
        RequestBody rAgama = rb(agama);
        RequestBody rKewarganegaraan = rb(kewarganegaraan);
        RequestBody rKeterangan = rb(keterangan);
        RequestBody rUsername = rb(username);

        MultipartBody.Part filePart = null;

        try {
            if (fileUri != null) {
                InputStream inputStream = getContentResolver().openInputStream(fileUri);
                ByteArrayOutputStream bos = new ByteArrayOutputStream();

                byte[] buf = new byte[1024];
                int n;
                while ((n = inputStream.read(buf)) > 0) bos.write(buf, 0, n);

                String mime = getContentResolver().getType(fileUri);
                if (mime == null) mime = "application/octet-stream";

                RequestBody reqFile = RequestBody.create(MediaType.parse(mime), bos.toByteArray());

                filePart = MultipartBody.Part.createFormData("file",
                        fileUri.getLastPathSegment(), reqFile);

                inputStream.close();
            } else {
                filePart = MultipartBody.Part.createFormData("file", "");
            }
        } catch (Exception e) {
            Toast.makeText(this, "Gagal membaca file!", Toast.LENGTH_SHORT).show();
            filePart = MultipartBody.Part.createFormData("file", "");
        }

        APIRequestData api = RetroServer.konekRetrofit().create(APIRequestData.class);

        Call<ResponSkm> call = api.uploadSKM(
                rNama, rAlamat, rJK, rTTL, rAgama,
                rPekerjaan, rKewarganegaraan, rKeterangan,
                rUsername, filePart
        );

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

    // Utility
    private RequestBody rb(String value) {
        return RequestBody.create(MediaType.parse("text/plain"), value);
    }
}
