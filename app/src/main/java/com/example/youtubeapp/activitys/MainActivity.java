package com.example.youtubeapp.activitys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.os.Bundle;
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
import com.example.youtubeapp.fragment.ChannelFragment;
import com.example.youtubeapp.fragment.PlayListVideoFragment;
import com.example.youtubeapp.fragment.ShortsFragment;
import com.example.youtubeapp.fragment.HomeFragment;
import com.example.youtubeapp.fragment.LibraryFragment;
import com.example.youtubeapp.fragment.NotificationFragment;
import com.example.youtubeapp.fragment.SearchFragment;
import com.example.youtubeapp.fragment.SearchResultsFragment;
import com.example.youtubeapp.fragment.SubcriptionFragment;
import com.example.youtubeapp.fragment.VideoContainDataFragment;
import com.example.youtubeapp.model.itemrecycleview.PlayListItem;
import com.example.youtubeapp.model.itemrecycleview.SearchItem;
import com.example.youtubeapp.model.itemrecycleview.VideoItem;
import com.example.youtubeapp.utiliti.Util;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;

public class MainActivity extends AppCompatActivity implements YouTubePlayer.OnInitializedListener {
    Toolbar tbNav;
    BottomNavigationView bnvFragment;

    HomeFragment homeFragment = new HomeFragment();
    ShortsFragment shortsFragment = new ShortsFragment();
    SubcriptionFragment subcriptionFragment = new SubcriptionFragment();
    NotificationFragment notificationFragment = new NotificationFragment();
    LibraryFragment libraryFragment = new LibraryFragment();
    FragmentManager fm = getSupportFragmentManager();
    Fragment active = homeFragment;

    FrameLayout flContent;
    AppBarLayout ablHome;
    NestedScrollView nsvVideo;

    LinearLayout llResize;
    LinearLayout llVideoPlay;
    TextView tvTitleVideo, tvTitleChannel;
    ImageView ivDelete;
    String titleVideo = "", titleChannel = "";

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
        // Add sẵn các fragment
        fm.beginTransaction().add(R.id.fl_content, libraryFragment, Util.TAG_LIBRARY)
                .hide(libraryFragment).commit();
        fm.beginTransaction().add(R.id.fl_content, notificationFragment, Util.TAG_NOTIFI)
                .hide(notificationFragment).commit();
        fm.beginTransaction().add(R.id.fl_content, subcriptionFragment, Util.TAG_SUB)
                .hide(subcriptionFragment).commit();
        fm.beginTransaction().add(R.id.fl_content, shortsFragment, Util.TAG_SHORTS)
                .hide(shortsFragment).commit();
        fm.beginTransaction().add(R.id.fl_content, homeFragment, Util.TAG_HOME)
                .commit();

        bnvFragment.getMenu().findItem(R.id.mn_home).setChecked(true);

