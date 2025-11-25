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
import com.ELayang.Desa.DataModel.Notifikasi.ResponPopup;
import com.ELayang.Desa.MainActivity;
import com.ELayang.Desa.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationService extends JobIntentService {

    private static final String CHANNEL_ID = "SuratAspirasiChannel";
    private static final int DELAY_INTERVAL = 60000; // 60 detik
    private final Handler handler = new Handler(Looper.getMainLooper());

    // --- Shared Preferences Keys ---
    private static final String PREF_NAME = "NotifPrefs";

    private static final String KEY_LAST_STATUS_SURAT = "last_surat_status";
    private static final String KEY_LAST_ALASAN_SURAT = "last_surat_alasan";

    private static final String KEY_LAST_STATUS_ASPIRASI = "last_aspirasi_status";
    // --------------------------------

    public static void enqueueWork(Context context, Intent work) {
        enqueueWork(context, NotificationService.class, 333, work);
    }

    @Override
    protected void onHandleWork(Intent intent) {
        monitorData();
        createNotificationChannel();
        requestNotificationPermission();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        monitorData();
        return START_STICKY;
    }

    private void monitorData() {
        checkForUpdatesSurat();
        checkForUpdatesAspirasi();     // <-- Tambahan aspirasi
        scheduleNextRun();
    }

    private void scheduleNextRun() {
        handler.postDelayed(this::monitorData, DELAY_INTERVAL);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Notifikasi Channel";
            String description = "Notifikasi Surat & Aspirasi";
            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            manager.createNotificationChannel(channel);
        }
    }

    private boolean isNotificationPermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)
                    == PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !isNotificationPermissionGranted()) {
            Intent intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                    .putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    // --- Save & Get Status Local ---
    private void saveLastStatusSurat(String status) {
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        prefs.edit().putString(KEY_LAST_STATUS_SURAT, status).apply();
    }

    private String getLastStatusSurat() {
        return getSharedPreferences(PREF_NAME, MODE_PRIVATE)
                .getString(KEY_LAST_STATUS_SURAT, "");
    }

    private void saveLastStatusAspirasi(String status) {
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        prefs.edit().putString(KEY_LAST_STATUS_ASPIRASI, status).apply();
    }

    private String getLastStatusAspirasi() {
        return getSharedPreferences(PREF_NAME, MODE_PRIVATE)
                .getString(KEY_LAST_STATUS_ASPIRASI, "");
    }

    // --- Surat Notifikasi ---
    private void checkForUpdatesSurat() {
        SharedPreferences sharedPreferences = getSharedPreferences("prefLogin", MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "");

        final String storedStatus = getLastStatusSurat();

        APIRequestData api = RetroServer.konekRetrofit().create(APIRequestData.class);
        Call<ResponPopup> call = api.getPopupNotifikasi(username);

        call.enqueue(new Callback<ResponPopup>() {
            @Override
            public void onResponse(Call<ResponPopup> call, Response<ResponPopup> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getKode() == 1) {

                    String currentStatus = response.body().getStatus();

                    if (!currentStatus.equalsIgnoreCase(storedStatus)) {

                        if ("Tolak".equalsIgnoreCase(currentStatus)) {
                            showNotification("Surat Ditolak", "Pengajuan surat Anda ditolak.");
                        } else if ("Selesai".equalsIgnoreCase(currentStatus)) {
                            showNotification("Surat Selesai Diproses",
                                    "Pengajuan surat Anda telah selesai diproses.");
                        }

                        saveLastStatusSurat(currentStatus);
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponPopup> call, Throwable t) {
                Log.e("NotifSurat", "Error: " + t.getMessage());
            }
        });
    }

    // --- Aspirasi Notifikasi ---
    private void checkForUpdatesAspirasi() {
        SharedPreferences sharedPreferences = getSharedPreferences("prefLogin", MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "");

        final String storedStatus = getLastStatusAspirasi();

        APIRequestData api = RetroServer.konekRetrofit().create(APIRequestData.class);
        Call<ResponPopup> call = api.getPopupNotifikasi(username);

        call.enqueue(new Callback<ResponPopup>() {
            @Override
            public void onResponse(Call<ResponPopup> call, Response<ResponPopup> response) {

                if (response.isSuccessful() && response.body() != null && response.body().getKode() == 1) {

                    String currentStatus = response.body().getStatus();

                    if (!currentStatus.equalsIgnoreCase(storedStatus)) {

                        if ("Tolak".equalsIgnoreCase(currentStatus)) {
                            showNotification("Aspirasi Ditolak", "Aspirasi Anda telah ditolak.");
                        } else if ("Selesai".equalsIgnoreCase(currentStatus)) {
                            showNotification("Aspirasi Selesai", "Aspirasi Anda telah selesai diproses.");
                        }

                        saveLastStatusAspirasi(currentStatus);
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponPopup> call, Throwable t) {
                Log.e("NotifAspirasi", "Error: " + t.getMessage());
            }
        });
    }

    // --- Display Notification ---
    private void showNotification(String title, String message) {

        if (!isNotificationPermissionGranted()) return;

        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setSmallIcon(R.drawable.logo)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent);

        NotificationManager manager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify((int) System.currentTimeMillis(), builder.build());
    }
}
