package com.yalematta.android.bakingproject.fragments;


import android.app.Dialog;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlaybackControlView;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.yalematta.android.bakingproject.entities.Step;
import com.yalematta.android.bakingproject.R;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by yalematta on 1/9/18.
 */

public class StepFragment extends Fragment implements Player.EventListener {

    //region Variables definitions

    private long position;

    @BindView(R.id.tvDesc)
    TextView tvDesc;
    @BindView(R.id.tvShortDesc)
    TextView tvShortDesc;
    @BindView(R.id.main_media_frame)
    FrameLayout mediaFrame;
    @BindView(R.id.playerView)
    SimpleExoPlayerView mPlayerView;
    @BindView(R.id.ivPicture)
    ImageView stepPicture;

    private Step clickedStep;
    private SimpleExoPlayer mExoPlayer;

    private boolean mExoPlayerFullScreen = false;
    private FrameLayout mFullScreenButton;
    private ImageView mFullScreenIcon;
    private Dialog mFullScreenDialog;

    private PlaybackStateCompat.Builder mStateBuilder;
    private static MediaSessionCompat mMediaSession;

    private MediaSource mVideoSource;

    private boolean fragmentResume = false;
    private boolean fragmentVisible = false;
    private boolean fragmentOnCreated = false;

    //endregion

    public static final StepFragment newInstance(Step step) {
        StepFragment f = new StepFragment();
        Bundle bdl = new Bundle();
        bdl.putParcelable("CLICKED_STEP", step);
        f.setArguments(bdl);
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        clickedStep = getArguments().getParcelable("CLICKED_STEP");

        View v = inflater.inflate(R.layout.fragment_step, container, false);
        ButterKnife.bind(this, v);

        position = C.TIME_UNSET;

        if (savedInstanceState != null) {
            position = savedInstanceState.getLong("SELECTED_POSITION", C.TIME_UNSET);
        }

        return v;
    }

    //region Initialization methods

    private void initMediaSession() {
        mMediaSession = new MediaSessionCompat(getContext(), this.getClass().getSimpleName());
        mMediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS | MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        // Do not let MediaButtons restart the player when the app is not visible.
        mMediaSession.setMediaButtonReceiver(null);

        mStateBuilder = new PlaybackStateCompat.Builder().setActions(
                PlaybackStateCompat.ACTION_PLAY |
                        PlaybackStateCompat.ACTION_PAUSE |
                        PlaybackStateCompat.ACTION_PLAY_PAUSE |
                        PlaybackStateCompat.ACTION_REWIND |
                        PlaybackStateCompat.ACTION_FAST_FORWARD);

        mMediaSession.setPlaybackState(mStateBuilder.build());
        mMediaSession.setCallback(new MySessionCallback());
        mMediaSession.setActive(true);
    }

    private void initDataInView() {

        if (!TextUtils.isEmpty(clickedStep.getThumbnailURL())) {
            Glide.with(getContext()).load(clickedStep.getThumbnailURL()).into(stepPicture);
        }

        if (TextUtils.isEmpty(clickedStep.getVideoURL())) {
            mediaFrame.setVisibility(View.GONE);
        }

        if (clickedStep.getDescription().equals(clickedStep.getShortDescription())) {
            tvDesc.setVisibility(View.GONE);
            tvShortDesc.setVisibility(View.VISIBLE);
            tvShortDesc.setText(clickedStep.getShortDescription());
        } else {
            tvDesc.setVisibility(View.VISIBLE);
            tvShortDesc.setVisibility(View.VISIBLE);
            tvDesc.setText(clickedStep.getDescription());
            tvShortDesc.setText(clickedStep.getShortDescription());
        }
    }

    private void initExoPlayer() {

        if (mPlayerView != null) {

            if (mExoPlayer == null) {

                initFullScreenDialog();
                initFullScreenButton();

                //Create an instance of the ExoPlayer
                BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
                TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
                TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
                LoadControl loadControl = new DefaultLoadControl();
                mExoPlayer = ExoPlayerFactory.newSimpleInstance(new DefaultRenderersFactory(getContext()), trackSelector, loadControl);
                mPlayerView.setPlayer(mExoPlayer);

                String streamUrl = clickedStep.getVideoURL();
                String userAgent = Util.getUserAgent(getContext(), getActivity().getApplicationContext().getApplicationInfo().packageName);
                DefaultHttpDataSourceFactory httpDataSourceFactory = new DefaultHttpDataSourceFactory(userAgent, null, DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS, DefaultHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS, true);
                DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(getContext(), null, httpDataSourceFactory);
                Uri mediaUri = Uri.parse(streamUrl);

                mVideoSource = new ExtractorMediaSource(mediaUri, dataSourceFactory, new DefaultExtractorsFactory(), null, null);
                mExoPlayer.prepare(mVideoSource);

                if (position != C.TIME_UNSET) mPlayerView.getPlayer().seekTo(position);

                mExoPlayer.setPlayWhenReady(true);
                mExoPlayer.addListener(this);

                initDataInView();
            }
        }
    }

