package com.ELayang.Desa.Menu;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
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
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ganti_password extends AppCompatActivity {

    private EditText password1, password2;
    private Button simpan;
    private ImageView foto;

    private SharedPreferences sharedPreferences;
    private String username, email, nama;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ganti_password);

        // Inisialisasi
        password1 = findViewById(R.id.e_password1);
        password2 = findViewById(R.id.e_password2);
        foto = findViewById(R.id.ikon);
        simpan = findViewById(R.id.simpan);

        sharedPreferences = getSharedPreferences("prefLogin", MODE_PRIVATE);
        username = sharedPreferences.getString("username", "");
        email = sharedPreferences.getString("email", "");
        nama = sharedPreferences.getString("nama", "");

        loadProfileImage();

        // Menyembunyikan password
        password1.setTransformationMethod(PasswordTransformationMethod.getInstance());
        password2.setTransformationMethod(PasswordTransformationMethod.getInstance());

        simpan.setOnClickListener(v -> validateAndUpdatePassword());
    }

    private void loadProfileImage() {
        APIRequestData api = RetroServer.konekRetrofit().create(APIRequestData.class);
        Call<ResponFotoProfil> call = api.getProfile(username);

        call.enqueue(new Callback<ResponFotoProfil>() {
            @Override
            public void onResponse(Call<ResponFotoProfil> call, Response<ResponFotoProfil> response) {
                if (response.isSuccessful() && response.body() != null) {

                    String url = response.body().getUrl_gambar_profil();

                    if (url != null && !url.isEmpty()) {
                        Glide.with(ganti_password.this)
                                .load(url.replace(" ", "%20"))
                                .placeholder(R.drawable.akun_profil)
                                .error(R.drawable.akun_profil)
                                .circleCrop()
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .skipMemoryCache(true)
                                .into(foto);
                    } else {
                        foto.setImageResource(R.drawable.akun_profil);
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponFotoProfil> call, Throwable t) {
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

        new AlertDialog.Builder(this)
                .setTitle("Konfirmasi")
                .setMessage("Apakah kamu yakin ingin mengganti password?")
                .setPositiveButton("Ya", (d, w) -> updatePassword(pass2))
                .setNegativeButton("Tidak", null)
                .show();
    }

    private void updatePassword(String newPassword) {
        APIRequestData api = RetroServer.konekRetrofit().create(APIRequestData.class);

        Call<ResponUpdate> call = api.updateAkun(
                username,
                nama,
                email,
                "",
                newPassword,
                1 // flag perubahan password
        );

        call.enqueue(new Callback<ResponUpdate>() {
            @Override
            public void onResponse(Call<ResponUpdate> call, Response<ResponUpdate> response) {
                ResponUpdate r = response.body();

                if (r != null) {
                    if (r.getKode() == 1) {

                        sharedPreferences.edit()
                                .putString("password", newPassword)
                                .apply();

                        Toast.makeText(ganti_password.this,
                                "Password berhasil diperbarui!",
                                Toast.LENGTH_SHORT).show();

                        finish();
                    } else {
                        Toast.makeText(ganti_password.this,
                                "Gagal: " + r.getPesan(),
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ganti_password.this,
                            "Response kosong dari server!",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponUpdate> call, Throwable t) {
                Toast.makeText(ganti_password.this,
                        "Error: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void kembali(View view) {
        finish();
    }
}
