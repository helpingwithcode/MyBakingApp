package com.helpingiwthcode.mybakingapp.fragments;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.helpingiwthcode.mybakingapp.util.BroadcastUtils;
import com.helpingiwthcode.mybakingapp.util.RecipeUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

/**
 * Created by helpingwithcode on 17/12/17.
 */

public class RecipeStepFragment extends Fragment{
    private int recipeId, stepId;
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
    private SimpleExoPlayer mExoPlayer;
    private boolean isTabletDevice;
    public long exoPlayerCurrentPosition = 0;

    public RecipeStepFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_recipe_step, container, false);
        ButterKnife.bind(this,rootView);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        isTabletDevice = getResources().getBoolean(R.bool.isTablet);
        checkSavedInstance(savedInstanceState);
        getStepIntent();
        setVideoHolderSize();
    }

    private void checkSavedInstance(Bundle savedInstanceState) {
        Timber.e("Checking savedInstance");
        if(savedInstanceState != null){
            //long videoPosition = savedInstanceState.getLong("playerPosition");
            exoPlayerCurrentPosition = savedInstanceState.getLong("playerPosition");
            //mExoPlayer.seekTo(videoPosition);
            Timber.e("mExoPlayer. will seekTo("+exoPlayerCurrentPosition+");");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Timber.e("Saving player position: "+exoPlayerCurrentPosition);
        outState.putLong("playerPosition", exoPlayerCurrentPosition);
    }

    private void getStepIntent() {
        Bundle extra = getArguments();
        if (extra != null) {
            recipeId = extra.getInt(RecipeUtils.RECIPE_ID, 0);
            stepId = extra.getInt(RecipeUtils.STEP_ID, 0);
            populateLayout();
        }
    }

    private void populateLayout() {
        String videoUrl;
        thisStep = idaoSteps.getStepFromRecipe(recipeId, stepId);
        videoUrl = thisStep.getVideoURL();
        shortDescriptionTv.setText(thisStep.getShortDescription() + ((stepId != 0) ? " - " + stepId + " of " + getStepCount() : ""));
        stepTv.setText(thisStep.getDescription());
        setButtonsLayout();
        if (!videoUrl.isEmpty())
            setExoPlayer(videoUrl);
        else
            mPlayerView.setVisibility(View.GONE);
    }

    private int getStepCount() {
        return idaoSteps.getStepCount(recipeId);
    }

    private void setButtonsLayout() {
        boolean isFirstStep, isLastStep;
        isFirstStep = (stepId == 0);
        if (!isFirstStep) {
            isLastStep = idaoSteps.isLastStep(recipeId, stepId);
            if (isLastStep)
                nextStepBt.setVisibility(View.GONE);
        } else
            previousStepBt.setVisibility(View.GONE);
    }

    private void setVideoHolderSize() {
        boolean isLandscape = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
        if(isLandscape && !isTabletDevice){
            Display display = getActivity().getWindowManager().getDefaultDisplay();
            int height = display.getHeight();
            mPlayerView.setMinimumHeight(height);
        }
    }

    private void setExoPlayer(String videoUrl) {
        initializePlayer(videoUrl);
    }

    private void initializePlayer(String recipeVideoUrl) {
        if (mExoPlayer == null) {
            TrackSelector trackSelector = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();
            mExoPlayer = ExoPlayerFactory.newSimpleInstance(getContext(), trackSelector, loadControl);
            Timber.e("exoPlayerCurrentPosition: "+exoPlayerCurrentPosition);
            if(exoPlayerCurrentPosition != 0)
                mExoPlayer.seekTo(exoPlayerCurrentPosition);
            Timber.e("initializePlayer\nmExoPlayer.seekTo("+exoPlayerCurrentPosition+");");
            mPlayerView.setPlayer(mExoPlayer);
            String userAgent = Util.getUserAgent(getContext(), RecipeUtils.APP_NAME);
            MediaSource mediaSource = new ExtractorMediaSource(Uri.parse(recipeVideoUrl), new DefaultDataSourceFactory(
                    getContext(), userAgent), new DefaultExtractorsFactory(), null, null);
            mExoPlayer.prepare(mediaSource);
            mExoPlayer.setPlayWhenReady(true);
        }
    }

    @OnClick({R.id.bt_previous_step, R.id.bt_next_step})
    public void onButtonClick(View view) {
        switch (view.getId()) {
            case R.id.bt_next_step:
                showStep(stepId, "next");
                break;
            case R.id.bt_previous_step:
                showStep(stepId, "previous");
                break;
        }
    }

    private void showStep(int stepIndex, String direction) {
        int newStepId = (direction.equals("next")) ? stepIndex + 1 : stepIndex - 1;
        if (idaoSteps.isStepAvailable(recipeId, stepIndex, direction)) {
            releasePlayer();
            Intent stepIntent = new Intent(RecipeUtils.BROADCAST_STEP_CLICKED);
            stepIntent.putExtra(RecipeUtils.RECIPE_ID, recipeId);
            stepIntent.putExtra(RecipeUtils.STEP_ID, newStepId);
            BroadcastUtils.sendBroadcast(getContext(), stepIntent);
        } else {
            showStep(newStepId, direction);
        }
    }

    private void releasePlayer() {
        try {
            exoPlayerCurrentPosition = mExoPlayer.getCurrentPosition();
            mExoPlayer.stop();
            mExoPlayer.release();
            mExoPlayer = null;
        } catch (Exception e) {
            Timber.e("Exception thrown on releasePlayer: " + e.getLocalizedMessage());
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onPause() {
        super.onPause();
        releasePlayer();
    }
}