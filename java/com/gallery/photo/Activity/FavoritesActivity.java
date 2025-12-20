package com.gallery.photo.Activity;

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
import com.gallery.photo.Utils.FavoriteUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FavoritesActivity extends AppCompatActivity {

    RecyclerView favoritesRecyclerView;
    List<MediaItem> favoriteItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        favoritesRecyclerView = findViewById(R.id.favoritesRecyclerView);
        favoritesRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));

        loadFavorites();

        MediaAdapter adapter = new MediaAdapter(this, favoriteItems, item -> {
            if (item.isVideo()) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse(item.getPath()), "video/*");
                startActivity(intent);
            } else {
                Intent intent = new Intent(this, FullScreenImageActivity.class);
                intent.putParcelableArrayListExtra(
                        FullScreenImageActivity.EXTRA_MEDIA_LIST,
                        new ArrayList<>(favoriteItems)
                );
                intent.putExtra(FullScreenImageActivity.EXTRA_MEDIA_POSITION, favoriteItems.indexOf(item));
                startActivity(intent);
            }
        });

        favoritesRecyclerView.setAdapter(adapter);
    }

    private void loadFavorites() {
        favoriteItems = new ArrayList<>();
        for (String path : FavoriteUtils.getAllFavorites(this)) {
            File file = new File(path);
            if (file.exists()) {
                // Assume only images for now, you can extend to detect videos
                favoriteItems.add(new MediaItem(path, path.endsWith(".mp4") || path.endsWith(".mkv")));
            }
        }

        if (favoriteItems.isEmpty()) {
            Toast.makeText(this, "No favorites yet!", Toast.LENGTH_SHORT).show();
        }
    }
}
