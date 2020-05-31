package com.example.sotel;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    SwipeRefreshLayout swipeLayt;
    ImageView superImageView,log;
    WebView webView;
    ProgressBar superProgressBar;
    Dialog load_dialog;

    String Newurl = "https://www.sotel.in/";

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        swipeLayt= findViewById(R.id.swipelayt);
        webView = findViewById(R.id.webview);
        superProgressBar = findViewById(R.id.myProgressBar);
        superImageView = findViewById(R.id.myImageView);
        log=findViewById(R.id.logo);


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.setView(R.layout.progress);
        }
        load_dialog = builder.create();

        swipeLayt.setColorSchemeResources(R.color.colorPrimaryDark);

        WebSettings webSettings = webView.getSettings();

        webSettings.setDomStorageEnabled(true);
        webSettings.setJavaScriptEnabled(true);
        Animation animation1 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.flash_leave_now);
        log.startAnimation(animation1);
        new Thread(){
            @Override
            public void run(){
                try {
                    sleep(5000);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            findViewById(R.id.main_layout).setVisibility(View.GONE);
                            findViewById(R.id.main_layout1).setVisibility(View.VISIBLE);

                            swipeLayt.setVisibility(View.VISIBLE);
                            webView.setWebViewClient(new MyWebViewClient(load_dialog));
                            webView.loadUrl(Newurl);
                            load_dialog.show();

                            swipeLayt.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                                @Override
                                public void onRefresh() {
                                    swipeLayt.setRefreshing(false);
                                    webView.loadUrl(Newurl);
                                }
                            });
                        }
                    });

                } catch (InterruptedException e) {
                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    finish();
                }

            }
        }.start();



        webView.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                superProgressBar.setProgress(newProgress);
            }

            @SuppressLint("NewApi")
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                Objects.requireNonNull(getSupportActionBar()).setTitle(title);
            }

            @Override
            public void onReceivedIcon(WebView view, Bitmap icon) {
                super.onReceivedIcon(view, icon);
                superImageView.setImageBitmap(icon);

            }
        });

    }

    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        }else {
            new AlertDialog.Builder(this)
                    .setMessage("Do you want to exit Sotel ?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @SuppressLint("NewApi")
                        public void onClick(DialogInterface dialog, int id) { finishAffinity(); }})
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {public void onClick(DialogInterface dialog, int id) { }})
                    .setCancelable(true).create().show();
        }
    }

    private class MyWebViewClient extends WebViewClient {

        Dialog dialog;

        private MyWebViewClient(Dialog dialog) {
            this.dialog = dialog;
            dialog.show();
        }

        public void onPageStarted (WebView view, String url, Bitmap favicon){ if(!dialog.isShowing()){dialog.show();} }

        public void onPageFinished(WebView view, String url) {
            Newurl = url;
            if(dialog.isShowing()){dialog.dismiss();}
            super.onPageFinished(view, url);
        }

        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            if(dialog.isShowing()){dialog.dismiss();}
            new AlertDialog.Builder(MainActivity.this).setMessage("Check Your Connection")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    }).setCancelable(false).create().show();
        }
    }

    @Override
    public void onResume(){
        super.onResume();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater myMenuInflater = getMenuInflater();
        myMenuInflater.inflate(R.menu.super_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.myMenuOne:
                onBackPressed();
                break;

            case R.id.myMenuTwo:
                GoForward();
                break;

            case R.id.myMenuThree:

                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setMessage("Do you want to exit Sotel ?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                finish();
                            }

                        });

                alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialogBuilder.setCancelable(true);
                    }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();


        }

        return true;
    }

    private void GoForward() {
        if (webView.canGoForward()) {
            webView.goForward();
        } else {
            Toast.makeText(this, "Can't go further!", Toast.LENGTH_SHORT).show();
        }
    }
    }



