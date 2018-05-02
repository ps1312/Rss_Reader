package br.ufpe.cin.if1001.rss.ui;

import android.app.Activity;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import br.ufpe.cin.if1001.rss.R;
import br.ufpe.cin.if1001.rss.db.SQLiteRSSHelper;
import br.ufpe.cin.if1001.rss.services.DownloadJobService;
import br.ufpe.cin.if1001.rss.services.DownloadXmlService;

public class MainActivity extends Activity {

    private RecyclerView conteudoRSS;
    private final String RSS_FEED = "http://rss.cnn.com/rss/edition.rss";
    private SQLiteRSSHelper db;
    private static final int JOB_ID = 1;
    private JobScheduler jobScheduler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = SQLiteRSSHelper.getInstance(this);

        jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);

        conteudoRSS = findViewById(R.id.conteudoRSS);

        //Aplicando algumas funcionalidades no recyclerView, o adapter vai ser estabelecido no onPostExecute da AsyncTask ExibirFeed
        conteudoRSS.setLayoutManager(new LinearLayoutManager(this));
        conteudoRSS.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        conteudoRSS.setItemAnimator(new DefaultItemAnimator());
    }

    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String linkfeed = preferences.getString("rssfeedlink", getResources().getString(R.string.rssfeed));

        //Criar intent para iniciar o servico de download do feed
        Intent downloadService = new Intent(getApplicationContext(), DownloadXmlService.class);
        downloadService.putExtra("url", linkfeed);
        startService(downloadService);

        agendarJob();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Registrar o broadcastreceiver dinamico quando o usuario estiver com o app em primeiro plano
        IntentFilter intentFilter = new IntentFilter(DownloadXmlService.DOWNLOAD_COMPLETE);
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(onDownloadCompleteEvent, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //Cancelar o registro do broadcastReceiver
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(onDownloadCompleteEvent);
    }

    //Evento para quando o broadcast for recebido
    private BroadcastReceiver onDownloadCompleteEvent = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(getApplicationContext(), "Notícias carregadas, exibindo o feed.", Toast.LENGTH_LONG).show();
            new ExibirFeed().execute();
        }
    };

    @Override
    protected void onDestroy() {
        db.close();
        super.onDestroy();
    }

    class ExibirFeed extends AsyncTask<Void, Void, Cursor> {

        @Override
        protected Cursor doInBackground(Void... voids) {
            Cursor c = db.getItems();
            c.getCount();
            return c;
        }

        @Override
        protected void onPostExecute(Cursor c) {
            if (c != null) {
                conteudoRSS.setAdapter(new RecyclerAdapter(getApplicationContext(), c));
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mainmenu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.btn_Config:
                startActivity(new Intent(this, ConfigActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void agendarJob() {
        Log.d("JOB SCHEDULER", "Agendando job scheduler");
        JobInfo.Builder jobBuilder = new JobInfo.Builder(JOB_ID, new ComponentName(this, DownloadJobService.class));

        //Conexao com internet necessaria
        jobBuilder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);

        //Realizar o download a cada n segundos de acordo com o shared prefs
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String timePeriodString = preferences.getString("rssrefreshtime", "3");
        String[] timeNumber = timePeriodString.split(" ");
        if (timePeriodString.contains("h")) {
            jobBuilder.setPeriodic((Integer.valueOf(timeNumber[0]) * 60) * 60000);
        } else {
            jobBuilder.setPeriodic(Integer.valueOf(timeNumber[0]) * 60000);
        }

        jobScheduler.schedule(jobBuilder.build());
    }
}
