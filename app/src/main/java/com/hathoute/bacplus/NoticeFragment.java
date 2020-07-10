package com.hathoute.bacplus;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

public class NoticeFragment extends Fragment {
    Context mContext;
    TextView tvNotice;
    TextView tvLoaded;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_offline_notice, container, false);

        ((OfflineDocsActivity)getActivity()).setActionBarTitle(R.string.choice_offline);

        int iAvailable = getArguments().getInt("available");
        tvNotice = view.findViewById(R.id.tvNotice);
        tvLoaded = view.findViewById(R.id.tvLoaded);

        setupViews(iAvailable);
        return view;
    }

    private void setupViews(int count) {
        tvLoaded.setText(getResources().getString(R.string.offline_available)
                .replace("$", String.valueOf(count)));
        tvNotice.setText(Html.fromHtml(getResources().getString(R.string.offline_notice)
                .replace("$", "<b>").replace("£", "</b>")));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
