package com.ELayang.Desa;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.ELayang.Desa.Asset.Notifikasi.NotificationService;
import com.ELayang.Desa.Login.login;
import com.ELayang.Desa.Menu.Notifikasi;
import com.ELayang.Desa.Menu.akun;
import com.ELayang.Desa.Menu.dashboard;
import com.ELayang.Desa.Menu.permintaan_surat;
import com.ELayang.Desa.Menu.riwayat_surat;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

public class menu extends AppCompatActivity {
    private FirebaseAuth mAuth;
    ImageButton dasboard, notifikasi;
    BottomNavigationView bottomNavigationView;
    FloatingActionButton fab;
    private String KEY_NAME = "NAMA";

//    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        Intent serviceIntent = new Intent(this, NotificationService.class);
        NotificationService.enqueueWork(this, serviceIntent);

        bottomNavigationView = findViewById(R.id.bottomNavView);
        bottomNavigationView.setElevation(0);
        bottomNavigationView.getMenu().findItem(R.id.permintaan).setEnabled(false);

        // 1. Dapatkan referensi FrameLayout
        FrameLayout frameLayout = findViewById(R.id.frame);

        // 2. Tentukan margin bawah yang diinginkan (120dp). Ubah angka ini sesuai kebutuhan Anda!
        final int desiredMarginDp = 2;

        // 3. Konversi nilai DP ke Pixel
        final float scale = getResources().getDisplayMetrics().density;
        int marginInPixels = (int) (desiredMarginDp * scale + 0.5f);

        // 4. Atur Margin Bawah pada FrameLayout
        // Pastikan LayoutParams adalah CoordinatorLayout.LayoutParams karena FrameLayout di dalamnya
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) frameLayout.getLayoutParams();
        params.bottomMargin = marginInPixels;
        frameLayout.setLayoutParams(params);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                int id = item.getItemId();

                if (id == R.id.dashboard) {
                    selectedFragment = new dashboard();
                } else if (id == R.id.notifikasi) {
                    selectedFragment = new Notifikasi();
                } else if (id == R.id.riwayat) {
                    selectedFragment = new riwayat_surat();
                } else if (id == R.id.profil) {
                    selectedFragment = new akun();
                }

                if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.frame, selectedFragment)
                            .commit();
                }

                // ✅ return true agar item menjadi "checked" (aktif)
                return true;
            }
        });

        // Set the initial fragment to display
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame, new dashboard())
                .commit();

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(v ->{
            Intent buka = new Intent(this, permintaan_surat.class);
            startActivity(buka);
        });




        // Mendapatkan username yang dikirimkan dari LoginActivity
//        String username = getIntent().getStringExtra("username");
//        // Membuat fragmen
//        dashboard fragment = new dashboard();
//        fragment.setUsername(username);
//        Toast.makeText(this, username, Toast.LENGTH_SHORT).show();

//        // Membuat bundle untuk mengirim data ke fragmen
//        Bundle bundle = new Bundle();
//        bundle.putString("username", username);
//
//        // Menambahkan bundle ke fragmen
//        fragment.setArguments(bundle);


            // Gunakan username sesuai kebutuhan, misalnya, tampilkan pada TextView
//            TextView hello = findViewById(R.id.hello);
//            hello.setText("Halo, "+ username);

        // Simpan username saat login berhasil
        // Mengambil username dari SharedPreferences




    }



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // Lakukan logout dan intent ke halaman login
            signOut();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void signOut() {
        // ... (kode logout yang lain)

        // Lakukan intent ke halaman login setelah logout
//        Intent intent = new Intent(this, login.class);
//        startActivity(intent);

//        mAuth.signOut();
//
        // Lakukan logout dari Google Sign-In (jika digunakan)
        GoogleSignIn.getClient(this, GoogleSignInOptions.DEFAULT_SIGN_IN).signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // Logout dari Google berhasil (jika ada)
                        finish(); // Keluar dari aktivitas setelah logout
                    }
                });
        finish(); // Optional, untuk menutup menu aktivitas
    }

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//            if(keyCode == KeyEvent.KEYCODE_BACK){
//                signOut();
////                finish();
//                onBackPressed();
//                return true;
//            }
//        return super.onKeyDown(keyCode, event);
//    }
//    private void signOut() {
//        // Lakukan logout dari Firebase
////        mAuth.signOut();
//
//        // Lakukan logout dari Google Sign-In (jika digunakan)
//        GoogleSignIn.getClient(this, GoogleSignInOptions.DEFAULT_SIGN_IN).signOut()
//                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        // Logout dari Google berhasil (jika ada)
//                        finish(); // Keluar dari aktivitas setelah logout
//                    }
//                });
//    }
    public void onBackPressed(){
        super.onBackPressed();
        Toast.makeText(this, "kembali", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(menu.this,login.class));
    }
}