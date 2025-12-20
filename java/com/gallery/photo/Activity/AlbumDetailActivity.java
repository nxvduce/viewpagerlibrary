package com.gallery.photo.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gallery.photo.Adapter.MediaAdapter;
import com.gallery.photo.Model.MediaItem;
import com.gallery.photo.R;
import com.gallery.photo.Utils.ImageUtils;

import java.util.ArrayList;
import java.util.List;

public class AlbumDetailActivity extends AppCompatActivity {

    public static final String EXTRA_ALBUM_NAME = "album_name";
    RecyclerView imageRecyclerView;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_detail);

        imageRecyclerView = findViewById(R.id.imageRecyclerView);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        layoutManager.setInitialPrefetchItemCount(10); // 👈 preload 10 items
        imageRecyclerView.setLayoutManager(layoutManager);

//        imageRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));

        String albumName = getIntent().getStringExtra(EXTRA_ALBUM_NAME);

        if (albumName == null || albumName.isEmpty()) {
            Toast.makeText(this, "Album name missing!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        List<MediaItem> mediaItems = ImageUtils.loadMediaFromAlbum(this, albumName);

        MediaAdapter adapter = new MediaAdapter(this, mediaItems, item -> {

            Intent intent = new Intent(this, FullScreenImageActivity.class);
            intent.putParcelableArrayListExtra(
                    FullScreenImageActivity.EXTRA_MEDIA_LIST,
                    new ArrayList<>(mediaItems)
            );
            intent.putExtra(FullScreenImageActivity.EXTRA_MEDIA_POSITION, mediaItems.indexOf(item));
            startActivity(intent);

        });

        imageRecyclerView.setAdapter(adapter);
    }
}
