//package com.ELayang.Desa.Asset.Notifikasi;
//
//import android.Manifest;
//import android.app.NotificationChannel;
//import android.app.NotificationManager;
//import android.app.PendingIntent;
//import android.content.Context;
//import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.os.Build;
//import android.os.Handler;
//import android.os.Looper;
//import android.provider.Settings;
//import android.util.Log;
//
//import androidx.core.app.JobIntentService;
//import androidx.core.app.NotificationCompat;
//
//import com.ELayang.Desa.MainActivity;
//import com.ELayang.Desa.R;
//import com.ELayang.Desa.aspirasi.SharedPrefManager;
//import com.ELayang.Desa.API.APIRequestData;
//import com.ELayang.Desa.API.RetroServer;
//import com.ELayang.Desa.DataModel.Notifikasi.ModelNotifikasiAspirasi;
//import com.ELayang.Desa.DataModel.Notifikasi.ResponNotifikasiAspirasi;
//import com.google.gson.Gson;
//
//import java.util.List;
//
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
//
//public class NotificationServiceAspirasi extends JobIntentService {
//
//    private static int notificationId = 0;
//    private static final String CHANNEL_ID = "AspirasiChannelID";
//    private static final int DELAY_INTERVAL = 60000; // 1 menit
//    private final Handler handler = new Handler(Looper.getMainLooper());
//    private String lastTanggapan = "";
//
//    public static void enqueueWork(Context context, Intent work) {
//        enqueueWork(context, NotificationServiceAspirasi.class, 112, work);
//    }
//
//    @Override
//    protected void onHandleWork(Intent intent) {
//        requestNotificationPermission();
//        createNotificationChannel();
//        startPolling();
//    }
//
//    private void startPolling() {
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                checkNotifikasiAspirasi();
//                handler.postDelayed(this, DELAY_INTERVAL);
//            }
//        }, 3000);
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        handler.removeCallbacksAndMessages(null);
//    }
//
//    private void checkNotifikasiAspirasi() {
//        String username = SharedPrefManager.getInstance(this).getUsername();
//        if (username == null || username.trim().isEmpty()) {
//            Log.w("NOTIF_USERNAME", "Username masih null");
//            return;
//        }
//
//        APIRequestData api = RetroServer.konekRetrofit().create(APIRequestData.class);
//        Call<ResponNotifikasiAspirasi> call = api.getNotifikasiAspirasi(username);
//
//        call.enqueue(new Callback<ResponNotifikasiAspirasi>() {
//            @Override
//            public void onResponse(Call<ResponNotifikasiAspirasi> call, Response<ResponNotifikasiAspirasi> response) {
//                if (response.isSuccessful() && response.body() != null) {
//                    List<ModelNotifikasiAspirasi> list = response.body().getData();
//                    if (list != null && !list.isEmpty()) {
//                        String tanggapanBaru = list.get(0).getTanggapan();
//                        if (tanggapanBaru != null && !tanggapanBaru.equals(lastTanggapan)) {
//                            lastTanggapan = tanggapanBaru;
//                            showNotification("Aspirasi Ditanggapi", "Tanggapan: " + tanggapanBaru);
//                        }
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ResponNotifikasiAspirasi> call, Throwable t) {
//                Log.e("API_RESPONSE", "Gagal koneksi: " + t.getMessage());
//            }
//        });
//    }
//
//    private void showNotification(String title, String message) {
//        if (!isNotificationPermissionGranted()) return;
//
//        Intent intent = new Intent(this, MainActivity.class);
//        PendingIntent pendingIntent = PendingIntent.getActivity(
//                this, 0, intent, PendingIntent.FLAG_IMMUTABLE
//        );
//
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
//                .setSmallIcon(R.drawable.logo)
//                .setContentTitle(title)
//                .setContentText(message)
//                .setPriority(NotificationCompat.PRIORITY_HIGH)
//                .setAutoCancel(true)
//                .setContentIntent(pendingIntent);
//
//        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        manager.notify(notificationId++, builder.build());
//    }
//
//    private void createNotificationChannel() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            NotificationChannel channel = new NotificationChannel(
//                    CHANNEL_ID,
//                    "Notifikasi Aspirasi",
//                    NotificationManager.IMPORTANCE_HIGH
//            );
//            channel.setDescription("Channel untuk notifikasi aspirasi");
//            NotificationManager manager = getSystemService(NotificationManager.class);
//            manager.createNotificationChannel(channel);
//        }
//    }
//
//    private boolean isNotificationPermissionGranted() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            return checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)
//                    == PackageManager.PERMISSION_GRANTED;
//        }
//        return true;
//    }
//
//    private void requestNotificationPermission() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)
//                    != PackageManager.PERMISSION_GRANTED) {
//                Intent intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
//                        .putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(intent);
//            }
//        }
//    }
//}
