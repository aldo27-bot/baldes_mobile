package com.ELayang.Desa.Surat;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import com.ELayang.Desa.R;


public class Beda_Nama extends AppCompatActivity {

    EditText etNamaLengkap, etNIK, etTTL, etPekerjaan, etAlamat, etKeterangan;
    Button btnKirim;
    ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.surat_beda_nama);

        // INISIALISASI
        etNamaLengkap = findViewById(R.id.etNamaLengkap);
        etNIK = findViewById(R.id.etNIK);
        etTTL = findViewById(R.id.etTTL);
        etPekerjaan = findViewById(R.id.etPekerjaan);
        etAlamat = findViewById(R.id.etAlamat);
        etKeterangan = findViewById(R.id.etKeterangan);
        btnKirim = findViewById(R.id.btnKirim);
        btnBack = findViewById(R.id.btnBack); // Inisialisasi tombol kembali

        // LISTENER UNTUK TOMBOL KEMBALI
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed(); // Fungsi untuk kembali ke Activity sebelumnya
            }
        });

        // LISTENER UNTUK TOMBOL KIRIM
        btnKirim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Proses pengambilan data sudah benar
                String nama = etNamaLengkap.getText().toString();
                String nik = etNIK.getText().toString();
                String ttl = etTTL.getText().toString();
                String pekerjaan = etPekerjaan.getText().toString();
                String alamat = etAlamat.getText().toString();
                String keterangan = etKeterangan.getText().toString();

                Toast.makeText(Beda_Nama.this,
                        "Data berhasil dikirim:\n" + nama,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}