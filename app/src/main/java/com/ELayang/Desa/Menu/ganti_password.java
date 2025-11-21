package com.ELayang.Desa.Menu;

import static com.ELayang.Desa.API.RetroServer.API_FotoProfil;
import static com.ELayang.Desa.API.RetroServer.API_IMAGE;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.ELayang.Desa.API.APIRequestData;
import com.ELayang.Desa.API.RetroServer;
import com.ELayang.Desa.DataModel.Akun.ModelLogin;
import com.ELayang.Desa.DataModel.Akun.ResponFotoProfil;
import com.ELayang.Desa.DataModel.Akun.ResponUpdate;
import com.ELayang.Desa.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.io.File;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ganti_password extends AppCompatActivity {
    EditText password1, password2;
    Button simpan;
    private boolean isPasswordVisible = true;
    private ImageView foto;
    private String KEY_NAME = "NAMA";
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ganti_password);

        password1 = findViewById(R.id.e_password1);
        password2 = findViewById(R.id.e_password2);
        foto = findViewById(R.id.ikon);

        SharedPreferences sharedPreferences = getSharedPreferences("prefLogin", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        ModelLogin api_image1 = new ModelLogin();
        api_image1.getAPI_IMAGE();
        String api_image = API_IMAGE+api_image1;
        RetroServer image = new RetroServer();

        String username = sharedPreferences.getString("username", "");
        String password = sharedPreferences.getString("password", "");
        String savedImagePath = sharedPreferences.getString("profile_image", "");

        // untuk menampilkan foto profil melalui server
        APIRequestData apiRequestData = RetroServer.konekRetrofit().create(APIRequestData.class);
        Call<ResponFotoProfil> call = apiRequestData.getFotoProfil(username);
        call.enqueue(new Callback<ResponFotoProfil>() {
            @Override
            public void onResponse(Call<ResponFotoProfil> call, Response<ResponFotoProfil> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ResponFotoProfil data = response.body();
                    if (data.isKode()) {
                        String imageUrl = API_FotoProfil + data.getImageUrl();
                        // Gunakan imageUrl untuk menampilkan gambar, misalnya di ImageView

                        Glide.with(ganti_password.this)
                                .load(imageUrl)  // URL gambar dari server
                                .placeholder(R.drawable.akun_profil)  // Placeholder jika gambar tidak ada
                                .error(R.drawable.akun_profil)  // Jika gagal memuat gambar
                                .circleCrop()  // Memotong gambar menjadi bentuk lingkaran
                                .diskCacheStrategy(DiskCacheStrategy.NONE) // Abaikan cache
                                .skipMemoryCache(true)  // Tidak menyimpan cache di memori
                                .listener(new RequestListener<Drawable>() {
                                    @Override
                                    public boolean onLoadFailed(GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                        // Ketika gambar gagal dimuat
                                        Log.e("Glide", "Gagal memuat foto profil: " + e.getMessage());
                                        Toast.makeText(ganti_password.this, "Gagal mengubah foto profil!", Toast.LENGTH_SHORT).show();
                                        return false; // Return false untuk tetap menampilkan placeholder error
                                    }

                                    @Override
                                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
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

        // Menyembunyikan password dengan PasswordTransformationMethod
        password1.setTransformationMethod(PasswordTransformationMethod.getInstance());
        password2.setTransformationMethod(PasswordTransformationMethod.getInstance());

        simpan = findViewById(R.id.simpan);
        simpan.setOnClickListener(v -> {
            if (password1.getText().toString().isEmpty()) {
                password1.setError("Password Harus Diisi");
                password1.requestFocus();
            } else if (password2.getText().toString().isEmpty()) {
                password2.setError("Masukan Konformasi Password");
                password2.requestFocus();
            } else if (!password1.getText().toString().equals(password2.getText().toString())) {
                password2.setError("Konfirmasi Password Tidak Sama");
                password2.requestFocus();
            } else if (password1.length() < 6) {
                password1.setError("Password Harus Lebih dari 6 karakter");
                password1.requestFocus();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Apakah kamu yakin ingin melanjutkan?")
                        .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                APIRequestData apiRequestData = RetroServer.konekRetrofit().create(APIRequestData.class);
                                Call<ResponUpdate> call = apiRequestData.update_akun(username,sharedPreferences.getString("email", ""),
                                        password2.getText().toString(), sharedPreferences.getString("nama", ""));
                                Log.d("DEBUG", "Password1: " + password1.getText().toString());
                                Log.d("DEBUG", "Password2: " + password2.getText().toString());
                                call.enqueue(new Callback<ResponUpdate>() {
                                    @Override
                                    public void onResponse(Call<ResponUpdate> call, Response<ResponUpdate> response) {
                                        if (response.body() != null) {
                                            if (response.body().getKode() == 1) {
                                                Toast.makeText(ganti_password.this, response.body().getPesan(), Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(ganti_password.this, "Gagal: " + response.body().getPesan(), Toast.LENGTH_SHORT).show();
                                            }
                                        } else {
                                            Toast.makeText(ganti_password.this, "Response kosong!", Toast.LENGTH_SHORT).show();
                                        }

                                    }
                                    @Override
                                    public void onFailure(Call<ResponUpdate> call, Throwable t) {
                                        Toast.makeText(ganti_password.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        })
                        .setNegativeButton ("Tidak", null)
                        .show();
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Toast.makeText(this, "gunakan tombol kembali yang ada di atas", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void kembali(View view) {
        finish();
    }
}