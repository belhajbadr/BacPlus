package com.hathoute.bacplus;

import android.app.Activity;
import android.app.Application;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v4.content.FileProvider;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class AppHelper {

    public static String hostURL = "http://80.211.97.124/";
    public static String ADMOB_ID = "ca-app-pub-9871252548902893~9736828725";

    public static class Storage {
        static final int None = 0;
        static final int Data = 1;
        static final int Cache = 2;
    }

    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);
        } catch (Exception e) {
            return null;
        }
    }

    public static void watchYoutubeVideo(Context context, String id){
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + id));
        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://www.youtube.com/watch?v=" + id));
        try {
            context.startActivity(appIntent);
        } catch (ActivityNotFoundException ex) {
            context.startActivity(webIntent);
        }
    }

    public static int getSubjectIDbyAbv(Context context, String subject) {
        String[] subjects = context.getResources().getStringArray(R.array.subjects_abv);
        int index = -1;
        for (int i = 0; i < subjects.length; i++) {
            if (subjects[i].equals(subject)) {
                index = i;
                break;
            }
        }
        return index;
    }

    public static int getOptionIdbyAbv(Context context, int Year, String option) {
        String[] options = context.getResources()
                .getStringArray(Year == MainActivity.YEAR_FIRST ? R.array.options_firstyear_helper :
                        R.array.options_secondyear_helper);
        int index = -1;
        for (int i = 0; i < options.length; i++) {
            if(options[i].equals(option)) {
                index = i;
                break;
            }
        }
        return index;
    }

    public static List<Integer> getOptionIdsbyAbvs(Context context, int Year, String options) {
        String[] optionsabv = context.getResources()
                .getStringArray(Year == MainActivity.YEAR_FIRST ? R.array.options_firstyear_helper :
                        R.array.options_secondyear_helper);

        List<String> tokenslist = Arrays.asList(options.split(";"));
        List<Integer> idsList = new ArrayList<>();

        for(int i = 0; i < optionsabv.length; i++) {
            if(tokenslist.contains(optionsabv[i]))
                idsList.add(i);
        }
        return idsList;
    }

    public static String formatOptions(int Year, String options) {
        List<Integer> idsList = getOptionIdsbyAbvs(App.getContext(), Year, options);
        String[] optionsName = App.getContext().getResources()
                .getStringArray(Year == MainActivity.YEAR_FIRST ? R.array.options_firstyear_array :
                        R.array.options_secondyear_array);

        StringBuilder strBuilder = new StringBuilder("");
        for(int i = 0; i < idsList.size(); i++) {
            if(i == 0) {
                strBuilder.append(optionsName[idsList.get(0)]);
                continue;
            }
            strBuilder.append(" - ");
            strBuilder.append(optionsName[idsList.get(i)]);
        }
        return strBuilder.toString();
    }

    public static String getYearStrbyId(int Year) {
        return App.getContext().getResources().getString(Year == MainActivity.YEAR_FIRST ?
                R.string.years_first : R.string.years_second);
    }

    public static int getYearResbyId(int Year) {
        return Year == MainActivity.YEAR_FIRST ? R.string.years_first : R.string.years_second;
    }

    public static boolean cleanAll(Context context) {
        boolean result = false;
        try {
            FileUtils.deleteDirectory(context.getFilesDir());
            result = true;
        } catch(Exception ignored) {
        }

        return result;
    }

    public static String getAppropriateSize(int bytes) {
        float convertedBytes;
        String sizePrefix;
        if(bytes < 1024) {
            convertedBytes = bytes;
            sizePrefix = "bytes";
        }
        else if(bytes < 1024*1024) {
            convertedBytes = (float)bytes/1024;
            sizePrefix = "KB";
        }
        else {
            convertedBytes = (float)bytes/(1024*1024);
            sizePrefix = "MB";
        }

        return String.format(Locale.ENGLISH, "%.2f", convertedBytes) + " " + sizePrefix;
    }

    public static boolean isNetworkAvailable(Context context) {
        try {
            ConnectivityManager connectivityManager
                    = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        } catch(Exception ignored) {
            return false;
        }
    }
}
