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

public class ExamActivity extends SlidingActivity {

    private Exam exam;
    private Resources resources;

    @Override
    public void init(Bundle savedInstanceState) {
        setImage(R.drawable.exam_image);
        setContent(R.layout.activity_exam);
        int iChosenSubject = getIntent().getIntExtra("subject", 0);
        int iChosenExam = getIntent().getIntExtra("exam", 0);
        exam = new BacDataDBHelper(this).getExam(iChosenSubject, iChosenExam);
        resources = this.getResources();
        setupViews();
        setupListeners();

        startService(new Intent(ExamActivity.this, AdManagerService.class));
    }

    public void setupViews() {
        TextView tvExam = findViewById(R.id.tvExam);
        TextView tvSubject = findViewById(R.id.tvSubject);
        TextView tvOptions = findViewById(R.id.tvOptions);
        TextView tvYear = findViewById(R.id.tvYear);
        TextView tvRegion = findViewById(R.id.tvRegion);

        if(exam.getType() != Exam.Types.CLASS)
            tvExam.setText(Html.fromHtml(resources.getString(R.string.exam_session)
                    .replace("$", "<b>" + exam.getExamId() + " " +
                    resources.getStringArray(R.array.exam_sessions)[exam.getType()]) + " " + "</b>"));
        else

            tvExam.setText(Html.fromHtml(resources.getString(R.string.exam_surveille)
                    .replace("$", "<b>" + exam.getExamId() + "</b>")));

        tvSubject.setText(Html.fromHtml(resources.getString(R.string.string_subject)
                .replace("$", "<b>" +
                        resources.getStringArray(R.array.subjects)[exam.getSubject()] + "</b>")));

        tvYear.setText(Html.fromHtml(resources.getString(R.string.string_year)
                .replace("$", "<b>" + resources.getString
                        (exam.getYear() == MainActivity.YEAR_FIRST ?
                                R.string.years_first : R.string.years_second) + "</b>")));

        String optionsStr = AppHelper.formatOptions(exam.getYear(), exam.getOptions());
        tvOptions.setText(Html.fromHtml(resources.getString(R.string.string_option)
                .replace("$", "<b>" + optionsStr + "</b>")));

        int iRegion = exam.getRegionId();
        if(iRegion == -1) {
            tvRegion.setVisibility(View.GONE);
        }
        else {
            String mRegion = getResources().getStringArray(R.array.regions_names)[iRegion];
            tvRegion.setText(Html.fromHtml(getResources().getString(R.string.string_region)
                    .replace("$", "<b>" + mRegion + "</b>")));
        }
    }

    public void setupListeners() {
        LinearLayout openFile = findViewById(R.id.openFile);
        openFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!exam.open(ExamActivity.this))
                    exam.download(ExamActivity.this, true);

                startService(new Intent(ExamActivity.this, AdManagerService.class));
            }
        });

        final LinearLayout downloadFile = findViewById(R.id.downloadFile);

        if(exam.isAvailable(ExamActivity.this) == AppHelper.Storage.Data) {
            ((LinearLayout) downloadFile.getParent()).removeView(downloadFile);
        }
        else {
            downloadFile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    exam.download(ExamActivity.this, false);
                }
            });
        }

        final LinearLayout deleteFile = findViewById(R.id.deleteFile);
        if(exam.isAvailable(ExamActivity.this) != AppHelper.Storage.Data) {
            ((LinearLayout) deleteFile.getParent()).removeView(deleteFile);
        }
        else {
            deleteFile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    exam.deleteDialog(ExamActivity.this);
                    startService(new Intent(ExamActivity.this, AdManagerService.class));
                }
            });
        }
    }
}
