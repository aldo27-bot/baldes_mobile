//package com.ELayang.Desa.Asset.Notifikasi;
//
//import android.content.SharedPreferences;
//import android.os.Bundle;
//import android.util.Log;
//import android.widget.Toast;
//import com.google.gson.Gson;
//
//
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.ELayang.Desa.API.APIRequestData;
//import com.ELayang.Desa.API.RetroServer;
//import com.ELayang.Desa.Asset.Adapter.NotifikasiAdapterAspirasi;
//import com.ELayang.Desa.DataModel.Notifikasi.ModelNotifikasi;
//import com.ELayang.Desa.DataModel.Notifikasi.ResponNotifikasi;
//import com.ELayang.Desa.R;
//import com.ELayang.Desa.DataModel.Notifikasi.ResponNotifikasiAspirasi;
//import com.ELayang.Desa.DataModel.Notifikasi.ModelNotifikasiAspirasi;
//
//
//import java.util.List;
//
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
//
//public class NotifikasiActivityAspirasi extends AppCompatActivity {
//    RecyclerView recyclerView;
//    NotifikasiAdapterAspirasi adapter;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.fragment_notifikasi);
//
//        recyclerView = findViewById(R.id.view);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//
//        // ✅ Ambil username dari SharedPreferences
//        SharedPreferences sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE);
//        String username = sharedPreferences.getString("username", null);
//
//        Log.d("NOTIF_USERNAME", "Username dikirim: " + username);
//
//        if (username != null) {
//            loadNotifikasi(username);
//        } else {
//            Toast.makeText(this, "Username tidak ditemukan di session", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    private void loadNotifikasi(String username) {
//        APIRequestData api = RetroServer.konekRetrofit().create(APIRequestData.class);
//        Call<ResponNotifikasiAspirasi> call = api.getNotifikasiAspirasi(username);
//        call.enqueue(new Callback<ResponNotifikasiAspirasi>() {
//            @Override
//            public void onResponse(Call<ResponNotifikasiAspirasi> call, Response<ResponNotifikasiAspirasi> response) {
//                if (response.isSuccessful() && response.body() != null) {
//                    // Tambahkan log di sini untuk melihat isi respon
//                    Log.d("API_RESPONSE", "Respon: " + new Gson().toJson(response.body()));
//
//                    List<ModelNotifikasiAspirasi> list = response.body().getData();
//                    if (list != null && !list.isEmpty()) {
//                        adapter = new NotifikasiAdapterAspirasi(list);
//                        recyclerView.setAdapter(adapter);
//                    } else {
//                        Log.e("API_RESPONSE", "Data kosong dalam response.body()");
//                    }
//                } else {
//                    try {
//                        Log.e("API_RESPONSE", "Error body: " + response.errorBody().string());
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                    Log.e("API_RESPONSE", "Response gagal atau kosong");
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ResponNotifikasiAspirasi> call, Throwable t) {
//                Log.e("API_RESPONSE", "Gagal konek: " + t.getMessage());
//            }
//        });
//    }
//}
//
