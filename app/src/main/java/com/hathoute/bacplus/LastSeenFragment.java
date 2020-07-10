package com.hathoute.bacplus;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class LastSeenFragment extends Fragment {
    Context mContext;
    RecyclerView rvLastSeen;
    List<Object> objectList;
    RVItemsAdapter mAdapter;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lastseen, container, false);

        ((OfflineDocsActivity)getActivity()).setActionBarTitle(R.string.nav_last_seen);

        rvLastSeen = view.findViewById(R.id.rvLastSeen);
        setupRecyclerView();
        prepareObjects();

        setHasOptionsMenu(true);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.actbar_items, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.mDelete:
                DialogHelper dialogHelper = new DialogHelper(mContext,
                        R.string.delete_history, new DialogHelper.OnChoiceListener() {
                    @Override
                    public void onChoice(boolean accepted) {
                        if(!accepted)
                            return;

                        new OfflineDBHelper(mContext).clear(OfflineDBHelper.LAST);
                        clearObjects();
                    }
                });
                dialogHelper.show();
                return true;

            case R.id.mRefresh:
                prepareObjects();
                return true;
        }
        return false;
    }

    private void setupRecyclerView() {
        rvLastSeen.setHasFixedSize(true);
        objectList = new ArrayList<>();
        mAdapter = new RVItemsAdapter(objectList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        rvLastSeen.setLayoutManager(mLayoutManager);
        rvLastSeen.setItemAnimator(new DefaultItemAnimator());
        rvLastSeen.setAdapter(mAdapter);
    }

    private void prepareObjects() {
        objectList.clear();
        Cursor cursor = new OfflineDBHelper(mContext).getLastSeen();
        if(cursor.moveToFirst()) {
            do {
                int Type = cursor.getInt(1);
                int Subject = cursor.getInt(2);
                int Id = cursor.getInt(3);
                Object object;
                if(Type == OfflineDBHelper.TYPE_LESSON) {
                    object = new BacDataDBHelper(mContext).getLesson(Subject, Id);
                }
                else {
                    object = new BacDataDBHelper(mContext).getExam(Subject, Id);
                }
                objectList.add(object);
            } while (cursor.moveToNext());
            mAdapter.notifyDataSetChanged();
        }
        else {
            objectList.add(R.drawable.icon_emptylist);
            mAdapter.notifyDataSetChanged();
        }
    }

    private void clearObjects() {
        int size = objectList.size();
        objectList.clear();
        mAdapter.notifyItemRangeRemoved(0, size);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
