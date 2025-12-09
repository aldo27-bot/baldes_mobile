package com.ELayang.Desa.Asset.Notifikasi;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.util.Log;
import android.content.SharedPreferences;

import androidx.core.app.NotificationCompat;

import com.ELayang.Desa.API.APIRequestData;
import com.ELayang.Desa.API.RetroServer;
import com.ELayang.Desa.DataModel.Notifikasi.ResponPopup;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationService extends JobService {

    @Override
    public boolean onStartJob(JobParameters params) {

        String username = params.getExtras().getString("username");

        APIRequestData api = RetroServer.konekRetrofit().create(APIRequestData.class);
        Call<ResponPopup> call = api.getPopupNotifikasi(username);

        call.enqueue(new Callback<ResponPopup>() {
            @Override
            public void onResponse(Call<ResponPopup> call, Response<ResponPopup> response) {

                if (response.body() != null && response.body().getKode() == 1) {

                    //  Ambil data lengkap dari server
                    String jenis  = response.body().getJenis();
                    String judul  = response.body().getJudul();
                    String status = response.body().getStatus();
                    String alasan = response.body().getAlasan();

                    //  Buat ID unik notifikasi
                    String notifKey = jenis + "_" + judul + "_" + status;

                    //  Ambil notifikasi terakhir yang disimpan
                    String lastNotif = getLastNotif();

                    //  Cek apakah notifikasi ini sudah pernah dikirim
                    if (!notifKey.equals(lastNotif)) {

                        //  Simpan notifikasi terbaru agar tidak terkirim ulang
                        saveLastNotif(notifKey);

                        //  Kirim notifikasi
                        NotificationHelper helper = new NotificationHelper(NotificationService.this);
                        NotificationCompat.Builder builder = helper.buildNotification(
                                jenis,
                                judul,
                                status,
                                alasan
                        );

                        helper.getManager().notify(1, builder.build());
                    }
                }

                jobFinished(params, true);
            }

            @Override
            public void onFailure(Call<ResponPopup> call, Throwable t) {
                Log.e("NotificationService", "Error: " + t.getMessage());
                jobFinished(params, true);
            }
        });

        return true; // async job
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }

    // =====================================================================================
    //  SISTEM ANTI-DUPLIKASI NOTIFIKASI
    // =====================================================================================

    private String getLastNotif() {
        SharedPreferences sp = getSharedPreferences("notifPref", MODE_PRIVATE);
        return sp.getString("lastNotif", "");
    }

    private void saveLastNotif(String data) {
        SharedPreferences sp = getSharedPreferences("notifPref", MODE_PRIVATE);
        sp.edit()
                .putString("lastNotif", data)
                .apply();
    }
}
