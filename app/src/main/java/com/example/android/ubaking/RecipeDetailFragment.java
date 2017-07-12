package com.example.android.ubaking;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.android.ubaking.model.RecipeStep;
import com.example.android.ubaking.utilities.RecipeDataUtils;
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


/**
 * Created by rubab on 6/27/17.
 */

/**
 * Initialize the fragment to load the first step of the chosen Recipe
 */
public class RecipeDetailFragment extends Fragment implements ExoPlayer.EventListener {

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

    public RecipeDetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Retrieve value of position of Recipe Step from static Java class
        selectedRecipeStepPosition = RecipeDataUtils.getPositionOfStep();

        // Inflate the fragment recipe details layout
        rootView = inflater.inflate(R.layout.fragment_recipe_detail, container, false);

        // Update Detail fragment
        updateView();

        return rootView;
    }

    public void updateView() {

        releasePlayer();

        mPlayerView = (SimpleExoPlayerView) rootView.findViewById(R.id.playerView);

        // Ensure that the video player is always displaying
        mMediaView = (FrameLayout) rootView.findViewById(R.id.view_media);
        mStepCardView = (CardView) rootView.findViewById(R.id.card_view_instructions);
        mNavigateCardView = (CardView) rootView.findViewById(R.id.card_view_videoNavigate);

        mMediaView.setVisibility(View.VISIBLE);
        mStepCardView.setVisibility(View.VISIBLE);
        mNavigateCardView.setVisibility(View.VISIBLE);

        // We are in mobile portrait mode
        if ((rootView.findViewById(R.id.recipe_detail_port) != null) &&
                !getResources().getBoolean(R.bool.twoPaneMode)) {

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
            initializePlayer(Uri.parse(videoURL));
        }

        // Initialize recipe step instructions
        mInstructionTextView = (TextView) rootView.findViewById(R.id.detail_step);
        mInstructionTextView.setText(getRecipeStep().getDesc());

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

        return RecipeDataUtils.getRecipeStepList().get(selectedRecipeStepPosition);
    }

    /**
     * This method will be called by the Details activity in order to set the specific
     * position of the recipe step that needs to be loaded by the fragment
     *
     * @param position
     */
    public void setRecipeStepPosition(int position) {

        selectedRecipeStepPosition = position;
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

            Log.d(TAG, "MEDIA playing");
        } else if (playbackState == ExoPlayer.STATE_READY) {

            Log.d(TAG, "MEDIA stopped");
        }
    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

    }

    @Override
    public void onPositionDiscontinuity() {

    }
}

