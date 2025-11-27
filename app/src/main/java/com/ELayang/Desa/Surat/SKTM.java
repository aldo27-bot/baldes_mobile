package com.ELayang.Desa.Surat;

import static android.app.Activity.RESULT_OK;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.ELayang.Desa.API.APIRequestData;
import com.ELayang.Desa.API.RetroServer;
import com.ELayang.Desa.DataModel.Surat.ResponSktm;
import com.ELayang.Desa.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SKTM extends AppCompatActivity {

    EditText eNama, eTTL, eAsalSekolah, eKeperluan, eNamaOrtu, eNikOrtu,
            eAlamatOrtu, eTtlOrtu, ePekerjaanOrtu;

    Button btnKirim;
    TextView btnPilihFile, tvNamaFile;
    ImageButton btnBack;

    Uri fileUri = null;
    File fileFix = null;

    SharedPreferences sharedPreferences;
    String username;

    private static final int FILE_REQUEST = 22;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.surat_sktm);

        sharedPreferences = getSharedPreferences("prefLogin", MODE_PRIVATE);
        username = sharedPreferences.getString("username", "");

        if (username.isEmpty()) {
            Toast.makeText(this, "Akun belum login!", Toast.LENGTH_SHORT).show();
        }

        // Bind view
        eNama = findViewById(R.id.etNama);
        eTTL = findViewById(R.id.etTTL);
        eAsalSekolah = findViewById(R.id.etAsalSekolah);
        eKeperluan = findViewById(R.id.etKeperluan);
        eNamaOrtu = findViewById(R.id.etNamaOrtu);
        eNikOrtu = findViewById(R.id.etNIKOrtu);
        eAlamatOrtu = findViewById(R.id.etAlamatOrtu);
        eTtlOrtu = findViewById(R.id.etTTLOrtu);
        ePekerjaanOrtu = findViewById(R.id.etPekerjaanOrtu);

        btnPilihFile = findViewById(R.id.btnPilihFile);
        tvNamaFile = findViewById(R.id.tvNamaFile);
        btnKirim = findViewById(R.id.btnKirim);
        btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> finish());
        btnPilihFile.setOnClickListener(v -> pilihFile());
        btnKirim.setOnClickListener(v -> kirimForm());
    }

    private void pilihFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        startActivityForResult(Intent.createChooser(intent, "Pilih File"), FILE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == FILE_REQUEST && resultCode == RESULT_OK && data != null) {
            fileUri = data.getData();
            if (fileUri != null) {
                String fileName = getFileName(fileUri);
                tvNamaFile.setText(fileName);

                fileFix = createTempFile(fileUri);
            }
        }
    }

    private File createTempFile(Uri uri) {
        File tempFile = null;
        try {
            String fileName = getFileName(uri);
            tempFile = new File(getCacheDir(), fileName);
            InputStream inputStream = getContentResolver().openInputStream(uri);
            FileOutputStream outputStream = new FileOutputStream(tempFile);

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, bytesRead);
            }

            inputStream.close();
            outputStream.close();
        } catch (Exception e) {
            Log.e("FILE_ERROR", e.getMessage());
        }
        return tempFile;
    }

    private String getFileName(Uri uri) {
        String result = "file_upload";
        try {
            var cursor = getContentResolver().query(uri, null, null, null, null);
            if (cursor != null) {
                int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                cursor.moveToFirst();
                result = cursor.getString(nameIndex);
                cursor.close();
            }
        } catch (Exception ignored) {}
        return result;
    }

    private boolean cekField(EditText et, String pesan) {
        if (et.getText().toString().trim().isEmpty()) {
            et.setError(pesan);
            et.requestFocus();
            return true;
        }
        return false;
    }

    private void kirimForm() {

        // VALIDASI FIELD WAJIB
        if (cekField(eNama, "Nama tidak boleh kosong")) return;
        if (cekField(eTTL, "TTL tidak boleh kosong")) return;
        if (cekField(eAsalSekolah, "Asal sekolah tidak boleh kosong")) return;
        if (cekField(eKeperluan, "Keperluan tidak boleh kosong")) return;
        if (cekField(eNamaOrtu, "Nama orang tua tidak boleh kosong")) return;
        if (cekField(eNikOrtu, "NIK orang tua tidak boleh kosong")) return;
        if (cekField(eAlamatOrtu, "Alamat orang tua tidak boleh kosong")) return;
        if (cekField(eTtlOrtu, "TTL orang tua tidak boleh kosong")) return;
        if (cekField(ePekerjaanOrtu, "Pekerjaan orang tua tidak boleh kosong")) return;

        // VALIDASI NIK 16 DIGIT
        if (eNikOrtu.getText().toString().trim().length() != 16) {
            eNikOrtu.setError("NIK harus 16 digit");
            eNikOrtu.requestFocus();
            return;
        }

        // VALIDASI FILE WAJIB
        if (fileFix == null) {
            Toast.makeText(this, "File wajib di-upload!", Toast.LENGTH_SHORT).show();
            return;
        }

        // === VALIDASI TOMBOL KIRIM (POPUP KONFIRMASI) ===
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Konfirmasi")
                .setMessage("Kirim Surat Keterangan Tidak Mampu?")
                .setPositiveButton("Ya", (dialog, which) -> kirimKeServer())
                .setNegativeButton("Tidak", (dialog, which) -> dialog.dismiss())
                .show();
    }

    // ==============================
    //     FUNGSI KIRIM KE SERVER
    // ==============================
    private void kirimKeServer() {

        ProgressDialog pd = new ProgressDialog(SKTM.this);
        pd.setMessage("Mengirim...");
        pd.show();

        RequestBody reqNama = RequestBody.create(MediaType.parse("text/plain"), eNama.getText().toString());
        RequestBody reqTTL = RequestBody.create(MediaType.parse("text/plain"), eTTL.getText().toString());
        RequestBody reqAsal = RequestBody.create(MediaType.parse("text/plain"), eAsalSekolah.getText().toString());
        RequestBody reqKeperluan = RequestBody.create(MediaType.parse("text/plain"), eKeperluan.getText().toString());
        RequestBody reqNamaOrtu = RequestBody.create(MediaType.parse("text/plain"), eNamaOrtu.getText().toString());
        RequestBody reqNikOrtu = RequestBody.create(MediaType.parse("text/plain"), eNikOrtu.getText().toString());
        RequestBody reqAlamatOrtu = RequestBody.create(MediaType.parse("text/plain"), eAlamatOrtu.getText().toString());
        RequestBody reqTTLOrtu = RequestBody.create(MediaType.parse("text/plain"), eTtlOrtu.getText().toString());
        RequestBody reqPekerjaanOrtu = RequestBody.create(MediaType.parse("text/plain"), ePekerjaanOrtu.getText().toString());
        RequestBody reqUsername = RequestBody.create(MediaType.parse("text/plain"), username);

        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), fileFix);
        MultipartBody.Part uploadFile = MultipartBody.Part.createFormData("file", fileFix.getName(), requestFile);

        APIRequestData api = RetroServer.konekRetrofit().create(APIRequestData.class);
        Call<ResponSktm> call = api.sktm(
                reqNama,
                reqTTL,
                reqAsal,
                reqKeperluan,
                reqNamaOrtu,
                reqNikOrtu,
                reqAlamatOrtu,
                reqTTLOrtu,
                reqPekerjaanOrtu,
                reqUsername,
                uploadFile
        );

        call.enqueue(new Callback<ResponSktm>() {
            @Override
            public void onResponse(Call<ResponSktm> call, Response<ResponSktm> response) {
                pd.dismiss();

                if (response.body() != null && response.body().getKode() == 1) {
                    Toast.makeText(SKTM.this, "Berhasil dikirim!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(SKTM.this, "Gagal mengirim!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponSktm> call, Throwable t) {
                pd.dismiss();
                Toast.makeText(SKTM.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
