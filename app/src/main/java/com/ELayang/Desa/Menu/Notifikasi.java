package com.ELayang.Desa.Menu;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.ELayang.Desa.API.APIRequestData;
import com.ELayang.Desa.API.RetroServer;
import com.ELayang.Desa.Asset.Adapter.AdapterNotifikasi;
import com.ELayang.Desa.DataModel.Notifikasi.ModelNotifikasi;
import com.ELayang.Desa.DataModel.Notifikasi.ResponNotifikasi;
import com.ELayang.Desa.DataModel.Notifikasi.ResponPopup;
import com.ELayang.Desa.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Notifikasi extends Fragment {

    private RecyclerView rv;
    private SwipeRefreshLayout swipe;
    private TextView tvNotAvail;

    private AdapterNotifikasi adapter;
    private List<ModelNotifikasi> list = new ArrayList<>();

    private SharedPreferences prefLogin;
    private SharedPreferences prefNotif;

    private static final String PREF_LOGIN = "prefLogin";
    private static final String PREF_NOTIF = "NotifPrefs";
    private static final String KEY_LAST_STATUS = "last_surat_status";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_notifikasi, container, false);

        rv = v.findViewById(R.id.view);
        swipe = v.findViewById(R.id.swipeRefresh);
        tvNotAvail = v.findViewById(R.id.tv_not_available);

        rv.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new AdapterNotifikasi(getContext(), list);
        rv.setAdapter(adapter);

        prefLogin = requireActivity().getSharedPreferences(PREF_LOGIN, Context.MODE_PRIVATE);
        prefNotif = requireActivity().getSharedPreferences(PREF_NOTIF, Context.MODE_PRIVATE);

        swipe.setOnRefreshListener(this::fetchNotifikasi);

        swipe.setRefreshing(true);
        fetchNotifikasi();
        checkPopup();

        return v;
    }

    // ========================================================================================
    // AMBIL LIST NOTIFIKASI
    // ========================================================================================
    private void fetchNotifikasi() {

        String username = prefLogin.getString("username", "");
        if (username.isEmpty()) {
            swipe.setRefreshing(false);
            tvNotAvail.setVisibility(View.VISIBLE);
            tvNotAvail.setText("Harap login terlebih dahulu.");
            return;
        }

        APIRequestData api = RetroServer.konekRetrofit().create(APIRequestData.class);
        Call<ResponNotifikasi> call = api.getNotifikasi(username);

        call.enqueue(new Callback<ResponNotifikasi>() {
            @Override
            public void onResponse(Call<ResponNotifikasi> call, Response<ResponNotifikasi> response) {
                swipe.setRefreshing(false);

                if (response.isSuccessful() && response.body() != null && response.body().getKode() == 1) {

                    list.clear();
                    list.addAll(response.body().getData());

                    // Hapus otomatis notif lama
                    removeOldNotifications();

                    adapter.notifyDataSetChanged();
                    tvNotAvail.setVisibility(list.isEmpty() ? View.VISIBLE : View.GONE);

                } else {
                    list.clear();
                    adapter.notifyDataSetChanged();
                    tvNotAvail.setVisibility(View.VISIBLE);
                    tvNotAvail.setText("Tidak ada notifikasi.");
                }
            }

            @Override
            public void onFailure(Call<ResponNotifikasi> call, Throwable t) {
                swipe.setRefreshing(false);
                tvNotAvail.setVisibility(View.VISIBLE);
                tvNotAvail.setText("Kesalahan: " + t.getMessage());
                Log.e("NOTIFIKASI", "ERROR => " + t.getMessage());
            }
        });
    }

    // ========================================================================================
    // HAPUS NOTIFIKASI > 30 HARI
    // ========================================================================================
    private void removeOldNotifications() {
        long now = System.currentTimeMillis();
        long THIRTY_DAYS = 30L * 24 * 60 * 60 * 1000;

        List<ModelNotifikasi> toRemove = new ArrayList<>();

        for (ModelNotifikasi notif : list) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                Date date = sdf.parse(notif.getTanggal());

                if (date != null && (now - date.getTime() > THIRTY_DAYS)) {
                    toRemove.add(notif);
                }

            } catch (Exception e) {
                Log.e("NOTIFIKASI", "Format tanggal salah: " + e.getMessage());
            }
        }

        list.removeAll(toRemove);
    }

    // ========================================================================================
    // POPUP NOTIFIKASI BARU
    // ========================================================================================
    private void checkPopup() {

        String username = prefLogin.getString("username", "");
        if (username.isEmpty()) return;

        APIRequestData api = RetroServer.konekRetrofit().create(APIRequestData.class);
        Call<ResponPopup> call = api.getPopupNotifikasi(username);

        call.enqueue(new Callback<ResponPopup>() {
            @Override
            public void onResponse(Call<ResponPopup> call, Response<ResponPopup> response) {

                if (response.isSuccessful() && response.body() != null && response.body().getKode() == 1) {

                    String currentStatus = response.body().getStatus();
                    String currentAlasan = response.body().getAlasan();

                    String last = prefNotif.getString(KEY_LAST_STATUS, "");

                    if (currentStatus != null && !currentStatus.equals(last)) {

                        String message = currentStatus +
                                (currentAlasan != null && !currentAlasan.isEmpty()
                                        ? "\nAlasan: " + currentAlasan : "");

                        new android.app.AlertDialog.Builder(requireContext())
                                .setTitle("Status Notifikasi")
                                .setMessage(message)
                                .setPositiveButton("OK", (d, i) -> d.dismiss())
                                .show();

                        prefNotif.edit().putString(KEY_LAST_STATUS, currentStatus).apply();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponPopup> call, Throwable t) {
                Log.e("POPUP", "ERROR => " + t.getMessage());
            }
        });
    }
}
