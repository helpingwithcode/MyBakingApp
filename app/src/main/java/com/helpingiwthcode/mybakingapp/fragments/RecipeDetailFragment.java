package com.helpingiwthcode.mybakingapp.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.media.session.MediaButtonReceiver;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.helpingiwthcode.mybakingapp.activities.RecipeDetailActivity;
import com.helpingiwthcode.mybakingapp.activities.RecipeStepActivity;
import com.helpingiwthcode.mybakingapp.adapters.RecipeAdapter;
import com.helpingiwthcode.mybakingapp.adapters.RecipeStepsAdapter;
import com.helpingiwthcode.mybakingapp.dao.DAOIngredients;
import com.helpingiwthcode.mybakingapp.dao.DAOSteps;
import com.helpingiwthcode.mybakingapp.idao.IDAOIngredients;
import com.helpingiwthcode.mybakingapp.idao.IDAOSteps;
import com.helpingiwthcode.mybakingapp.model.Ingredients;
import com.helpingiwthcode.mybakingapp.model.Recipe;
import com.helpingiwthcode.mybakingapp.model.Steps;
import com.helpingiwthcode.mybakingapp.util.Preferences;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * Created by helpingwithcode on 17/12/17.
 */

public class RecipeDetailFragment extends Fragment implements RecipeStepsAdapter.RecipeStepAdapterOnClick, ExoPlayer.EventListener{
    @BindView(R.id.ep_video) SimpleExoPlayerView mPlayerView;
    @BindView(R.id.rv_steps) RecyclerView stepsRv;
    @BindView(R.id.tv_ingredients) TextView ingredientsTv;
    private SimpleExoPlayer mExoPlayer;
    private static MediaSessionCompat mMediaSession;
    private PlaybackStateCompat.Builder mStateBuilder;
    private Recipe recipe;
    private int recipeId;
    private List<Steps> steps;
    private RecipeStepsAdapter stepsAdapter;
    private DividerItemDecoration mDividerItemDecoration;
    IDAOIngredients idaoIngredients = new DAOIngredients();
    IDAOSteps idaoSteps = new DAOSteps();
    private Preferences preferences;

    public RecipeDetailFragment() {
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_recipe_detail, container, false);
        ButterKnife.bind(this,rootView);
        preferences = new Preferences(getContext());
        getRecipeIntent();
        return rootView;
    }

    private void getRecipeIntent() {
        recipeId = preferences.rInt("recipeId");
        Timber.e("RecipeId from intent: " + recipeId);
        setSteps();
        setIngredients();
        setRecipeVideoIntroduction();
        //Timber.e("Bundle: "+arguments);
//        if(arguments != null){
//            recipeId = arguments.getInt("recipeId",0);
//            Timber.e("RecipeId from intent: "+recipeId);
//            setSteps();
//            setIngredients();
//            setRecipeVideoIntroduction();
//        }
    }

    private void setRecipeVideoIntroduction() {
        setVideoPlay();
    }

    private void setIngredients() {
        List<Ingredients> ingredientsList = idaoIngredients.getIngredientsFromRecipe(recipeId);
        String ingredientsText = "";
        String spacing = "";
        int ingredientIndex = 0;
        for(Ingredients ingredients : ingredientsList){
            ingredientIndex++;
            spacing = (ingredientIndex == ingredientsList.size()-1) ? "\n" : "\n\n";
            ingredientsText += ingredientIndex+": "+ingredients.getQuantity()+" "+ingredients.getMeasure()+" "+ingredients.getIngredient()+spacing;
        }
        ingredientsTv.setText(ingredientsText);
    }

    private void setSteps() {
        steps = idaoSteps.getStepsFromRecipe(recipeId);
        stepsAdapter = new RecipeStepsAdapter(this,steps);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        stepsRv.setLayoutManager(linearLayoutManager);
        stepsRv.setAdapter(stepsAdapter);
        mDividerItemDecoration = new DividerItemDecoration(
                stepsRv.getContext(),
                linearLayoutManager.getOrientation()
        );
        stepsRv.addItemDecoration(mDividerItemDecoration);
    }

    private void setVideoPlay() {
        //mPlayerView.setDefaultArtwork(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_recipe));

        initializeMediaSession();
        initializePlayer(idaoSteps.getVideoUrl(recipeId));
    }

    private void initializeMediaSession() {

        // Create a MediaSessionCompat.
        mMediaSession = new MediaSessionCompat(getContext(), "Recipe");

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
            mExoPlayer = ExoPlayerFactory.newSimpleInstance(getContext(), trackSelector, loadControl);
            mPlayerView.setPlayer(mExoPlayer);

            // Set the ExoPlayer.EventListener to this activity.
            mExoPlayer.addListener(this);

            // Prepare the MediaSource.
            String userAgent = Util.getUserAgent(getContext(), "ClassicalMusicQuiz");
            MediaSource mediaSource = new ExtractorMediaSource(Uri.parse(recipeVideoUrl), new DefaultDataSourceFactory(
                    getContext(), userAgent), new DefaultExtractorsFactory(), null, null);
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
    public void thisClick(int thisStepId, int thisRecipeId) {
        Intent stepIntent = new Intent(getContext(), RecipeStepActivity.class);
        stepIntent.putExtra("stepId", thisStepId);
        stepIntent.putExtra("recipeId", thisRecipeId);
        startActivity(stepIntent);
    }

    /**
     * Media Session Callbacks, where all external clients control the player.
     */
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
