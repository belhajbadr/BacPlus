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

import java.util.List;

public class ExamsFragment extends Fragment {

    private Context mContext;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_exams, container, false);
        final int iChosenYear = getArguments().getInt("year");
        final int iChosenOption = getArguments().getInt("option");
        final int iChosenSubject = getArguments().getInt("subject");
        Subject subject = new Subject(mContext, iChosenYear, iChosenOption, iChosenSubject);
        ListView lvExams = view.findViewById(R.id.lvExams);
        final ExamsAdapter adapter = new ExamsAdapter(mContext, subject);
        lvExams.setAdapter(adapter);
        lvExams.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(mContext, ExamActivity.class);
                intent.putExtra("subject", iChosenSubject)
                        .putExtra("exam", adapter.getItem(position).getId());
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
