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

public class ThumbnailAdapter extends RecyclerView.Adapter<ThumbnailAdapter.ViewHolder> {

    private final Context context;
    private final List<MediaItem> mediaItems;
    private final OnThumbnailClickListener listener;
    private int selectedPosition = -1;

    public interface OnThumbnailClickListener {
        void onThumbnailClick(int position);
    }

    public ThumbnailAdapter(Context context, List<MediaItem> mediaItems, OnThumbnailClickListener listener) {
        this.context = context;
        this.mediaItems = mediaItems;
        this.listener = listener;
    }

    public void setSelected(int position) {
        int old = selectedPosition;
        selectedPosition = position;
        if (old >= 0) notifyItemChanged(old);
        notifyItemChanged(position);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_thumbnail, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MediaItem item = mediaItems.get(position);

        // Load thumbnail
        Glide.with(context)
                .load(item.getPath())
                .centerCrop()
                .into(holder.thumbnail);

        // Show play icon if video
        holder.playIcon.setVisibility(item.isVideo() ? View.VISIBLE : View.GONE);

        // Highlight selected thumbnail
        holder.thumbnail.setBackgroundResource(
                position == selectedPosition ? R.drawable.thumbnail_selected : 0
        );

        // Click listener
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onThumbnailClick(position);
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
