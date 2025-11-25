package com.ELayang.Desa.aspirasi;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.ELayang.Desa.R;
import com.squareup.picasso.Picasso;

public class DetailAspirasiActivity extends AppCompatActivity {

    TextView tvNoPengajuan, tvJudul, tvIsi, tvTanggal, tvStatus, tvAlasan;
    ImageView imgFoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_aspirasi);

        tvNoPengajuan = findViewById(R.id.tv_detail_no);
        tvJudul = findViewById(R.id.tv_detail_judul);
        tvIsi = findViewById(R.id.tv_detail_isi);
        tvTanggal = findViewById(R.id.tv_detail_tanggal);
        tvStatus = findViewById(R.id.tv_detail_status);
        tvAlasan = findViewById(R.id.tv_detail_alasan);
        imgFoto = findViewById(R.id.img_detail_foto);

        // GET DATA FROM INTENT
        String no = getIntent().getStringExtra("nopengajuan");
        String judul = getIntent().getStringExtra("judul");
        String isi = getIntent().getStringExtra("isi");
        String tanggal = getIntent().getStringExtra("tanggal");
        String status = getIntent().getStringExtra("status");
        String alasan = getIntent().getStringExtra("alasan");
        String foto = getIntent().getStringExtra("foto");

        tvNoPengajuan.setText(no);
        tvJudul.setText(judul);
        tvIsi.setText(isi);
        tvTanggal.setText(tanggal);
        tvStatus.setText(status);

        if (alasan != null && !alasan.isEmpty()) {
            tvAlasan.setText("Alasan : " + alasan);
            tvAlasan.setVisibility(TextView.VISIBLE);
        } else {
            tvAlasan.setVisibility(TextView.GONE);
        }

        if (foto != null && !foto.isEmpty()) {
            Picasso.get().load(foto).into(imgFoto);
        } else {
            imgFoto.setImageResource(R.drawable.kunir);
        }
    }
}
