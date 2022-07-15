package com.example.youtubeapp.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.youtubeapp.R;
import com.example.youtubeapp.activitys.MainActivity;
import com.example.youtubeapp.fragment.ShortsFragment;
import com.example.youtubeapp.model.itemrecycleview.VideoItem;
import com.example.youtubeapp.model.shortsvideo.ExoPlayerItem;
import com.example.youtubeapp.my_interface.AddLifecycleCallbackListener;
import com.example.youtubeapp.my_interface.OnVideoPreparedListener;
import com.example.youtubeapp.utiliti.Util;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MergingMediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSource;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayerFragment;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.utils.YouTubePlayerUtils;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.ui.PlayerUiController;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import at.huber.youtubeExtractor.VideoMeta;
import at.huber.youtubeExtractor.YouTubeExtractor;
import at.huber.youtubeExtractor.YtFile;
import de.hdodenhof.circleimageview.CircleImageView;

public class ShortsVideoAdapter extends RecyclerView.Adapter<ShortsVideoAdapter.ShortsViewHolder> {
    ArrayList<VideoItem> listItems;
    String idVideo;
    Context context;
    int id ;
    OnVideoPreparedListener onVideoPreparedListener;

    public ShortsVideoAdapter(Context context, OnVideoPreparedListener onVideoPreparedListener) {
        this.context = context;
        this.onVideoPreparedListener = onVideoPreparedListener;
    }



    public void setData(ArrayList<VideoItem> listItems) {
        this.listItems = listItems;
    }
    @NonNull
    @Override
    public ShortsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_video_shorts, parent, false);
        id = View.generateViewId();
        return new ShortsViewHolder(view);
    }

    @SuppressLint("ResourceType")
    @Override
    public void onBindViewHolder(@NonNull ShortsViewHolder holder, int position) {
        VideoItem item = listItems.get(position);
        if (item == null) {
            return;
        }
        holder.setData(item);

    }

    @Override
    public int getItemCount() {
        if (listItems != null) {
            return listItems.size();
        }
        return 0;
    }

    class ShortsViewHolder extends RecyclerView.ViewHolder{
        StyledPlayerView spvVideo;
        TextView tvLike, tvCmtCount, tvTitleChannel, tvDesc;
        CircleImageView civLogoChannel;
        ProgressBar pbLoading;
        ExoPlayer exoPlayer;
        MediaSource mediaSourceAudio, mediaSourceVideo;
        public ShortsViewHolder(@NonNull View itemView) {
            super(itemView);
            spvVideo = itemView.findViewById(R.id.spv_video);
            tvLike = itemView.findViewById(R.id.tv_like_count_shorts);
            tvCmtCount = itemView.findViewById(R.id.tv_comment_count_shorts);
            tvTitleChannel = itemView.findViewById(R.id.tv_title_channel_shorts);
            tvDesc = itemView.findViewById(R.id.tv_desc_shorts);
            civLogoChannel = itemView.findViewById(R.id.civ_logo_channel_shorts);
            pbLoading = itemView.findViewById(R.id.pb_loading_shorts);
        }
        public void setData(VideoItem item) {
            String urlLogoChannel = item.getUrlLogoChannel();
            String titleChannel = item.getTvTitleChannel();
            String titleVideo = item.getTvTitleVideo();
            idVideo = item.getIdVideo();
            String url = "https://www.youtube.com/watch?v=" + idVideo;


            String idChannel = item.getIdChannel();

            tvDesc.setText(titleVideo);
            tvCmtCount.setText("cmtCount");
            tvTitleChannel.setText(titleChannel);
            tvLike.setText("likeCount");
            Picasso.get().load(urlLogoChannel).into(civLogoChannel);
            setVideoPath(url);
        }
        public void setVideoPath(String url) {
            exoPlayer = new ExoPlayer.Builder(context).build();
            exoPlayer.addListener(new Player.Listener() {
                @Override
                public void onPlayerError(PlaybackException error) {
                    Player.Listener.super.onPlayerError(error);
                    Toast.makeText(context, "Can't play video", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                    Player.Listener.super.onPlayerStateChanged(playWhenReady, playbackState);
                    if (playbackState == Player.STATE_BUFFERING) {
                        pbLoading.setVisibility(View.VISIBLE);
                    } else if (playbackState == Player.STATE_READY) {
                        pbLoading.setVisibility(View.GONE);
                    }
                }
            });

            spvVideo.setPlayer(exoPlayer);
            playVideoYoutube(url);

            onVideoPreparedListener.onVideoPrepared(new ExoPlayerItem(exoPlayer, getAbsoluteAdapterPosition()));

        }

        @SuppressLint("StaticFieldLeak")
        private void playVideoYoutube(String youtubeUrl) {
            new YouTubeExtractor(context) {

                @Override
                protected void onExtractionComplete(SparseArray<YtFile> ytFiles, VideoMeta videoMeta) {
                    String videoUrl = "";
                    if (ytFiles != null) {
                        int videoTag = 137;
                        int audioTag = 140;

                        List<Integer> iTags = new ArrayList<>();
                        iTags.add(18);
                        iTags.add(22);
                        iTags.add(137);

                        if (ytFiles.get(137) == null) {
                            if (ytFiles.get(136).getUrl() == null) {
                                if (ytFiles.get(135).getUrl() == null) {
                                    videoUrl = ytFiles.get(134).getUrl();
                                } else
                                {
                                    videoUrl = ytFiles.get(135).getUrl();
                                }
                            } else {
                                videoUrl = ytFiles.get(136).getUrl();
                            }
                        } else {
                            videoUrl = ytFiles.get(137).getUrl();
                        }

                        DefaultDataSource.Factory dataSource =
                                new DefaultDataSource.Factory(context);
                        mediaSourceAudio = new ProgressiveMediaSource.Factory(
                                new DefaultDataSource.Factory(context)).createMediaSource(
                                MediaItem.fromUri(videoUrl));
                        mediaSourceVideo = new ProgressiveMediaSource.Factory(
                                new DefaultDataSource.Factory(context)).createMediaSource(
                                MediaItem.fromUri(ytFiles.get(audioTag).getUrl()));
                        exoPlayer.setMediaSource(new MergingMediaSource(
                                        true,
                                        mediaSourceVideo,
                                        mediaSourceAudio),
                                true
                        );
                        exoPlayer.prepare();
                        exoPlayer.seekTo(0);
                        exoPlayer.setRepeatMode(Player.REPEAT_MODE_ONE);

                        if (getAbsoluteAdapterPosition() == 0) {
                            // sẵn sang phát khi quay lại
                            exoPlayer.setPlayWhenReady(true);
                            exoPlayer.play();
                        }
                    }
                }
            }.extract(youtubeUrl, false, true);
        }
    }

    }





