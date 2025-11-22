package com.ELayang.Desa.Menu;

import static com.ELayang.Desa.Menu.akun.KEY_PREF;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class edit_profil extends AppCompatActivity {

    private ImageButton btnGantiFoto;
    private ImageView fotoProfil;
    private ProgressBar progressBar;
    private TextInputEditText etNama, etEmail, etUsername;
    private MaterialButton btnSimpan, btnGantiPassword;

    private Uri imageUri;
    private Bitmap bitmap;
    private String base64Image = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profil);

        btnGantiFoto = findViewById(R.id.btn_edit_image);
        fotoProfil = findViewById(R.id.pp);
        progressBar = findViewById(R.id.progressBar);
        etNama = findViewById(R.id.e_nama);
        etEmail = findViewById(R.id.e_email);
        etUsername = findViewById(R.id.e_username);
        btnSimpan = findViewById(R.id.simpan);
        btnGantiPassword = findViewById(R.id.gantipw);

        loadData(); // Load data termasuk foto profil

        btnGantiFoto.setOnClickListener(v -> pilihGambar());
        btnSimpan.setOnClickListener(v -> updateProfil());
        btnGantiPassword.setOnClickListener(v -> {
            Intent intent = new Intent(edit_profil.this, ganti_password.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        });
    }

    private void loadData() {
        SharedPreferences sp = getSharedPreferences(KEY_PREF, MODE_PRIVATE);
        etNama.setText(sp.getString("nama",""));
        etEmail.setText(sp.getString("email",""));
        etUsername.setText(sp.getString("username",""));
        String savedUrl = sp.getString("photo_url","");

        if (savedUrl != null && !savedUrl.isEmpty()) {
            loadFotoDenganGlide(savedUrl);
        } else {
            String username = sp.getString("username","");
            ambilFotoDariAPI(username);
        }
    }

    private void loadFotoDenganGlide(String url) {
        Glide.with(this)
                .load(url.replace(" ", "%20"))
                .circleCrop()
                .placeholder(R.drawable.akun_profil)
                .error(R.drawable.akun_profil)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        Log.e("Glide", "Gagal memuat foto profil: " + e.getMessage());
                        fotoProfil.setImageResource(R.drawable.akun_profil);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        Log.d("Glide", "Foto profil berhasil dimuat.");
                        return false;
                    }
                })
                .into(fotoProfil);
    }

    private void ambilFotoDariAPI(String username) {
        APIRequestData api = RetroServer.konekRetrofit().create(APIRequestData.class);
        api.getProfile(username).enqueue(new Callback<ResponFotoProfil>() {
            @Override
            public void onResponse(Call<ResponFotoProfil> call, Response<ResponFotoProfil> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getKode() == 1) {
                    String url = response.body().getUrl_gambar_profil();
                    if (url != null && !url.isEmpty()) {
                        getSharedPreferences(KEY_PREF, MODE_PRIVATE).edit().putString("photo_url", url).apply();
                        loadFotoDenganGlide(url);
                    } else {
                        fotoProfil.setImageResource(R.drawable.akun_profil);
                    }
                } else {
                    fotoProfil.setImageResource(R.drawable.akun_profil);
                }
            }

            @Override
            public void onFailure(Call<ResponFotoProfil> call, Throwable t) {
                Log.e("API", "Gagal ambil foto profil: " + t.getMessage());
                fotoProfil.setImageResource(R.drawable.akun_profil);
            }
        });
    }

    private void pilihGambar() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if (requestCode==100 && resultCode==RESULT_OK && data!=null){
            imageUri = data.getData();
            try {
                Bitmap original = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                bitmap = resizeBitmap(original, 500);
                fotoProfil.setImageBitmap(bitmap);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
                base64Image = "data:image/jpeg;base64," + Base64.encodeToString(baos.toByteArray(), Base64.NO_WRAP);

            } catch (IOException e){
                e.printStackTrace();
                Toast.makeText(this,"Gagal membaca gambar",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private Bitmap resizeBitmap(Bitmap original, int maxSize){
        int width = original.getWidth();
        int height = original.getHeight();
        float ratio = (float) width/height;
        if (ratio>1){ width=maxSize; height=(int)(width/ratio); }
        else { height=maxSize; width=(int)(height*ratio); }
        return Bitmap.createScaledBitmap(original,width,height,true);
    }

    private void updateProfil(){
        String username = etUsername.getText().toString().trim();
        String nama = etNama.getText().toString().trim();
        String email = etEmail.getText().toString().trim();

        if (username.isEmpty()){
            etUsername.setError("Username wajib diisi");
            etUsername.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        APIRequestData api = RetroServer.konekRetrofit().create(APIRequestData.class);
        Call<ResponUpdate> call = api.updateAkun(username,nama,email,base64Image);

        call.enqueue(new Callback<ResponUpdate>() {
            @Override
            public void onResponse(Call<ResponUpdate> call, Response<ResponUpdate> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body()!=null){
                    ResponUpdate body = response.body();
                    if (body.getKode()==1){
                        simpanSharedPref(body.getData());
                        Toast.makeText(edit_profil.this,"Profil berhasil diperbarui",Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(edit_profil.this, body.getPesan(),Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(edit_profil.this,"Gagal terhubung server",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponUpdate> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(edit_profil.this,"Error: "+t.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void simpanSharedPref(ResponUpdate.Data data){
        SharedPreferences sp = getSharedPreferences(KEY_PREF,MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("username", data.getUsername());
        editor.putString("nama", data.getNama());
        editor.putString("email", data.getEmail());
        editor.putString("photo_url", data.getUrlGambarProfil());
        editor.apply();

        if (data.getUrlGambarProfil()!=null && !data.getUrlGambarProfil().isEmpty()){
            loadFotoDenganGlide(data.getUrlGambarProfil());
        }
    }

    public void kembali(View view) {
        finish();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }
}
