package com.hathoute.bacplus;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.klinker.android.sliding.SlidingActivity;

import java.io.File;

public class LessonActivity extends SlidingActivity {

    private Lesson lesson;
    private Resources resources;
    private boolean isProcessing = false;

    @Override
    public void init(Bundle savedInstanceState) {
        setImage(R.drawable.lesson_image);
        setContent(R.layout.activity_lesson);
        int iChosenSubject = getIntent().getIntExtra("subject", 0);
        int iChosenLesson = getIntent().getIntExtra("lesson", 0);
        lesson = new BacDataDBHelper(this).getLesson(iChosenSubject, iChosenLesson);
        resources = this.getResources();
        setupViews();
        setupListeners();

        startService(new Intent(LessonActivity.this, AdManagerService.class));
    }

    public void setupViews() {
        TextView tvLesson = findViewById(R.id.tvExam);
        TextView tvSubject = findViewById(R.id.tvSubject);
        TextView tvYear = findViewById(R.id.tvYear);
        TextView tvOptions = findViewById(R.id.tvOptions);

        tvLesson.setText(Html.fromHtml(resources.getString(R.string.lesson_name)
                .replace("$", "<b>" + lesson.getName() + "</b>")));
        tvSubject.setText(Html.fromHtml(resources.getString(R.string.string_subject)
                .replace("$", "<b>" +
                        resources.getStringArray(R.array.subjects)[lesson.getSubject()] + "</b>")));
        tvYear.setText(Html.fromHtml(resources.getString(R.string.string_year)
                .replace("$", "<b>" + resources.getString
                        (lesson.getYear() == MainActivity.YEAR_FIRST ?
                                R.string.years_first : R.string.years_second) + "</b>")));

        String optionsStr = AppHelper.formatOptions(lesson.getYear(), lesson.getOptions());
        tvOptions.setText(Html.fromHtml(resources.getString(R.string.string_option)
                .replace("$", "<b>" + optionsStr + "</b>")));
    }

    public void setupListeners() {
        LinearLayout openFile = findViewById(R.id.openFile);
        openFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!lesson.open(LessonActivity.this)) {
                    lesson.download(LessonActivity.this, true);
                }
                startService(new Intent(LessonActivity.this, AdManagerService.class));
            }
        });

        final LinearLayout downloadFile = findViewById(R.id.downloadFile);
        if(lesson.isAvailable(LessonActivity.this) == AppHelper.Storage.Data) {
            ((LinearLayout) downloadFile.getParent()).removeView(downloadFile);
        }
        else {
            downloadFile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    lesson.download(LessonActivity.this, false);
                }
            });
        }

        final LinearLayout deleteFile = findViewById(R.id.deleteFile);
        if(lesson.isAvailable(LessonActivity.this) != AppHelper.Storage.Data) {
            ((LinearLayout) deleteFile.getParent()).removeView(deleteFile);
        }
        else {
            deleteFile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    lesson.deleteDialog(LessonActivity.this);
                    startService(new Intent(LessonActivity.this, AdManagerService.class));
                }
            });
        }
    }
}
