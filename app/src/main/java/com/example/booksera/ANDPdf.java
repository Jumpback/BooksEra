package com.example.booksera;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.jean.jcplayer.model.JcAudio;
import com.example.jean.jcplayer.view.JcPlayerView;
import com.github.barteksc.pdfviewer.PDFView;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;

public class ANDPdf extends AppCompatActivity {

    String link="", productList="", product="";
    PDFView pdfView;



    // for audiobook
    String phyaudio="https://firebasestorage.googleapis.com/v0/b/androidaudio-25155.appspot.com/o/HeadFirstAndroidDevelopmentABrainFriendlyGuide2ndEdition-1.mp3?alt=media&token=7ece8fc2-2344-4a32-9eaf-76dd4b677eee";
    String mathsaudio="https://firebasestorage.googleapis.com/v0/b/androidaudio-25155.appspot.com/o/professional_android_4_application_development.mp3?alt=media&token=f5e49d85-ab33-4664-97ae-b829ec468e6a";
    String cmsaudio="https://firebasestorage.googleapis.com/v0/b/online-music-player-5376d.appspot.com/o/cms.mp3?alt=media&token=a0627acc-1d20-458a-8b19-45af51631fcf";
    String ceaudio="https://firebasestorage.googleapis.com/v0/b/online-music-player-5376d.appspot.com/o/ce.mp3?alt=media&token=3e4a0ed2-e2ad-42a9-8e12-9c4e6efa0722";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_a_n_d_pdf);

        //audiobook

        JcPlayerView jp3 = findViewById(R.id.jcplayer3);

        ArrayList<JcAudio> ja3 = new ArrayList<>();

        ja3.add(JcAudio.createFromURL("BRAIN FRIENDLY GUIDE", phyaudio));
        ja3.add(JcAudio.createFromURL("PROFESSIONAL ANDROID APPLICATION DEVELOPMENT GUIDE", mathsaudio));

        jp3.initPlaylist(ja3, null);
        jp3.createNotification(); // default icon

        //for pdf

        product = getIntent().getStringExtra("title");
        productList=getIntent().getStringExtra("productList");
        link=getIntent().getStringExtra("link");
        Objects.requireNonNull ( getSupportActionBar () ).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(productList);
        pdfView=findViewById(R.id.showPdf);

        if (isConnected()) {
            Toast.makeText(getApplicationContext(), "Internet Connected", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();

            AlertDialog.Builder builder = new AlertDialog.Builder(ANDPdf.this);
            builder.setTitle("NoInternet Connection Alert")
                    .setMessage("GO to Setting ?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(ANDPdf.this,"Go Back TO HomePage!",Toast.LENGTH_SHORT).show();
                        }
                    });
            //Creating dialog box
            AlertDialog dialog  = builder.create();
            dialog.show();

        }

        Bundle bundle = getIntent().getExtras();
        if (null != bundle) {
            link = getIntent().getStringExtra("link");
        }
        new ANDPdf.RetrievePDFStream().execute(link);
    }

    public boolean isConnected() {
        boolean connected = false;

        try {
            ConnectivityManager cm = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo nInfo = cm.getActiveNetworkInfo();
            connected = nInfo != null && nInfo.isAvailable() && nInfo.isConnected();
            return connected;
        } catch (Exception e) {
            Log.e("Connectivity Exception", e.getMessage());
        }
        return false;
    }


    @SuppressLint("StaticFieldLeak")
    class RetrievePDFStream extends AsyncTask<String, Void, InputStream> {

        ProgressDialog progressDialog;
        protected void onPreExecute()
        {
            progressDialog = new ProgressDialog(ANDPdf.this);
            progressDialog.setTitle("getting the book content...");
            progressDialog.setMessage("Please wait...");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

        }
        @Override
        protected InputStream doInBackground(String... strings) {
            InputStream inputStream = null;

            try {

                URL urlx = new URL(strings[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) urlx.openConnection();
                if (urlConnection.getResponseCode() == 200) {
                    inputStream = new BufferedInputStream(urlConnection.getInputStream());

                }
            } catch (IOException e) {
                return null;
            }
            return inputStream;

        }

        @Override
        protected void onPostExecute(InputStream inputStream) {
            pdfView.fromStream(inputStream).load();
            progressDialog.dismiss();
        }
    }

    @Override public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == android.R.id.home)
        {
            onBackPressed();
            return true;
        }
        return false;
    }
}