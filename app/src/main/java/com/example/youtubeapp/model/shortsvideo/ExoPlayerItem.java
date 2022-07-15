package com.example.youtubeapp.model.shortsvideo;

import com.google.android.exoplayer2.ExoPlayer;

public class ExoPlayerItem {
    ExoPlayer exoPlayer;
    int position;

    public ExoPlayerItem(ExoPlayer exoPlayer, int position) {
        this.exoPlayer = exoPlayer;
        this.position = position;
    }

    public ExoPlayer getExoPlayer() {
        return exoPlayer;
    }

    public void setExoPlayer(ExoPlayer exoPlayer) {
        this.exoPlayer = exoPlayer;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
