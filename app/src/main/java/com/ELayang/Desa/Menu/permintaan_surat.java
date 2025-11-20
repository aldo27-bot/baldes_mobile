package com.ELayang.Desa.Menu;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton; // Pastikan ImageButton di-import
import android.widget.LinearLayout;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
// import androidx.appcompat.widget.Toolbar; // Tidak perlu mengimpor Toolbar jika tidak digunakan

import com.ELayang.Desa.R;
import com.ELayang.Desa.Surat.FormSuratDomisiliActivity;
import com.ELayang.Desa.Surat.SKM;
import com.ELayang.Desa.Surat.SKTM;
import com.ELayang.Desa.Surat.SKK;
import com.ELayang.Desa.Surat.Beda_Nama;
import com.ELayang.Desa.Surat.SKB;
import com.ELayang.Desa.Surat.SuratUsaha;
import com.ELayang.Desa.aspirasi.TambahAspirasiActivity;

public class permintaan_surat extends AppCompatActivity {

    ImageButton customBackButton;
    LinearLayout itemSuratDomisili, itemSuratSKTM, itemSuratKehilangan,
            itemSuratBedaNama, itemSuratUsaha, itemSuratKematian,
            itemSkbb, itemAspirasi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // LANGKAH 1: PASTIKAN LAYOUT DIMUAT DULU
        setContentView(R.layout.activity_permintaan_surat); // <<< GANTI dengan nama file layout XML yang benar

        // LANGKAH 2: CARI VIEW (FIND VIEW)
        customBackButton = findViewById(R.id.btnkembali);
        // Baris 34 dari error adalah di sekitar sini, sebelum setOnClickListener

        // LANGKAH 3: PASANG LISTENER
        // Crash terjadi di sini jika customBackButton == null
        if (customBackButton != null) {
            customBackButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        } else {
            // Log ini jika Anda ingin memastikan R.id.btnkembali tidak ditemukan
            Log.e("PermintaanSurat", "Error: customBackButton (R.id.btnkembali) tidak ditemukan!");
        }

        // ... inisialisasi LinearLayout lainnya di bawah sini
        itemSuratDomisili = findViewById(R.id.item_surat_domisili);
        // ... dst




        // ============ Inisialisasi Toolbar (Cukup temukan, tidak perlu setSupportActionBar atau NavigationListener) ============
        // Karena kita menggunakan ImageButton kustom, kita tidak perlu setSupportActionBar
        // atau setNavigationOnClickListener.
        // Anda dapat menghapus kode Toolbar ini jika Anda tidak memerlukannya untuk konfigurasi lain:
        // Toolbar toolbar = findViewById(R.id.toolbar);
        // setSupportActionBar(toolbar);
        // toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // ============ Inisialisasi Komponen Daftar Surat ============
        itemSuratDomisili = findViewById(R.id.item_surat_domisili);
        itemSuratSKTM = findViewById(R.id.item_surat_sktm);
        itemSuratKehilangan = findViewById(R.id.item_surat_kehilangan);
        itemSuratBedaNama = findViewById(R.id.item_surat_bedanama);
        itemSuratUsaha = findViewById(R.id.item_surat_usaha);
        itemSuratKematian = findViewById(R.id.item_surat_kematian);
        itemSkbb = findViewById(R.id.item_skbb);
        itemAspirasi = findViewById(R.id.item_aspirasi);

        // ============ Klik Event Tiap Surat ============
        itemSuratDomisili.setOnClickListener(v ->
                startActivity(new Intent(this, FormSuratDomisiliActivity.class)));

        itemSuratSKTM.setOnClickListener(v ->
                startActivity(new Intent(this, SKTM.class)));

        itemSuratKehilangan.setOnClickListener(v ->
                startActivity(new Intent(this, SKK.class)));

        itemSuratBedaNama.setOnClickListener(v ->
                startActivity(new Intent(this, Beda_Nama.class)));

        itemSuratUsaha.setOnClickListener(v ->
                startActivity(new Intent(this, SuratUsaha.class)));

        itemSuratKematian.setOnClickListener(v ->
                startActivity(new Intent(this, SKM.class)));

        itemSkbb.setOnClickListener(v ->
                startActivity(new Intent(this, SKB.class)));

        itemAspirasi.setOnClickListener(v ->
                startActivity(new Intent(this, TambahAspirasiActivity.class)));
    }
}