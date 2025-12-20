package com.gallery.photo.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

public class FavoriteUtils {
    private static final String PREF_NAME = "favorites_pref";
    private static final String KEY_FAVORITES = "favorites";

    public static void addFavorite(Context context, String path) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        Set<String> favs = new HashSet<>(prefs.getStringSet(KEY_FAVORITES, new HashSet<>()));
        favs.add(path);
        prefs.edit().putStringSet(KEY_FAVORITES, favs).apply();
    }

    public static void removeFavorite(Context context, String path) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        Set<String> favs = new HashSet<>(prefs.getStringSet(KEY_FAVORITES, new HashSet<>()));
        favs.remove(path);
        prefs.edit().putStringSet(KEY_FAVORITES, favs).apply();
    }

    public static boolean isFavorite(Context context, String path) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        Set<String> favs = prefs.getStringSet(KEY_FAVORITES, new HashSet<>());
        return favs.contains(path);
    }

    public static Set<String> getAllFavorites(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getStringSet(KEY_FAVORITES, new HashSet<>());
    }
}
