package com.ELayang.Desa.Menu;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ELayang.Desa.API.APIRequestData;
import com.ELayang.Desa.API.RetroServer;
import com.ELayang.Desa.Asset.Adapter.SuratAdapter;
import com.ELayang.Desa.DataModel.ModelSurat;
import com.ELayang.Desa.DataModel.ResponSurat;
import com.ELayang.Desa.R;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class permintaan_surat extends AppCompatActivity {

    private ArrayList<ModelSurat> data = new ArrayList<>();
    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permintaan_surat);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.navigation_view);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // ✅ Ambil data dari API
        APIRequestData ardData = RetroServer.konekRetrofit().create(APIRequestData.class);
        ardData.surat().enqueue(new Callback<ResponSurat>() {
            @Override
            public void onResponse(Call<ResponSurat> call, Response<ResponSurat> response) {
                if (response.isSuccessful() && response.body() != null) {
                    data.addAll(response.body().getdata());
                    setUpRecyclerView();
                } else {
                    Toast.makeText(permintaan_surat.this, "Gagal memuat data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponSurat> call, Throwable t) {
                Toast.makeText(permintaan_surat.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // ✅ Tombol Surat Kehilangan
        findViewById(R.id.item_surat_kehilangan).setOnClickListener(v -> {
            Intent intent = new Intent(permintaan_surat.this, detail_permintaan_surat.class);
            intent.putExtra("kode_surat", "SKK"); // ✅ Surat Kehilangan
            intent.putExtra("keterangan", "Surat Keterangan Kehilangan");
            startActivity(intent);
        });

        // ✅ Tombol Aspirasi
        findViewById(R.id.item_aspirasi).setOnClickListener(v -> {
            Intent intent = new Intent(permintaan_surat.this, detail_permintaan_surat.class);
            intent.putExtra("kode_surat", "aspirasi"); // ✅ Aspirasi
            intent.putExtra("keterangan", "Ajukan aspirasi atau saran untuk desa");
            startActivity(intent);
        });

        // ✅ Klik item list surat
        SuratAdapter.setOnItemClickListener(v -> {
            TextView txtkode_surat = v.findViewById(R.id.textsatu);
            TextView txtketerangan = v.findViewById(R.id.textdua);

            Intent intent = new Intent(permintaan_surat.this, detail_permintaan_surat.class);
            intent.putExtra("kode_surat", txtkode_surat.getText().toString());
            intent.putExtra("keterangan", txtketerangan.getText().toString());
            startActivity(intent);
        });
    }

    private void setUpRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new SuratAdapter(data, this));
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
