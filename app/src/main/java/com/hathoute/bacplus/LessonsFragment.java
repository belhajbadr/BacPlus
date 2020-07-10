package com.hathoute.bacplus;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

public class LessonsFragment extends Fragment {

    private Context mContext;
    private int iChosenYear;
    private int iChosenOption;
    private int iChosenSubject;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lessons, container, false);
        iChosenYear = getArguments().getInt("year");
        iChosenOption = getArguments().getInt("option");
        iChosenSubject = getArguments().getInt("subject");
        Subject subject = new Subject(mContext, iChosenYear, iChosenOption, iChosenSubject);
        ListView lvLessons = view.findViewById(R.id.lvLessons);
        final LessonsAdapter adapter = new LessonsAdapter(mContext, subject);
        lvLessons.setAdapter(adapter);
        lvLessons.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(mContext, LessonActivity.class);
                intent.putExtra("subject", iChosenSubject)
                        .putExtra("lesson", adapter.getItem(position).getId());
                startActivity(intent);
            }
        });

        return view;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }
}
