package com.hathoute.bacplus;

import android.os.Build;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.List;

public class TabbedSubjectActivity extends AppCompatActivity {

    TabLayout tabLayout;
    private int iChosenYear;
    private int iChosenOption;
    private int iChosenSubject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabbed_subject);

        iChosenYear = getIntent().getIntExtra("year", 0);
        iChosenOption = getIntent().getIntExtra("option", 0);
        iChosenSubject = getIntent().getIntExtra("subject", 0);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        ((TextView)findViewById(R.id.tvTbTitle)).setText(getResources().getStringArray(R.array.subjects)[iChosenSubject]);
        setSupportActionBar(toolbar);

        configureAds();

        configureTabs();
    }

    void configureAds() {
        AdView adBottom = findViewById(R.id.adBottom);
        AdRequest adRequest = new AdRequest.Builder().build();
        adBottom.loadAd(adRequest);
    }

    void configureTabs() {
        tabLayout = findViewById(R.id.tab_layout);
        String[] tabs = this.getResources().getStringArray(R.array.subject_tabs);
        for(String tab : tabs) {
            tabLayout.addTab(tabLayout.newTab().setText(tab));
        }
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);

        final ViewPager viewPager = findViewById(R.id.pager);
        final PagerAdapter pagerAdapter = new PagerAdapter(getSupportFragmentManager(),
                tabLayout.getTabCount(), iChosenYear, iChosenOption, iChosenSubject);
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager.setCurrentItem(iChosenSubject);
    }
}
