package com.example.youtubeapp.activitys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

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
import com.example.youtubeapp.adapter.ViewPagerMainAdapter;
import com.example.youtubeapp.fragment.BottomSheetDialogUserFragment;
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

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements YouTubePlayer.OnInitializedListener {
    Toolbar tbNav;
    BottomNavigationView bnvFragment;

    HomeFragment homeFragment;
    ShortsFragment shortsFragment;
    SubcriptionFragment subcriptionFragment;
    NotificationFragment notificationFragment;
    LibraryFragment libraryFragment;

    FrameLayout flContent;
    CoordinatorLayout cdlMain;
    AppBarLayout ablHome;
    NestedScrollView nsvVideo;
    ViewPager2 vp2Main;

    LinearLayout llResize;
    TextView tvTitleVideo, tvTitleChannel;
    ImageView ivDelete;
    String titleVideo = "", titleChannel = "";

    ArrayList<Fragment> listFragment;

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
        vp2Main = findViewById(R.id.vp2_main);
        cdlMain = findViewById(R.id.cdl_main);
        // Bottom sheet để mở video play
        clVideoPlay = findViewById(R.id.cl_video_play);
        sheetBehavior = BottomSheetBehavior.from(clVideoPlay);
        llResize = findViewById(R.id.ll_resize2);
        tvTitleChannel = findViewById(R.id.tv_title_channel_resize);
        tvTitleVideo = findViewById(R.id.tv_title_video_resize);
        ivDelete = findViewById(R.id.iv_delete_resize);



        listFragment = new ArrayList<>();

        bnvFragment.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.mn_home:
                        flContent.setVisibility(View.GONE);
                        deleteBackStackFrameLayout();
                        setToolBarMainVisible();
                        vp2Main.setCurrentItem(0,false);
                        if (vp2Main.getCurrentItem() == 0) {
                            if (item.isChecked()) {
                                // Nếu lướt quá sâu thì chỉ cần nhấn vào nút home sẽ quay lại ngay lập tức
                                if (homeFragment.rvItemVideo.getAdapter().getItemCount() > 21) {
                                    homeFragment.topRecycleViewFast();
                                } else {
                                    homeFragment.topRecycleView();
                                }
                            }
                        }
                        break;
                    case R.id.mn_explore:
                        flContent.setVisibility(View.GONE);
                        // Xóa backstack có trên FrameLayout
                        deleteBackStackFrameLayout();
                        // Nếu  = 0 thì sẽ chỉ load 1 lần shorts fragment do
                        // viewPager2 tự động load trước, nếu không chặn lại thì sẽ phát tự động
                        if (Util.NUMBER_CHECK_LOAD_SHORTS == 0) {
                            Util.CHECK_LOAD_SHORTS = true;
                            Util.NUMBER_CHECK_LOAD_SHORTS = 1 ;
                        } else {
                            Util.CHECK_LOAD_SHORTS = false;
                        }
                        vp2Main.setCurrentItem(1,false);
                        Log.d("aaaaaaaaaaaa", "skada");

                        break;
                    case R.id.mn_subcription:
                        setToolBarMainVisible();
                        flContent.setVisibility(View.GONE);
                        deleteBackStackFrameLayout();
                        Log.d("dfafasas", "co");
                        vp2Main.setCurrentItem(2,false);
                        break;
                    case R.id.mn_notification:
                        setToolBarMainVisible();
                        flContent.setVisibility(View.GONE);
                        deleteBackStackFrameLayout();
                        vp2Main.setCurrentItem(3,false);
                        break;
                    case R.id.mn_library:
                        setToolBarMainVisible();
                        flContent.setVisibility(View.GONE);
                        deleteBackStackFrameLayout();
                        vp2Main.setCurrentItem(4,false);
                        break;
                }
                return false;
            }
        });
