package com.hathoute.bacplus;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import static com.google.android.gms.ads.AdRequest.DEVICE_ID_EMULATOR;
import static com.google.android.gms.ads.AdRequest.ERROR_CODE_INVALID_REQUEST;
import static com.google.android.gms.ads.AdRequest.ERROR_CODE_NO_FILL;

public class AdManagerService extends Service {
    private InterstitialAd mInterstitialAd;
    private int showAd = 2;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mInterstitialAd = new InterstitialAd(getApplicationContext());
        mInterstitialAd.setAdUnitId("YOUR_AD_UNIT");
        AdRequest adRequest = new AdRequest.Builder().build();
        mInterstitialAd.loadAd(adRequest);

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                // Load the next interstitial.
                AdRequest adRequest = new AdRequest.Builder().build();
                mInterstitialAd.loadAd(adRequest);
            }
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mInterstitialAd.isLoaded() && showAd >= 5) {
                    mInterstitialAd.show();
                    showAd = 0;
                }
                else {
                    showAd++;
                    AdRequest adRequest = new AdRequest.Builder().build();
                    mInterstitialAd.loadAd(adRequest);
                }
            }
        }, 2000);
        return START_STICKY;
    }
}
