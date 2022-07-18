package com.example.youtubeapp.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.youtubeapp.R;
import com.example.youtubeapp.activitys.MainActivity;
import com.example.youtubeapp.adapter.ViewPagerChannelAdapter;
import com.example.youtubeapp.utiliti.Util;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class ChannelFragment extends Fragment {
    TabLayout tlChannel;
    ViewPager2 vp2Content;
    ViewPagerChannelAdapter adapter;
    String idChannel = "", titleChannel = "";
    TextView tvTitleChannel;
    MainActivity mainActivity;
    ImageButton ibBack;
    BottomNavigationView bnvChannel;
    Toolbar tbChannel;
    AppBarLayout ablChannel;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_channel, container, false);
        initView(view);
        setTabLayout();
        getIdChannelAndTransHomeChannel();
        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.onBackPressed();
            }
        });
        tbChannel.setVisibility(View.VISIBLE);
        ablChannel.setVisibility(View.VISIBLE);
//        setBnvChannel();
        return view;
    }
    private void initView(View view) {
        tlChannel = view.findViewById(R.id.tl_channel);
        vp2Content =  view.findViewById(R.id.vp2_content);
        tvTitleChannel =  view.findViewById(R.id.tv_title_channel_nav);
        ibBack =  view.findViewById(R.id.ib_back_home_channel);
        bnvChannel =  view.findViewById(R.id.bnv_fragment_channel);
        tbChannel = view.findViewById(R.id.tb_nav_channel);
        ablChannel = view.findViewById(R.id.abl_nav_channel);
        mainActivity = (MainActivity) getActivity();
    }

    private void setTabLayout() {
        adapter = new ViewPagerChannelAdapter(getActivity());
        vp2Content.setAdapter(adapter);
        new TabLayoutMediator(tlChannel, vp2Content, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                switch (position) {
                    case 0 :
                        tab.setText("HOME");
                        break;
                    case 1 :
                        tab.setText("VIDEOS");
                        break;
                    case 2 :
                        tab.setText("PLAYLISTS");
                        break;
                    case 3 :
                        tab.setText("COMMUNITY");
                        break;
                    case 4 :
                        tab.setText("CHANNELS");
                        break;
                    case 5 :
                        tab.setText("ABOUT");
                        break;
                }
            }
        }).attach();
    }
    private void getIdChannelAndTransHomeChannel() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            idChannel = bundle.getString(Util.EXTRA_ID_CHANNEL_TO_CHANNEL);
            titleChannel = bundle.getString(Util.EXTRA_TITLE_CHANNEL_TO_CHANNEL);
        }
        tvTitleChannel.setText(titleChannel);
        adapter.setData(idChannel);
    }

//    private void setBnvChannel() {
//        bnvChannel.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
//            @SuppressLint("NonConstantResourceId")
//            @Override
//            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//                switch (item.getItemId()) {
//                    case R.id.mn_home_channel:
//                        getSupportFragmentManager().popBackStack(HomeFragment.TAG, 0);
//                        break;
//                    case R.id.mn_explore_channel:
//
//                        break;
//                    case R.id.mn_subcription_channel:
//
//                        break;
//                    case R.id.mn_notification_channel:
//
//                        break;
//                    case R.id.mn_library_channel:
//
//                        break;
//                }
//                return false;
//
//            }
//        });
//    }
}