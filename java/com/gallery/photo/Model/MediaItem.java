package com.gallery.photo.Model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class MediaItem implements Parcelable {
    private String path;
    private boolean isVideo;

    public MediaItem(String path, boolean isVideo) {
        this.path = path;
        this.isVideo = isVideo;
    }

    protected MediaItem(Parcel in) {
        path = in.readString();
        isVideo = in.readByte() != 0;
    }

    public static final Creator<MediaItem> CREATOR = new Creator<MediaItem>() {
        @Override
        public MediaItem createFromParcel(Parcel in) {
            return new MediaItem(in);
        }
        @Override
        public MediaItem[] newArray(int size) {
            return new MediaItem[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(path);
        dest.writeByte((byte) (isVideo ? 1 : 0));
    }

    @Override
    public int describeContents() { return 0; }

    public String getPath() { return path; }
    public boolean isVideo() { return isVideo; }
}

