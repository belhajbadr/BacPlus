package com.hathoute.bacplus;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.FileProvider;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.File;

public class Exam {

    public class Types {
        public static final int NORMAL = 0;
        public static final int RATTRAPAGE = 1;
        public static final int CLASS = 2;
    }

    private final ContentValues cv;
    private final int Subject;

    public Exam(int Subject, ContentValues row) {
        this.Subject = Subject;
        this.cv = row;
    }

    public int getId() {
        return cv.getAsInteger("id");
    }

    public int getExamId() {
        return cv.getAsInteger(BacDataDBHelper.Columns.ExamId);
    }

    public int getYear() {
        return cv.getAsInteger(BacDataDBHelper.Columns.Year);
    }

    public int getType() {
        return cv.getAsInteger(BacDataDBHelper.Columns.Type);
    }

    public String getOptions() {
        return cv.getAsString(BacDataDBHelper.Columns.Options);
    }

    public int getSubject() {
        return this.Subject;
    }

    public int getRegionId() {
        return cv.getAsInteger(BacDataDBHelper.Columns.Region);
    }

    public String getDirectoryPath(Context context) {
        return "bacplus/" + context.getResources().getStringArray(R.array.subjects_abv)[Subject] +
                "/exams";
    }

    public String formatName() {
        Context context = App.getContext();
        int arrayid;
        if(getType() == Exam.Types.CLASS)
            arrayid = 2;
        else if(getYear() == MainActivity.YEAR_FIRST)
            arrayid = 1;
        else
            arrayid = 0;

        return context.getResources().getStringArray(R.array.exam_types)[arrayid]
                + " " + context.getResources().getString(R.string.number) + ": " + getExamId();
    }

    public boolean open(Context context) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        if(isAvailable(context) == AppHelper.Storage.None)
            return false;

        File pdfdir = new File(isAvailable(context) == AppHelper.Storage.Cache ?
                context.getCacheDir() : context.getFilesDir(), getDirectoryPath(context));
        File pdfFile = new File(pdfdir, getId() + ".pdf");
        //intent.setDataAndType(Uri.fromFile(pdfFile), "application/pdf");
        intent.setDataAndType(FileProvider.getUriForFile(context,
                context.getApplicationContext().getPackageName() + ".provider",
                pdfFile), "application/pdf");
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        Intent intent1 = Intent.createChooser(intent, "Open With");
        try {
            context.startActivity(intent1);
            new OfflineDBHelper(context).add(OfflineDBHelper.LAST, this);
        } catch (ActivityNotFoundException ignored) {
        }
        return true;
    }

    public void download(Context context, boolean isOpen) {
        new DownloadsManager(context, this, !isOpen)
                .execute();
    }

    public void deleteDialog(final Context context) {
        final Dialog dDelete = new Dialog(context);
        dDelete.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dDelete.setContentView(R.layout.dialog_download);
        dDelete.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        final TextView tvNotice = dDelete.findViewById(R.id.tvNotice);
        final ProgressBar pbLoading = dDelete.findViewById(R.id.pbLoading);
        pbLoading.setVisibility(View.GONE);
        final Button bCancel = dDelete.findViewById(R.id.bCancel);
        final Button bConfirm = dDelete.findViewById(R.id.bOpen);

        tvNotice.setText(R.string.delete_notice);
        bCancel.setText(R.string.no);
        bConfirm.setText(R.string.yes);

        dDelete.setCanceledOnTouchOutside(false);
        dDelete.show();

        bCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dDelete.dismiss();
            }
        });

        bConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvNotice.setText(delete() ? R.string.delete_success : R.string.delete_fail);
                bCancel.setText(R.string.back);
                bConfirm.setVisibility(View.GONE);
            }
        });
    }

    public boolean delete() {
        return getFile().delete();
    }

    public int isAvailable(Context context) {
        File file = new File(getDirectoryPath(context), getId()+".pdf");
        File cacheFile = new File(context.getCacheDir(), file.toString());
        File dataFile = new File(context.getFilesDir(), file.toString());
        if(dataFile.exists())
            return AppHelper.Storage.Data;
        else if(cacheFile.exists())
            return AppHelper.Storage.Cache;
        else
            return AppHelper.Storage.None;
    }

    private File getFile() {
        return new File(App.getContext().getFilesDir(),
                getDirectoryPath(App.getContext()) + "/" +
                        getId() + ".pdf");
    }
}
