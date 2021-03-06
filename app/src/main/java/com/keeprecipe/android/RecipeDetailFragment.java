package com.keeprecipe.android;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.keeprecipe.android.model.RecipeStep;
import com.keeprecipe.android.utilities.RecipeDataUtils;


/**
 * Created by rubab on 6/27/17.
 */

/**
 * Initialize the fragment to load the first step of the chosen Recipe
 */
public class RecipeDetailFragment extends Fragment implements
        ExoPlayer.EventListener,
        YouTubePlayer.OnInitializedListener {

    private static final String TAG = RecipeDetailFragment.class.getSimpleName();

    private int selectedRecipeStepPosition = 0;
    private String videoURL;

    private View rootView;
    private SimpleExoPlayer mExoPlayer;
    private SimpleExoPlayerView mPlayerView;
    private TextView mInstructionTextView;
    private FrameLayout mMediaView;

    private Button mPrevBtn;
    private Button mNextBtn;

    private CardView mStepCardView;
    private CardView mNavigateCardView;

    // Youtube player
    private static YouTubePlayer YPlayer;

    private YouTubePlayerSupportFragment youTubePlayerFragment;
    private FragmentTransaction transaction;

    public RecipeDetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the fragment recipe details layout
        rootView = inflater.inflate(R.layout.fragment_recipe_detail, container, false);

        // Retrieve value of position of Recipe Step from static Java class
        selectedRecipeStepPosition = RecipeDataUtils.getPositionOfStep();

        // Update Detail fragment - need this for single pane
        updateView();

        return rootView;
    }

    public void updateView() {

        // Full screen for Youtube is false
        RecipeDataUtils.setFullScreen(false);

        // Release existing Youtube player resource
        if (YPlayer != null) {
            YPlayer.release();
        }

        // Release exoplayer resource
        releasePlayer();

        mPlayerView = (SimpleExoPlayerView) rootView.findViewById(R.id.playerView);

        // Ensure that the video player is always displaying
        mMediaView = (FrameLayout) rootView.findViewById(R.id.view_media);
        mStepCardView = (CardView) rootView.findViewById(R.id.card_view_instructions);
        mNavigateCardView = (CardView) rootView.findViewById(R.id.card_view_videoNavigate);

        mMediaView.setVisibility(View.VISIBLE);
        mStepCardView.setVisibility(View.VISIBLE);
        mNavigateCardView.setVisibility(View.VISIBLE);


        // We are in mobile portrait mode, also need to apply to landscape
        if (!getResources().getBoolean(R.bool.twoPaneMode)) {

            mPrevBtn = (Button) rootView.findViewById(R.id.detail_previous_button);
            mNextBtn = (Button) rootView.findViewById(R.id.detail_next_button);

            // Determine whether we need to hide or show the previous button
            if (selectedRecipeStepPosition == 0) {
                mPrevBtn.setVisibility(View.GONE);
            } else {
                mPrevBtn.setVisibility(View.VISIBLE);
            }

            // Determine whether we need to hide or show the next button
            if (RecipeDataUtils.isFinalPosition()) {
                mNextBtn.setVisibility(View.GONE);
            } else {
                mNextBtn.setVisibility(View.VISIBLE);
            }
        }

        // TABLET Mode - Hide Navigation Card
        if (getResources().getBoolean(R.bool.twoPaneMode)) {

            mNavigateCardView.setVisibility(View.GONE);
        }

        // Determine whether thumbnail or video url of RecipeStep object has video url
        if (getRecipeStep().getVideoURL().length() < 1 && getRecipeStep().getThumbURL().length() < 1) {

            // No video found for this particular step
            videoURL = null;

            // Hide the cardview displaying the video player
            mMediaView.setVisibility(View.GONE);

        } else if (getRecipeStep().getThumbURL().length() > 0) {

            videoURL = getRecipeStep().getThumbURL();
        } else {

            videoURL = getRecipeStep().getVideoURL();
        }

        if (videoURL != null) {

            // Determine whether we have a youtube player or regular video
            if (videoURL.contains("youtu")) {

                // Hide the exoplayer
                mPlayerView.setVisibility(View.GONE);

                // Initialize Youtube player
                youTubePlayerFragment = YouTubePlayerSupportFragment.newInstance();
                transaction = getChildFragmentManager().beginTransaction();

                Log.v(TAG, "ADDING youtube trx");
                transaction.add(R.id.youtube_fragment, youTubePlayerFragment).commit();

                youTubePlayerFragment.initialize(getString(R.string.youtube_api), this);

            } else {
                initializePlayer(Uri.parse(videoURL));
            }
        }

        // Initialize recipe step instructions
        mInstructionTextView = (TextView) rootView.findViewById(R.id.detail_step);
        mInstructionTextView.setText(getRecipeStep().getDesc());
    }


    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {

        if (!b) {
            // Portrait Mode
            YPlayer = youTubePlayer;
            YPlayer.setFullscreen(false);
            YPlayer.setOnFullscreenListener(new YouTubePlayer.OnFullscreenListener() {

                @Override
                public void onFullscreen(boolean b) {

                    if (b) {
                        RecipeDataUtils.setFullScreen(true);
                    }
                    else {
                        RecipeDataUtils.setFullScreen(false);
                    }

                }
            });

            YPlayer.loadVideo(RecipeDataUtils.getYoutubeVideoId(videoURL));

            // Hide full screen button in portrait mode and two-pane mode
            YPlayer.setShowFullscreenButton(false);

            // if we are in mobile-landscape mode, enable full screen option
            if ((rootView.findViewById(R.id.recipe_detail_land) != null) &&
                    !getResources().getBoolean(R.bool.twoPaneMode)) {

                YPlayer.setShowFullscreenButton(true);
            }

            YPlayer.play();
        }

    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        Toast.makeText(getContext(), youTubeInitializationResult.toString(), Toast.LENGTH_LONG).show();
    }


    /**
     * Initialize ExoPlayer.
     *
     * @param mediaUri The URI of the sample to play.
     */

    private void initializePlayer(Uri mediaUri) {

        if (mExoPlayer == null) {
            // Create an instance of the ExoPlayer.
            TrackSelector trackSelector = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();
            mExoPlayer = ExoPlayerFactory.newSimpleInstance(getContext(), trackSelector, loadControl);
            mPlayerView.setPlayer(mExoPlayer);

            // if we are in mobile-landscape mode, display media in full screen
            if ((rootView.findViewById(R.id.recipe_detail_land) != null) &&
                    !getResources().getBoolean(R.bool.twoPaneMode)) {

                // Hide the other card views:
                mStepCardView.setVisibility(View.GONE);
                mNavigateCardView.setVisibility(View.GONE);

                // Hide toolbar
                ((AppCompatActivity) getActivity()).getSupportActionBar().hide();

                // Hide status bar
                getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            }

            // Full screen the media card view
            mPlayerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL);

            mExoPlayer.addListener(this);

            // Prepare the MediaSource.
            String userAgent = Util.getUserAgent(getContext(), "RecipeDetailFragment");
            MediaSource mediaSource = new ExtractorMediaSource(mediaUri, new DefaultDataSourceFactory(
                    getContext(), userAgent), new DefaultExtractorsFactory(), null, null);
            mExoPlayer.prepare(mediaSource);
            mExoPlayer.setPlayWhenReady(true);
        }
    }

    /**
     * Release ExoPlayer.
     */
    private void releasePlayer() {

        if (mExoPlayer != null) {

            mExoPlayer.stop();
            mExoPlayer.release();
            mExoPlayer = null;
        }
    }

    @Override
    public void onPause() {
        releasePlayer();
        super.onPause();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        releasePlayer();

    }

    /**
     * This will return the RecipeStep object from which we will extract the Description and
     *
     * @return
     */
    private RecipeStep getRecipeStep() {

        //return RecipeDataUtils.getRecipeStepList().get(selectedRecipeStepPosition);
        return RecipeDataUtils.getRecipeStepList().get(RecipeDataUtils.getPositionOfStep());

    }

    /**
     * This method will be called by the Details activity in order to set the specific
     * position of the recipe step that needs to be loaded by the fragment
     *
     * @param position
     */
    public void setRecipeStepPosition(int position) {

        //selectedRecipeStepPosition = position;

        //selectedRecipeStepPosition = RecipeDataUtils.getPositionOfStep();
    }

    public static YouTubePlayer getYPlayer() {
        return YPlayer;
    }

    public static void setYPlayer(YouTubePlayer YPlayer) {
        RecipeDetailFragment.YPlayer = YPlayer;
    }

    /**
     * On Configuration change, we need to preserve the selected item
     *
     * @param currentState
     */
    @Override
    public void onSaveInstanceState(Bundle currentState) {

        currentState.putInt("positionOfRecipeStep", selectedRecipeStepPosition);

        RecipeDataUtils.setPositionOfStep(selectedRecipeStepPosition);
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

        if ((playbackState == ExoPlayer.STATE_READY) && playWhenReady) {

        } else if (playbackState == ExoPlayer.STATE_READY) {

        }
    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

    }

    @Override
    public void onPositionDiscontinuity() {

    }

}

