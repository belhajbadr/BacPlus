package com.hathoute.bacplus;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class VideosAdapter extends ArrayAdapter<Video> {

    private Context mContext;
    private List<Video> videos;

    private static class ViewHolder {
        TextView tvTitle;
        TextView tvChannel;
        ImageView ivThumbnail;
    }

    public VideosAdapter(ArrayList<Video> videos, Context context, Subject subject) {
        super(context, R.layout.video_item_arabic, videos);
        this.mContext = context;
        this.videos = videos;
    }

    @Override
    public int getCount() {
        return videos.size();
    }

    @Override
    public Video getItem(int position) {
        return videos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Video video = getItem(position);

        VideosAdapter.ViewHolder viewHolder;

        if (convertView == null) {

            viewHolder = new VideosAdapter.ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.video_item_arabic, parent, false);
            viewHolder.tvTitle = convertView.findViewById(R.id.tvTitle);
            viewHolder.tvChannel = convertView.findViewById(R.id.tvChannel);
            viewHolder.ivThumbnail = convertView.findViewById(R.id.ivThumbnail);

            convertView.setTag(viewHolder);
        }
        else
            viewHolder = (VideosAdapter.ViewHolder) convertView.getTag();



        viewHolder.tvTitle.setText(video.getTitle());
        viewHolder.tvChannel.setText(Html.fromHtml(mContext.getResources()
                .getString(R.string.video_author).replace("$", "<b>" +
                        video.getChannel() + "</b>")));
        viewHolder.ivThumbnail.setImageBitmap(video.getThumbnail());
        // Return the completed view to render on screen
        return convertView;
    }
}
