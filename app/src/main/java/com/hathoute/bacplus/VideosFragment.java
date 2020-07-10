package com.hathoute.bacplus;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class VideosFragment extends Fragment {

    private Context mContext;
    ListView lvVideos;
    Subject subject;
    boolean rowsEnd;
    boolean isLoading;

    private static final int NOTICE_DISCONNECTED = 0;
    private static final int NOTICE_NET_PROBLEM = 1;
    private static final int NOTICE_NOTHING_FOUND = 2;

    private static final int[][] notice_items = {
            {R.drawable.icon_disconnected, R.string.notice_disconnected},
            {R.drawable.icon_networkproblem, R.string.notice_networkproblem},
            {R.drawable.icon_nothingfound, R.string.notice_empty_videoslist}
    };

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_videos, container, false);
        int iChosenYear = getArguments().getInt("year");
        int iChosenOption = getArguments().getInt("option");
        int iChosenSubject = getArguments().getInt("subject");
        subject = new Subject(mContext, iChosenYear, iChosenOption, iChosenSubject);
        lvVideos = view.findViewById(R.id.lvVideos);
        final VideosAdapter adapter = new VideosAdapter(new ArrayList<Video>(), mContext, subject);
        lvVideos.setAdapter(adapter);
        new configureData().execute();
        lvVideos.setOnScrollListener(new AbsListView.OnScrollListener() {

            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {

                if(firstVisibleItem+visibleItemCount == totalItemCount && totalItemCount!=0)
                {
                    if(!isLoading && !rowsEnd)
                    {
                        isLoading = true;
                        new configureData().execute();
                    }
                }
            }
        });
        lvVideos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AppHelper.watchYoutubeVideo(mContext, adapter.getItem(position).getYTId());
            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(!AppHelper.isNetworkAvailable(mContext)) {
                    setNotice(NOTICE_DISCONNECTED);
                }
                else if(subject.getVideos().size() == 0) {
                    setNotice(NOTICE_NOTHING_FOUND);
                }
                else {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if(adapter.getCount() == 0) {
                                setNotice(NOTICE_NET_PROBLEM);
                            }
                        }
                    }, 10000);
                }
            }
        }, 500);

        return view;
    }

    private void setNotice(int Type) {
        if(getView() == null)
            return;

        final TextView tvNotice = getView().findViewById(R.id.tvNotice);
        final ImageView ivNotice = getView().findViewById(R.id.ivNotice);

        ivNotice.setImageDrawable(mContext.getResources().getDrawable(notice_items[Type][0]));
        tvNotice.setText(notice_items[Type][1]);

        lvVideos.setVisibility(View.INVISIBLE);
    }

    private void removeNotice() {
        if(getView() == null)
            return;

        RelativeLayout noticeContainer = getView().findViewById(R.id.noticeContainer);
        if(noticeContainer.getVisibility() == View.VISIBLE) {
            lvVideos.setVisibility(View.VISIBLE);
            noticeContainer.setVisibility(View.GONE);
        }
    }

    private int curRow = 0;

    class configureData extends AsyncTask<Void, Video, Boolean> {
        private VideosAdapter videosAdapter;
        List<Video> videos;
        int lastRow = curRow;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            videos = subject.getVideos();
            videosAdapter = (VideosAdapter) lvVideos.getAdapter();
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            while(curRow < lastRow+10) {
                try {
                    Video video = videos.get(curRow);
                    video.generateData();
                    publishProgress(video);
                } catch (IndexOutOfBoundsException ignored) {
                    return true;
                } catch (Exception ignored) {
                    videos.remove(curRow);
                    lastRow--;
                    continue;
                }
                curRow++;
            }
            return false;
        }

        @Override
        protected void onProgressUpdate(Video... videos) {
            videosAdapter.add(videos[0]);
            removeNotice();
        }

        @Override
        protected void onPostExecute(Boolean result) {
            rowsEnd = result;
            isLoading = false;
        }
    }

}
