package com.ELayang.Desa.Asset.Notifikasi;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.util.Log;

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

                    // 🔥 Ambil data lengkap dari server
                    String jenis  = response.body().getJenis();   // contoh: "surat" atau "aspirasi"
                    String judul  = response.body().getJudul();   // contoh: "SKTM"
                    String status = response.body().getStatus();  // contoh: "Selesai"
                    String alasan = response.body().getAlasan();  // opsional

                    NotificationHelper helper = new NotificationHelper(NotificationService.this);

                    // 🔥 Buat notifikasi detail
                    NotificationCompat.Builder builder = helper.buildNotification(
                            jenis,
                            judul,
                            status,
                            alasan
                    );

                    // 🔥 Tampilkan notifikasi
                    helper.getManager().notify(1, builder.build());
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
}
