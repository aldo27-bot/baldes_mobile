package com.ELayang.Desa.Menu;

import static com.ELayang.Desa.API.RetroServer.API_FotoProfil;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.text.method.TransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.ELayang.Desa.API.APIRequestData;
import com.ELayang.Desa.API.RetroServer;
import com.ELayang.Desa.DataModel.Akun.ResponFotoProfil;
import com.ELayang.Desa.DataModel.Akun.ResponUpdate;
import com.ELayang.Desa.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ganti_password extends AppCompatActivity {

    private EditText password1, password2;
    private Button simpan;
    private ImageView foto, togglePassword1, togglePassword2;
    private boolean isPassword1Visible = false, isPassword2Visible = false;

    private SharedPreferences sharedPreferences;
    private String username, email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ganti_password);

        password1 = findViewById(R.id.e_password1);
        password2 = findViewById(R.id.e_password2);
        foto = findViewById(R.id.ikon);
        simpan = findViewById(R.id.simpan);

        // SharedPreferences
        sharedPreferences = getSharedPreferences("prefLogin", MODE_PRIVATE);
        username = sharedPreferences.getString("username", "");
        email = sharedPreferences.getString("email", "");

        // Load foto profil
        loadProfileImage();

        // Sembunyikan password default
        password1.setTransformationMethod(PasswordTransformationMethod.getInstance());
        password2.setTransformationMethod(PasswordTransformationMethod.getInstance());

        // Tombol simpan password
        simpan.setOnClickListener(v -> validateAndUpdatePassword());
    }

    private void loadProfileImage() {
        APIRequestData apiRequestData = RetroServer.konekRetrofit().create(APIRequestData.class);
        Call<ResponFotoProfil> call = apiRequestData.getProfile(username);
        call.enqueue(new Callback<ResponFotoProfil>() {
            @Override
            public void onResponse(Call<ResponFotoProfil> call, Response<ResponFotoProfil> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ResponFotoProfil data = response.body();
                    if (data.getKode() == 1 && data.getUrl_gambar_profil() != null && !data.getUrl_gambar_profil().isEmpty()) {
                        String imageUrl = data.getUrl_gambar_profil().replace(" ", "%20");

                        Glide.with(ganti_password.this)
                                .load(imageUrl)
                                .placeholder(R.drawable.akun_profil)
                                .error(R.drawable.akun_profil)
                                .circleCrop()
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .skipMemoryCache(true)
                                .transition(com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade(300)) // fade-in 300ms
                                .listener(new RequestListener<Drawable>() {
                                    @Override
                                    public boolean onLoadFailed(GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                        Log.e("Glide", "Gagal memuat foto profil: " + e.getMessage());
                                        Toast.makeText(ganti_password.this, "Gagal memuat foto profil!", Toast.LENGTH_SHORT).show();
                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                        Log.d("Glide", "Foto profil berhasil dimuat.");
                                        return false;
                                    }
                                })
                                .into(foto);

                    } else {
                        Log.e("API", "Foto profil kosong atau URL tidak valid");
                        foto.setImageResource(R.drawable.akun_profil);
                    }
                } else {
                    Log.e("API", "Response body kosong atau gagal");
                    foto.setImageResource(R.drawable.akun_profil);
                }
            }

            @Override
            public void onFailure(Call<ResponFotoProfil> call, Throwable t) {
                Log.e("API", "Error: " + t.getMessage());
                foto.setImageResource(R.drawable.akun_profil);
            }
        });
    }


    private void validateAndUpdatePassword() {
        String pass1 = password1.getText().toString().trim();
        String pass2 = password2.getText().toString().trim();

        if (pass1.isEmpty()) {
            password1.setError("Password harus diisi");
            password1.requestFocus();
            return;
        }

        if (pass2.isEmpty()) {
            password2.setError("Konfirmasi password harus diisi");
            password2.requestFocus();
            return;
        }

        if (!pass1.equals(pass2)) {
            password2.setError("Konfirmasi password tidak sama");
            password2.requestFocus();
            return;
        }

        if (pass1.length() < 6) {
            password1.setError("Password minimal 6 karakter");
            password1.requestFocus();
            return;
        }

        // Konfirmasi dialog
        new AlertDialog.Builder(this)
                .setMessage("Apakah kamu yakin ingin mengubah password?")
                .setPositiveButton("Ya", (dialog, which) -> updatePassword(pass2))
                .setNegativeButton("Tidak", null)
                .show();
    }

    private void updatePassword(String newPassword) {
        APIRequestData api = RetroServer.konekRetrofit().create(APIRequestData.class);
        api.updateAkun(username, email, newPassword, sharedPreferences.getString("nama", ""))
                .enqueue(new Callback<ResponUpdate>() {
                    @Override
                    public void onResponse(Call<ResponUpdate> call, Response<ResponUpdate> response) {
                        if (response.body() != null) {
                            if (response.body().getKode() == 1) {
                                Toast.makeText(ganti_password.this, response.body().getPesan(), Toast.LENGTH_SHORT).show();
                                sharedPreferences.edit().putString("password", newPassword).apply();
                                finish();
                            } else {
                                Toast.makeText(ganti_password.this, "Gagal: " + response.body().getPesan(), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(ganti_password.this, "Response kosong!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponUpdate> call, Throwable t) {
                        Toast.makeText(ganti_password.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void kembali(View view) {
        finish();
    }
}