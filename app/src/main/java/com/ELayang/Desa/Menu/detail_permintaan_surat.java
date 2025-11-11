package com.ELayang.Desa.Menu;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ELayang.Desa.R;
import com.ELayang.Desa.Surat.SKCK;
import com.ELayang.Desa.Surat.SKK;
import com.ELayang.Desa.Surat.SKTM;
import com.ELayang.Desa.Surat.Surat_Ijin;
import com.ELayang.Desa.aspirasi.TambahAspirasiActivity;

import java.util.Objects;

public class detail_permintaan_surat extends AppCompatActivity {
    private ImageView kembali;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_permintaan_surat);

        kembali = findViewById(R.id.kembali);
        kembali.setOnClickListener(v -> finish());

        Intent intent = getIntent();
        String kodeSurat = intent.getStringExtra("kode_surat");
        String keterangan = intent.getStringExtra("keterangan");
        String noPengajuan = intent.getStringExtra("no_pengajuan");

        TextView kodeSuratTextView = findViewById(R.id.kode_surat);
        TextView keteranganTextView = findViewById(R.id.keterangan);

        kodeSuratTextView.setText(kodeSurat);
        keteranganTextView.setText(keterangan);

        Toast.makeText(this, "Kode surat: " + kodeSurat, Toast.LENGTH_LONG).show();

        // ✅ Routing yang benar
        if (Objects.equals(kodeSurat, "skck")) {
            pindah(SKCK.class, noPengajuan);

        } else if (Objects.equals(kodeSurat, "surat_ijin")) {
            pindah(Surat_Ijin.class, noPengajuan);

        } else if (Objects.equals(kodeSurat, "SKTM")) {
            pindah(SKTM.class, noPengajuan);

        } else if (Objects.equals(kodeSurat, "SKK")) { // ✅ SURAT KEHILANGAN
            pindah(SKK.class, noPengajuan);

        } else if (Objects.equals(kodeSurat, "aspirasi")) {
            pindah(TambahAspirasiActivity.class, noPengajuan);

        } else {
            Toast.makeText(this, "Kode surat tidak dikenali", Toast.LENGTH_SHORT).show();
        }
    }

    private void pindah(Class<?> tujuan, String noPengajuan) {
        Intent i = new Intent(this, tujuan);
        i.putExtra("nopengajuan", noPengajuan);
        startActivity(i);
        finish();
    }
}
