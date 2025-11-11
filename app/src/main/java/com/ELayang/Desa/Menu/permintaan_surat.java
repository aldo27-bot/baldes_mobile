package com.ELayang.Desa.Menu;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.ELayang.Desa.R;
import com.ELayang.Desa.Surat.FormSuratDomisiliActivity;
import com.ELayang.Desa.Surat.SKTM;
import com.ELayang.Desa.Surat.SKK;
//import com.ELayang.Desa.Surat.SuratBedaNamaActivity;
//import com.ELayang.Desa.Surat.SuratUsahaActivity;
//import com.ELayang.Desa.Surat.SuratKematianActivity;
//import com.ELayang.Desa.Surat.SuratKelakuanBaikActivity;
import com.ELayang.Desa.aspirasi.TambahAspirasiActivity;

public class permintaan_surat extends AppCompatActivity {

    LinearLayout itemSuratDomisili, itemSuratSKTM, itemSuratKehilangan,
            itemSuratBedaNama, itemSuratUsaha, itemSuratKematian,
            itemSkbb, itemAspirasi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permintaan_surat);

        // ============ Inisialisasi Toolbar ============
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // ============ Inisialisasi Komponen ============
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

//        itemSuratBedaNama.setOnClickListener(v ->
//                startActivity(new Intent(this, SuratBedaNamaActivity.class)));
//
//        itemSuratUsaha.setOnClickListener(v ->
//                startActivity(new Intent(this, SuratUsahaActivity.class)));
//
//        itemSuratKematian.setOnClickListener(v ->
//                startActivity(new Intent(this, SuratKematianActivity.class)));
//
//        itemSkbb.setOnClickListener(v ->
//                startActivity(new Intent(this, SuratKelakuanBaikActivity.class)));

        itemAspirasi.setOnClickListener(v ->
                startActivity(new Intent(this, TambahAspirasiActivity.class)));
    }
}
