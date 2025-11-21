package com.ELayang.Desa.Menu;

import static android.content.Context.MODE_PRIVATE;

import static com.ELayang.Desa.API.RetroServer.API_FotoProfil;
import static com.ELayang.Desa.API.RetroServer.API_IMAGE;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.viewmodel.CreationExtras;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ELayang.Desa.API.APIRequestData;
import com.ELayang.Desa.API.RetroServer;
import com.ELayang.Desa.DataModel.Akun.ModelLogin;
import com.ELayang.Desa.DataModel.Akun.ResponFotoProfil;
import com.ELayang.Desa.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.android.material.button.MaterialButton;
import com.squareup.picasso.Picasso;

import java.io.File;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class akun extends Fragment implements GoogleApiClient.OnConnectionFailedListener {
    TextView nama, email, usernamee;
    private ImageView foto;
    MaterialButton buka;
    Button keluar;
    private static final String CHANNEL_ID = "MyChannelID";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_akun, container, false);

        nama = view.findViewById(R.id.masukan_nama);
        email = view.findViewById(R.id.masukan_email);
        usernamee = view.findViewById(R.id.masukan_username);
        foto = view.findViewById(R.id.foto_profil);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("prefLogin", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        String username = sharedPreferences.getString("username", "");
        String password = sharedPreferences.getString("password", "");
        String savedImagePath = sharedPreferences.getString("profile_image", "");

        // untuk menampilkan foto profil melalui server
        APIRequestData apiRequestData = RetroServer.konekRetrofit().create(APIRequestData.class);
        Call<ResponFotoProfil> call = apiRequestData.getFotoProfil(username);
        call.enqueue(new Callback<ResponFotoProfil>() {
            @Override
            public void onResponse(Call<ResponFotoProfil> call, Response<ResponFotoProfil> response) {
                if(response.isSuccessful() && response.body() != null) {
                    ResponFotoProfil data = response.body();
                    if (data.isKode()) {
                        String imageUrl = API_FotoProfil + data.getImageUrl();
                        // Gunakan imageUrl untuk menampilkan gambar, misalnya di ImageView

                        Glide.with(akun.this)
                                .load(imageUrl)  // URL gambar dari server
                                .placeholder(R.drawable.akun_profil)  // Placeholder jika gambar tidak ada
                                .error(R.drawable.akun_profil)  // Jika gagal memuat gambar
                                .circleCrop()  // Memotong gambar menjadi bentuk lingkaran
                                .diskCacheStrategy(DiskCacheStrategy.NONE) // Abaikan cache
                                .skipMemoryCache(true)  // Tidak menyimpan cache di memori
                                .listener(new RequestListener<Drawable>() {
                                    @Override
                                    public boolean onLoadFailed(GlideException e, Object model,
                                                                Target<Drawable> target, boolean isFirstResource) {
                                        // Ketika gambar gagal dimuat
                                        Log.e("Glide", "Gagal memuat foto profil: " + e.getMessage());
                                        return false; // Return false untuk tetap menampilkan placeholder error
                                    }

                                    @Override
                                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target,
                                                                   DataSource dataSource, boolean isFirstResource) {
                                        // Ketika gambar berhasil dimuat
                                        Log.d("Glide", "Foto profil berhasil dimuat.");
                                        return false; // Return false untuk tetap melanjutkan proses pemuatan gambar ke ImageView
                                    }
                                })
                                .into(foto);
                    } else {
                        Log.e("API", "Pesan error: " + data.getPesan());
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponFotoProfil> call, Throwable t) {
                Log.e("API", "Error: " + t.getMessage());
            }
        });
        nama.setText(sharedPreferences.getString("nama", ""));
        email.setText(sharedPreferences.getString("email", ""));
        usernamee.setText(sharedPreferences.getString("username", ""));

        keluar = view.findViewById(R.id.logout);
        keluar.setOnClickListener(v -> {

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Konfirmasi Keluar");
            builder.setMessage("Apakah anda yakin ingin keluar?");
            builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Hapus data dari SharedPreferences
                    editor.clear();
                    editor.apply();

                    getActivity().finish();
                }
            });
            builder.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();

        });

        buka = view.findViewById(R.id.buka);
        buka.setOnClickListener(v -> {

            Intent buka = new Intent(getContext(), edit_profil.class);
            startActivity(buka);
        });

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e("GoogleSignIn", "Connection failed: " + connectionResult);
    }

    @NonNull
    @Override
    public CreationExtras getDefaultViewModelCreationExtras() {
        return super.getDefaultViewModelCreationExtras();
    }
}