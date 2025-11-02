package com.ELayang.Desa.aspirasi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import com.ELayang.Desa.R;
import com.ELayang.Desa.API.APIRequestData;
import com.ELayang.Desa.DataModel.Aspirasi;
import com.ELayang.Desa.DataModel.AspirasiResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ListAspirasiActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    APIRequestData apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_aspirasi);

        recyclerView = findViewById(R.id.recyclerViewAspirasi);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Setup Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.1.7/baldes_web/DatabaseMobile/") // ganti sesuai IP atau domain server
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(APIRequestData.class);

        loadAspirasi();
    }

    private void loadAspirasi() {
        // Panggil API tanpa token
        apiService.getAspirasi().enqueue(new Callback<AspirasiResponse>() {
            @Override
            public void onResponse(Call<AspirasiResponse> call, Response<AspirasiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Aspirasi> list = response.body().getAspirasi();
                    recyclerView.setAdapter(new AspirasiAdapter(list));
                } else {
                    Toast.makeText(ListAspirasiActivity.this, "Gagal memuat data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AspirasiResponse> call, Throwable t) {
                Toast.makeText(ListAspirasiActivity.this, "Kesalahan: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
