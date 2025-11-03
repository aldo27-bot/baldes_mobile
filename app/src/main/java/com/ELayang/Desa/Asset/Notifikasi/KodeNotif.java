package com.ELayang.Desa.Asset.Notifikasi;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class KodeNotif extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Channel utama (default)
            NotificationChannel channel = new NotificationChannel(
                    "prayoga",
                    "E-lades",
                    NotificationManager.IMPORTANCE_HIGH
            );

            // Channel tambahan untuk notifikasi aspirasi
            NotificationChannel channelAspirasi = new NotificationChannel(
                    "AspirasiChannelID",
                    "Notifikasi Aspirasi",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channelAspirasi.setDescription("Channel untuk notifikasi aspirasi warga");

            // Daftarkan kedua channel ke sistem
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
                manager.createNotificationChannel(channelAspirasi);
            }
        }
    }
}
