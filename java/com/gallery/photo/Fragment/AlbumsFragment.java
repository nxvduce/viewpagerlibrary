package com.gallery.photo.Fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.core.content.ContextCompat;

import com.gallery.photo.Activity.AlbumDetailActivity;
import com.gallery.photo.Adapter.AlbumAdapter;
import com.gallery.photo.Utils.ImageUtils;
import com.gallery.photo.Model.AlbumItem;
import com.gallery.photo.R;

import java.util.List;

public class AlbumsFragment extends Fragment {

    private static final int REQUEST_STORAGE_PERMISSION = 101;
    private RecyclerView albumRecyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_album, container, false);

        albumRecyclerView = view.findViewById(R.id.albumRecyclerView);

        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        layoutManager.setInitialPrefetchItemCount(10); // 👈 preload 10 items
        albumRecyclerView.setLayoutManager(layoutManager);


//        albumRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
//        albumRecyclerView.setHasFixedSize(true);

        checkAndRequestPermission();

        return view;
    }

    private void checkAndRequestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_MEDIA_IMAGES)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                        new String[]{Manifest.permission.READ_MEDIA_IMAGES},
                        REQUEST_STORAGE_PERMISSION
                );
            } else {
                loadAlbums();
            }
        } else {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_STORAGE_PERMISSION
                );
            } else {
                loadAlbums();
            }
        }
    }

    private void loadAlbums() {
        List<AlbumItem> albumList = ImageUtils.loadAlbums(requireContext());

        AlbumAdapter adapter = new AlbumAdapter(requireContext(), albumList, album -> {
            Intent intent = new Intent(getContext(), AlbumDetailActivity.class);
            intent.putExtra(AlbumDetailActivity.EXTRA_ALBUM_NAME, album.getAlbumName()); // 👈 always pass name
            startActivity(intent);

            Toast.makeText(requireContext(), "Opening " + album.getAlbumName(), Toast.LENGTH_SHORT).show();
        });
        albumRecyclerView.setAdapter(adapter);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_STORAGE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadAlbums();
            } else {
                Toast.makeText(requireContext(), "Permission required to view albums", Toast.LENGTH_LONG).show();
            }
        }
    }
}
