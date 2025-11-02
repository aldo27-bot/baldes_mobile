package com.ELayang.Desa.Menu;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
    private NavigationView navigationView;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permintaan_surat);

        // ==================== DRAWER (HAMBURGER MENU) ====================
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Klik menu di hamburger
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_surat) {
                drawerLayout.closeDrawers();
            } else if (id == R.id.nav_aspirasi) {
                Intent aspirasiIntent = new Intent(permintaan_surat.this, detail_permintaan_surat.class);
                aspirasiIntent.putExtra("kode_surat", "aspirasi");
                aspirasiIntent.putExtra("keterangan", "Ajukan aspirasi atau saran untuk desa");
                startActivity(aspirasiIntent);
                drawerLayout.closeDrawers();
            }
            return true;
        });

        // ==================== RETROFIT (AMBIL DATA SURAT) ====================
        APIRequestData ardData = RetroServer.konekRetrofit().create(APIRequestData.class);
        Call<ResponSurat> getSuratRespons = ardData.surat();

        SharedPreferences sharedPreferences = getSharedPreferences("prefSurat", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        getSuratRespons.enqueue(new Callback<ResponSurat>() {
            @Override
            public void onResponse(Call<ResponSurat> call, Response<ResponSurat> response) {
                if (response.isSuccessful()) {
                    ResponSurat responSurat = response.body();
                    if (responSurat != null && responSurat.getKode() == 1) {
                        ArrayList<ModelSurat> suratList = responSurat.getdata();
                        if (suratList != null && !suratList.isEmpty()) {
                            data.addAll(suratList);
                            setUpRecyclerView();
                        } else {
                            Toast.makeText(permintaan_surat.this, "Data surat kosong", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(permintaan_surat.this, "Kode respons bukan 1", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(permintaan_surat.this, "Respons tidak berhasil", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponSurat> call, Throwable t) {
                Toast.makeText(permintaan_surat.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // ==================== KLIK TOMBOL ASPIRASI (DI XML) ====================
        findViewById(R.id.item_aspirasi).setOnClickListener(v -> {
            Intent intent = new Intent(permintaan_surat.this, detail_permintaan_surat.class);
            intent.putExtra("kode_surat", "aspirasi");
            intent.putExtra("keterangan", "Ajukan aspirasi atau saran untuk desa");
            startActivity(intent);
        });

        // ==================== LISTENER SURAT ====================
        SuratAdapter.setOnItemClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView txtkode_surat = v.findViewById(R.id.textsatu);
                TextView txtketerangan = v.findViewById(R.id.textdua);

                String kodeSurat = txtkode_surat.getText().toString();
                String keterangan = txtketerangan.getText().toString();

                Intent intent = new Intent(permintaan_surat.this, detail_permintaan_surat.class);
                intent.putExtra("kode_surat", kodeSurat);
                intent.putExtra("keterangan", keterangan);
                startActivity(intent);
            }
        });
    }

    private void setUpRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.view);
        SuratAdapter suratAdapter = new SuratAdapter(data, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(suratAdapter);
    }

    public void kembali(View view) {
        onBackPressed();
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
