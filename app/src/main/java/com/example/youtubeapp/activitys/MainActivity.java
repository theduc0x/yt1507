package com.example.youtubeapp.activitys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.youtubeapp.R;
import com.example.youtubeapp.adapter.CategoryAdapter;
import com.example.youtubeapp.api.ApiServicePlayList;
import com.example.youtubeapp.fragment.ShortsFragment;
import com.example.youtubeapp.fragment.HomeFragment;
import com.example.youtubeapp.fragment.LibraryFragment;
import com.example.youtubeapp.fragment.NotificationFragment;
import com.example.youtubeapp.fragment.SearchFragment;
import com.example.youtubeapp.fragment.SearchResultsFragment;
import com.example.youtubeapp.fragment.SubcriptionFragment;
import com.example.youtubeapp.fragment.VideoContainDataFragment;
import com.example.youtubeapp.model.itemrecycleview.CategoryItem;
import com.example.youtubeapp.model.itemrecycleview.SearchItem;
import com.example.youtubeapp.model.itemrecycleview.VideoItem;
import com.example.youtubeapp.model.listcategory.Category;
import com.example.youtubeapp.model.listcategory.Items;
import com.example.youtubeapp.my_interface.IItemOnClickCategoryListener;
import com.example.youtubeapp.utiliti.Util;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import java.util.ArrayList;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements YouTubePlayer.OnInitializedListener {
    Toolbar tbNav;
    BottomNavigationView bnvFragment;
    HomeFragment homeFragment = new HomeFragment();
    ShortsFragment exploreFragment = new ShortsFragment();
    SubcriptionFragment subcriptionFragment = new SubcriptionFragment();
    NotificationFragment notificationFragment = new NotificationFragment();
    LibraryFragment libraryFragment = new LibraryFragment();
    FrameLayout flContent;
    AppBarLayout ablHome;
    NestedScrollView nsvVideo;

    LinearLayout llResize;
    LinearLayout llVideoPlay;
    TextView tvTitleVideo, tvTitleChannel;
    ImageView ivDelete;
    String titleVideo = "", titleChannel  = "";

    ConstraintLayout clVideoPlay;
    BottomSheetBehavior sheetBehavior;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tbNav = findViewById(R.id.tb_nav);
        tbNav.setVisibility(View.VISIBLE);
        bnvFragment = findViewById(R.id.bnv_fragment);
        bnvFragment.setVisibility(View.VISIBLE);
        flContent = findViewById(R.id.fl_content);
        ablHome = findViewById(R.id.abl_nav);
        nsvVideo = findViewById(R.id.ll_video_play);
        // Bottom sheet để mở video play
        clVideoPlay = findViewById(R.id.cl_video_play);
        sheetBehavior = BottomSheetBehavior.from(clVideoPlay);
        llResize = findViewById(R.id.ll_resize2);
        tvTitleChannel = findViewById(R.id.tv_title_channel_resize);
        tvTitleVideo = findViewById(R.id.tv_title_video_resize);
        ivDelete = findViewById(R.id.iv_delete_resize);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fl_content, homeFragment, Util.TAG_HOME);
        fragmentTransaction.addToBackStack(HomeFragment.TAG);
        fragmentTransaction.commit();
        Log.d("ducaksd", getSupportFragmentManager().getBackStackEntryCount()+"");
        bnvFragment.getMenu().findItem(R.id.mn_home).setChecked(true);
        // Sự kiện click thanh menu bottom
        bnvFragment.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.mn_home:
                        setToolBarMainVisible();
                        if (item.isChecked()) {
                            homeFragment = (HomeFragment) getSupportFragmentManager()
                                    .findFragmentByTag(Util.TAG_HOME);
                            // Nếu lướt quá sâu thì chỉ cần nhấn vào nút home sẽ quay lại ngay lập tức
                            if (homeFragment.rvItemVideo.getAdapter().getItemCount() > 21) {
                                homeFragment.topRecycleViewFast();
                            } else {
                                homeFragment.topRecycleView();
                            }

                        } else {
                            item.setChecked(true);
                            getSupportFragmentManager().popBackStack(HomeFragment.TAG, 0);
                        }
                        break;
                    case R.id.mn_explore:
                        setToolBarMainInvisible();
                        item.setChecked(true);
                        selectFragment(exploreFragment, Util.TAG_SHORTS, ShortsFragment.TAG);
                        Log.d("ducaksd", getSupportFragmentManager().getBackStackEntryCount()+"");
                        break;
                    case R.id.mn_subcription:
                        setToolBarMainVisible();

                        item.setChecked(true);
                        selectFragment(subcriptionFragment, Util.TAG_SUB, SubcriptionFragment.TAG);
                        Log.d("ducaksd", getSupportFragmentManager().getBackStackEntryCount()+"");
                        break;
                    case R.id.mn_notification:
                        setToolBarMainVisible();

                        item.setChecked(true);
                        selectFragment(notificationFragment, Util.TAG_NOTIFI, NotificationFragment.TAG);
                        Log.d("ducaksd", getSupportFragmentManager().getBackStackEntryCount()+"");
                        break;
                    case R.id.mn_library:
                        setToolBarMainVisible();

                        item.setChecked(true);
                        selectFragment(libraryFragment, Util.TAG_LIBRARY, LibraryFragment.TAG);
                        Log.d("ducaksd", getSupportFragmentManager().getBackStackEntryCount()+"");
                        break;
                }
                return false;
            }
        });

        tbNav.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.mn_search:
                        addFragmentSearch("");
                        break;

                }
                return false;
            }
        });

        nsvVideo.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                sheetBehavior.setDraggable(true);
                return false;
            }
        });
    }

    // Phương thức chọn fragment khi click menu
    private void selectFragment(Fragment fragment, String tag, String tagBackStack) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fl_content, fragment, tag);
        fragmentTransaction.addToBackStack(tagBackStack);
        fragmentTransaction.commit();
    }

    // add phần tìm kiếm
    public void addFragmentSearch(String s) {
        setToolBarMainInvisible();
        bnvFragment.setVisibility(View.GONE);
        SearchFragment searchFragment = new SearchFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Util.BUNDLE_EXTRA_TEXT_EDITTEXT, s);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        searchFragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.fl_content, searchFragment, "fragSearch");
        fragmentTransaction.addToBackStack("SearchFragment");
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        HomeFragment fragment =
                (HomeFragment) getSupportFragmentManager().findFragmentByTag(Util.TAG_HOME);
        if (fragment != null && fragment.isVisible()) {
            setToolBarMainVisible();
        }

    }
    // Add thêm kết quả của việc search vào main
    public void addFragmentSearchResults(String q) {
        setToolBarMainInvisible();
        bnvFragment.setVisibility(View.VISIBLE);

//        removeFragmentSearch();
        SearchResultsFragment searchResultsFragment = new SearchResultsFragment();
        getSupportFragmentManager().popBackStack();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putString(Util.BUNDLE_EXTRA_Q, q);
        searchResultsFragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.fl_content, searchResultsFragment, "fragSearchRe");
        fragmentTransaction.addToBackStack("SearchFragmentRe");
        fragmentTransaction.commit();
    }
    // hiển thị toolbar và bnvbar
    public void setToolBarMainVisible() {
        // hiển thị sau khi cuộn trang
        if (tbNav.getParent() instanceof AppBarLayout){
            ((AppBarLayout)tbNav.getParent()).setExpanded(true,true);
        }
        bnvFragment.setVisibility(View.VISIBLE);
        tbNav.setVisibility(View.VISIBLE);
        Log.d("fjdsal","success");
    }
    public void setToolBarMainInvisible() {
        tbNav.setVisibility(View.GONE);
    }

    ////////////////////////////////////////////////////////////////////////////////
    //   Video Play

    public void setVisibleResize() {
        llResize.setVisibility(View.VISIBLE);
        ivDelete.setVisibility(View.VISIBLE);
    }

    public void setDisableResize() {
        llResize.setVisibility(View.GONE);
        ivDelete.setVisibility(View.GONE);
    }


    String idVideo;
    YouTubePlayerFragment youTubePlayerFragment;

    public void setDataVideoPlay(String idVideoS, VideoItem itemVideo, SearchItem itemVideoS) {
        Log.d("pause", sheetBehavior.getState() + "");
//        if (sheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
//            sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
//
//            eventPlay(idVideoS, itemVideo, itemVideoS);
//        } else
//        sheetBehavior.setState(STATE);
        if(sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED){

            eventPlay(idVideoS, itemVideo, itemVideoS);
        } else if (sheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED){
            sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        } else {
            sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
    }
    // Khi đang thu nhỏ video và click bên Home thì sẽ hủy player view đang chạy
    public void setResetVideo() {
            sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            if (youTubePlayerFragment != null) {
                youTubePlayerFragment.onDestroy();
            }
        setDisableResize();
    }
    public void setDraggable(boolean set) {
        sheetBehavior.setDraggable(set);
    }

    private void eventPlay(String idVideoS, VideoItem itemVideo, SearchItem itemVideoS) {
        idVideo = idVideoS;
        addFragmentMain(itemVideo, itemVideoS);

        tvTitleVideo.setText(titleVideo);
        tvTitleChannel.setText(titleChannel);
        youTubePlayerFragment = (YouTubePlayerFragment) getFragmentManager()
                .findFragmentById(R.id.fm_youtube_play_view);
        youTubePlayerFragment.initialize(Util.API_KEY, this);
        sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        sheetBehavior.setDraggable(false);

        sheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    // Nếu bottom sheet đang mở rộng
                    case BottomSheetBehavior.STATE_EXPANDED:
                        setDraggable(false);
                        bnvFragment.setVisibility(View.GONE);
                        setDisableResize();
                        ViewGroup.LayoutParams paramss =
                                (ViewGroup.LayoutParams )youTubePlayerFragment.getView().getLayoutParams();
                        int width = getResources().getDisplayMetrics().widthPixels;
                        int height = getResources().getDisplayMetrics().heightPixels;
                        paramss.height = (int) (height*0.35);
                        paramss.width = width;
                        youTubePlayerFragment.getView().setLayoutParams(paramss);
                        break;
                        // Khi đang thu nhỏ Bottom Sheet
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        bnvFragment.setVisibility(View.VISIBLE);
                        setVisibleResize();
                        ViewGroup.LayoutParams params =
                                (ViewGroup.LayoutParams )youTubePlayerFragment.getView().getLayoutParams();
                        params.height = 290;
                        params.width = 550;
                        youTubePlayerFragment.getView().setLayoutParams(params);
                        sheetBehavior.setPeekHeight(450);
                        break;
                        // Khi bị ẩn đi
                    case BottomSheetBehavior.STATE_HIDDEN:
                        youTubePlayerFragment.onDestroy();
                        setDisableResize();
                        break;
                }
            }
            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
    }

    private void addFragmentMain(VideoItem itemVideo, SearchItem itemVideoS) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        VideoContainDataFragment videoContainDataFragment = new VideoContainDataFragment();

        Bundle bundle = new Bundle();
        if (itemVideo != null && itemVideoS == null) {
                bundle.putSerializable(Util.BUNDLE_EXTRA_OBJECT_ITEM_VIDEO, itemVideo);
                bundle.putString(Util.EXTRA_KEY_ITEM_VIDEO, "Video");

                titleVideo = itemVideo.getTvTitleVideo();
                titleChannel = itemVideo.getTvTitleChannel();

        } else {
                bundle.putSerializable(Util.BUNDLE_EXTRA_OBJECT_ITEM_VIDEO, itemVideoS);
                bundle.putString(Util.EXTRA_KEY_ITEM_VIDEO, "Search");

                titleVideo = itemVideoS.getTvTitleVideo();
                titleChannel = itemVideoS.getTitleChannel();
        }

        videoContainDataFragment.setArguments(bundle);
        transaction.replace(R.id.fl_content_data, videoContainDataFragment);

        transaction.commit();
    }

// Chạy video
    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
        if (youTubePlayer.isPlaying()) {
            youTubePlayerFragment.onDestroy();
        }
        YouTubePlayer.PlayerStyle playerStyle = YouTubePlayer.PlayerStyle.DEFAULT;
        if(!b) {
            youTubePlayer.setPlayerStyle(playerStyle);
            youTubePlayer.loadVideo(idVideo);
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        final int REQUEST_CODE = 1;

        if(youTubeInitializationResult.isUserRecoverableError()) {
            youTubeInitializationResult.getErrorDialog(this,REQUEST_CODE).show();
        } else {
            String errorMessage =
                    String.format("There was an error initializing the YoutubePlayer (%1$s)",
                            youTubeInitializationResult.toString());
            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
        }
    }
}