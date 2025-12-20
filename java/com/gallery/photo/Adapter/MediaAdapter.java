package com.gallery.photo.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.gallery.photo.Model.MediaItem;
import com.gallery.photo.R;

import java.util.List;

public class MediaAdapter extends RecyclerView.Adapter<MediaAdapter.ViewHolder> {

    private final Context context;
    private final List<MediaItem> mediaItems;
    private final OnMediaClickListener listener;

    public interface OnMediaClickListener {
        void onMediaClick(MediaItem item);
    }

    public MediaAdapter(Context context, List<MediaItem> mediaItems, OnMediaClickListener listener) {
        this.context = context;
        this.mediaItems = mediaItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_media, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MediaItem item = mediaItems.get(position);

        Glide.with(context)
                .load(item.getPath())
                .centerCrop()
                .placeholder(R.drawable.placeholder)
                .into(holder.thumbnail);

        holder.playIcon.setVisibility(item.isVideo() ? View.VISIBLE : View.GONE);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onMediaClick(item);
        });
    }

    @Override
    public int getItemCount() {
        return mediaItems.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView thumbnail, playIcon;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            thumbnail = itemView.findViewById(R.id.thumbnail);
            playIcon = itemView.findViewById(R.id.playIcon);
        }
    }
}
