package com.ELayang.Desa.Menu;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ELayang.Desa.API.APIRequestData;
import com.ELayang.Desa.API.RetroServer;
import com.ELayang.Desa.Asset.RiwayatSurat.SuratDiajukan;
import com.ELayang.Desa.DataModel.RiwayatSurat.ModelDiajukan;
import com.ELayang.Desa.DataModel.RiwayatSurat.ResponDiajukan;
import com.ELayang.Desa.R;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class riwayat_surat_suratDiajukan extends Fragment {

    private View view;
    private ArrayList<ModelDiajukan> data = new ArrayList<>();
    private RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_riwayat_surat_surat_diajukan, container, false);

        recyclerView = view.findViewById(R.id.D_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        loadData();

        // Tombol back fisik
        requireActivity().getOnBackPressedDispatcher().addCallback(
                getViewLifecycleOwner(),
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        if (getParentFragmentManager().getBackStackEntryCount() > 0) {
                            getParentFragmentManager().popBackStack();
                        }
                    }
                });

        return view;
    }

    private void loadData() {

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("prefLogin", Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "");

        APIRequestData apiRequestData = RetroServer.konekRetrofit().create(APIRequestData.class);
        Call<ResponDiajukan> call = apiRequestData.proses(username);

        call.enqueue(new Callback<ResponDiajukan>() {
            @Override
            public void onResponse(Call<ResponDiajukan> call, Response<ResponDiajukan> response) {

                ResponDiajukan respon = response.body();

                if (respon != null && respon.getKode() == 1) {

                    ArrayList<ModelDiajukan> list = (ArrayList<ModelDiajukan>) respon.getData();

                    if (list != null && !list.isEmpty()) {
                        data.clear();
                        data.addAll(list);

                        SuratDiajukan adapter = new SuratDiajukan(getActivity(), data);
                        recyclerView.setAdapter(adapter);

                    } else {
                        Toast.makeText(getContext(), "Belum ada surat diajukan", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(getContext(), respon != null ? respon.getPesan() : "Gagal mengambil data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponDiajukan> call, Throwable t) {
                Log.e("SuratDiajukan", "Error: " + t.getMessage());
                Toast.makeText(getContext(), "Terjadi kesalahan: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
