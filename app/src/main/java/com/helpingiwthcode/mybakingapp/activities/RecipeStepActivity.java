package com.helpingiwthcode.mybakingapp.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.media.session.MediaButtonReceiver;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.helpingiwthcode.mybakingapp.R;
import com.helpingiwthcode.mybakingapp.dao.DAOSteps;
import com.helpingiwthcode.mybakingapp.idao.IDAOSteps;
import com.helpingiwthcode.mybakingapp.model.Steps;

import org.w3c.dom.Text;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

public class RecipeStepActivity extends AppCompatActivity implements ExoPlayer.EventListener {

    private int recipeId,stepId;
    IDAOSteps idaoSteps = new DAOSteps();
    Steps thisStep;

    @BindView(R.id.ep_video)
    SimpleExoPlayerView mPlayerView;
    @BindView(R.id.tv_recipe_step)
    TextView stepTv;
    @BindView(R.id.tv_step_short)
    TextView shortDescriptionTv;
    @BindView(R.id.bt_next_step)
    Button nextStepBt;
    @BindView(R.id.bt_previous_step)
    Button previousStepBt;
    private static MediaSessionCompat mMediaSession;
    private PlaybackStateCompat.Builder mStateBuilder;
    private SimpleExoPlayer mExoPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_step);
        ButterKnife.bind(this);
        getStepIntent();
        //TODO set activity name
    }

    @OnClick({R.id.bt_previous_step, R.id.bt_next_step})
    public void onButtonClick(View view){
        switch (view.getId()){
            case R.id.bt_next_step:
                showStep(stepId+1);
                break;
            case R.id.bt_previous_step:
                showStep(stepId-1);
                break;
        }
    }

    private void showStep(int stepIndex) {
        Intent stepIntent = new Intent(this, RecipeStepActivity.class);
        stepIntent.putExtra("recipeId",recipeId);
        stepIntent.putExtra("stepId",stepIndex);
        startActivity(stepIntent);
        finish();
    }

    private void getStepIntent() {
        Bundle extra = getIntent().getExtras();
        if(extra != null){
            recipeId = extra.getInt("recipeId",0);
            stepId = extra.getInt("stepId",0);
            populateLayout();
        }
    }

    private void populateLayout() {
        String videoUrl;
        thisStep = idaoSteps.getStepFromRecipe(recipeId,stepId);
        videoUrl = thisStep.getVideoURL();
        shortDescriptionTv.setText(thisStep.getShortDescription());
        stepTv.setText(thisStep.getDescription());
        setButtonsLayout();
        if(!videoUrl.isEmpty())
            setExoPlayer(videoUrl);
        else
            mPlayerView.setVisibility(View.GONE);
    }

    private void setButtonsLayout() {
        boolean isFirstStep, isLastStep;
        isFirstStep = (stepId == 0);
        if(!isFirstStep){
            isLastStep = idaoSteps.isLastStep(recipeId,stepId);
            if(isLastStep)
                nextStepBt.setVisibility(View.GONE);
        }
        else
            previousStepBt.setVisibility(View.GONE);
    }

    private void setExoPlayer(String videoUrl) {
        initializeMediaSession();
        initializePlayer(videoUrl);
    }

    private void initializeMediaSession() {

        // Create a MediaSessionCompat.
        mMediaSession = new MediaSessionCompat(this, "Recipe");

        // Enable callbacks from MediaButtons and TransportControls.
        mMediaSession.setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                        MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        // Do not let MediaButtons restart the player when the app is not visible.
        mMediaSession.setMediaButtonReceiver(null);

        // Set an initial PlaybackState with ACTION_PLAY, so media buttons can start the player.
        mStateBuilder = new PlaybackStateCompat.Builder()
                .setActions(
                        PlaybackStateCompat.ACTION_PLAY |
                                PlaybackStateCompat.ACTION_PAUSE |
                                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                                PlaybackStateCompat.ACTION_PLAY_PAUSE);

        mMediaSession.setPlaybackState(mStateBuilder.build());
        mMediaSession.setCallback(new MySessionCallback());
        mMediaSession.setActive(true);

    }

    private void initializePlayer(String recipeVideoUrl) {
        if (mExoPlayer == null) {
            // Create an instance of the ExoPlayer.
            TrackSelector trackSelector = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();
            mExoPlayer = ExoPlayerFactory.newSimpleInstance(this, trackSelector, loadControl);
            mPlayerView.setPlayer(mExoPlayer);

            // Set the ExoPlayer.EventListener to this activity.
            mExoPlayer.addListener(this);

            // Prepare the MediaSource.
            String userAgent = Util.getUserAgent(this, "ClassicalMusicQuiz");
            MediaSource mediaSource = new ExtractorMediaSource(Uri.parse(recipeVideoUrl), new DefaultDataSourceFactory(
                    this, userAgent), new DefaultExtractorsFactory(), null, null);
            mExoPlayer.prepare(mediaSource);
            mExoPlayer.setPlayWhenReady(true);
        }
    }
    //
    private void releasePlayer() {
        mExoPlayer.stop();
        mExoPlayer.release();
        mExoPlayer = null;
    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest) {
    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
    }

    @Override
    public void onLoadingChanged(boolean isLoading) {
    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if((playbackState == ExoPlayer.STATE_READY) && playWhenReady){
            mStateBuilder.setState(PlaybackStateCompat.STATE_PLAYING,
                    mExoPlayer.getCurrentPosition(), 1f);
        } else if((playbackState == ExoPlayer.STATE_READY)){
            mStateBuilder.setState(PlaybackStateCompat.STATE_PAUSED,
                    mExoPlayer.getCurrentPosition(), 1f);
        }
        mMediaSession.setPlaybackState(mStateBuilder.build());
        //showNotification(mStateBuilder.build());
    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {
    }

    @Override
    public void onPositionDiscontinuity() {
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

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
        public void onSkipToPrevious() {
            mExoPlayer.seekTo(0);
        }
    }

    public static class MediaReceiver extends BroadcastReceiver {

        public MediaReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            MediaButtonReceiver.handleIntent(mMediaSession, intent);
        }
    }
}
