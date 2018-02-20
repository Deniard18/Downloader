package com.nightingale.user.downloader;

import android.graphics.Color;
import android.os.AsyncTask;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;
import java.util.concurrent.TimeUnit;


public class MainActivity extends AppCompatActivity {

    MyTask mTask;

    //ConstraintLayout mLayout;
    ScrollView mLayout;

    int filesAmount;
    Boolean pauseFlag = false;

    Button btnGetReady;
    Button btnDownload;
    Button btnPause;
    Button btnStatus;
    Button btnStop;
    TextView tvInfo;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        if(mTask != null){
//            mTask.link(this);
//        }

        tvInfo = (TextView) findViewById(R.id.tvInfo);
        btnGetReady = (Button) findViewById(R.id.btnGetReady);
        btnDownload = (Button) findViewById(R.id.btnDownload);
        btnPause = (Button) findViewById(R.id.btnPause);
        btnStatus = (Button) findViewById(R.id.btnStatus);
        btnStop = (Button) findViewById(R.id.btnStop);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        mLayout = (ScrollView) findViewById(R.id.mLayout);

        btnGetReady.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTask = new MyTask();
                mTask.link(MainActivity.this);

                pauseFlag = false;
                filesAmount = 20;
                mLayout.setBackgroundColor(Color.WHITE);
                tvInfo.setText("");
                progressBar.setProgress(0);
                btnDownload.setEnabled(true);
            }
        });
        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTask.execute(filesAmount);
                btnGetReady.setEnabled(false);
                btnDownload.setEnabled(false);
                btnPause.setEnabled(true);
                btnStop.setEnabled(true);
            }
        });
        btnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(btnPause.getText().toString().equals("Pause")){
                    btnPause.setText("Resume");
                    tvInfo.setText("Paused");
                    mTask.cancel(false);
                    pauseFlag = true;

                } else if(btnPause.getText().toString().equals("Resume")) {
                    btnPause.setText("Pause");
                    tvInfo.setText("Resumed");
                    pauseFlag = false;
                    mTask = new MyTask();
                    mTask.link(MainActivity.this);
                    mTask.execute(filesAmount);
                }
            }
        });
        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTask.cancel(false);
                tvInfo.setText("Canceled");
                btnPause.setText("Pause");
                finishTask();
            }
        });
        btnStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mTask != null){
                    if(pauseFlag){
                        Toast.makeText(MainActivity.this, "PAUSED", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, mTask.getStatus().toString(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "NOT STARTED", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void finishTask(){
        tvInfo.setText("End");
        pauseFlag = false;
        btnGetReady.setEnabled(true);
        btnDownload.setEnabled(false);
        btnPause.setEnabled(false);
        btnStop.setEnabled(false);
        progressBar.setProgressDrawable(getResources().getDrawable(R.drawable.progressbar_new));
    }

    public static class MyTask extends AsyncTask<Integer, Integer, Integer> {

        MainActivity activity;

        int cnt = 0;

        //get instance of MainActivity
        void link(MainActivity act) {
            activity = act;
        }

        //delete instance of MainActivity
        void unLink() {
            activity = null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //change background
            //tvInfo.setText("Begin");
            activity.mLayout.setBackgroundColor(activity.getResources().getColor(R.color.newBackground));
            activity.progressBar.setProgress(0);
            activity.progressBar.setMax(activity.filesAmount);
        }

        @Override
        protected Integer doInBackground(Integer... params) {
            try {
                do {
                    if(activity.mTask.isCancelled()){
                        return null;
                    }
                    if(downloadFile()){
                        publishProgress(++cnt);
                    }
                }while(cnt < params[0]);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return params[0] - cnt;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            activity.progressBar.setProgress(values[0]);
            activity.tvInfo.setText("Downloaded " + values[0] + " files of " + activity.filesAmount);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            activity.filesAmount = activity.filesAmount - cnt + 1;
        }

        @Override
        protected void onPostExecute(Integer aVoid) {
            super.onPostExecute(aVoid);
            //change progress bar
            activity.filesAmount = aVoid;
            activity.finishTask();
        }

        private Boolean downloadFile() throws InterruptedException {
            Random r = new Random();
            TimeUnit.SECONDS.sleep(1);
            //return r.nextBoolean();
            return true;
        }
    }
}
