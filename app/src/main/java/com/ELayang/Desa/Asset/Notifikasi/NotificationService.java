package com.ELayang.Desa.Asset.Notifikasi;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;

import androidx.core.app.JobIntentService;
import androidx.core.app.NotificationCompat;

import com.ELayang.Desa.API.APIRequestData;
import com.ELayang.Desa.API.RetroServer;
import com.ELayang.Desa.DataModel.Notifikasi.ResponNotifikasi;
import com.ELayang.Desa.MainActivity;
import com.ELayang.Desa.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationService extends JobIntentService {

    private static final String CHANNEL_ID = "SuratChannel";
    private static final int DELAY_INTERVAL = 60000; // 60 detik
    private final Handler handler = new Handler(Looper.getMainLooper());
    private String lastStatus = "";
    private String lastAlasan = "";

    public static void enqueueWork(Context context, Intent work) {
        enqueueWork(context, NotificationService.class, 333, work);
    }

    @Override
    protected void onHandleWork(Intent intent) {
        monitorSurat();
        createNotificationChannel();
        requestNotificationPermission();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        monitorSurat();
        return START_STICKY;
    }

    private void monitorSurat() {
        checkForUpdatesSurat();
        scheduleNextRun();
    }

    private void scheduleNextRun() {
        handler.postDelayed(this::monitorSurat, DELAY_INTERVAL);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "SuratChannelName";
            String description = "Notifikasi Surat";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            manager.createNotificationChannel(channel);
        }
    }

    private boolean isNotificationPermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!isNotificationPermissionGranted()) {
                if (!hasNotifiedUserForPermission()) {
                    Intent intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                            .putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    setUserNotifiedForPermission(true);
                }
            }
        }
    }

    private boolean hasNotifiedUserForPermission() {
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        return prefs.getBoolean("has_notified_for_permission_surat", false);
    }

    private void setUserNotifiedForPermission(boolean notified) {
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("has_notified_for_permission_surat", notified);
        editor.apply();
    }

    // 🔔 Cek update surat
    private void checkForUpdatesSurat() {
        SharedPreferences sharedPreferences = getSharedPreferences("prefLogin", Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "");

        APIRequestData api = RetroServer.konekRetrofit().create(APIRequestData.class);
        Call<ResponNotifikasi> call = api.notifikasi_popup(username);

        call.enqueue(new Callback<ResponNotifikasi>() {
            @Override
            public void onResponse(Call<ResponNotifikasi> call, Response<ResponNotifikasi> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getKode() == 1) {
                    String status = response.body().getStatus();
                    String alasan = response.body().getAlasan();

                    if (status != null && !status.equals(lastStatus)) {
                        lastStatus = status;
                        lastAlasan = alasan != null ? alasan : "";

                        if ("Tolak".equals(status)) {
                            showNotification("Surat Ditolak", "Alasan: " + lastAlasan);
                        } else if ("Selesai".equals(status)) {
                            showNotification("Surat Selesai Diproses", lastAlasan);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponNotifikasi> call, Throwable t) {
                Log.e("NotifSurat", "Gagal: " + t.getMessage());
            }
        });
    }

    private void showNotification(String title, String message) {
        if (!isNotificationPermissionGranted()) {
            Log.w("NotificationSurat", "Izin notifikasi belum diberikan.");
            return;
        }

        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.logo)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify((int) System.currentTimeMillis(), builder.build());
    }
}
