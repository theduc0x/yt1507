package com.example.youtubeapp.fragment;

import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.youtubeapp.R;
import com.example.youtubeapp.activitys.MainActivity;
import com.example.youtubeapp.adapter.PlayListItemVideoChannelAdapter;
import com.example.youtubeapp.api.ApiServicePlayList;
import com.example.youtubeapp.model.detailvideo.DetailVideo;
import com.example.youtubeapp.model.detailvideo.ItemVideo;
import com.example.youtubeapp.model.itemrecycleview.ItemVideoInPlayList;
import com.example.youtubeapp.model.itemrecycleview.PlayListItem;
import com.example.youtubeapp.model.itemrecycleview.SearchItem;
import com.example.youtubeapp.model.itemrecycleview.VideoItem;
import com.example.youtubeapp.model.playlistitem.Items;
import com.example.youtubeapp.model.playlistitem.PlayListItemVideo;
import com.example.youtubeapp.my_interface.IItemOnClickVideoListener;
import com.example.youtubeapp.my_interface.PaginationScrollListener;
import com.example.youtubeapp.utiliti.Util;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlayListVideoFragment extends Fragment {
    public static final String TAG = PlayListVideoFragment.class.getName();
    String idPlayList, videoCount, titlePlayList, titleChannel;
    TextView tvTitlePlayListTB, tvTitlePlayList, tvTitleChannel,
            tvVideoCount;
    RecyclerView rvListVideo;
    PlayListItemVideoChannelAdapter adapter;
    ArrayList<ItemVideoInPlayList> listItems;
    ArrayList<ItemVideoInPlayList> listAdd;
    Toolbar tbPlayListVideo;
    ImageButton ivBack;
    LinearLayout llOpenVideo;
    MainActivity mainActivity;

    private String pageToken = "";
    private boolean isLoading;
    private boolean isLastPage;
    private int totalPage = 5;
    private int currenPage = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_play_list_video, container, false);
        getData();
        initView(view);
        setData();
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().popBackStack();
            }
        });
        listItems = new ArrayList<>();

        LinearLayoutManager linearLayoutManager =
                new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        rvListVideo.setLayoutManager(linearLayoutManager);
        adapter = new PlayListItemVideoChannelAdapter(new IItemOnClickVideoListener() {
            @Override
            public void OnClickItemVideo(VideoItem item) {
//                Intent toPlayVideo = new Intent(VideoPlayListActivity.this, VideoPlayActivity.class);
//                Bundle bundle = new Bundle();
//                bundle.putSerializable(Util.BUNDLE_EXTRA_OBJECT_ITEM_VIDEO, item);
//                bundle.putString(Util.EXTRA_KEY_ITEM_VIDEO, "Video");
//                toPlayVideo.putExtras(bundle);
//                startActivity(toPlayVideo);
                mainActivity.setResetVideo();
                mainActivity.setDataVideoPlay(item.getIdVideo(), item, null);
            }
        });
        rvListVideo.setAdapter(adapter);
        setFirstData();

        rvListVideo.addOnScrollListener(new PaginationScrollListener(linearLayoutManager) {
            @Override
            public void loadMoreItem() {
                isLoading = true;
                currenPage += 1;
                loadNextPage();
            }
            @Override
            public boolean isLoading() {
                return isLoading;
            }
            @Override
            public boolean isLastPage() {
                return isLastPage;
            }
        });

        tbPlayListVideo.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.mn_search_channel:
                        mainActivity.addFragmentSearch("");
                }
                return false;
            }
        });
        return view;
    }
    private void setFirstData() {
        listItems = null;
        callApiListVideoInPlayList(pageToken, idPlayList, "10");
    }
    // Set propress bar load data
    private void setProgressBar() {
        if (currenPage < totalPage) {
            adapter.addFooterLoading();
        } else {
            isLastPage = true;
        }
    }
    // Load dữ liệu của page tiếp theo
    private void loadNextPage() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getActivity(), "Load Page" + currenPage, Toast.LENGTH_SHORT).show();
                callApiListVideoInPlayList(pageToken, idPlayList, "10");
                isLoading = false;
            }
        },500);
    }


    private void initView(View view) {
        tvTitlePlayListTB = view.findViewById(R.id.tv_title_play_list_video);
        tvTitlePlayList = view.findViewById(R.id.title_playlist_detail);
        tvTitleChannel = view.findViewById(R.id.tv_title_channel_detail);
        tvVideoCount = view.findViewById(R.id.tv_view_count_detail);
        rvListVideo = view.findViewById(R.id.rv_item_video_playList);
        tbPlayListVideo = view.findViewById(R.id.tb_nav_play_list_video);
        ivBack = view.findViewById(R.id.ib_back_playlist);
        llOpenVideo = view.findViewById(R.id.ll_open_video_play_from_listplay);
        mainActivity = (MainActivity) getActivity();
    }
    private void setData() {
        tvTitlePlayList.setText(titlePlayList);
        tvVideoCount.setText(videoCount + " video");
        tvTitleChannel.setText(titleChannel);
    }

    private void getData() {
        Bundle bundleRe = getArguments();
        if (bundleRe != null) {
            String key = bundleRe.getString(Util.EXTRA_KEY_ITEM_PLAYLIST);
            if (key.equals("Search")) {
                SearchItem item = (SearchItem) bundleRe.getSerializable(Util.BUNDLE_EXTRA_PLAY_LIST_TO_VIDEO_PLAY_LIST);
                idPlayList = item.getIdPlayList();
                videoCount = item.getVideoCountPlayList();
                titlePlayList = item.getTvTitleVideo();
                titleChannel = item.getTitleChannel();
            } else {
                PlayListItem item = (PlayListItem) bundleRe.getSerializable(Util.BUNDLE_EXTRA_PLAY_LIST_TO_VIDEO_PLAY_LIST);
                idPlayList = item.getIdPlayList();
                videoCount = item.getVideoCount();
                titlePlayList = item.getTitleVideo();
                titleChannel = item.getTitleChannel();
            }
        }
    }

    private void callApiListVideoInPlayList(String nextPageToken, String idPlayList, String maxResults) {
        listAdd = new ArrayList<>();
        ApiServicePlayList.apiServicePlayList.listInPlayList(
                nextPageToken,
                "contentDetails,snippet,id,status",
                idPlayList,
                Util.API_KEY,
                maxResults
        ).enqueue(new Callback<PlayListItemVideo>() {
            @Override
            public void onResponse(Call<PlayListItemVideo> call,
                                   Response<PlayListItemVideo> response) {
                String urlThumbnails = "", titleChannel = "", titleVideo = "",
                        idVideo = "", viewCount = "", publishAt = "",
                        privacyStatus = "";
                PlayListItemVideo item = response.body();
                if (item != null) {

                    int totalPlayList =  item.getPageInfo().getTotalResults();
                    if (totalPlayList % 10 != 0) {
                        totalPage = (totalPlayList / 10) + 1;
                    } else {
                        totalPage = (totalPlayList / 10);
                    }
                    pageToken = item.getNextPageToken();
                    ArrayList<Items> listItem = item.getItems();
                    for (int i = 0; i < listItem.size(); i++) {
                        idVideo = listItem.get(i).getSnippet().getResourceId().getVideoId();
                        privacyStatus = listItem.get(i).getStatus().getPrivacyStatus();

                        if (listItem.get(i).getSnippet().getThumbnails().getMaxres() != null) {
                            urlThumbnails = listItem.get(i).getSnippet()
                                    .getThumbnails().getMaxres().getUrl();
                        } else if (listItem.get(i).getSnippet().getThumbnails().getStandard() != null) {
                            urlThumbnails = listItem.get(i).getSnippet()
                                    .getThumbnails().getStandard().getUrl();
                        }else if (listItem.get(i).getSnippet().getThumbnails().getHigh() != null){
                            urlThumbnails = listItem.get(i).getSnippet()
                                    .getThumbnails().getHigh().getUrl();
                        } else {
                            urlThumbnails = getString(R.string.url_image_transparent);
                        }
                        callApiViewCountVideo(idVideo, listAdd, i);
                        titleChannel = listItem.get(i).getSnippet().getChannelTitle();
                        titleVideo = listItem.get(i).getSnippet().getTitle();
                        publishAt = listItem.get(i).getSnippet().getPublishedAt();

                        listAdd.add(new ItemVideoInPlayList(urlThumbnails, titleVideo,
                                titleChannel, "",publishAt, idVideo, privacyStatus));
                    }
                    if (listItems == null) {
                        listItems = listAdd;
                        adapter.setData(listItems);
                        setProgressBar();
                    } else {
                        adapter.removeFooterLoading();
                        listItems.addAll(listAdd);
                        adapter.notifyDataSetChanged();
                        setProgressBar();
                    }
                }
            }

            @Override
            public void onFailure(Call<PlayListItemVideo> call, Throwable t) {

            }
        });
    }
    // Lấy view của video
    private void callApiViewCountVideo(String idVideo, ArrayList<ItemVideoInPlayList> listItemV,
                                       int pos) {
        ApiServicePlayList.apiServicePlayList.detailVideo(
                "snippet",
                "statistics",
                null,
                idVideo,
                Util.API_KEY
        ).enqueue(new Callback<DetailVideo>() {
            @Override
            public void onResponse(Call<DetailVideo> call, Response<DetailVideo> response) {
                String viewCount = "";
                DetailVideo video = response.body();
                if (video != null) {
                    ArrayList<ItemVideo> listItem = video.getItems();
                    for (int i = 0; i <listItem.size(); i++ ) {
                        viewCount = listItem.get(0).getStatistics().getViewCount();
                        listItemV.get(pos).setViewCountVideo(viewCount);
                        adapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onFailure(Call<DetailVideo> call, Throwable t) {

            }
        });
    }

}