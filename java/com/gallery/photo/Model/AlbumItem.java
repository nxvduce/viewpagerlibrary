package com.gallery.photo.Model;

public class AlbumItem {
    private String albumName;
    private String thumbnailPath;
    private int itemCount;
    private boolean hasVideo; // 👈 new field

    public AlbumItem(String albumName, String thumbnailPath, int itemCount, boolean hasVideo) {
        this.albumName = albumName;
        this.thumbnailPath = thumbnailPath;
        this.itemCount = itemCount;
        this.hasVideo = hasVideo;
    }

    public String getAlbumName() { return albumName; }
    public String getThumbnailPath() { return thumbnailPath; }
    public int getItemCount() { return itemCount; }
    public boolean hasVideo() { return hasVideo; }
}
