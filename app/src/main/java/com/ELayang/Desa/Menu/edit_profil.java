package com.ELayang.Desa.Menu;

import static com.ELayang.Desa.R.id.btn_edit_image;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.ELayang.Desa.API.APIRequestData;
import com.ELayang.Desa.API.RetroServer;
import com.ELayang.Desa.DataModel.Akun.ResponUpdate;
import com.ELayang.Desa.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class edit_profil extends AppCompatActivity {

    EditText nama, email, usernamee;
    Button simpan, gantipw;
    ImageView ikon;
    ProgressBar progressBar;

    private static final int CAMERA_REQUEST_CODE = 1;
    private static final int GALLERY_REQUEST_CODE = 2;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;

    private String KEY_PREF = "prefLogin";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profil);

        nama = findViewById(R.id.e_nama);
        email = findViewById(R.id.e_email);
        usernamee = findViewById(R.id.e_username);
        ikon = findViewById(R.id.pp);
        simpan = findViewById(R.id.simpan);
        gantipw = findViewById(R.id.gantipw);
        progressBar = findViewById(R.id.progressBar);
        ImageButton btnEditImage = findViewById(btn_edit_image);

        progressBar.setVisibility(View.GONE);

        // Load SharedPrefs
        SharedPreferences sp = getSharedPreferences(KEY_PREF, MODE_PRIVATE);
        String username = sp.getString("username", "");
        String password = sp.getString("password", "");

        nama.setText(sp.getString("nama", ""));
        email.setText(sp.getString("email", ""));
        usernamee.setText(username);

        loadProfileImage(sp.getString("profile_image", ""));

        btnEditImage.setOnClickListener(v -> new AlertDialog.Builder(edit_profil.this)
                .setTitle("Pilih Aksi")
                .setItems(new String[]{"Ambil Foto", "Pilih dari Galeri"}, (dialog, which) -> {
                    if (which == 0) checkCameraPermission();
                    else openGallery();
                })
                .show());

        simpan.setOnClickListener(v -> {
            if (nama.getText().toString().isEmpty()) {
                nama.setError("Nama Harus Diisi");
                return;
            }
            if (email.getText().toString().isEmpty()) {
                email.setError("Email Harus Diisi");
                return;
            }
            if (usernamee.getText().toString().isEmpty()) {
                usernamee.setError("Username Harus Diisi");
                return;
            }

            new AlertDialog.Builder(this)
                    .setMessage("Apakah kamu yakin ingin melanjutkan?")
                    .setPositiveButton("Ya", (dialog, which) -> updateProfil(username, password))
                    .setNegativeButton("Tidak", null)
                    .show();
        });

        gantipw.setOnClickListener(v -> startActivity(new Intent(edit_profil.this, ganti_password.class)));
    }


    // ============================ FOTO PROFIL ========================================

    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
        } else {
            openCamera();
        }
    }

    private void openCamera() {
        startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE), CAMERA_REQUEST_CODE);
    }

    private void openGallery() {
        startActivityForResult(new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI),
                GALLERY_REQUEST_CODE);
    }

    private Bitmap getCircularBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        Paint paint = new Paint();
        paint.setAntiAlias(true);

        canvas.drawCircle(bitmap.getWidth() / 2f, bitmap.getHeight() / 2f,
                Math.min(bitmap.getWidth() / 2f, bitmap.getHeight() / 2f), paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, 0, 0, paint);

        return output;
    }

    private File saveImageToInternalStorage(Bitmap bitmap) throws IOException {
        File file = new File(getCacheDir(), "profile_image.jpg");
        FileOutputStream fos = new FileOutputStream(file);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        fos.close();
        return file;
    }

    private void loadProfileImage(String path) {
        if (path == null || path.isEmpty()) {
            ikon.setImageResource(R.drawable.akun_profil);
            return;
        }

        File file = new File(path);
        if (file.exists()) {
            Glide.with(this)
                    .load(file)
                    .circleCrop()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into(ikon);
        } else {
            ikon.setImageResource(R.drawable.akun_profil);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            Bitmap bitmap = null;

            if (requestCode == CAMERA_REQUEST_CODE) {
                bitmap = (Bitmap) data.getExtras().get("data");
            } else if (requestCode == GALLERY_REQUEST_CODE) {
                try {
                    Uri uri = data.getData();
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (bitmap != null) {
                Bitmap circularBitmap = getCircularBitmap(bitmap);

                try {
                    File saved = saveImageToInternalStorage(circularBitmap);
                    SharedPreferences sp = getSharedPreferences(KEY_PREF, MODE_PRIVATE);
                    sp.edit().putString("profile_image", saved.getAbsolutePath()).apply();
                    loadProfileImage(saved.getAbsolutePath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    // ============================= UPDATE PROFIL ======================================

    private void updateProfil(String username, String password) {
        SharedPreferences sp = getSharedPreferences(KEY_PREF, MODE_PRIVATE);
        String path = sp.getString("profile_image", "");

        RequestBody usernameBody = RequestBody.create(MediaType.parse("text/plain"), username);
        RequestBody emailBody = RequestBody.create(MediaType.parse("text/plain"), email.getText().toString());
        RequestBody passwordBody = RequestBody.create(MediaType.parse("text/plain"), password);
        RequestBody namaBody = RequestBody.create(MediaType.parse("text/plain"), nama.getText().toString());

        APIRequestData api = RetroServer.konekRetrofit().create(APIRequestData.class);

        if (!path.isEmpty()) {

            File img = new File(path);
            RequestBody imgBody = RequestBody.create(MediaType.parse("image/jpeg"), img);
            MultipartBody.Part imgPart =
                    MultipartBody.Part.createFormData("profile_image", img.getName(), imgBody);

            api.updateAkunWithImage(usernameBody, emailBody, passwordBody, namaBody, imgPart)
                    .enqueue(new Callback<ResponUpdate>() {
                        @Override
                        public void onResponse(Call<ResponUpdate> call, Response<ResponUpdate> response) {

                            if (response.body() != null && response.body().getKode() == 1) {

                                sp.edit()
                                        .putString("nama", nama.getText().toString())
                                        .putString("email", email.getText().toString())
                                        .apply();

                                Toast.makeText(edit_profil.this, "Akun Berhasil Diupdate", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(edit_profil.this, "Gagal update akun", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponUpdate> call, Throwable t) {
                            Toast.makeText(edit_profil.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

        } else {
            api.updateAkunWithoutImage(usernameBody, emailBody, passwordBody, namaBody)
                    .enqueue(new Callback<ResponUpdate>() {
                        @Override
                        public void onResponse(Call<ResponUpdate> call, Response<ResponUpdate> response) {

                            if (response.body() != null && response.body().getKode() == 1) {

                                sp.edit()
                                        .putString("nama", nama.getText().toString())
                                        .putString("email", email.getText().toString())
                                        .apply();

                                Toast.makeText(edit_profil.this, "Akun Berhasil Diupdate", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(edit_profil.this, "Gagal update akun", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponUpdate> call, Throwable t) {
                            Toast.makeText(edit_profil.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }


    // ============================= BACK BUTTON ========================================

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Toast.makeText(this, "Gunakan tombol kembali di atas", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void kembali(View view) {
        finish();
    }
}
