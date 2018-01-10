package com.yalematta.android.bakingproject.Views;


import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.yalematta.android.bakingproject.Models.Step;
import com.yalematta.android.bakingproject.R;

import java.util.concurrent.ExecutionException;

/**
 * Created by yalematta on 1/9/18.
 */

public class StepFragment extends Fragment {

    private Step clickedStep;
    private Bitmap stepBitmap;
    private SimpleExoPlayer mExoPlayer;
    private TextView tvShortDesc, tvDesc;
    private SimpleExoPlayerView mPlayerView;
    private MediaSessionCompat mMediaSession;
    private PlaybackStateCompat.Builder mStateBuilder;

    public static final StepFragment newInstance(Step step) {
        StepFragment f = new StepFragment();
        Bundle bdl = new Bundle(1);
        bdl.putParcelable("CLICKED_STEP", step);
        f.setArguments(bdl);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        clickedStep = getArguments().getParcelable("CLICKED_STEP");

        View v = inflater.inflate(R.layout.fragment_step, container, false);
        mPlayerView = v.findViewById(R.id.playerView);
        tvShortDesc = v.findViewById(R.id.tvShortDesc);
        tvDesc = v.findViewById(R.id.tvDesc);

        if (!TextUtils.isEmpty(clickedStep.getThumbnailURL())) {

            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    Looper.prepare();
                    try {
                        stepBitmap = Glide.
                                with(getContext()).
                                load(clickedStep.getThumbnailURL()).
                                asBitmap().
                                into(100, 100). // Width and height
                                get();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Void dummy) {
                    if (null != stepBitmap) {
                        // The full bitmap should be available here
                        mPlayerView.setDefaultArtwork(stepBitmap);
                        Log.d("Glide: ", "Image loaded");
                    }
                }
            }.execute();
        }

        if (!TextUtils.isEmpty(clickedStep.getVideoURL())) {

            mMediaSession = new MediaSessionCompat(getContext(), this.getClass().getSimpleName());

            mMediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                    MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

            mMediaSession.setMediaButtonReceiver(null);

            mStateBuilder = new PlaybackStateCompat.Builder()
                    .setActions(
                            PlaybackStateCompat.ACTION_PLAY |
                                    PlaybackStateCompat.ACTION_PAUSE |
                                    PlaybackStateCompat.ACTION_PLAY_PAUSE |
                                    PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS);

            mMediaSession.setPlaybackState(mStateBuilder.build());

            mMediaSession.setCallback(new MySessionCallback());

            mMediaSession.setActive(true);

            initializePlayer(Uri.parse(clickedStep.getVideoURL()));
        }

        if (TextUtils.isEmpty(clickedStep.getVideoURL()) && TextUtils.isEmpty(clickedStep.getThumbnailURL())) {
            mPlayerView.setVisibility(View.GONE);
        }

        if(clickedStep.getDescription().equals(clickedStep.getShortDescription())){
            tvDesc.setVisibility(View.GONE);
        }
        else{
            tvDesc.setText(clickedStep.getDescription());
            tvShortDesc.setText(clickedStep.getShortDescription());
        }

        return v;
    }

    private void initializePlayer(Uri mediaUri) {
        if (mExoPlayer == null) {
            //Create an instance of the ExoPlayer
            TrackSelector trackSelector = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();
            mExoPlayer = ExoPlayerFactory.newSimpleInstance(getContext(), trackSelector, loadControl);
            mPlayerView.setPlayer(mExoPlayer);
            //Prepare the MediaSource
            String userAgent = Util.getUserAgent(getContext(), "BakingProject");
            MediaSource mediaSource = new ExtractorMediaSource(mediaUri, new DefaultDataSourceFactory(
                    getContext(), userAgent), new DefaultExtractorsFactory(), null, null);
            mExoPlayer.prepare(mediaSource);
            mExoPlayer.setPlayWhenReady(true);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e("Paused", String.valueOf(clickedStep.getStepId()));
        if (mExoPlayer != null) {
            releasePlayer();
            Log.e("Released Player", String.valueOf(clickedStep.getStepId()));
        }
    }

    public void releasePlayer() {
        mExoPlayer.stop();
        mExoPlayer.release();
        mExoPlayer = null;
    }

    private class MySessionCallback extends MediaSessionCompat.Callback {
        @Override
        public void onPlay() {
            mExoPlayer.setPlayWhenReady(true);
        }

        @Override
        public void onPause() {
            mExoPlayer.setPlayWhenReady(false);
        }

        @Override
        public void onSkipToPrevious(){
            mExoPlayer.seekTo(0);
        }
    }
}
