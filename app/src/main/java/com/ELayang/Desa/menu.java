package com.ELayang.Desa;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.widget.Toast;

import com.ELayang.Desa.Asset.Notifikasi.NotificationService;
import com.ELayang.Desa.Login.login;
import com.ELayang.Desa.Menu.Notifikasi;
import com.ELayang.Desa.Menu.akun;
import com.ELayang.Desa.Menu.dashboard;
import com.ELayang.Desa.Menu.permintaan_surat;
import com.ELayang.Desa.Menu.riwayat_surat;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

public class menu extends AppCompatActivity {

    private FirebaseAuth mAuth;
    BottomNavigationView bottomNavigationView;
    FloatingActionButton fab;
    private String KEY_NAME = "NAMA";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        // =============================
        // BOTTOM NAVIGATION
        // =============================
        bottomNavigationView = findViewById(R.id.bottomNavView);
        bottomNavigationView.setElevation(0);

        bottomNavigationView.getMenu().findItem(R.id.permintaan).setEnabled(false);

        bottomNavigationView.setOnItemSelectedListener(item -> {

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
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frame, selectedFragment)
                        .commit();
            }

            return true;
        });

        // =============================
        // FRAGMENT AWAL
        // =============================
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame, new dashboard())
                .commit();

        // =============================
        // FLOATING ACTION BUTTON
        // =============================
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(v -> {
            Intent buka = new Intent(this, permintaan_surat.class);
            startActivity(buka);
        });

        // =============================
        // JADWALKAN SERVICE NOTIFIKASI
        // =============================
        schedulePopupNotif();
    }

    // ==================================================
    // 🔔 JADWALKAN JOB NOTIFIKASI SETIAP 15 MENIT
    // ==================================================
    private void schedulePopupNotif() {
        SharedPreferences sp = getSharedPreferences("prefLogin", MODE_PRIVATE);
        String username = sp.getString("username", "");

        PersistableBundle bundle = new PersistableBundle();
        bundle.putString("username", username);

        ComponentName componentName = new ComponentName(this, NotificationService.class);

        JobInfo jobInfo = new JobInfo.Builder(333, componentName)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setPeriodic(15 * 60 * 1000) // 15 menit
                .setExtras(bundle)
                .setPersisted(true)
                .build();

        JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        scheduler.schedule(jobInfo);
    }

    // ==================================================
    // HANDLE BACK → LOGOUT
    // ==================================================
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            signOut();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void signOut() {
        GoogleSignIn.getClient(this, GoogleSignInOptions.DEFAULT_SIGN_IN).signOut()
                .addOnCompleteListener(this, task -> finish());

        finish();
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(this, "kembali", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(menu.this, login.class));
    }
}
