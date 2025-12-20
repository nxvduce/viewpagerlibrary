package com.gallery.photo.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.gallery.photo.Model.MediaItem;
import com.gallery.photo.R;

import java.util.List;

public class FullScreenImageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context context;
    private final List<MediaItem> mediaItems;

    private static final int TYPE_IMAGE = 0;
    private static final int TYPE_VIDEO = 1;

    public FullScreenImageAdapter(Context context, List<MediaItem> mediaItems) {
        this.context = context;
        this.mediaItems = mediaItems;
    }

    @Override
    public int getItemViewType(int position) {
        return mediaItems.get(position).isVideo() ? TYPE_VIDEO : TYPE_IMAGE;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_IMAGE) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_fullscreen_image, parent, false);
            return new ImageViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.item_fullscreen_video, parent, false);
            return new VideoViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MediaItem item = mediaItems.get(position);
        if (holder instanceof ImageViewHolder) {
            Glide.with(context)
                    .load(item.getPath())
                    .into(((ImageViewHolder) holder).photoView);
        } else if (holder instanceof VideoViewHolder) {
            ((VideoViewHolder) holder).videoView.setVideoPath(item.getPath());
            ((VideoViewHolder) holder).videoView.start();
        }
    }

    @Override
    public int getItemCount() {
        return mediaItems.size();
    }

    static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView photoView;
        ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            photoView = itemView.findViewById(R.id.fullscreenImageView);
        }
    }

    static class VideoViewHolder extends RecyclerView.ViewHolder {
        VideoView videoView;
        VideoViewHolder(@NonNull View itemView) {
            super(itemView);
            videoView = itemView.findViewById(R.id.fullscreenVideoView);
        }
    }
}

