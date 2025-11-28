package com.ELayang.Desa;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.KeyEvent;
import android.widget.Toast;

import com.ELayang.Desa.Asset.Notifikasi.NotificationService;
import com.ELayang.Desa.Login.login;
import com.ELayang.Desa.Menu.permintaan_surat;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

public class menu extends AppCompatActivity {

    private FirebaseAuth mAuth;

    BottomNavigationView bottomNavigationView;
    FloatingActionButton fab;
    ViewPager2 viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        // =============================
        // INIT
        // =============================
        bottomNavigationView = findViewById(R.id.bottomNavView);
        fab = findViewById(R.id.fab);
        viewPager = findViewById(R.id.viewPager);

        bottomNavigationView.setElevation(0);
        bottomNavigationView.getMenu().findItem(R.id.permintaan).setEnabled(false);

        // =============================
        // SETUP VIEWPAGER2
        // =============================
        ViewPagerAdapter adapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(adapter);

        // swipe → navbar update (menggunakan ID agar selalu sinkron)
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        bottomNavigationView.getMenu().findItem(R.id.dashboard).setChecked(true);
                        break;
                    case 1:
                        bottomNavigationView.getMenu().findItem(R.id.notifikasi).setChecked(true);
                        break;
                    case 2:
                        bottomNavigationView.getMenu().findItem(R.id.riwayat).setChecked(true);
                        break;
                    case 3:
                        bottomNavigationView.getMenu().findItem(R.id.profil).setChecked(true);
                        break;
                }
            }
        });

        // navbar ditekan → viewpager pindah
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.dashboard) {
                viewPager.setCurrentItem(0, true);
            } else if (id == R.id.notifikasi) {
                viewPager.setCurrentItem(1, true);
            } else if (id == R.id.riwayat) {
                viewPager.setCurrentItem(2, true);
            } else if (id == R.id.profil) {
                viewPager.setCurrentItem(3, true);
            }
            return true;
        });

        // FAB → pindah ke permintaan surat
        fab.setOnClickListener(v -> startActivity(new Intent(this, permintaan_surat.class)));

        // Schedule notifikasi popup
        schedulePopupNotif();
    }

    private void schedulePopupNotif() {
        SharedPreferences sp = getSharedPreferences("prefLogin", MODE_PRIVATE);
        String username = sp.getString("username", "");

        PersistableBundle bundle = new PersistableBundle();
        bundle.putString("username", username);

        ComponentName componentName = new ComponentName(this, NotificationService.class);

        JobInfo jobInfo = new JobInfo.Builder(333, componentName)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setPeriodic(15 * 60 * 1000)
                .setExtras(bundle)
                .setPersisted(true)
                .build();

        JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        scheduler.schedule(jobInfo);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            signOut();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void signOut() {
        GoogleSignIn.getClient(this, GoogleSignInOptions.DEFAULT_SIGN_IN)
                .signOut()
                .addOnCompleteListener(this, task -> finish());
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(this, "kembali", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(menu.this, login.class));
    }
}
