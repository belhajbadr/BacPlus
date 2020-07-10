package com.hathoute.bacplus;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class LessonsAdapter extends BaseAdapter {

    private Context mContext;
    private List<Lesson> lessons;

    private static class ViewHolder {
        TextView tvLessonId;
        TextView tvLessonName;
    }

    public LessonsAdapter(Context context, Subject subject) {
        this.mContext = context;
        this.lessons = subject.getLessons();
    }

    @Override
    public int getCount() {
        return lessons.size();
    }

    @Override
    public Lesson getItem(int position) {
        return lessons.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Lesson lesson = getItem(position);

        ViewHolder viewHolder;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.lesson_item_arabic, parent, false);
            viewHolder.tvLessonId = convertView.findViewById(R.id.lessonId);
            viewHolder.tvLessonName = convertView.findViewById(R.id.lessonName);

            convertView.setTag(viewHolder);
        }
        else
            viewHolder = (ViewHolder) convertView.getTag();

        viewHolder.tvLessonId.setText(Html.fromHtml(mContext.getResources()
                .getString(R.string.lesson_id)
                .replace("$", "<b>" + (position+1) + "</b>")));
        viewHolder.tvLessonName.setText(lesson.getName());
        // Return the completed view to render on screen
        return convertView;
    }
}
