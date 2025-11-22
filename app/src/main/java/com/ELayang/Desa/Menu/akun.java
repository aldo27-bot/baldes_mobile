package com.ELayang.Desa.Menu;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.ELayang.Desa.API.APIRequestData;
import com.ELayang.Desa.API.RetroServer;
import com.ELayang.Desa.DataModel.Akun.ResponFotoProfil;
import com.ELayang.Desa.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class akun extends Fragment {

    TextView nama, email, usernamee;
    private ImageView foto;
    Button keluar;
    View view;

    public static final String KEY_PREF = "prefLogin";

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

        SharedPreferences sp = getActivity().getSharedPreferences(KEY_PREF, MODE_PRIVATE);
        String username = sp.getString("username", "");
        nama.setText(sp.getString("nama", ""));
        email.setText(sp.getString("email", ""));
        usernamee.setText(username);

        loadProfileImage(username);

        // buka edit profil
        buka.setOnClickListener(v -> startActivityForResult(
                new Intent(getContext(), edit_profil.class), 100));

        // logout
        keluar.setOnClickListener(v -> logout());

        return view;
    }

    private void loadProfileImage(String username) {
        SharedPreferences sp = getActivity().getSharedPreferences(KEY_PREF, MODE_PRIVATE);
        String savedUrl = sp.getString("photo_url", "");

        if (!savedUrl.isEmpty()) {
            // gunakan savedUrl jika ada
            loadWithGlide(savedUrl);
            return;
        }

        // ambil dari API jika belum ada
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
                .transition(DrawableTransitionOptions.withCrossFade(300))
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        Log.e("Glide", "Gagal memuat foto profil: " + e.getMessage());
                        Toast.makeText(getContext(), "Gagal memuat foto profil!", Toast.LENGTH_SHORT).show();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        Log.d("Glide", "Foto profil berhasil dimuat.");
                        return false;
                    }
                })
                .into(foto);
    }

    private void logout() {
        SharedPreferences sp = getActivity().getSharedPreferences(KEY_PREF, MODE_PRIVATE);
        sp.edit().clear().apply();
        getActivity().finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 100 && resultCode == RESULT_OK) {
            SharedPreferences sp = getActivity().getSharedPreferences(KEY_PREF, MODE_PRIVATE);
            loadProfileImage(sp.getString("username", ""));
        }
    }
}
