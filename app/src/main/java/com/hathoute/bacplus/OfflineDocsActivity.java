package com.hathoute.bacplus;

import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class OfflineDocsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    private DrawerLayout dlOffline;
    private ActionBarDrawerToggle drawerToggle;
    private int iAdded;
    private int[] menuIdList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline_docs);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
            if (getWindow().getDecorView().getLayoutDirection() == View.LAYOUT_DIRECTION_LTR){
                getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            }

        Toolbar toolbar = findViewById(R.id.tbOffline);
        setSupportActionBar(toolbar);

        dlOffline = findViewById(R.id.dlOffline);

        drawerToggle = new ActionBarDrawerToggle(this, dlOffline, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        dlOffline.addDrawerListener(drawerToggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        populateNavView();
        new OfflineDBHelper(this).clear(OfflineDBHelper.AVAILABLE);
        scanOffline(getFilesDir());

        configureAd();

        showNotice();
    }

    void configureAd() {
        AdView adBottom = findViewById(R.id.adBottom);
        AdRequest adRequest = new AdRequest.Builder().build();
        adBottom.loadAd(adRequest);
    }

    public void showNotice() {
        Fragment fragment = new NoticeFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("available", iAdded);
        fragment.setArguments(bundle);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, fragment, "NOTICE")
                .commit();
    }

    private void populateNavView() {
        //Prepare NavigationView items
        NavigationView nvOffline = findViewById(R.id.nvOffline);
        nvOffline.setNavigationItemSelectedListener(this);
        Menu menu = nvOffline.getMenu();
        SubMenu submenu = menu.getItem(2).getSubMenu();
        submenu.clear();

        List<Integer> idList = formatSubjects();
        String[] subjectNames = getResources().getStringArray(R.array.subjects);
        for(Integer id : idList) {
            submenu.add(0, id, 1, subjectNames[id]).setCheckable(true)
                    .setChecked(false).setIcon(R.drawable.ic_downloads);
        }
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment;
        FragmentManager fragmentManager = getSupportFragmentManager();
        Bundle bundle;
        int itemId = item.getItemId();
        switch(itemId) {
            case R.id.nav_lastseen:
                fragment = new LastSeenFragment();
                fragmentManager.beginTransaction()
                        .replace(R.id.content_frame, fragment, "LAST")
                        .commit();
                break;
            case R.id.nav_allfiles:
                fragment = new AvailableDocsFragment();
                bundle = new Bundle();
                bundle.putInt("subject", -1);
                fragment.setArguments(bundle);
                fragmentManager.beginTransaction()
                        .replace(R.id.content_frame, fragment, "ALL")
                        .commit();
                break;
            default:
                fragment = new AvailableDocsFragment();
                bundle = new Bundle();
                bundle.putInt("subject", itemId);
                fragment.setArguments(bundle);
                fragmentManager.beginTransaction()
                        .replace(R.id.content_frame, fragment, "ALL")
                        .commit();
                break;
        }

        dlOffline.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if(dlOffline.isDrawerOpen(GravityCompat.START))
            dlOffline.closeDrawer(GravityCompat.START);
        else
            super.onBackPressed();
    }

    private void scanOffline(File rootFolder) {
        iAdded = 0;
        try {
            File[] files = rootFolder.listFiles(); //here you will get NPE if directory doesn't contains  any file,handle it like this.
            for (File file : files) {
                if (file.isDirectory()) {
                    System.out.println("AAEZA | FILE NAME: " + file.getName());
                    subScanOffline(file);
                } else if (file.getName().endsWith(".pdf")) {
                    System.out.println("AAEZA | FOUND FILE: " + file.getName());
                    addAppropriateDoc(file);
                    iAdded++;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.out.println("AAEZA | ADDED: " + iAdded + " Document as Available!");
        }
    }

    private void subScanOffline(File rootFolder) {
        try {
            File[] files = rootFolder.listFiles(); //here you will get NPE if directory doesn't contains  any file,handle it like this.
            for (File file : files) {
                if (file.isDirectory()) {
                    System.out.println("AAEZA | subFILE NAME: " + file.getName());
                    subScanOffline(file);
                } else if (file.getName().endsWith(".pdf")) {
                    System.out.println("AAEZA | subFOUND FILE: " + file.getName());
                    addAppropriateDoc(file);
                    iAdded++;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addAppropriateDoc(File doc) {
        int docId = Integer.valueOf(FilenameUtils.getBaseName(doc.getName()));
        File typeDir = doc.getParentFile();
        String typeName = typeDir.getName();
        String subjectAbv = typeDir.getParentFile().getName();
        int subjectId = AppHelper.getSubjectIDbyAbv(this, subjectAbv);
        Object document;
        if(typeName.equals("lessons"))
            document = new BacDataDBHelper(this).getLesson(subjectId, docId);
        else
            document = new BacDataDBHelper(this).getExam(subjectId, docId);

        if(new OfflineDBHelper(this).add(OfflineDBHelper.AVAILABLE, document))
            System.out.println("AAEZA | Added!");
    }

    private List<Integer> formatSubjects() {
        List<Integer> idList = new ArrayList<>();
        Cursor cursor = new OfflineDBHelper(this).getAvailable();
        if(cursor.moveToFirst()) {
            do {
                Integer id = cursor.getInt(2);
                if(!idList.contains(id))
                    idList.add(id);
            } while(cursor.moveToNext());
        }
        Collections.sort(idList);
        return idList;
    }

    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    public void setActionBarTitle(int resId) {
        getSupportActionBar().setTitle(resId);
    }
}
