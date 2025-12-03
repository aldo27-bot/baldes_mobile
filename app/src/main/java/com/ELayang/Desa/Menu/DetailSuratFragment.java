package com.ELayang.Desa.Menu;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.ImageButton;

import com.ELayang.Desa.R;

public class DetailSuratFragment extends AppCompatActivity {

    private TextView tvNama, tvNik, tvNoPengajuan, tvTanggal, tvKodeSurat, tvStatus;
    private ImageButton btnKembali;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_detail_surat);

        tvNama = findViewById(R.id.tvNama);
        tvNik = findViewById(R.id.tvNik);
        tvNoPengajuan = findViewById(R.id.tvNoPengajuan);
        tvTanggal = findViewById(R.id.tvTanggal);
        tvKodeSurat = findViewById(R.id.tvKodeSurat);
        tvStatus = findViewById(R.id.tvStatus);

        btnKembali = findViewById(R.id.btnkembali);
        btnKembali.setOnClickListener(v -> finish());

        tvNama.setText(getIntent().getStringExtra("nama"));
        tvNik.setText(getIntent().getStringExtra("nik"));
        tvNoPengajuan.setText(getIntent().getStringExtra("no_pengajuan"));
        tvTanggal.setText(getIntent().getStringExtra("tanggal"));
        tvKodeSurat.setText(getIntent().getStringExtra("kode_surat"));
        tvStatus.setText(getIntent().getStringExtra("status"));
    }
}
