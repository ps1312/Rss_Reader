package br.ufpe.cin.if1001.rss.services;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import br.ufpe.cin.if1001.rss.R;

public class DownloadJobService extends JobService {
    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        Intent downloadServiceIntent = new Intent(getApplicationContext(), DownloadXmlService.class);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String linkfeed = preferences.getString("rssfeedlink", getResources().getString(R.string.rssfeed));
        downloadServiceIntent.putExtra("url", linkfeed);
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
