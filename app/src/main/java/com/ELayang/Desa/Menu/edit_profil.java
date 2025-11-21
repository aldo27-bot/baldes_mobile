package com.ELayang.Desa.Menu;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.ELayang.Desa.API.APIRequestData;
import com.ELayang.Desa.API.RetroServer;
import com.ELayang.Desa.R;
import com.ELayang.Desa.DataModel.Akun.ResponUpdate;
import com.ELayang.Desa.utils.RealPathUtil;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.File;

public class edit_profil extends AppCompatActivity {

    EditText eEmail, eUsername, eNama;
    ImageView pp;
    ImageButton btnEditImage;
    Button simpanBtn;
    Uri imageUri = null;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profil);

        // Bind UI
        eEmail = findViewById(R.id.e_email);
        eUsername = findViewById(R.id.e_username);
        eNama = findViewById(R.id.e_nama);

        pp = findViewById(R.id.pp);
        btnEditImage = findViewById(R.id.btn_edit_image);
        simpanBtn = findViewById(R.id.simpan);

        // Load dari SharedPreferences
        sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        eEmail.setText(sharedPreferences.getString("email", ""));
        eUsername.setText(sharedPreferences.getString("username", ""));
        eNama.setText(sharedPreferences.getString("nama", ""));

        String imageUrl = sharedPreferences.getString("profile_image", "");
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(this).load(imageUrl).into(pp);
        }

        btnEditImage.setOnClickListener(v -> pilihFoto());
        simpanBtn.setOnClickListener(v -> updateData());
    }

    private void pilihFoto() {
        Intent intentGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intentGallery, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            pp.setImageURI(imageUri);
        }
    }

    private void updateData() {
        ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Mengupdate...");
        pd.setCancelable(false);
        pd.show();

        APIRequestData api = RetroServer.konekRetrofit().create(APIRequestData.class);

        // Ambil nilai sekarang dari EditText / SharedPreferences
        String username = eUsername.getText().toString().trim();
        String email = eEmail.getText().toString().trim();
        String nama = eNama.getText().toString().trim();
        // ambil password lama dari sharedPreferences (jika backend butuh password)
        String password = sharedPreferences.getString("password", "");

        // buat RequestBody untuk field teks
        RequestBody usernameBody = RequestBody.create(MediaType.parse("text/plain"), username);
        RequestBody emailBody = RequestBody.create(MediaType.parse("text/plain"), email);
        RequestBody namaBody = RequestBody.create(MediaType.parse("text/plain"), nama);
        RequestBody passwordBody = RequestBody.create(MediaType.parse("text/plain"), password);

        MultipartBody.Part imagePart = null;

        if (imageUri != null) {
            try {
                String realPath = RealPathUtil.getRealPath(this, imageUri);
                if (realPath != null && !realPath.isEmpty()) {
                    File file = new File(realPath);
                    RequestBody fileBody = RequestBody.create(MediaType.parse("image/*"), file);
                    imagePart = MultipartBody.Part.createFormData("profile_image", file.getName(), fileBody);
                } else {
                    // fallback: coba gunakan uri last path segment sebagai file name (tidak ideal, tapi aman)
                    File file = new File(getCacheDir(), "upload_temp.jpg");
                    imagePart = MultipartBody.Part.createFormData("profile_image", file.getName(),
                            RequestBody.create(MediaType.parse("image/*"), file));
                }
            } catch (Exception ex) {
                Log.e("RealPathErr", "RealPathUtil error: " + ex.getMessage());
                // biarkan imagePart null sehingga akan menggunakan branch tanpa gambar
                imagePart = null;
            }
        }

        // Jika ada gambar -> panggil multipart updateAkunWithImage (interface: username,email,password,nama,profile_image)
        if (imagePart != null) {
            Call<ResponUpdate> call = api.updateAkunWithImage(usernameBody, emailBody, passwordBody, namaBody, imagePart);

            call.enqueue(new Callback<ResponUpdate>() {
                @Override
                public void onResponse(Call<ResponUpdate> call, Response<ResponUpdate> response) {
                    pd.dismiss();
                    if (response.isSuccessful() && response.body() != null && response.body().getKode() == 1) {

                        // simpan ke SharedPreferences: gunakan url lengkap jika tersedia
                        if (response.body().getData() != null) {
                            String urlFull = response.body().getData().getUrl_gambar_profil();
                            if (urlFull == null || urlFull.isEmpty()) {
                                urlFull = response.body().getData().getProfile_image();
                            }
                            editor.putString("email", response.body().getData().getEmail());
                            editor.putString("nama", response.body().getData().getNama());
                            if (urlFull != null) editor.putString("profile_image", urlFull);
                            editor.apply();
                        }

                        Toast.makeText(edit_profil.this, "Berhasil Update", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(edit_profil.this, "Gagal Update (response)", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponUpdate> call, Throwable t) {
                    pd.dismiss();
                    Toast.makeText(edit_profil.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("Retrofit", t.toString());
                }
            });

        } else {
            // tanpa gambar -> gunakan FormUrlEncoded update_akun(username,email,password,nama)
            Call<ResponUpdate> call = api.update_akun(username, email, password, nama);

            call.enqueue(new Callback<ResponUpdate>() {
                @Override
                public void onResponse(Call<ResponUpdate> call, Response<ResponUpdate> response) {
                    pd.dismiss();
                    if (response.isSuccessful() && response.body() != null && response.body().getKode() == 1) {

                        if (response.body().getData() != null) {
                            editor.putString("email", response.body().getData().getEmail());
                            editor.putString("nama", response.body().getData().getNama());
                            // jika API kirim url_gambar_profil meskipun tanpa upload, simpan juga
                            String urlFull = response.body().getData().getUrl_gambar_profil();
                            if (urlFull != null && !urlFull.isEmpty()) {
                                editor.putString("profile_image", urlFull);
                            }
                            editor.apply();
                        }

                        Toast.makeText(edit_profil.this, "Berhasil Update", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(edit_profil.this, "Gagal Update (response)", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponUpdate> call, Throwable t) {
                    pd.dismiss();
                    Toast.makeText(edit_profil.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("Retrofit", t.toString());
                }
            });
        }
    }
}
