package com.hathoute.bacplus;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import org.apache.commons.io.FileUtils;

public class OnAppDestroyedService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.e("ClearFromRecentService", "Deleting Cache");
        try {
            FileUtils.deleteDirectory(this.getCacheDir());
        } catch(Exception ignored) {
        }
        stopSelf();
    }
}
