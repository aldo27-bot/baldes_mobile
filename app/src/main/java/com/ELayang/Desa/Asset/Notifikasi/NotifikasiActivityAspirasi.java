package com.ELayang.Desa.Asset.Notifikasi;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.ELayang.Desa.Asset.Adapter.NotifikasiAdapterAspirasi;
import com.ELayang.Desa.API.APIRequestData;
import com.ELayang.Desa.DataModel.Notifikasi.ResponNotifikasi;
import com.ELayang.Desa.DataModel.Notifikasi.ModelNotifikasi;
import com.ELayang.Desa.R;
import java.util.List;
import retrofit2.*;
import retrofit2.converter.gson.GsonConverterFactory;

public class NotifikasiActivityAspirasi extends AppCompatActivity {
    RecyclerView recyclerView;
    NotifikasiAdapterAspirasi adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_notifikasi);

        recyclerView = findViewById(R.id.view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        String username = "user_login"; // ganti dengan username dari session/login
        loadNotifikasi(username);
    }

    private void loadNotifikasi(String username) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2/DatabaseMobile/") // Ganti base URL servermu
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        APIRequestData api = retrofit.create(APIRequestData.class);
        Call<ResponNotifikasi> call = api.getNotifikasi(username);
        call.enqueue(new Callback<ResponNotifikasi>() {
            @Override
            public void onResponse(Call<ResponNotifikasi> call, Response<ResponNotifikasi> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<ModelNotifikasi> list = response.body().getNotifikasi();
                    adapter = new NotifikasiAdapterAspirasi(list);
                    recyclerView.setAdapter(adapter);
                } else {
                    Toast.makeText(NotifikasiActivityAspirasi.this, "Tidak ada notifikasi", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponNotifikasi> call, Throwable t) {
                Toast.makeText(NotifikasiActivityAspirasi.this, "Gagal: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}

