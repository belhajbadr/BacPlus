package com.hathoute.bacplus;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import java.net.URL;

public class Video {

    private Subject Subject;
    private String YTId;
    private Bitmap Thumbnail;
    private String Title;
    private String Channel;

    public Video(Subject Subject, String YTId) {
        this.Subject = Subject;
        this.YTId = YTId;
    }

    public String getLink() {
        return "https://www.youtube.com/watch?v=" + YTId;
    }

    public String getTitle() {
        return this.Title;
    }

    public void generateData() throws Exception {
        try {
            this.Thumbnail = AppHelper.getBitmapFromURL("http://img.youtube.com/vi/" + YTId + "/hqdefault.jpg");
            this.Thumbnail = Bitmap.createBitmap(Thumbnail, 0, 45, 480, 270);
        } catch (Exception ignored) {
        }

        URL embededURL = new URL("http://www.youtube.com/oembed?url=" +
                "https://www.youtube.com/watch?v=" +
                YTId + "&format=json"
        );
        this.Title = new JSONObject(IOUtils.toString(embededURL)).getString("title");
        this.Channel = new JSONObject(IOUtils.toString(embededURL)).getString("author_name");
    }

    public Bitmap getThumbnail() {
        //Todo: Remove comment.
        /*if(Thumbnail == null)
            Thumbnail = BitmapFactory.decodeResource(mContext.getResources()
                    .getDrawable(R.drawable.default_thumbnail));*/
        return this.Thumbnail;
    }

    public String getChannel() {
        return this.Channel;
    }

    public String getYTId() {
        return this.YTId;
    }

    public int getYear() {
        return this.Subject.getYear();
    }

    public int getSubject() {
        return this.Subject.getSubject();
    }
}