        NavigationBarView.OnItemSelectedListener onItemSelectedListener =
                new NavigationBarView.OnItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.mn_home:
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
                                    setToolBarMainVisible();
                                    fm.beginTransaction().hide(active).addToBackStack(Util.TAG_HOME)
                                            .show(homeFragment).commit();
                                    active = homeFragment;
                                }

                                homeFragment = (HomeFragment) getSupportFragmentManager()
                                        .findFragmentByTag(Util.TAG_HOME);
                                if (!homeFragment.isVisible() ) {
                                    getSupportFragmentManager().popBackStack(HomeFragment.TAG, 0);
                                }

                                return true;

                            case R.id.mn_explore:
                                setToolBarMainInvisible();
                                tbHide();
                                fm.beginTransaction().hide(active).addToBackStack(Util.TAG_SHORTS)
                                        .show(shortsFragment).commit();
                                active = shortsFragment;
                                return true;

                            case R.id.mn_subcription:
                                setToolBarMainVisible();
                                fm.beginTransaction().hide(active).addToBackStack(Util.TAG_SUB)
                                        .show(subcriptionFragment).commit();
                                active = subcriptionFragment;
                                return true;

                            case R.id.mn_notification:
                                setToolBarMainVisible();
                                fm.beginTransaction().hide(active).addToBackStack(Util.TAG_NOTIFI)
                                        .show(notificationFragment).commit();
                                active = notificationFragment;
                                return true;

                            case R.id.mn_library:
                                setToolBarMainVisible();
                                fm.beginTransaction().hide(active).addToBackStack(Util.TAG_LIBRARY)
                                        .show(libraryFragment).commit();
                                active = libraryFragment;
                                return true;
                        }
                        return false;
                    }
                };

        bnvFragment.setOnItemSelectedListener(onItemSelectedListener);

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

    // add phần tìm kiếm
    public void addFragmentSearch(String s) {
        setToolBarMainInvisible();
        bnvFragment.setVisibility(View.GONE);
        SearchFragment searchFragment = new SearchFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Util.BUNDLE_EXTRA_TEXT_EDITTEXT, s);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        searchFragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.fl_content, searchFragment, Util.TAG_SEARCH);
        fragmentTransaction.addToBackStack(SearchFragment.TAG);
        fragmentTransaction.commit();
    }

    // open fragment channel
    public void addFragmentChannel(String idChannel, String titleChannel) {
        ablHome.setVisibility(View.VISIBLE);
        tbDisPlay();
        tbNav.setVisibility(View.GONE);
        ChannelFragment channelFragment = new ChannelFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Util.EXTRA_ID_CHANNEL_TO_CHANNEL, idChannel);
        bundle.putString(Util.EXTRA_TITLE_CHANNEL_TO_CHANNEL, titleChannel);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        channelFragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.fl_content, channelFragment, Util.TAG_CHANNEL);
        fragmentTransaction.addToBackStack(ChannelFragment.TAG);
        fragmentTransaction.commit();
        Log.d("sadjdaksa", getSupportFragmentManager().getBackStackEntryCount()+"");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        HomeFragment fragmentH =
                (HomeFragment) getSupportFragmentManager().findFragmentByTag(Util.TAG_HOME);
        ShortsFragment fragmentS =
                (ShortsFragment) getSupportFragmentManager().findFragmentByTag(Util.TAG_SHORTS);
        SubcriptionFragment fragmentSup =
                (SubcriptionFragment) getSupportFragmentManager().findFragmentByTag(Util.TAG_SUB);
        NotificationFragment fragmentN =
                (NotificationFragment) getSupportFragmentManager().findFragmentByTag(Util.TAG_NOTIFI);
        LibraryFragment fragmentL =
                (LibraryFragment) getSupportFragmentManager().findFragmentByTag(Util.TAG_LIBRARY);

        if (fragmentS != null && fragmentS.isVisible()) {
            bnvFragment.getMenu().findItem(R.id.mn_explore).setChecked(true);
        }

        back(fragmentH, R.id.mn_home);
        back(fragmentSup, R.id.mn_subcription);
        back(fragmentN, R.id.mn_notification);
        back(fragmentL, R.id.mn_library);
    }
    // Xem hiện đang ở menu nào, nếu đúng là menu đó thì cho nút menu chuyển thành màu đỏ
    private void back(Fragment fragment, int id) {
        if (fragment != null && fragment.isVisible()) {
            setToolBarMainVisible();
            bnvFragment.getMenu().findItem(id).setChecked(true);
        }
    }

    // Add thêm kết quả của việc search vào main
    public void addFragmentSearchResults(String q) {
        bnvFragment.setVisibility(View.VISIBLE);
        SearchResultsFragment searchResultsFragment = new SearchResultsFragment();
//        getSupportFragmentManager().popBackStack();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putString(Util.BUNDLE_EXTRA_Q, q);
        searchResultsFragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.fl_content, searchResultsFragment, Util.TAG_RESULTS_SEARCH);
        fragmentTransaction.addToBackStack(SearchResultsFragment.TAG);
        fragmentTransaction.commit();

        Log.d("sadjdaksa", getSupportFragmentManager().getBackStackEntryCount()+"");
    }

    // hiển thị toolbar và bnvbar
    public void setToolBarMainVisible() {
        // hiển thị sau khi cuộn trang
        tbDisPlay();
        bnvFragment.setVisibility(View.VISIBLE);
        tbNav.setVisibility(View.VISIBLE);
        ablHome.setVisibility(View.VISIBLE);
    }

    public void setToolBarMainInvisible() {
        tbNav.setVisibility(View.GONE);

    }

    public void tbHide() {
        if (tbNav.getParent() instanceof AppBarLayout) {
            ((AppBarLayout) tbNav.getParent()).setExpanded(false, true);
        }
    }

    public void tbDisPlay() {
        if (tbNav.getParent() instanceof AppBarLayout) {
            ((AppBarLayout) tbNav.getParent()).setExpanded(true, true);
        }
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
        if (sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {

            eventPlay(idVideoS, itemVideo, itemVideoS);
        } else if (sheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
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
    // set xem có cho vuốt để thu nhỏ video không
    public void setDraggable(boolean set) {
        sheetBehavior.setDraggable(set);
    }
    // Khi nhấn vào channel sẽ thu nhỏ được video đang chạy
    public void resizeVideoPlay() {
        sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
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
                                (ViewGroup.LayoutParams) youTubePlayerFragment.getView().getLayoutParams();
                        int width = getResources().getDisplayMetrics().widthPixels;
                        int height = getResources().getDisplayMetrics().heightPixels;
                        paramss.height = (int) (height * 0.35);
                        paramss.width = width;
                        youTubePlayerFragment.getView().setLayoutParams(paramss);
                        break;
                    // Khi đang thu nhỏ Bottom Sheet
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        bnvFragment.setVisibility(View.VISIBLE);
                        setVisibleResize();
                        ViewGroup.LayoutParams params =
                                (ViewGroup.LayoutParams) youTubePlayerFragment.getView().getLayoutParams();
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

    // Add play list
    public void addFragmenPlayListVideo(PlayListItem itemPlayList, SearchItem itemVideoS) {
        PlayListVideoFragment PlayListVideoFragment = new PlayListVideoFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Bundle bundle = new Bundle();

        if (itemPlayList != null && itemVideoS == null) {
            bundle.putSerializable(Util.BUNDLE_EXTRA_PLAY_LIST_TO_VIDEO_PLAY_LIST, itemPlayList);
            bundle.putString(Util.EXTRA_KEY_ITEM_PLAYLIST, "Channel");

        } else {
            bundle.putSerializable(Util.BUNDLE_EXTRA_PLAY_LIST_TO_VIDEO_PLAY_LIST, itemVideoS);
            bundle.putString(Util.EXTRA_KEY_ITEM_PLAYLIST, "Search");
        }
        PlayListVideoFragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.fl_content, PlayListVideoFragment, Util.TAG_RESULTS_SEARCH);
        fragmentTransaction.addToBackStack(com.example.youtubeapp.fragment.PlayListVideoFragment.TAG);
        fragmentTransaction.commit();

        Log.d("sadjdaksa", getSupportFragmentManager().getBackStackEntryCount()+"");
    }
    // Chạy video
    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
        if (youTubePlayer.isPlaying()) {
            youTubePlayerFragment.onDestroy();
        }
        YouTubePlayer.PlayerStyle playerStyle = YouTubePlayer.PlayerStyle.DEFAULT;
        if (!b) {
            youTubePlayer.setPlayerStyle(playerStyle);
            youTubePlayer.loadVideo(idVideo);
        }
    }
    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        final int REQUEST_CODE = 1;

        if (youTubeInitializationResult.isUserRecoverableError()) {
            youTubeInitializationResult.getErrorDialog(this, REQUEST_CODE).show();
        } else {
            String errorMessage =
                    String.format("There was an error initializing the YoutubePlayer (%1$s)",
                            youTubeInitializationResult.toString());
            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
        }
    }
}