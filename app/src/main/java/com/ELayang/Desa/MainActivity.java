package com.ELayang.Desa;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.ELayang.Desa.Login.login;
import com.ELayang.Desa.menu;
import com.ELayang.Desa.Asset.Notifikasi.NotificationServiceAspirasi;

import java.util.Map;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPreferences = getSharedPreferences("prefLogin", Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("username","");
        String nama = sharedPreferences.getString("nama","");
        String password = sharedPreferences.getString("password","");
        Map<String, ?> allEntries = sharedPreferences.getAll();

        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            Log.d("SharedPreferencesData", entry.getKey() + ": " + entry.getValue().toString());
        }

        if(username.equals("")){
            // User belum login, buka activity login
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent buka = new Intent(MainActivity.this, login.class);
                    startActivity(buka);
                    finish();
                }
            }, 500);
        } else {
            // User sudah login, jalankan service notifikasi dulu
            Intent notifIntent = new Intent(MainActivity.this, NotificationServiceAspirasi.class);
            NotificationServiceAspirasi.enqueueWork(MainActivity.this, notifIntent);

            // Lanjut ke menu utama
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(MainActivity.this, menu.class);
                    startActivity(intent);
                    finish();
                }
            }, 500);
        }
    }
}
