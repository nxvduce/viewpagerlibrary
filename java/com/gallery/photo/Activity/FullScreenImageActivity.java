package com.gallery.photo.Activity;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.gallery.photo.Adapter.FullScreenImageAdapter;
import com.gallery.photo.Adapter.ThumbnailAdapter;
import com.gallery.photo.Model.MediaItem;
import com.gallery.photo.R;
import com.gallery.photo.Utils.FavoriteUtils;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.text.SimpleDateFormat;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

public class FullScreenImageActivity extends AppCompatActivity {

    public static final String EXTRA_MEDIA_LIST = "media_list";
    public static final String EXTRA_MEDIA_POSITION = "media_position";

    private ViewPager2 viewPager;
    private RecyclerView thumbnailRecyclerView;
    private FullScreenImageAdapter pagerAdapter;
    private ThumbnailAdapter thumbnailAdapter;

    // Bottom bar buttons
    private ImageView favBtn, editBtn, shareBtn, deleteBtn, moreBtn;

    private ArrayList<MediaItem> mediaItems;
    private int currentPosition;
    LinearLayout bottom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen_image);

        // Bind bottom bar buttons
        favBtn = findViewById(R.id.fav);
        editBtn = findViewById(R.id.edit);
        shareBtn = findViewById(R.id.share);
        deleteBtn = findViewById(R.id.delete);
        moreBtn = findViewById(R.id.more);
        bottom = findViewById(R.id.bottom);

        viewPager = findViewById(R.id.viewPager);
        thumbnailRecyclerView = findViewById(R.id.thumbnailRecyclerView);

        mediaItems = getIntent().getParcelableArrayListExtra(EXTRA_MEDIA_LIST);
        currentPosition = getIntent().getIntExtra(EXTRA_MEDIA_POSITION, 0);

        pagerAdapter = new FullScreenImageAdapter(this, mediaItems);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(currentPosition, false);

        thumbnailAdapter = new ThumbnailAdapter(this, mediaItems, position -> {
            viewPager.setCurrentItem(position, true);
        });

        thumbnailRecyclerView.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        thumbnailRecyclerView.setAdapter(thumbnailAdapter);

        // Initialize fav icon for the starting item
        MediaItem startItem = mediaItems.get(currentPosition);
        if (FavoriteUtils.isFavorite(this, startItem.getPath())) {
            favBtn.setImageResource(R.drawable.ic_fillfavorite);
        } else {
            favBtn.setImageResource(R.drawable.ic_favorite);
        }

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                currentPosition = position;
                thumbnailAdapter.setSelected(position);
                thumbnailRecyclerView.smoothScrollToPosition(position);

                MediaItem item = mediaItems.get(position);
                if (FavoriteUtils.isFavorite(FullScreenImageActivity.this, item.getPath())) {
                    favBtn.setImageResource(R.drawable.ic_fillfavorite); // filled
                } else {
                    favBtn.setImageResource(R.drawable.ic_favorite); // outline
                }
            }
        });


        setupBottomBar();
    }

    private void setupBottomBar() {
        favBtn.setOnClickListener(v -> {
            MediaItem item = mediaItems.get(currentPosition);
            if (FavoriteUtils.isFavorite(this, item.getPath())) {
                // Remove from favorites
                FavoriteUtils.removeFavorite(this, item.getPath());
                favBtn.setImageResource(R.drawable.ic_favorite); // outline heart
                Toast.makeText(this, "Removed from Favorites", Toast.LENGTH_SHORT).show();
            } else {
                // Add to favorites
                FavoriteUtils.addFavorite(this, item.getPath());
                favBtn.setImageResource(R.drawable.ic_fillfavorite); // filled heart
                Toast.makeText(this, "Added to Favorites", Toast.LENGTH_SHORT).show();
            }
        });

        editBtn.setOnClickListener(v -> {
            MediaItem item = mediaItems.get(currentPosition);
            Intent intent = new Intent(Intent.ACTION_EDIT);
            intent.setDataAndType(Uri.fromFile(new File(item.getPath())), "image/*");
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            try {
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(this, "No editor found!", Toast.LENGTH_SHORT).show();
            }
        });

        shareBtn.setOnClickListener(v -> {
            MediaItem item = mediaItems.get(currentPosition);
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType(item.isVideo() ? "video/*" : "image/*");
            shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(item.getPath())));
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(Intent.createChooser(shareIntent, "Share via"));
        });

        deleteBtn.setOnClickListener(v -> {
            MediaItem item = mediaItems.get(currentPosition);
            File file = new File(item.getPath());
            if (file.exists() && file.delete()) {
                Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show();
                mediaItems.remove(currentPosition);
                pagerAdapter.notifyItemRemoved(currentPosition);
                thumbnailAdapter.notifyItemRemoved(currentPosition);
            } else {
                Toast.makeText(this, "Delete failed", Toast.LENGTH_SHORT).show();
            }
        });

        moreBtn.setOnClickListener(v -> {
            showMorePopupAbove(moreBtn);
        });


    }

    private void showMoreDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_more_options);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        MediaItem currentItem = mediaItems.get(currentPosition);
        String path = currentItem.getPath();

        // Bind options
        dialog.findViewById(R.id.option_details).setOnClickListener(v -> {
            Toast.makeText(this, "Path: " + path, Toast.LENGTH_LONG).show();
            dialog.dismiss();
        });

        dialog.findViewById(R.id.option_portrait).setOnClickListener(v -> {
            Toast.makeText(this, "Portrait effect coming soon", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        dialog.findViewById(R.id.option_copy).setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Media Path", path);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(this, "Copied to clipboard", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        dialog.findViewById(R.id.option_wallpaper).setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_ATTACH_DATA);
            intent.setDataAndType(Uri.fromFile(new File(path)), "image/*");
            intent.putExtra("mimeType", "image/*");
            startActivity(Intent.createChooser(intent, "Set as Wallpaper"));
            dialog.dismiss();
        });

        dialog.findViewById(R.id.option_print).setOnClickListener(v -> {
            Toast.makeText(this, "Print feature coming soon", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        dialog.show();
    }

    private void showMorePopupAbove(ImageView anchorView) {

        View popupView = LayoutInflater.from(this).inflate(R.layout.dialog_more_options, null);
        PopupWindow popupWindow = new PopupWindow(popupView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true);

        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.setElevation(10f);

        // Get location of bottom layout
        LinearLayout bottomLayout = findViewById(R.id.bottom);
        int[] location = new int[2];
        bottomLayout.getLocationOnScreen(location);
        int anchorX = location[0];
        int anchorY = location[1];

        // Measure popup height/width before showing
        popupView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        int popupHeight = popupView.getMeasuredHeight();
        int popupWidth = popupView.getMeasuredWidth();

        // 👉 Align to right side above bottom layout
        int xPos = anchorX + bottomLayout.getWidth() - popupWidth - 50; // 16px padding from right
        int yPos = anchorY - popupHeight - 50; // 10px above bottom bar

        popupWindow.showAtLocation(bottomLayout, Gravity.NO_GRAVITY, xPos, yPos);


        // Bind actions
        popupView.findViewById(R.id.option_details).setOnClickListener(v -> {
            MediaItem item = mediaItems.get(currentPosition);
            showDetailsPanel(item);
            popupWindow.dismiss();
        });

        popupView.findViewById(R.id.option_portrait).setOnClickListener(v -> {
            Toast.makeText(this, "Portrait effect coming soon", Toast.LENGTH_SHORT).show();
            popupWindow.dismiss();
        });

        popupView.findViewById(R.id.option_copy).setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            clipboard.setPrimaryClip(ClipData.newPlainText("Media Path", mediaItems.get(currentPosition).getPath()));
            Toast.makeText(this, "Copied to clipboard", Toast.LENGTH_SHORT).show();
            popupWindow.dismiss();
        });

        popupView.findViewById(R.id.option_wallpaper).setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_ATTACH_DATA);
            intent.setDataAndType(Uri.fromFile(new File(mediaItems.get(currentPosition).getPath())), "image/*");
            intent.putExtra("mimeType", "image/*");
            startActivity(Intent.createChooser(intent, "Set as Wallpaper"));
            popupWindow.dismiss();
        });

        popupView.findViewById(R.id.option_print).setOnClickListener(v -> {
            Toast.makeText(this, "Print feature coming soon", Toast.LENGTH_SHORT).show();
            popupWindow.dismiss();
        });
    }

    private void showDetailsPanel(MediaItem item) {
        bottom.setVisibility(View.GONE);
        thumbnailRecyclerView.setVisibility(View.GONE);
        LinearLayout detailsPanel = findViewById(R.id.detailsPanel);
        TextView fileName = findViewById(R.id.fileName);
        TextView fileSize = findViewById(R.id.fileSize);
        TextView fileResolution = findViewById(R.id.fileResolution);
        TextView fileDate = findViewById(R.id.fileDate);

        File file = new File(item.getPath());
        fileName.setText("Name: " + file.getName());
        fileSize.setText("Size: " + (file.length() / 1024) + " KB");

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(item.getPath(), options);
        fileResolution.setText("Resolution: " + options.outWidth + " x " + options.outHeight);

        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault());
        fileDate.setText("Date: " + sdf.format(file.lastModified()));

        detailsPanel.setVisibility(View.VISIBLE);
        detailsPanel.animate().translationY(0).setDuration(300).start();

        // Shrink image area slightly
        viewPager.animate().scaleX(0.9f).scaleY(0.9f).setDuration(300).start();

        // 👉 Disable swipe + hide thumbnails
        viewPager.setUserInputEnabled(false);

        detailsPanel.setVisibility(View.VISIBLE);
        detailsPanel.animate().translationY(0).setDuration(300).start();

        viewPager.animate().scaleX(0.9f).scaleY(0.9f).setDuration(300).start();
        viewPager.setUserInputEnabled(false);
        thumbnailRecyclerView.setVisibility(View.GONE);

        enableSwipeToCloseDetails(); // 👈 add gesture

    }

    private void hideDetailsPanel() {
        LinearLayout detailsPanel = findViewById(R.id.detailsPanel);
        RecyclerView thumbnailRecyclerView = findViewById(R.id.thumbnailRecyclerView);

        detailsPanel.animate().translationY(detailsPanel.getHeight()).setDuration(300)
                .withEndAction(() -> detailsPanel.setVisibility(View.GONE));

        viewPager.animate().scaleX(1f).scaleY(1f).setDuration(300).start();

        // 👉 Enable swipe + show thumbnails again
        viewPager.setUserInputEnabled(true);
        thumbnailRecyclerView.setVisibility(View.VISIBLE);
        bottom.setVisibility(View.VISIBLE);
    }

    private void enableSwipeToCloseDetails() {
        LinearLayout detailsPanel = findViewById(R.id.detailsPanel);

        detailsPanel.setOnTouchListener(new View.OnTouchListener() {
            float startY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startY = event.getY();
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        float deltaY = event.getY() - startY;
                        if (deltaY > 0) { // dragging down
                            detailsPanel.setTranslationY(deltaY);
                        }
                        return true;

                    case MotionEvent.ACTION_UP:
                        float totalDeltaY = event.getY() - startY;
                        if (totalDeltaY > 150) { // threshold to close
                            hideDetailsPanel(); // close panel
                        } else {
                            // snap back if not enough swipe
                            detailsPanel.animate().translationY(0).setDuration(200).start();
                        }
                        return true;
                }
                return false;
            }
        });
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        hideDetailsPanel();
    }
}
