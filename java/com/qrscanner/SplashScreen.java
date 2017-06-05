package com.qrscanner;

/**
 * Created by wllim on 2/5/16.
 */

import android.os.Bundle;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;

import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.view.animation.AnimationUtils;
import android.view.View;


public class SplashScreen extends AppCompatActivity {

    ImageView splashimg, irisLogo;
    Button btn_google;
    private static final String TAG = "SplashScreen";


    @Override
    protected  void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

       splashimg= (ImageView)findViewById(R.id.splashscreen);

        irisLogo=(ImageView)findViewById(R.id.irislogo);
        //Google login button
        btn_google= (Button)findViewById(R.id.button_google_splashscreen);

        Animation animationFadeIn =AnimationUtils.loadAnimation(this,R.anim.fadein);
        Animation animationFadeInShort = AnimationUtils.loadAnimation(this,R.anim.fadeintwosec);
        splashimg.startAnimation(animationFadeInShort);
        btn_google.startAnimation(animationFadeIn);

        btn_google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), LoginGoogle.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                finish();
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            onBackPressed();
        }
        return super.onKeyDown(keyCode, event);
    }

    public void onBackPressed() {
        //Intent myIntent = new Intent(getApplicationContext(), SplashScreen.class);
        //myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //startActivity(myIntent);
        finish();
        //return;
    }
}
/*
  Thread timerThread = new Thread() {
            public void run() {
                try {
                    sleep(5000);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {

                    //Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                    //startActivity(intent);
                }
            }
        };
        timerThread.start();

         @Override
    protected void onPause(){
        super.onPause();
        finish();
    }
 */