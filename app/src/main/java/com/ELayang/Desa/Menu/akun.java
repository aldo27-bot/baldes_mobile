package com.ELayang.Desa.Menu;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;
import static com.ELayang.Desa.API.RetroServer.API_FotoProfil;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import com.google.android.gms.common.ConnectionResult;

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

        loadFoto(username);

        buka.setOnClickListener(v -> startActivityForResult(new Intent(getContext(), edit_profil.class), 100));

        keluar.setOnClickListener(v -> logout());

        return view;
    }

    private void loadFoto(String username) {

        SharedPreferences sp = getActivity().getSharedPreferences(KEY_PREF, MODE_PRIVATE);
        String savedUrl = sp.getString("photo_url", "");

        if (!savedUrl.isEmpty()) {
            Glide.with(this)
                    .load(API_FotoProfil + savedUrl)
                    .circleCrop()
                    .placeholder(R.drawable.akun_profil)
                    .error(R.drawable.akun_profil)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into(foto);
            return;
        }

        APIRequestData api = RetroServer.konekRetrofit().create(APIRequestData.class);
        api.getFotoProfil(username).enqueue(new Callback<ResponFotoProfil>() {
            @Override
            public void onResponse(Call<ResponFotoProfil> call, Response<ResponFotoProfil> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isKode()) {

                    String url = response.body().getImageUrl();
                    sp.edit().putString("photo_url", url).apply();

                    Glide.with(akun.this)
                            .load(API_FotoProfil + url)
                            .circleCrop()
                            .placeholder(R.drawable.akun_profil)
                            .error(R.drawable.akun_profil)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                            .into(foto);

                } else {
                    Log.e("API", "Gagal memuat foto");
                }
            }

            @Override
            public void onFailure(Call<ResponFotoProfil> call, Throwable t) {
                Log.e("API", "Error: " + t.getMessage());
            }
        });
    }

    private void logout() {
        SharedPreferences sp = getActivity().getSharedPreferences(KEY_PREF, MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        editor.apply();
        getActivity().finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 100 && resultCode == RESULT_OK) {
            SharedPreferences sp = getActivity().getSharedPreferences(KEY_PREF, MODE_PRIVATE);
            loadFoto(sp.getString("username", ""));
        }
    }
}