    //endregion

    //region FullScreen methods

    private void initFullScreenDialog() {
        mFullScreenDialog = new Dialog(getContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen) {
            public void onBackPressed() {
                if (mExoPlayerFullScreen)
                    closeFullScreenDialog();
                super.onBackPressed();
            }
        };
    }

    private void openFullScreenDialog() {
        ((ViewGroup) mPlayerView.getParent()).removeView(mPlayerView);
        mFullScreenDialog.addContentView(mPlayerView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        mFullScreenIcon.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_fullscreen_skrink));
        mExoPlayerFullScreen = true;
        mFullScreenDialog.show();
    }

    private void closeFullScreenDialog() {
        ((ViewGroup) mPlayerView.getParent()).removeView(mPlayerView);

        mediaFrame.addView(mPlayerView);
        mExoPlayerFullScreen = false;
        mFullScreenDialog.dismiss();
        mFullScreenIcon.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_fullscreen_expand));
    }

    private void initFullScreenButton() {

        PlaybackControlView controlView = mPlayerView.findViewById(R.id.exo_controller);
        mFullScreenIcon = controlView.findViewById(R.id.exo_fullscreen_icon);
        mFullScreenButton = controlView.findViewById(R.id.exo_fullscreen_button);
        mFullScreenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mExoPlayerFullScreen)
                    openFullScreenDialog();
                else
                    closeFullScreenDialog();
            }
        });
    }

    //endregion

    //region Play.EventListener methods

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {

    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if ((playbackState == Player.STATE_READY) && playWhenReady) {
            mStateBuilder.setState(PlaybackStateCompat.STATE_PLAYING,
                    mExoPlayer.getCurrentPosition(), 1f);
        } else if ((playbackState == Player.STATE_READY)) {
            mStateBuilder.setState(PlaybackStateCompat.STATE_PAUSED,
                    mExoPlayer.getCurrentPosition(), 1f);
        }
        mMediaSession.setPlaybackState(mStateBuilder.build());
    }

    @Override
    public void onRepeatModeChanged(int repeatMode) {

    }

    @Override
    public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

    }

    @Override
    public void onPositionDiscontinuity(int reason) {

    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

    }

    @Override
    public void onSeekProcessed() {

    }
    //endregion

    //region When Fragment is paused or visible or not visible methods
    @Override
    public void setUserVisibleHint(boolean visible) {
        super.setUserVisibleHint(visible);
        if (visible && isResumed()) {   // only at fragment screen is resumed
            fragmentResume = true;
            fragmentVisible = false;
            fragmentOnCreated = true;
            whenFragmentVisible();
        } else if (visible) {        // only at fragment onCreated
            fragmentResume = false;
            fragmentVisible = true;
            fragmentOnCreated = true;
        } else if (!visible && fragmentOnCreated) { // only when you go out of fragment screen
            fragmentVisible = false;
            fragmentResume = false;
            whenFragmentNotVisible();
        }
    }

    private void whenFragmentVisible() {
        initMediaSession();
        initExoPlayer();
    }

    @Override
    public void onPause() {
        super.onPause();

        if (mPlayerView != null && mPlayerView.getPlayer() != null) {

            if (mExoPlayer != null) {
                position = mExoPlayer.getCurrentPosition();
                mExoPlayer.stop();
                mExoPlayer.release();
                mExoPlayer = null;
            }
        }

        if (mFullScreenDialog != null)
            mFullScreenDialog.dismiss();
    }

    private void whenFragmentNotVisible() {
        if (mPlayerView != null && mPlayerView.getPlayer() != null) {

            if (mExoPlayer != null) {
                mExoPlayer.stop();
                mExoPlayer.release();
                mExoPlayer = null;
            }
        }

        if (mFullScreenDialog != null)
            mFullScreenDialog.dismiss();
    }

    private class MySessionCallback extends MediaSessionCompat.Callback {
        @Override
        public void onPlay() {
            if (mExoPlayer != null)
                mExoPlayer.setPlayWhenReady(true);
        }

        @Override
        public void onPause() {
            if (mExoPlayer != null)
                mExoPlayer.setPlayWhenReady(false);
        }

        @Override
        public void onSkipToPrevious() {
            if (position != C.TIME_UNSET) mExoPlayer.seekTo(position);
        }
    }
    //endregion

    @Override
    public void onResume() {
        super.onResume();
        whenFragmentVisible();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putLong("SELECTED_POSITION", position);
    }

}
