package com.example.youtubeapp.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.youtubeapp.R;
import com.example.youtubeapp.activitys.MainActivity;
import com.example.youtubeapp.adapter.ShortsVideoAdapter;
import com.example.youtubeapp.api.ApiServicePlayList;
import com.example.youtubeapp.model.detailvideo.DetailVideo;
import com.example.youtubeapp.model.detailvideo.ItemVideo;
import com.example.youtubeapp.model.infochannel.Channel;
import com.example.youtubeapp.model.infochannel.Itemss;
import com.example.youtubeapp.model.itemrecycleview.SearchItem;
import com.example.youtubeapp.model.itemrecycleview.VideoItem;
import com.example.youtubeapp.model.searchyoutube.ItemsSearch;
import com.example.youtubeapp.model.searchyoutube.Search;
import com.example.youtubeapp.model.shortsvideo.ExoPlayerItem;
import com.example.youtubeapp.my_interface.IItemOnClickOpenCommentFromShortsVideo;
import com.example.youtubeapp.my_interface.OnVideoPreparedListener;
import com.example.youtubeapp.utiliti.Util;
import com.google.android.exoplayer2.ExoPlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShortsFragment extends Fragment {
    public static final String TAG = ShortsFragment.class.getName();
    YouTubePlayerView ypvVideo;
    ArrayList<VideoItem> listItems;
    ShortsVideoAdapter adapter;
    String pageToken = "";
    List<ExoPlayerItem> listItem;
    ViewPager2 vp2Video;
    MainActivity mainActivity;
    ExoPlayer exoPlayerPause;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("duc1", "onCreateView2");
        View view = inflater.inflate(R.layout.fragment_shorts, container, false);

//        if(Util.CHECK_LOAD_SHORTS){

            vp2Video = view.findViewById(R.id.vp2_shorts_video);
            mainActivity = (MainActivity) getActivity();
            listItems = new ArrayList<>();
            listItem = new ArrayList<>();

            calLApiVideoShortRandom(pageToken, "20", null);
            adapter = new ShortsVideoAdapter(getContext(), new OnVideoPreparedListener() {
                @Override
                public void onVideoPrepared(ExoPlayerItem exoPlayerItem) {
                    listItem.add(exoPlayerItem);
                }
            }, new IItemOnClickOpenCommentFromShortsVideo() {
                @Override
                public void onCLickOpenCommentShorts(String idVideo, String cmtCount) {
                    openBottomSheetDiaLogComment(idVideo, cmtCount);
                }
            });

            vp2Video.setAdapter(adapter);
            adapter.setData(listItems);
            vp2Video.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                @Override
                public void onPageSelected(int position) {
                    int index = 0;
                    Iterator list = listItem.listIterator();
                    int var10000 = 0;
                    while(true) {
                        if (!list.hasNext()) {
                            var10000 = -1;
                            break;
                        }
                        ExoPlayerItem item = (ExoPlayerItem) list.next();
                        if (item.getExoPlayer().isPlaying()) {
                            var10000 = index;
                            break;
                        }
                        ++index;
                    }
                    int previousIndex = var10000;
                    if (previousIndex != -1) {
                        ExoPlayer player = listItem.get(previousIndex).getExoPlayer();
                        player.pause();
                        player.setPlayWhenReady(false);
                    }
                    int indexPos = 0;
                    Iterator var17 = listItem.listIterator();
                    while(true) {
                        if (!var17.hasNext()) {
                            var10000 = -1;
                            break;
                        }
                        ExoPlayerItem item = (ExoPlayerItem) var17.next();
                        if (item.getPosition() == position) {
                            var10000 = indexPos;
                            break;
                        }
                        ++indexPos;
                    }

                    int newIndex = var10000;
                    if (newIndex != -1) {
                        // ẩn tb khi lướt lên
//                        mainActivity.tbHide();
//                        mainActivity.setToolBarMainInvisible();
//                        mainActivity.setToolBarMainInvisible();
//                        mainActivity.tbHide();
                        ExoPlayer playerx = listItem.get(newIndex).getExoPlayer();
                        playerx.setPlayWhenReady(true);
                        playerx.play();

                    }
                }
            });


        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("duc1", "onPause2");

        mainActivity.setToolBarMainVisible();

        List $this$indexOfFirst$iv = (List) this.listItem;
        int index$iv = 0;
        Iterator var5 = $this$indexOfFirst$iv.iterator();

        int var10000;
        while (true) {
            if (!var5.hasNext()) {
                var10000 = -1;
                break;
            }
            Object item$iv = var5.next();
            ExoPlayerItem it = (ExoPlayerItem) item$iv;
            var10000 = it.getPosition();

            if (var10000 == vp2Video.getCurrentItem()) {
                var10000 = index$iv;
                break;
            }
            ++index$iv;
        }

        int index = var10000;
        if (index != -1) {
            ExoPlayer player = ((ExoPlayerItem) this.listItem.get(index)).getExoPlayer();
            player.pause();
            player.setPlayWhenReady(false);
        }
    }

    public void onResume() {
        super.onResume();

        mainActivity.setToolBarMainInvisible();
        mainActivity.tbHide();

        Log.d("duc1", "onResume2");
        int index$iv = 0;
        Iterator var5 = listItem.listIterator();

        int var10000;
        while (true) {
            if (!var5.hasNext()) {
                var10000 = -1;
                break;
            }
            Object item$iv = var5.next();
            ExoPlayerItem it = (ExoPlayerItem) item$iv;
            var10000 = it.getPosition();
            if (var10000 == vp2Video.getCurrentItem()) {
                var10000 = index$iv;
                break;
            }
            ++index$iv;
        }

        int index = var10000;
        if (index != -1) {
            ExoPlayer player = ((ExoPlayerItem) this.listItem.get(index)).getExoPlayer();
            player.setPlayWhenReady(true);
            player.play();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d("duc1", "onDetach2");
        Collection var1 = (Collection)this.listItem;
        if (!var1.isEmpty()) {
            Iterator var2 = this.listItem.iterator();

            while(var2.hasNext()) {
                ExoPlayerItem item = (ExoPlayerItem)var2.next();
                ExoPlayer player = item.getExoPlayer();
                player.stop();
                player.clearMediaItems();
            }
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("duc1", "onDestroy2");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d("duc1", "onStop2");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("duc1", "onStart2");


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d("duc1", "onDestroyView2");
        Collection var1 = (Collection)this.listItem;
        if (!var1.isEmpty()) {
            Iterator var2 = this.listItem.iterator();

            while(var2.hasNext()) {
                ExoPlayerItem item = (ExoPlayerItem)var2.next();
                ExoPlayer player = item.getExoPlayer();
                player.stop();
                player.clearMediaItems();
            }
        }
    }
    private void calLApiVideoShortRandom(String nextPageToken, String maxResults, String order) {
        ApiServicePlayList.apiServicePlayList.videoUpdateNews(
                nextPageToken,
                "snippet",
                "id",
                null,
                order,
                "shorts vn",
                "video",
                "vn",
                Util.API_KEY,
                maxResults
        ).enqueue(new Callback<Search>() {
            @Override
            public void onResponse(Call<Search> call, Response<Search> response) {
                String urlThumbnails = "", titleVideo = "", publishAt = "",
                        viewCount = "", idVideo = "", likeCount = "", commentCount = "",
                        idChannel = "", urlLogoChannel = "", titleChannel = "", subCount = "";
                Search video = response.body();
                if (video != null) {
                    List<ItemsSearch> listItem = video.getItems();
                    for (int i = 0; i < listItem.size(); i++) {
                        if (listItem.get(i).getSnippet().getThumbnails().getMaxres() != null) {
                            urlThumbnails = listItem.get(i).getSnippet().getThumbnails().getMaxres().getUrl();
                        } else if (listItem.get(i).getSnippet().getThumbnails().getStandard() != null) {
                            urlThumbnails = listItem.get(i).getSnippet().getThumbnails().getStandard().getUrl();
                        }else {
                            urlThumbnails = listItem.get(i).getSnippet().getThumbnails().getHigh().getUrl();
                        }
                        titleVideo = listItem.get(i).getSnippet().getTitle();
                        publishAt = listItem.get(i).getSnippet().getPublishedAt();
                        idVideo = listItem.get(i).getId().getVideoId();
                        idChannel = listItem.get(i).getSnippet().getChannelId();
                        titleChannel = listItem.get(i).getSnippet().getChannelTitle();
                        callApiViewCountVideo(idVideo, listItems, i);
                        callApiChannelFull(idChannel, listItems, i);

                        listItems.add(new VideoItem(urlThumbnails, "", titleVideo,
                                publishAt, titleChannel, "", idVideo, "",
                                "", idChannel, "", "", ""));
                    }
                }
            }

            @Override
            public void onFailure(Call<Search> call, Throwable t) {

            }
        });
    }

    private void callApiViewCountVideo(String idVideo, List<VideoItem> listItemV,
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
                String viewCount = "", cmtCount = "", likeCount = "";
                DetailVideo video = response.body();
                if (video != null) {
                    ArrayList<ItemVideo> listItem = video.getItems();
                    for (int i = 0; i <listItem.size(); i++ ) {
                        viewCount = listItem.get(0).getStatistics().getViewCount();
                        cmtCount = listItem.get(0).getStatistics().getCommentCount();
                        likeCount = listItem.get(0).getStatistics().getLikeCount();
                        listItemV.get(pos).setViewCountVideo(viewCount);
                        if (listItem.get(0).getStatistics().getLikeCount() == null) {
                            listItemV.get(pos).setLikeCountVideo("");
                        } else {
                            listItemV.get(pos).setLikeCountVideo(likeCount);
                        }
                        listItemV.get(pos).setCommentCount(cmtCount);
                    }
                    adapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onFailure(Call<DetailVideo> call, Throwable t) {

            }
        });
    }

    //           Api thông tin channel;
    public void callApiChannelFull(String idChannel1, List<VideoItem> listItemS, int pos) {
        ApiServicePlayList.apiServicePlayList.infoChannelFull(
                "contentDetails",
                "snippet",
                "statistics",
                "topicDetails",
                "brandingSettings",
                idChannel1,
                Util.API_KEY
        ).enqueue(new Callback<Channel>() {
            @Override
            public void onResponse(Call<Channel> call, Response<Channel> response) {
                String subCount = "",
                        urlLogoChannel = "";
                boolean isCheckHideSub = false;
                Channel channel = response.body();
                if (channel != null) {
                    ArrayList<Itemss> listItem = channel.getItems();
                    Itemss item = listItem.get(0);
                    isCheckHideSub = item.getStatistics().isHiddenSubscriberCount();
                    if (isCheckHideSub) {
                        subCount = "";
                    } else {
                        subCount = item.getStatistics().getSubscriberCount();
                    }
                    if (item.getSnippet().getThumbnails().getHigh() == null) {
                        urlLogoChannel = item.getSnippet().getThumbnails().getMedium().getUrl();
                    } else {
                        urlLogoChannel = item.getSnippet().getThumbnails().getHigh().getUrl();
                    }
                    listItemS.get(pos).setSubCount(subCount);
                    listItemS.get(pos).setUrlLogoChannel(urlLogoChannel);

                    adapter.notifyItemChanged(pos);
                }
            }

            @Override
            public void onFailure(Call<Channel> call, Throwable t) {
            }
        });
    }

    // Open Bottom Sheet Comment From Shorts Video
    private void openBottomSheetDiaLogComment(String idVideo, String cmtCount) {
        BottomSheetDialogCommentFragment bottomSheetDialogCommentFragment =
                BottomSheetDialogCommentFragment.newInstance(idVideo, cmtCount);
        bottomSheetDialogCommentFragment.show(getChildFragmentManager(),
                BottomSheetDialogCommentFragment.TAG);
    }

}