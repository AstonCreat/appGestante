package com.example.italo.gestante;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;


public class SplashMain extends AppCompatActivity {

    private static int SPLASH_TIME_OUT = 3000;
    private boolean InternetCheck = true;
    private ProgressBar spinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_main);

        spinner = (ProgressBar)findViewById(R.id.splashLogo);
        spinner.setVisibility(View.VISIBLE);
        PostDelayedMethod();

    }

    public void PostDelayedMethod(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isOnline()){
                    Intent intent = new Intent(SplashMain.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                }else {
                    //spinner.setVisibility(View.VISIBLE);
                    //spinner.setVisibility(View.GONE);
                    DialogAppear();
                }
            }
        },SPLASH_TIME_OUT);
    }

    public void DialogAppear(){
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(SplashMain.this,R.style.MyCustomDialog);
        mBuilder.setView(R.layout.alertwifi);
        mBuilder.setCancelable(false);

        mBuilder.setNegativeButton("Cancelar",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
        mBuilder.setPositiveButton("Tente novamente",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        PostDelayedMethod();
                    }
                });
        mBuilder.show();
    }

    protected boolean isOnline(){
        ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return (netInfo != null && netInfo.isConnectedOrConnecting() && netInfo.isAvailable());
    }

}
