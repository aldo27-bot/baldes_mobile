package com.ELayang.Desa.Asset.Notifikasi;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.ELayang.Desa.R;

public class NotificationHelper extends ContextWrapper {

    public static final String CHANNEL_ID = "popup01";
    public static final String CHANNEL_NAME = "Notifikasi Popup";

    private NotificationManager manager;

    public NotificationHelper(Context base) {
        super(base);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setLockscreenVisibility(NotificationCompat.VISIBILITY_PUBLIC);

            getManager().createNotificationChannel(channel);
        }
    }

    public NotificationManager getManager() {
        if (manager == null) {
            manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return manager;
    }

    /**
     * Notifikasi lebih jelas:
     * Surat: SKTM
     * Status: Selesai
     * (Alasan: ...)
     */
    public NotificationCompat.Builder buildNotification(
            String jenis, String judul, String status, String alasan
    ) {
        String title = "";
        String message = "";

        // ------ Judul notifikasi ------
        if (jenis != null) {
            if (jenis.equalsIgnoreCase("surat")) {
                title = "Surat: " + judul;
            } else if (jenis.equalsIgnoreCase("aspirasi")) {
                title = "Aspirasi: " + judul;
            } else {
                title = judul; // fallback
            }
        } else {
            title = judul;
        }

        // ------ Isi notifikasi ------
        message = "Status: " + status;

        if (alasan != null && !alasan.isEmpty()) {
            message += " (Alasan: " + alasan + ")";
        }

        return new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setSmallIcon(R.drawable.notification_icon)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);
    }
}
