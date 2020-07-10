package com.hathoute.bacplus;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.android.gms.ads.MobileAds;

import java.util.Random;

public class SplashScreenActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        final ImageView spLogo = findViewById(R.id.ivLogo);
        final ProgressBar spLoad = findViewById(R.id.progressBar);
        Animation splashanim = AnimationUtils.loadAnimation(this, R.anim.splash_fade_in);
        spLoad.startAnimation(splashanim);
        MobileAds.initialize(this, AppHelper.ADMOB_ID);
        Thread splashTimer = new Thread() {
            public void run() {
                Random ran = new Random();
                Integer sleeptime = 3000 + ran.nextInt(3000);
                try {
                    sleep(sleeptime);
                }catch(InterruptedException e){
                    e.printStackTrace();
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        MainActivity_Launch(spLogo, spLoad);
                    }
                });
            }
        };
        splashTimer.start();
    }

    public void MainActivity_Launch(ImageView spLogo, ProgressBar spLoad) {
        Animation splashanimback = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.splash_fade_out);
        spLogo.startAnimation(splashanimback);
        spLoad.startAnimation(splashanimback);
        final Context packageContext = this;
        final Intent activityMain = new Intent(packageContext, WelcomeActivity.class);
        Thread splashendTimer = new Thread() {
            public void run() {
                try {
                    sleep(1500);
                }catch(InterruptedException e){
                    e.printStackTrace();
                }
                finally {
                    startActivity(activityMain);
                    finish();
                }
            }
        };

        splashendTimer.start();
    }
}

