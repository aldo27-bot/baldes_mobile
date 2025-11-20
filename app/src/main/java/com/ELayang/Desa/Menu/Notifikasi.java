package com.ELayang.Desa.Menu;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView; // Import TextView

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ELayang.Desa.API.APIRequestData;
import com.ELayang.Desa.API.RetroServer;
import com.ELayang.Desa.Asset.Adapter.AdapterNotifikasi;
import com.ELayang.Desa.DataModel.Notifikasi.ModelNotifikasi;
import com.ELayang.Desa.DataModel.Notifikasi.ResponNotifikasi;
import com.ELayang.Desa.R;

import java.util.ArrayList;
import java.util.List; // Menggunakan List untuk type-safety

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Notifikasi extends Fragment {
    private ArrayList<ModelNotifikasi> data = new ArrayList<>();
    private RecyclerView recyclerView;
    private TextView tvNotAvailable; // Deklarasi TextView

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Asumsi layout file untuk Fragment ini bernama R.layout.fragment_notifikasi
        // (atau mungkin R.layout.notifikasi, sesuai yang ada di XML sebelumnya)
        View view = inflater.inflate(R.layout.fragment_notifikasi, container, false);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("prefLogin", Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "");

        recyclerView = view.findViewById(R.id.view);
        tvNotAvailable = view.findViewById(R.id.tv_not_available); // 1. Dapatkan Referensi TextView

        // Set the layout manager
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        // Awalnya, sembunyikan RecyclerView (opsional, tapi disarankan saat memuat data)
        recyclerView.setVisibility(View.GONE);
        tvNotAvailable.setVisibility(View.GONE);

        // Panggil API untuk memuat data
        loadNotifikasi(username);

        return view;
    }

    // Metode untuk memuat notifikasi
    private void loadNotifikasi(String username) {
        APIRequestData apiRequestData = RetroServer.konekRetrofit().create(APIRequestData.class);
        Call<ResponNotifikasi> call = apiRequestData.notif(username);

        call.enqueue(new Callback<ResponNotifikasi>() {
            @Override
            public void onResponse(Call<ResponNotifikasi> call, Response<ResponNotifikasi> response) {
                // 2. Logika Penanganan Respons API
                if (response.body() != null && response.body().getKode() == 1) {
                    List<ModelNotifikasi> list = response.body().getData(); // Gunakan List
                    data.clear();

                    if (list != null && !list.isEmpty()) {
                        data.addAll(list);

                        // Data ada: Tampilkan RecyclerView, Sembunyikan TextView
                        AdapterNotifikasi recyclerViewAdapter = new AdapterNotifikasi(data);
                        recyclerView.setAdapter(recyclerViewAdapter);
                        recyclerViewAdapter.notifyDataSetChanged();

                        recyclerView.setVisibility(View.VISIBLE);
                        tvNotAvailable.setVisibility(View.GONE);

                    } else {
                        // Data kosong: Sembunyikan RecyclerView, Tampilkan TextView
                        recyclerView.setVisibility(View.GONE);
                        tvNotAvailable.setText("BELUM ADA NOTIFIKASI"); // Pesan khusus jika list kosong
                        tvNotAvailable.setVisibility(View.VISIBLE);
                    }

                } else {
                    // API mengembalikan kode error (bukan 1): Tampilkan TextView "Fitur Belum Tersedia"
                    recyclerView.setVisibility(View.GONE);
                    tvNotAvailable.setText(R.string.text_fitur_belum_tersedia); // Asumsi Anda punya string di values
                    tvNotAvailable.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<ResponNotifikasi> call, Throwable t) {
                // Gagal koneksi/jaringan: Tampilkan TextView "Fitur Belum Tersedia"
                recyclerView.setVisibility(View.GONE);
                tvNotAvailable.setText("--FITUR AKAN SEGERA TERSEDIA--"); // Pesan khusus
                tvNotAvailable.setVisibility(View.VISIBLE);

                // Disarankan: Tambahkan logging error (seperti Log.e("NotifikasiFrag", "Error: ", t);)
            }
        });
    }
}