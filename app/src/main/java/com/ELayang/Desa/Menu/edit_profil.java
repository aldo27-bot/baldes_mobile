package com.ELayang.Desa.Menu;

import static com.ELayang.Desa.API.RetroServer.API_FotoProfil;
import static com.ELayang.Desa.API.RetroServer.API_IMAGE;
import static com.ELayang.Desa.R.id.btn_edit_image;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.loader.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import com.ELayang.Desa.API.APIRequestData;
import com.ELayang.Desa.API.RetroServer;
import com.ELayang.Desa.DataModel.Akun.ModelLogin;
import com.ELayang.Desa.DataModel.Akun.ResponFotoProfil;
import com.ELayang.Desa.DataModel.Akun.ResponLogin;
import com.ELayang.Desa.DataModel.Akun.ResponUpdate;
import com.ELayang.Desa.Login.login;
import com.ELayang.Desa.R;
import com.ELayang.Desa.menu;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.squareup.picasso.Picasso;

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
    private static final int CAMERA_REQUEST_CODE = 1;
    private static final int GALLERY_REQUEST_CODE = 2;
    private ImageView ikon;
    private String KEY_NAME = "NAMA";

    private File saveImageToInternalStorage(Bitmap bitmap) throws IOException {
        String fileName = "profile_image_" + System.currentTimeMillis() + ".jpg"; // Nama file unik
        File file = new File(getCacheDir(), fileName);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    private void saveImagePathToSharedPreferences(String imagePath) {
        SharedPreferences sharedPreferences = getSharedPreferences("prefLogin", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("profile_image", imagePath); // Simpan path absolut
        editor.apply();
    }

    private Bitmap loadImageFromSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("prefLogin", MODE_PRIVATE);
        String imagePath = sharedPreferences.getString("profile_image", null);  // Ambil path gambar
        if (imagePath != null) {
            File imgFile = new  File(imagePath);
            if(imgFile.exists()) {
                return BitmapFactory.decodeFile(imgFile.getAbsolutePath());  // Memuat gambar dari file
            }
        }
        return null;  // Kembalikan null jika tidak ada gambar yang disimpan
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profil);

        nama = findViewById(R.id.e_nama);
        email = findViewById(R.id.e_email);
        usernamee= findViewById(R.id.e_username);
        ikon = findViewById(R.id.pp);

        SharedPreferences sharedPreferences = getSharedPreferences("prefLogin", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.apply();
        RetroServer image = new RetroServer();
        String username = sharedPreferences.getString("username", "");
        String password = sharedPreferences.getString("password", "");

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

                        Glide.with(edit_profil.this)
                                .load(imageUrl)  // URL gambar dari server
                                .placeholder(R.drawable.akun_profil)
                                .error(R.drawable.akun_profil)
                                .circleCrop()
                                .diskCacheStrategy(DiskCacheStrategy.NONE) // Abaikan cache
                                .skipMemoryCache(true)  // Tidak menyimpan cache di memori
                                .listener(new RequestListener<Drawable>() {
                                    @Override
                                    public boolean onLoadFailed(GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                        // Ketika gambar gagal dimuat
                                        Log.e("Glide", "Gagal memuat foto profil: " + e.getMessage());
                                        Toast.makeText(edit_profil.this, "Tidak ada foto profil", Toast.LENGTH_SHORT).show();
                                        return false; // Return false untuk tetap menampilkan placeholder error
                                    }

                                    @Override
                                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource,
                                                                   boolean isFirstResource) {
                                        // Ketika gambar berhasil dimuat
                                        Log.d("Glide", "Foto profil berhasil dimuat.");
                                        return false; // Return false untuk tetap melanjutkan proses pemuatan gambar ke ImageView
                                    }
                                })
                                .into(ikon);
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

        simpan = findViewById(R.id.simpan);
        simpan.setOnClickListener(v -> {
            if (nama.getText().toString().isEmpty()) {
                nama.setError("Nama Harus Diisi");
                nama.requestFocus();
            } else if (email.getText().toString().isEmpty()) {
                email.setError("Email Harus Diisi");
                email.requestFocus();
            }else if (usernamee.getText().toString().isEmpty()) {
                usernamee.setError("Username Harus Diisi");
                usernamee.requestFocus();
            }  else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Apakah kamu yakin ingin melanjutkan?")
                        .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d("EditProfil", "diklik");
                                try {
                                    // Memuat gambar dari SharedPreferences
                                    Bitmap savedBitmap = loadImageFromSharedPreferences();

                                    // Kirim data lainnya
                                    RequestBody usernameBody = RequestBody.create(MediaType.parse("text/plain"), username);
                                    RequestBody emailBody = RequestBody.create(MediaType.parse("text/plain"), email.getText().toString());
                                    RequestBody passwordBody = RequestBody.create(MediaType.parse("text/plain"), password);
                                    RequestBody namaBody = RequestBody.create(MediaType.parse("text/plain"), nama.getText().toString());

                                    if (savedBitmap != null) {
                                        Log.d("EditProfil", "Bitmap" + savedBitmap);

                                        // Ubah Bitmap menjadi File
                                        File imageFile = convertBitmapToFile(savedBitmap);

                                        // Membuat request body untuk file gambar
                                        RequestBody requestBody = RequestBody.create(MediaType.parse("image/jpeg"), imageFile);
                                        MultipartBody.Part imagePart = MultipartBody.Part.createFormData("profile_image", imageFile.getName(), requestBody);

                                        // Panggil API untuk update data
                                        APIRequestData apiRequestData = RetroServer.konekRetrofit().create(APIRequestData.class);
                                        Call<ResponUpdate> call = apiRequestData.updateAkunWithImage(usernameBody, emailBody, passwordBody, namaBody, imagePart);

                                        call.enqueue(new Callback<ResponUpdate>() {
                                            @Override
                                            public void onResponse(Call<ResponUpdate> call, Response<ResponUpdate> response) {
                                                Log.d("EditProfil", "API respon: " + response.body().isKode());
                                                if (response.body().isKode()) {
                                                    SharedPreferences sharedPreferences = getSharedPreferences("prefLogin", MODE_PRIVATE);
                                                    SharedPreferences.Editor editor = sharedPreferences.edit();

                                                    editor.putString("username",username);
                                                    editor.putString("email", email.getText().toString());
                                                    editor.putString("nama", nama.getText().toString());
                                                    editor.putString("password", password);
                                                    if (imagePart != null) {
                                                        editor.putString("profile_image", imagePart.body().toString());
                                                    }
                                                    editor.apply();
                                                    Toast.makeText(edit_profil.this, "Akun Berhasil Diupdate", Toast.LENGTH_SHORT).show();
                                                    finish();
                                                } else {
                                                    Toast.makeText(edit_profil.this, "Akun Gagal Diupdate", Toast.LENGTH_SHORT).show();
                                                }
                                            }

                                            @Override
                                            public void onFailure(Call<ResponUpdate> call, Throwable t) {
                                                Log.e("EditProfil", "API request failed", t);
                                                Toast.makeText(edit_profil.this, "Terjadi kesalahan: " + t.getMessage(), Toast.LENGTH_LONG).show();
                                            }
                                        });
                                    } else {
                                        if (savedBitmap == null) {
                                            Log.e("EditProfil", "Saved bitmap is null");
                                        }

                                        // Panggil API untuk update data
                                        APIRequestData apiRequestData = RetroServer.konekRetrofit().create(APIRequestData.class);
                                        Call<ResponUpdate> call = apiRequestData.updateAkunWithoutImage(usernameBody, emailBody, passwordBody, namaBody);
                                        call.enqueue(new Callback<ResponUpdate>() {
                                            @Override
                                            public void onResponse(Call<ResponUpdate> call, Response<ResponUpdate> response) {
                                                Log.d("EditProfil", "API respon: " + response.body().isKode());
                                                if (response.body().isKode()) {
                                                    SharedPreferences sharedPreferences = getSharedPreferences("prefLogin", MODE_PRIVATE);
                                                    SharedPreferences.Editor editor = sharedPreferences.edit();

                                                    editor.putString("username",username);
                                                    editor.putString("email", email.getText().toString());
                                                    editor.putString("nama", nama.getText().toString());
                                                    editor.putString("password", password);
                                                    editor.apply();
                                                    Toast.makeText(edit_profil.this, "Akun Berhasil Diupdate", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(edit_profil.this, "Akun Gagal Diupdate", Toast.LENGTH_SHORT).show();
                                                }
                                            }

                                            @Override
                                            public void onFailure(Call<ResponUpdate> call, Throwable t) {
                                                Log.e("EditProfil", "API request failed", t);
                                                Toast.makeText(edit_profil.this, "Terjadi kesalahan: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Log.e("EditProfil", "Error proses update" + e);
                                }
                            }
                        })
                        .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .show();

            }
        });

        ImageButton btnEditImage = findViewById(btn_edit_image);
        btnEditImage.setOnClickListener(v -> {
            // Membuka dialog untuk memilih antara kamera atau galeri
            new AlertDialog.Builder(edit_profil.this)
                    .setTitle("Pilih Aksi")
                    .setItems(new String[] {"Ambil Foto", "Pilih dari Galeri"}, (dialog, which) -> {
                        if (which == 0) {
                            checkCameraPermission();
                        } else {
                            openGallery();
                        }
                    })
                    .show();
        });
        gantipw = findViewById(R.id.gantipw);
        gantipw.setOnClickListener(v -> {
            Intent intent = new Intent(edit_profil.this, ganti_password.class);
            startActivity(intent);
        });
    }

    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE);
    }

    private Bitmap getCircularBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);

        // Membuat lingkaran
        int x = bitmap.getWidth() / 2;
        int y = bitmap.getHeight() / 2;
        int radius = Math.min(x, y);

        canvas.drawCircle(x, y, radius, paint);

        // Memotong gambar
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, 0, 0, paint);

        return output;
    }

    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;

    // Fungsi untuk memeriksa dan meminta izin kamera
    private void checkCameraPermission() {
        // Periksa apakah izin CAMERA sudah diberikan
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // Jika belum diberikan, minta izin
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
        } else {
            // Jika izin sudah ada, langsung buka kamera
            openCamera();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            Uri imageUri = null;
            try {
                if (requestCode == GALLERY_REQUEST_CODE) {
                    imageUri = data.getData();  // Ambil URI dari galeri
                    if (imageUri != null) {
                        try {
                            // Mengambil gambar dari URI menggunakan ContentResolver
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);

                            // Menyimpan gambar ke penyimpanan internal seperti foto kamera
                            File savedFile = saveImageToInternalStorage(bitmap);
                            imageUri = Uri.fromFile(savedFile);

                            // Menampilkan gambar pada ikon setelah diproses
                            Bitmap circularBitmap = getCircularBitmap(bitmap);
                            Glide.with(this)
                                    .load(circularBitmap)
                                    .placeholder(R.drawable.akun_profil)
                                    .error(R.drawable.ic_launcher_foreground)
                                    .circleCrop()
                                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                                    .skipMemoryCache(true)
                                    .into(ikon);

                            // Simpan path ke SharedPreferences
                            saveImagePathToSharedPreferences(imageUri.getPath());

                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(this, "Gagal memuat gambar 2", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else if (requestCode == CAMERA_REQUEST_CODE) {
                    // Dapatkan foto dari kamera
                    Bitmap photo = (Bitmap) data.getExtras().get("data");
                    if (photo != null) {
                        Bitmap circularBitmap = getCircularBitmap(photo);
                        if (circularBitmap != null) {
                            File savedFile = saveImageToInternalStorage(circularBitmap);
                            imageUri = Uri.fromFile(savedFile);
                        } else {
                            Toast.makeText(this, "Gagal memproses gambar", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Foto tidak valid", Toast.LENGTH_SHORT).show();
                    }
                }
                if (imageUri != null) {
                    Glide.with(this)
                            .load(imageUri)
                            .placeholder(R.drawable.akun_profil)
                            .error(R.drawable.ic_launcher_foreground)
                            .circleCrop()
                            .diskCacheStrategy(DiskCacheStrategy.NONE) // Abaikan cache
                            .skipMemoryCache(true)
                            .into(ikon);

                    saveImagePathToSharedPreferences(imageUri.getPath());
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Gagal memuat gambar 3", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Tidak ada gambar yang dipilih", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            // Jika izin CAMERA diberikan
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(this, "Izin Kamera diperlukan untuk mengambil foto", Toast.LENGTH_SHORT).show();
            }
        }
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

    private File convertBitmapToFile(Bitmap bitmap) {
        File file = new File(getCacheDir(), "profile_image.jpg");
        try (FileOutputStream fos = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }
}