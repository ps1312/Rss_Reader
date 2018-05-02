package br.ufpe.cin.if1001.rss.services;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.util.Log;

public class DownloadJobService extends JobService {
    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        Intent downloadServiceIntent = new Intent(getApplicationContext(), DownloadXmlService.class);
        getApplicationContext().startService(downloadServiceIntent);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        Intent downloadServiceIntent = new Intent(getApplicationContext(), DownloadXmlService.class);
        getApplicationContext().stopService(downloadServiceIntent);
        return false;
    }
}
