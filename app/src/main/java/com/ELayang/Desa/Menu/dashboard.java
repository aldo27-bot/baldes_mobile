package com.ELayang.Desa.Menu;

import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ELayang.Desa.API.APIRequestData;
import com.ELayang.Desa.API.RetroServer;
import com.ELayang.Desa.Asset.imagePagerAdapter;
import com.ELayang.Desa.DataModel.StatusDasboardModel;
import com.ELayang.Desa.DataModel.StatusDasboardRespon;
import com.ELayang.Desa.R;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class dashboard extends Fragment {

    private TextView selesai, proses, tolak, masuk;
    private imagePagerAdapter adapter;
    private Handler autoScrollHandler;
    private Runnable autoScrollRunnable;
    private static final int AUTO_SCROLL_DELAY = 4000;
    private boolean isUserScrolling = false;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_dashboard, container, false);

        selesai = rootView.findViewById(R.id.surat_selesai);
        tolak = rootView.findViewById(R.id.surat_ditolak);
        masuk = rootView.findViewById(R.id.surat_masuk);
        swipeRefreshLayout = rootView.findViewById(R.id.swipe_refresh);

        TextView hello = rootView.findViewById(R.id.hello);

        // Ambil data SharedPreferences
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("prefLogin", Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "");
        String nama = sharedPreferences.getString("nama", "");
        hello.setText("Halo, " + nama);

        // Setup ViewPager
        ViewPager viewPager = rootView.findViewById(R.id.viewPager);
        adapter = new imagePagerAdapter(getContext());
        viewPager.setAdapter(adapter);
        int middle = adapter.getCount() / 2;
        viewPager.setCurrentItem(middle, true);

        autoScrollHandler = new Handler(Looper.getMainLooper());
        autoScrollRunnable = new Runnable() {
            @Override
            public void run() {
                int currentItem = viewPager.getCurrentItem();
                viewPager.setCurrentItem(currentItem + 1, true);
                autoScrollHandler.postDelayed(this, AUTO_SCROLL_DELAY);
            }
        };

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
            @Override
            public void onPageSelected(int position) {
                stopAutoScroll();
                isUserScrolling = true;
            }
            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == ViewPager.SCROLL_STATE_IDLE && isUserScrolling) {
                    startAutoScroll();
                    isUserScrolling = false;
                }
            }
        });

        // Swipe refresh listener
        swipeRefreshLayout.setOnRefreshListener(() -> {
            loadDashboardData();
        });

        // Load data pertama kali
        loadDashboardData();

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        startAutoScroll();
        loadDashboardData(); // update data setiap fragment muncul
    }

    @Override
    public void onPause() {
        super.onPause();
        stopAutoScroll();
    }

    private void startAutoScroll() {
        autoScrollHandler.postDelayed(autoScrollRunnable, AUTO_SCROLL_DELAY);
    }

    private void stopAutoScroll() {
        autoScrollHandler.removeCallbacks(autoScrollRunnable);
    }

    private void loadDashboardData() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("prefLogin", Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "");

        APIRequestData apiRequestData = RetroServer.konekRetrofit().create(APIRequestData.class);
        Call<StatusDasboardRespon> call = apiRequestData.dashboard(username);
        call.enqueue(new Callback<StatusDasboardRespon>() {
            @Override
            public void onResponse(Call<StatusDasboardRespon> call, Response<StatusDasboardRespon> response) {
                swipeRefreshLayout.setRefreshing(false); // hentikan refresh
                if (response.isSuccessful() && response.body() != null && response.body().isKode()) {
                    StatusDasboardModel model = response.body().getData().get(0);
                    selesai.setText(model.getSelesai() != null ? model.getSelesai() : "0");
                    tolak.setText(model.getTolak() != null ? model.getTolak() : "0");
                    masuk.setText(model.getMasuk() != null ? model.getMasuk() : "0");
                } else {
                    selesai.setText("0");
                    tolak.setText("0");
                    masuk.setText("0");
                }
            }

            @Override
            public void onFailure(Call<StatusDasboardRespon> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                selesai.setText("0");
                tolak.setText("0");
                masuk.setText("0");
                Log.e("DashboardError", t.getMessage());
            }
        });
    }
}
