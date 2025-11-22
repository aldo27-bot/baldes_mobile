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

import java.util.Map;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView logo = findViewById(R.id.logoImage);

        Animation animLogo = AnimationUtils.loadAnimation(this, R.anim.logo_animation);
        logo.startAnimation(animLogo);

        SharedPreferences sharedPreferences = getSharedPreferences("prefLogin", Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("username","");
        Map<String, ?> allEntries = sharedPreferences.getAll();

        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            Log.d("SharedPreferencesData", entry.getKey() + ": " + entry.getValue().toString());
        }

        int delay = 3000;

        if(username.equals("")){
            new Handler().postDelayed(() -> {
                Intent buka = new Intent(MainActivity.this, login.class);
                startActivity(buka);
                finish();
            }, delay);

        } else {
            new Handler().postDelayed(() -> {
                Intent intent = new Intent(MainActivity.this, menu.class);
                startActivity(intent);
                finish();
            }, delay);
        }
    }
}
