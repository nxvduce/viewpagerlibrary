package com.gallery.photo.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;


import com.gallery.photo.Model.AlbumItem;
import com.gallery.photo.Model.MediaItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ImageUtils {
    public static List<AlbumItem> loadAlbums(Context context) {
        List<AlbumItem> albums = new ArrayList<>();
        HashMap<String, AlbumItem> albumMap = new HashMap<>();

        Uri uri = MediaStore.Files.getContentUri("external");
        String[] projection = {
                MediaStore.Files.FileColumns.BUCKET_ID,
                MediaStore.Files.FileColumns.BUCKET_DISPLAY_NAME,
                MediaStore.Files.FileColumns.DATA,
                MediaStore.Files.FileColumns.MEDIA_TYPE
        };

        String selection = MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE + " OR "
                + MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;

        Cursor cursor = context.getContentResolver().query(
                uri,
                projection,
                selection,
                null,
                MediaStore.Files.FileColumns.DATE_ADDED + " DESC"
        );

        if (cursor != null) {
            int bucketIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.BUCKET_ID);
            int bucketNameColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.BUCKET_DISPLAY_NAME);
            int dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA);
            int typeColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MEDIA_TYPE);

            while (cursor.moveToNext()) {
                String bucketId = cursor.getString(bucketIdColumn);
                String bucketName = cursor.getString(bucketNameColumn);
                String path = cursor.getString(dataColumn);
                int mediaType = cursor.getInt(typeColumn);

                boolean isVideo = (mediaType == MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO);

                if (!albumMap.containsKey(bucketId)) {
                    albumMap.put(bucketId, new AlbumItem(bucketName, path, 1, isVideo));
                } else {
                    AlbumItem existing = albumMap.get(bucketId);
                    albumMap.put(bucketId, new AlbumItem(
                            existing.getAlbumName(),
                            existing.getThumbnailPath(),
                            existing.getItemCount() + 1,
                            existing.hasVideo() || isVideo
                    ));
                }
            }
            cursor.close();
            albums.addAll(albumMap.values());
        }
        return albums;
    }

    public static List<MediaItem> loadMediaFromAlbum(Context context, String albumName) {
        List<MediaItem> mediaItems = new ArrayList<>();

        if (albumName == null) {
            return mediaItems; // 👈 return empty list instead of crashing
        }

        Uri uri = MediaStore.Files.getContentUri("external");
        String[] projection = {
                MediaStore.Files.FileColumns.DATA,
                MediaStore.Files.FileColumns.BUCKET_DISPLAY_NAME,
                MediaStore.Files.FileColumns.MEDIA_TYPE
        };

        String selection = MediaStore.Files.FileColumns.BUCKET_DISPLAY_NAME + "=?";
        String[] selectionArgs = new String[]{albumName};

        Cursor cursor = context.getContentResolver().query(
                uri,
                projection,
                selection,
                selectionArgs,
                MediaStore.Files.FileColumns.DATE_ADDED + " DESC"
        );

        if (cursor != null) {
            int dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA);
            int typeColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MEDIA_TYPE);

            while (cursor.moveToNext()) {
                String path = cursor.getString(dataColumn);
                int mediaType = cursor.getInt(typeColumn);
                boolean isVideo = (mediaType == MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO);
                mediaItems.add(new MediaItem(path, isVideo));
            }
            cursor.close();
        }

        return mediaItems;
    }




}
