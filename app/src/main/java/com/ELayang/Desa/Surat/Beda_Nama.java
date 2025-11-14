package com.ELayang.Desa.Surat;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.ELayang.Desa.R;


public class Beda_Nama extends AppCompatActivity {

    EditText etNamaLama, etNamaBaru, etNIK, etTTL, etPekerjaan, etAlamat, etKeterangan;
    Button btnKirim;
    ImageButton btnBack;
    TextView btnChooseFile;
    TextView tvNamaFile;
    Uri fileUri = null;

    private ActivityResultLauncher<Intent> filePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.surat_beda_nama);

        // Inisialisasi EditText, Button, ImageButton yang sudah ada
        etNamaLama = findViewById(R.id.etNamaLama);
        etNamaBaru = findViewById(R.id.etNamaBaru);
        etNIK = findViewById(R.id.etNIK);
        etTTL = findViewById(R.id.etTTL);
        etPekerjaan = findViewById(R.id.etPekerjaan);
        etAlamat = findViewById(R.id.etAlamat);
        etKeterangan = findViewById(R.id.etKeterangan);
        btnKirim = findViewById(R.id.btnKirim);
        btnBack = findViewById(R.id.btnBack);


        btnChooseFile = findViewById(R.id.btnChooseFile);
        tvNamaFile = findViewById(R.id.tvNamaFile);


        filePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Intent data = result.getData();
                        fileUri = data.getData();
                        if (fileUri != null) {
                            // Tampilkan nama file
                            String fileName = getFileName(fileUri);
                            tvNamaFile.setText(fileName);
                            Toast.makeText(this, "File dipilih: " + fileName, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        tvNamaFile.setText("Tidak ada file yang dipilih");
                        fileUri = null;
                    }
                }
        );

        // 2. Tambahkan Listener untuk Tombol Pilih File
        btnChooseFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFilePicker();
            }
        });

        // Listener yang sudah ada
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        btnKirim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                kirimDataPengajuan();
            }
        });
    }

    // Metode untuk membuka File Picker
    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*"); // Batasan jenis file: semua jenis
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            filePickerLauncher.launch(Intent.createChooser(intent, "Pilih File"));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "Tidak ada aplikasi file manager terinstal.", Toast.LENGTH_SHORT).show();
        }
    }

    // Metode untuk mendapatkan nama file dari Uri (diperlukan untuk menampilkan nama di TextView)
    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (android.database.Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int nameIndex = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME);
                    if (nameIndex != -1) {
                        result = cursor.getString(nameIndex);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }


    private void kirimDataPengajuan() {
        // 1. Ambil data dari EditText, disesuaikan dengan kolom database
        String namaLama = etNamaLama.getText().toString().trim();
        String namaBaru = etNamaBaru.getText().toString().trim();
        String nik = etNIK.getText().toString().trim();
        String tempatTanggalLahir = etTTL.getText().toString().trim();
        String pekerjaan = etPekerjaan.getText().toString().trim();
        String alamat = etAlamat.getText().toString().trim();
        String keterangan = etKeterangan.getText().toString().trim();

        // Ambil File URI
        Uri selectedFileUri = this.fileUri; // File URI sudah tersedia

        // 2. Lakukan Validasi Sederhana
        if (TextUtils.isEmpty(namaLama) || TextUtils.isEmpty(namaBaru) || TextUtils.isEmpty(nik) ||
                TextUtils.isEmpty(tempatTanggalLahir) || TextUtils.isEmpty(pekerjaan) ||
                TextUtils.isEmpty(alamat) || TextUtils.isEmpty(keterangan)) {

            Toast.makeText(this, "Mohon lengkapi semua data teks.", Toast.LENGTH_LONG).show();
            // ... (Tambahkan penanda error yang sudah ada)
            return;
        }

        // Tambahan Validasi File: Pastikan file sudah dipilih
        if (selectedFileUri == null) {
            Toast.makeText(this, "Mohon unggah file pendukung.", Toast.LENGTH_LONG).show();
            // Memberi feedback visual pada tombol file
            tvNamaFile.setError("File harus dipilih");
            return;
        }

        // TODO: Implementasi pengiriman data ke API (termasuk file upload)

        Toast.makeText(Beda_Nama.this,
                "Pengajuan BERHASIL. Nama Lama: " + namaLama + ", File: " + getFileName(selectedFileUri),
                Toast.LENGTH_LONG).show();
    }
}