//      Sự khi click thì đổi màu menu
        vp2Main.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);

                switch (position) {
                    case 0:
                        bnvFragment.getMenu().findItem(R.id.mn_home).setChecked(true);
                        break;
                    case 1:
                        bnvFragment.getMenu().findItem(R.id.mn_explore).setChecked(true);
                        break;
                    case 2:
                        bnvFragment.getMenu().findItem(R.id.mn_subcription).setChecked(true);
                        break;
                    case 3:
                        bnvFragment.getMenu().findItem(R.id.mn_notification).setChecked(true);
                        break;
                    case 4:
                        bnvFragment.getMenu().findItem(R.id.mn_library).setChecked(true);
                        break;
                }
            }
        });
        setupViewPager(vp2Main);

        tbNav.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.mn_search:
                        Util.FRAGMENT_CURRENT = 1;
                        addFragmentSearch("");
                        break;
                    case R.id.mn_account:
                        openBottomSheetDiaLogLogin();
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
    // Hiển thị FrameLayout chứa các fragment và set Elevation cao để hiển thị lên trên viewpager
    private void setFlContentVisible() {
        flContent.setVisibility(View.VISIBLE);
        flContent.setElevation(40);
    }
    // Xóa các backstack đã lưu trong list, làm mới lại FrameLayout
    private void deleteBackStackFrameLayout() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (listFragment != null) {
            for (Fragment fragment : listFragment) {
                ft.remove(fragment);
            }
            ft.commit();
            listFragment = new ArrayList<>();
        }
    }


    private void setupViewPager(ViewPager2 viewPager2) {
        ViewPagerMainAdapter adapter =
                new ViewPagerMainAdapter(getSupportFragmentManager(), getLifecycle());

        homeFragment = new HomeFragment();
        shortsFragment = new ShortsFragment();
        subcriptionFragment = new SubcriptionFragment();
        notificationFragment = new NotificationFragment();
        libraryFragment = new LibraryFragment();

        adapter.addFragment(homeFragment);
        adapter.addFragment(shortsFragment);
        adapter.addFragment(subcriptionFragment);
        adapter.addFragment(notificationFragment);
        adapter.addFragment(libraryFragment);

        viewPager2.setAdapter(adapter);
        viewPager2.setUserInputEnabled(false);
        viewPager2.setOffscreenPageLimit(2);

    }


    // add phần tìm kiếm
    public void addFragmentSearch(String s) {
        setFlContentVisible();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        setToolBarMainInvisible();
        bnvFragment.setVisibility(View.GONE);
        SearchFragment searchFragment = new SearchFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Util.BUNDLE_EXTRA_TEXT_EDITTEXT, s);
        searchFragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.fl_content, searchFragment, Util.TAG_SEARCH);
        fragmentTransaction.addToBackStack(SearchFragment.TAG);
        listFragment.add(searchFragment);
        fragmentTransaction.commit();
    }

    // open fragment channel
    public void addFragmentChannel(String idChannel, String titleChannel) {
        setFlContentVisible();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        ablHome.setVisibility(View.VISIBLE);
        tbDisPlay();
        tbNav.setVisibility(View.GONE);
        ChannelFragment channelFragment = new ChannelFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Util.EXTRA_ID_CHANNEL_TO_CHANNEL, idChannel);
        bundle.putString(Util.EXTRA_TITLE_CHANNEL_TO_CHANNEL, titleChannel);
        channelFragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.fl_content, channelFragment, Util.TAG_CHANNEL);
        fragmentTransaction.addToBackStack(ChannelFragment.TAG);
        listFragment.add(channelFragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (Util.FRAGMENT_CURRENT == 1) {
            setToolBarMainVisible();
        }
    }


//    // Xem hiện đang ở menu nào, nếu đúng là menu đó thì cho nút menu chuyển thành màu đỏ
//    private void back(Fragment fragment, int id) {
//        if (fragment != null && fragment.isVisible()) {
//            setToolBarMainVisible();
//            bnvFragment.getMenu().findItem(id).setChecked(true);
//        }
//    }

    // Add thêm kết quả của việc search vào main
    public void addFragmentSearchResults(String q) {
        setFlContentVisible();
        bnvFragment.setVisibility(View.VISIBLE);
        SearchResultsFragment searchResultsFragment = new SearchResultsFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putString(Util.BUNDLE_EXTRA_Q, q);
        searchResultsFragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.fl_content, searchResultsFragment, Util.TAG_RESULTS_SEARCH);
        fragmentTransaction.addToBackStack(SearchResultsFragment.TAG);
        listFragment.add(searchResultsFragment);
        fragmentTransaction.commit();
    }

    // hiển thị toolbar và bnvbar
    public void setToolBarMainVisible() {
        // hiển thị sau khi cuộn trang
        ablHome.setVisibility(View.VISIBLE);
        tbDisPlay();
        bnvFragment.setVisibility(View.VISIBLE);
        tbNav.setVisibility(View.VISIBLE);

    }

    public void setToolBarMainInvisible() {
        tbNav.setVisibility(View.GONE);
    }
    public void setBnvVisible() {
            bnvFragment.setVisibility(View.VISIBLE);
    }
    public void tbVisible() {
        bnvFragment.setVisibility(View.VISIBLE);
    }
    public void setAblHomeInvisible() {
        ablHome.setVisibility(View.GONE);
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

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("duc1", "onStopMain");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("duc1","onStartMain");

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("duc1","onResumeMain");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("duc1","onPauseMain");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("duc1","onRestartMain");
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
        setFlContentVisible();
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
        setFlContentVisible();
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
        fragmentTransaction.replace(R.id.fl_content, PlayListVideoFragment, Util.TAG_PLAYLIST_DETAIL);
        fragmentTransaction.addToBackStack(com.example.youtubeapp.fragment.PlayListVideoFragment.TAG);
        listFragment.add(PlayListVideoFragment);
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
    private void openBottomSheetDiaLogLogin() {
        BottomSheetDialogUserFragment sheetDialogUserFragment =
                BottomSheetDialogUserFragment.newInstance();
        sheetDialogUserFragment.show(getSupportFragmentManager(), BottomSheetDialogUserFragment.TAG);
    }
}