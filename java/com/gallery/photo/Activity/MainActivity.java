package com.gallery.photo.Activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.gallery.photo.Fragment.AlbumsFragment;
import com.gallery.photo.Fragment.PicturesFragment;
import com.gallery.photo.Fragment.StoriesFragment;
import com.gallery.photo.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNav = findViewById(R.id.bottomNav);

        // 👇 Force proper label visibility

        // 👇 Load AlbumsFragment by default
        loadFragment(new AlbumsFragment());
        bottomNav.setSelectedItemId(R.id.nav_albums);

        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selected = null;
            int id = item.getItemId();

            if (id == R.id.nav_pictures) {
                selected = new PicturesFragment();
            } else if (id == R.id.nav_albums) {
                selected = new AlbumsFragment();
            } else if (id == R.id.nav_stories) {
                selected = new StoriesFragment();
            }

            if (selected != null) {
                loadFragment(selected);
            }
            return true;
        });
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }
}
