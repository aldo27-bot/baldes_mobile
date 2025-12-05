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
    private EditText etNama, etTTL, etPekerjaan, etAlamat, etKeterangan;
    private Spinner spinnerJK, spinnerAgama, spinnerKewarganegaraan;
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
        etAlamat = findViewById(R.id.etAlamat);
        etKeterangan = findViewById(R.id.etKeterangan);

        spinnerJK = findViewById(R.id.spinnerJK);
        spinnerAgama = findViewById(R.id.spinnerAgama);
        spinnerKewarganegaraan = findViewById(R.id.e_Kewarganegaraan);

        // Jenis Kelamin
        ArrayAdapter<CharSequence> adapterJK = ArrayAdapter.createFromResource(
                this, R.array.jenis_kelamin_array, android.R.layout.simple_spinner_item
        );
        adapterJK.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerJK.setAdapter(adapterJK);

        // Agama
        ArrayAdapter<CharSequence> adapterAgama = ArrayAdapter.createFromResource(
                this, R.array.agama_resmi_array, android.R.layout.simple_spinner_item
        );
        adapterAgama.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAgama.setAdapter(adapterAgama);

        // Kewarganegaraan
        ArrayAdapter<CharSequence> adapterKW = ArrayAdapter.createFromResource(
                this, R.array.kewarganegaraan_array, android.R.layout.simple_spinner_item
        );
        adapterKW.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerKewarganegaraan.setAdapter(adapterKW);

        btnKirim = findViewById(R.id.btnKirim);
        btnChooseFile = findViewById(R.id.btn_choose_file);
        tFileName = findViewById(R.id.t_file_name);

        tFileName.setText("Tidak ada file yang dipilih");

        btnBack.setOnClickListener(v -> onBackPressed());
        btnChooseFile.setOnClickListener(v -> chooseFile());
        btnKirim.setOnClickListener(v -> validasiSebelumKirim());
    }

    private void validasiSebelumKirim() {
        if (!isValidForm()) return;

        new AlertDialog.Builder(this)
                .setTitle("Konfirmasi")
                .setMessage("Kirim Surat Kematian?")
                .setPositiveButton("Kirim", (d, w) -> kirimData())
                .setNegativeButton("Batal", null)
                .show();
    }

    private boolean isValidForm() {

        String namaInput = etNama.getText().toString().trim();

        // Validasi Nama
        if (namaInput.isEmpty()) {
            etNama.setError("Nama wajib diisi");
            return false;
        }
        if (containsEmoji(namaInput)) {
            etNama.setError("Tidak boleh mengandung emoji");
            return false;
        }
        if (!namaInput.matches("^[a-zA-Z ]+$")) {
            etNama.setError("Nama hanya boleh berisi huruf dan spasi");
            return false;
        }

        if (etTTL.getText().toString().trim().isEmpty()) {
            etTTL.setError("TTL wajib diisi");
            return false;
        }
        if (containsEmoji(etTTL.getText().toString())) {
            etTTL.setError("Tidak boleh mengandung emoji");
            return false;
        }

        if (spinnerJK.getSelectedItem().equals("Pilih Jenis Kelamin")) {
            Toast.makeText(this, "Pilih jenis kelamin!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (spinnerAgama.getSelectedItem().equals("Pilih Agama")) {
            Toast.makeText(this, "Pilih agama!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (spinnerKewarganegaraan.getSelectedItem().equals("Pilih Kewarganegaraan")) {
            Toast.makeText(this, "Pilih kewarganegaraan!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (etPekerjaan.getText().toString().trim().isEmpty()) {
            etPekerjaan.setError("Pekerjaan wajib diisi");
            return false;
        }
        if (containsEmoji(etPekerjaan.getText().toString())) {
            etPekerjaan.setError("Tidak boleh mengandung emoji");
            return false;
        }

        if (etAlamat.getText().toString().trim().isEmpty()) {
            etAlamat.setError("Alamat wajib diisi");
            return false;
        }
        if (containsEmoji(etAlamat.getText().toString())) {
            etAlamat.setError("Tidak boleh mengandung emoji");
            return false;
        }

        if (etKeterangan.getText().toString().trim().isEmpty()) {
            etKeterangan.setError("Keterangan wajib diisi");
            return false;
        }
        if (containsEmoji(etKeterangan.getText().toString())) {
            etKeterangan.setError("Tidak boleh mengandung emoji");
            return false;
        }

        return true;
    }

    private boolean containsEmoji(String text) {
        if (text == null) return false;
        for (int i = 0; i < text.length(); i++) {
            int type = Character.getType(text.charAt(i));
            if (type == Character.SURROGATE || type == Character.OTHER_SYMBOL) {
                return true;
            }
        }
        return false;
    }

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
            tFileName.setText(fileName);
        } else {
            fileUri = null;
            tFileName.setText("Tidak ada file yang dipilih");
        }
    }

    private void kirimData() {

        RequestBody rNama = rb(etNama.getText().toString());
        RequestBody rTTL = rb(etTTL.getText().toString());
        RequestBody rJK = rb(spinnerJK.getSelectedItem().toString());
        RequestBody rAgama = rb(spinnerAgama.getSelectedItem().toString());
        RequestBody rPekerjaan = rb(etPekerjaan.getText().toString());
        RequestBody rAlamat = rb(etAlamat.getText().toString());
        RequestBody rKW = rb(spinnerKewarganegaraan.getSelectedItem().toString());
        RequestBody rKet = rb(etKeterangan.getText().toString());
        RequestBody rUsername = rb(usernameUser);

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
                rPekerjaan, rKW, rKet,
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

    private RequestBody rb(String value) {
        return RequestBody.create(MediaType.parse("text/plain"), value);
    }
}
