package com.ELayang.Desa;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.ELayang.Desa.Login.login;
import com.ELayang.Desa.menu;
import com.ELayang.Desa.Asset.Notifikasi.NotificationServiceAspirasi;

import java.util.Map;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 🔹 Ambil ImageView logo dari layout
        ImageView logo = findViewById(R.id.logoImage);

        // 🔹 Panggil animasi (fade + zoom)
        Animation animLogo = AnimationUtils.loadAnimation(this, R.anim.logo_animation);
        logo.startAnimation(animLogo);

        // 🔹 Cek shared preferences
        SharedPreferences sharedPreferences = getSharedPreferences("prefLogin", Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("username","");
        Map<String, ?> allEntries = sharedPreferences.getAll();

        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            Log.d("SharedPreferencesData", entry.getKey() + ": " + entry.getValue().toString());
        }

        // 🔹 Delay untuk memberi waktu animasi tampil
        int delay = 4000;

        if(username.equals("")){
            // User belum login → buka login setelah animasi
            new Handler().postDelayed(() -> {
                Intent buka = new Intent(MainActivity.this, login.class);
                startActivity(buka);
                finish();
            }, delay);

        } else {
            // User sudah login → jalankan notifikasi dulu
            Intent notifIntent = new Intent(MainActivity.this, NotificationServiceAspirasi.class);
            NotificationServiceAspirasi.enqueueWork(MainActivity.this, notifIntent);

            // Lanjut ke menu
            new Handler().postDelayed(() -> {
                Intent intent = new Intent(MainActivity.this, menu.class);
                startActivity(intent);
                finish();
            }, delay);
        }
    }
}
