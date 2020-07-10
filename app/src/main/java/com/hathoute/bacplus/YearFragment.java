package com.hathoute.bacplus;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class YearFragment extends Fragment implements View.OnClickListener {

    OnCallbackReceived mCallback;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_year, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallback = (OnCallbackReceived) context;
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.bFirst).setOnClickListener(this);
        view.findViewById(R.id.bSecond).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int userYear;
        switch(v.getId()) {
            case R.id.bFirst:
                userYear = MainActivity.YEAR_FIRST;
                break;

            case R.id.bSecond:
                userYear = MainActivity.YEAR_SECOND;
                break;

            default:
                return;
        }

        mCallback.onYearChoice(userYear);
    }

    public interface OnCallbackReceived {
        public void onYearChoice(int Year);
    }
}
