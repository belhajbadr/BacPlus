package com.hathoute.bacplus;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class ExamsAdapter extends BaseAdapter {

    private Context mContext;
    private List<Exam> exams;

    private static class ViewHolder {
        TextView tvExamType;
        TextView tvExamId;
        TextView tvRegion;
    }

    public ExamsAdapter(Context context, Subject subject) {
        this.mContext = context;
        this.exams = subject.getExams();
        // Old way to class files, future is now nibba.
        /*for(int i = 0; i < this.exams.size(); i++) {
            if(this.exams.get(i).getType() == Exam.Types.RATTRAPAGE)
                continue;

            int j = 0;
            while(j < i) {
                if(this.exams.get(j).getExamId() < this.exams.get(i).getExamId()
                        || (this.exams.get(j).getExamId() <= this.exams.get(i).getExamId() && this.exams.get(j).getType() > Exam.Types.RATTRAPAGE)) {
                    Exam exam = this.exams.remove(j);
                    this.exams.add(exam);
                    i--;
                    continue;
                }
                j++;
            }
        }*/
    }

    @Override
    public int getCount() {
        return exams.size();
    }

    @Override
    public Exam getItem(int position) {
        return exams.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Exam exam = getItem(position);

        ExamsAdapter.ViewHolder viewHolder;

        if (convertView == null) {

            viewHolder = new ExamsAdapter.ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.exam_item_arabic, parent, false);
            viewHolder.tvExamType = convertView.findViewById(R.id.examType);
            viewHolder.tvExamId = convertView.findViewById(R.id.examId);
            viewHolder.tvRegion = convertView.findViewById(R.id.examRegion);

            convertView.setTag(viewHolder);
        }
        else
            viewHolder = (ExamsAdapter.ViewHolder) convertView.getTag();

        String ExamId;
        String[] Type = mContext.getResources().getStringArray(R.array.exam_sessions);
        switch(exam.getType()) {
            case Exam.Types.CLASS:
                ExamId = mContext.getResources()
                        .getString(R.string.exam_surveille)
                        .replace("$", "<b>" + (exam.getExamId()) + "</b>");
                break;
            case Exam.Types.RATTRAPAGE:
                ExamId = mContext.getResources()
                        .getString(R.string.exam_session)
                        .replace("$", "<b>" + (exam.getExamId())
                                + " " + Type[1] + "</b>");
                break;
            default: //Guaranteed to be Exam.Types.NORMAL, just for Initialization.
                ExamId = mContext.getResources()
                        .getString(R.string.exam_session)
                        .replace("$", "<b>" + (exam.getExamId())
                                + " " + Type[0] + "</b>");
                break;
        }
        viewHolder.tvExamId.setText(Html.fromHtml(ExamId));
        int arrayid;
        if(exam.getType() == Exam.Types.CLASS)
            arrayid = 2;
        else if(exam.getYear() == MainActivity.YEAR_FIRST)
            arrayid = 1;
        else
            arrayid = 0;
        viewHolder.tvExamType.setText(mContext.getResources()
                .getStringArray(R.array.exam_types)[arrayid]);

        int iRegion = exam.getRegionId();
        if(iRegion == -1) {
            viewHolder.tvRegion.setVisibility(View.GONE);
        }
        else {
            String mRegion = mContext.getResources().getStringArray(R.array.regions_names)[iRegion];
            viewHolder.tvRegion.setText(Html.fromHtml(
                    mContext.getResources().getString(R.string.exam_region)
                            .replace("$", "<b>" + mRegion + "</b>")));
        }
        // Return the completed view to render on screen
        return convertView;
    }
}
