package com.ELayang.Desa.Menu;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;



import androidx.fragment.app.Fragment;

import com.ELayang.Desa.API.APIRequestData;
import com.ELayang.Desa.API.RetroServer;
import com.ELayang.Desa.DataModel.Akun.ResponFotoProfil;
import com.ELayang.Desa.Login.login;
import com.ELayang.Desa.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class akun extends Fragment {

    TextView nama, email, usernamee;
    private ImageView foto;
    Button keluar;
    View view;

    public static final String KEY_PREF = "prefLogin";

    private SharedPreferences sp;
    private SharedPreferences.OnSharedPreferenceChangeListener listener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_akun, container, false);

        nama = view.findViewById(R.id.masukan_nama);
        email = view.findViewById(R.id.masukan_email);
        usernamee = view.findViewById(R.id.masukan_username);
        foto = view.findViewById(R.id.foto_profil);
        Button buka = view.findViewById(R.id.buka);
        keluar = view.findViewById(R.id.logout);

        sp = getActivity().getSharedPreferences(KEY_PREF, MODE_PRIVATE);

        // load data awal
        String username = sp.getString("username", "");
        nama.setText(sp.getString("nama", ""));
        email.setText(sp.getString("email", ""));
        usernamee.setText(username);

        loadProfileImage(username);

        // buka edit profil
        buka.setOnClickListener(v -> startActivityForResult(
                new android.content.Intent(getContext(), edit_profil.class), 100));

        // logout
        keluar.setOnClickListener(v -> logout());

        // listener SharedPreferences
        listener = (sharedPreferences, key) -> {
            if (key == null) return; // jika key null, abaikan

            switch (key) {
                case "photo_url":
                    String newPhotoUrl = sharedPreferences.getString("photo_url", "");
                    if (newPhotoUrl != null && !newPhotoUrl.isEmpty()) loadWithGlide(newPhotoUrl);
                    break;
                case "nama":
                    String newNama = sharedPreferences.getString("nama", "");
                    if (newNama != null) nama.setText(newNama);
                    break;
                case "email":
                    String newEmail = sharedPreferences.getString("email", "");
                    if (newEmail != null) email.setText(newEmail);
                    break;
                case "username":
                    String newUsername = sharedPreferences.getString("username", "");
                    if (newUsername != null) usernamee.setText(newUsername);
                    break;
            }
        };

        sp.registerOnSharedPreferenceChangeListener(listener);

        return view;
    }

    private void loadProfileImage(String username) {
        String savedUrl = sp.getString("photo_url", "");

        if (!savedUrl.isEmpty()) {
            loadWithGlide(savedUrl);
            return;
        }

        APIRequestData apiRequestData = RetroServer.konekRetrofit().create(APIRequestData.class);
        Call<ResponFotoProfil> call = apiRequestData.getProfile(username);
        call.enqueue(new Callback<ResponFotoProfil>() {
            @Override
            public void onResponse(Call<ResponFotoProfil> call, Response<ResponFotoProfil> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ResponFotoProfil data = response.body();
                    if (data.getKode() == 1) {
                        String url = data.getUrl_gambar_profil();
                        if (url != null && !url.isEmpty()) {
                            url = url.replace(" ", "%20");
                            sp.edit().putString("photo_url", url).apply();
                            loadWithGlide(url);
                        } else {
                            foto.setImageResource(R.drawable.akun_profil);
                        }
                    } else {
                        foto.setImageResource(R.drawable.akun_profil);
                    }
                } else {
                    foto.setImageResource(R.drawable.akun_profil);
                }
            }

            @Override
            public void onFailure(Call<ResponFotoProfil> call, Throwable t) {
                Log.e("API", "Error load profile: " + t.getMessage());
                foto.setImageResource(R.drawable.akun_profil);
            }
        });
    }

    private void loadWithGlide(String url) {
        Glide.with(this)
                .load(url)
                .placeholder(R.drawable.akun_profil)
                .error(R.drawable.akun_profil)
                .circleCrop()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(foto);
    }

    private void logout() {
        // Cek apakah ada data login
        if (sp.getAll().isEmpty()) {
            Toast.makeText(getActivity(), "Anda belum login.", Toast.LENGTH_SHORT).show();
            return; // keluar jika tidak ada data login
        }

        // Tampilkan dialog konfirmasi
        new AlertDialog.Builder(getActivity())
                .setTitle("Konfirmasi Logout")
                .setMessage("Apakah Anda yakin ingin keluar?")
                .setPositiveButton("Ya", (dialog, which) -> {
                    // Hapus semua data login
                    sp.edit().clear().apply();

                    // Buka LoginActivity dan hapus semua activity sebelumnya
                    Intent intent = new Intent(getActivity(), login.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                })
                .setNegativeButton("tidak", (dialog, which) -> dialog.dismiss())
                .show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (sp != null && listener != null) sp.unregisterOnSharedPreferenceChangeListener(listener);
    }
}
