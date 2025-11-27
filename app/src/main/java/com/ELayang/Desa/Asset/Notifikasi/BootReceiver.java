package com.ELayang.Desa.Asset.Notifikasi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.os.PersistableBundle;
import android.content.SharedPreferences;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {

            SharedPreferences sp = context.getSharedPreferences("prefLogin", Context.MODE_PRIVATE);
            String username = sp.getString("username", "");

            PersistableBundle bundle = new PersistableBundle();
            bundle.putString("username", username);

            ComponentName componentName = new ComponentName(context, NotificationService.class);

            JobInfo jobInfo = new JobInfo.Builder(333, componentName)
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                    .setPeriodic(15 * 60 * 1000)
                    .setExtras(bundle)
                    .setPersisted(true)
                    .build();

            JobScheduler scheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
            scheduler.schedule(jobInfo);
        }
    }
}
