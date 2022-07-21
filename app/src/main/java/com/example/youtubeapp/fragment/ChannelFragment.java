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
    public static final String TAG = ChannelFragment.class.getName();
    TabLayout tlChannel;
    ViewPager2 vp2Content;
    ViewPagerChannelAdapter adapter;
    String idChannel = "", titleChannel = "";
    TextView tvTitleChannel;
    MainActivity mainActivity;
    ImageButton ibBack;
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
                getParentFragmentManager().popBackStack();
                if (Util.FRAGMENT_CURRENT == 1) {
                    mainActivity.setToolBarMainVisible();
                }



//                mainActivity.onBackPressed();
            }
        });
        tbChannel.setVisibility(View.VISIBLE);
        ablChannel.setVisibility(View.VISIBLE);
        tbChannel.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.mn_search_channel:
                        // Kiểm tra xem đang mở search từ fragment nào
                        Util.FRAGMENT_CURRENT = 2;
                        mainActivity.addFragmentSearch("");
                }
                return false;
            }
        });

        if (tbChannel.getParent() instanceof AppBarLayout) {
            ((AppBarLayout) tbChannel.getParent()).setExpanded(true, true);
        }
        return view;
    }
    private void initView(View view) {
        tlChannel = view.findViewById(R.id.tl_channel);
        vp2Content =  view.findViewById(R.id.vp2_content);
        tvTitleChannel =  view.findViewById(R.id.tv_title_channel_nav);
        ibBack =  view.findViewById(R.id.ib_back_home_channel);
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
}