package com.helpingiwthcode.mybakingapp.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.media.session.MediaButtonReceiver;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.helpingiwthcode.mybakingapp.adapters.RecipeStepsAdapter;
import com.helpingiwthcode.mybakingapp.dao.DAOIngredients;
import com.helpingiwthcode.mybakingapp.dao.DAOSteps;
import com.helpingiwthcode.mybakingapp.idao.IDAOIngredients;
import com.helpingiwthcode.mybakingapp.idao.IDAOSteps;
import com.helpingiwthcode.mybakingapp.model.Ingredients;
import com.helpingiwthcode.mybakingapp.model.Recipe;
import com.helpingiwthcode.mybakingapp.model.Steps;
import com.helpingiwthcode.mybakingapp.realm.RealmMethods;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.RealmResults;
import io.realm.Sort;
import timber.log.Timber;

public class RecipeDetailActivity extends AppCompatActivity implements ExoPlayer.EventListener, RecipeStepsAdapter.RecipeStepAdapterOnClick{

    private SimpleExoPlayer mExoPlayer;
    private static MediaSessionCompat mMediaSession;
    private PlaybackStateCompat.Builder mStateBuilder;
    @BindView(R.id.playerView) SimpleExoPlayerView mPlayerView;
    @BindView(R.id.rv_steps) RecyclerView stepsRv;
    @BindView(R.id.bt_steps) Button stepsBt;
    @BindView(R.id.bt_ingredients) Button ingredientsBt;
    @BindView(R.id.tv_ingredients)
    TextView ingredientsTv;
    private Recipe recipe;
    private int recipeId;
    private List<Steps> steps;
    private RecipeStepsAdapter stepsAdapter;
    private DividerItemDecoration mDividerItemDecoration;
    private String INGREDIENTS = "ingredients";
    private String STEPS = "steps";
    private String showingView = STEPS;
    IDAOIngredients idaoIngredients = new DAOIngredients();
    IDAOSteps idaoSteps = new DAOSteps();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);
        ButterKnife.bind(this);
        getRecipeIntent();
//        setSteps();
//        setVideoPlay();
    }

    @OnClick({R.id.bt_ingredients, R.id.bt_steps})
    public void onButtonClick(View v){
        switch (v.getId()){
            case R.id.bt_ingredients:
                if(showingView.equals(INGREDIENTS))
                    return;
                showingView = INGREDIENTS;
                showSelectedView();
                break;
            case R.id.bt_steps:
                if(showingView.equals(STEPS))
                    return;
                showingView = STEPS;
                showSelectedView();
                break;
        }
    }

    private void showSelectedView() {
        boolean showingSteps = (showingView.equals(STEPS));
        stepsRv.setVisibility((showingSteps)?View.VISIBLE:View.GONE);
        ingredientsTv.setVisibility((!showingSteps)?View.VISIBLE:View.GONE);
    }

    private void getRecipeIntent() {
        Bundle extra = getIntent().getExtras();
        if(extra != null){
            recipeId = extra.getInt("recipeId",0);
            Timber.e("RecipeId from intent: "+recipeId);
            setSteps();
            setIngredients();
            showSelectedView();
//            setVideoPlay();
        }
    }

    private void setIngredients() {
        List<Ingredients> ingredientsList = idaoIngredients.getIngredientsFromRecipe(recipeId);
        String ingredientsText = "";
        int ingredientIndex = 0;
        for(Ingredients ingredients : ingredientsList){
            ingredientIndex++;
            ingredientsText += ingredientIndex+": "+ingredients.getQuantity()+" "+ingredients.getMeasure()+" "+ingredients.getIngredient()+"\n";
        }
        ingredientsTv.setText(ingredientsText);
    }

    private void setSteps() {
        steps = idaoSteps.getStepsFromRecipe(recipeId);
        Timber.e("Steps from Recipe["+recipeId+"]: "+steps.toString());
        stepsAdapter = new RecipeStepsAdapter(this,steps);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        stepsRv.setLayoutManager(linearLayoutManager);
        stepsRv.setAdapter(stepsAdapter);
        mDividerItemDecoration = new DividerItemDecoration(
                stepsRv.getContext(),
                linearLayoutManager.getOrientation()
        );
        stepsRv.addItemDecoration(mDividerItemDecoration);
    }

    private void setVideoPlay() {
//        Recipe recipe = RealmMethods.realm().where(Recipe.class).findFirst();
//        int recipeId = recipe.getId();

        Steps steps = RealmMethods.realm().where(Steps.class).equalTo("recipeId",recipeId).findFirst();
        String url = steps.getVideoURL();
        mPlayerView.setDefaultArtwork(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_recipe));

        initializeMediaSession();
        initializePlayer(url);
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
