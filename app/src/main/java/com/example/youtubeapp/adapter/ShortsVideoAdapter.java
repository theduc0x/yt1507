package com.example.youtubeapp.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.youtubeapp.R;
import com.example.youtubeapp.model.itemrecycleview.VideoItem;
import com.example.youtubeapp.model.shortsvideo.ExoPlayerItem;
import com.example.youtubeapp.my_interface.IItemOnClickOpenCommentFromShortsVideo;
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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

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
    IItemOnClickOpenCommentFromShortsVideo onClickOpenCommentFromShortsVideo;

    public ShortsVideoAdapter(Context context, OnVideoPreparedListener onVideoPreparedListener,
                              IItemOnClickOpenCommentFromShortsVideo onClickOpenCommentFromShortsVideo) {
        this.context = context;
        this.onVideoPreparedListener = onVideoPreparedListener;
        this.onClickOpenCommentFromShortsVideo = onClickOpenCommentFromShortsVideo;
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
        LinearLayout llOpenCommentSheet;
        View vPause;
        ImageView ivPause;

        public ShortsViewHolder(@NonNull View itemView) {
            super(itemView);
            spvVideo = itemView.findViewById(R.id.spv_video);
            tvLike = itemView.findViewById(R.id.tv_like_count_shorts);
            tvCmtCount = itemView.findViewById(R.id.tv_comment_count_shorts);
            tvTitleChannel = itemView.findViewById(R.id.tv_title_channel_shorts);
            tvDesc = itemView.findViewById(R.id.tv_desc_shorts);
            civLogoChannel = itemView.findViewById(R.id.civ_logo_channel_shorts);
            pbLoading = itemView.findViewById(R.id.pb_loading_shorts);
            llOpenCommentSheet = itemView.findViewById(R.id.ll_open_comment_sheet);
            vPause = itemView.findViewById(R.id.v_pause_play_shorts);
            ivPause = itemView.findViewById(R.id.iv_pause);
        }
        public void setData(VideoItem item) {
            String urlLogoChannel = item.getUrlLogoChannel();
            String titleChannel = item.getTvTitleChannel();
            String titleVideo = item.getTvTitleVideo();
            String likeCount = item.getLikeCountVideo();
            String cmtCount = item.getCommentCount();
            idVideo = item.getIdVideo();
            String idVideoS = item.getIdVideo();
            String url = "https://www.youtube.com/watch?v=" + idVideo;
            String idChannel = item.getIdChannel();

            tvDesc.setText(titleVideo);
            if (cmtCount.equals("")) {
                tvCmtCount.setVisibility(View.GONE);
            } else {
                tvCmtCount.setVisibility(View.VISIBLE);
                tvCmtCount.setText(Util.convertViewCount(Double.parseDouble(cmtCount)));
            }

            tvTitleChannel.setText(titleChannel);
            if (!likeCount.equals("")) {
                tvLike.setVisibility(View.VISIBLE);
                tvLike.setText(Util.convertViewCount(Double.parseDouble(likeCount)));
            } else {
                tvLike.setVisibility(View.GONE);
            }

            if (!urlLogoChannel.equals("")) {
                Picasso.get().load(urlLogoChannel).into(civLogoChannel);
            }

            llOpenCommentSheet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickOpenCommentFromShortsVideo.onCLickOpenCommentShorts(
                            idVideoS, cmtCount
                    );
                }
            });

            vPause.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (exoPlayer.isPlaying()) {
                        ivPause.setVisibility(View.VISIBLE);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                ivPause.setVisibility(View.GONE);
                            }
                        }, 500);
                        exoPlayer.pause();
                    } else {
                        ivPause.setVisibility(View.GONE);
                        exoPlayer.play();
                    }
                }
            });


            setVideoPath(url);
        }

        public void setPause() {
            ivPause.setVisibility(View.GONE);
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
                            if (ytFiles.get(136) == null) {
                                if (ytFiles.get(135) == null) {
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
            }.extract(youtubeUrl);
        }
    }

    }





