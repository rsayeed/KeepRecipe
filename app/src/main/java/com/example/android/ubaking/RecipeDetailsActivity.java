package com.example.android.ubaking;

import android.app.Dialog;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.example.android.ubaking.model.Recipe;
import com.example.android.ubaking.model.RecipeIngredients;
import com.example.android.ubaking.model.RecipeStep;
import com.example.android.ubaking.utilities.RecipeDataUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by rubab on 6/25/17.
 */

public class RecipeDetailsActivity extends AppCompatActivity implements
        RecipeListFragment.OnClickListener {

    private static final String TAG = RecipeDetailsActivity.class.getSimpleName();

    Recipe mRecipe;
    @BindView(R.id.my_toolbar)
    Toolbar toolbar;

    private View savedListView;
    private int fragmentID;
    private int positionOfRecipeStep;
    private RecipeDetailFragment recipeDetailFragment;

    /**
     * Save an integer value that determines which fragment needs to be loaded in Single
     * pane modes only. For detail fragments, need to save the position of the recipe step as well
     *
     * @param currentState
     */
    @Override
    public void onSaveInstanceState(Bundle currentState) {

        Log.v(TAG, "Saving Fragment Values");
        currentState.putInt("fragmentId", fragmentID);
        currentState.putInt("positionOfRecipeStep", positionOfRecipeStep);

        // Preserve the value of the position in the static class so that Fragment can load it
        RecipeDataUtils.setPositionOfStep(positionOfRecipeStep);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        // Bind the views
        ButterKnife.bind(this);

        //Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Get data saved from Intent
        Intent intentThatStartedThisActivity = getIntent();

        if (intentThatStartedThisActivity != null) {
            mRecipe = (Recipe) intentThatStartedThisActivity.getParcelableExtra("RecipeObj");

            // Change the title of ActionBar to reflect the name of the recipe that was passed from
            // MainActivity
            getSupportActionBar().setTitle(mRecipe.getRecipeName());

            // This is required so that the Widget can load the appropriate data
            RecipeDataUtils recipeDataUtils = new RecipeDataUtils(mRecipe, mRecipe.getRecipeIngredients(),
                    mRecipe.getRecipeSteps());

        }

        // TWO-PANE MODE
        if (getResources().getBoolean(R.bool.twoPaneMode)) {
            // We don't need to manually create a Fragment manager because the
            // fragments will be automatically loaded with the two-pane layout
            Log.v(TAG, "TWO PANE MODE, LOADING FRAGMENT");
            return;
        }

        // SINGLE PANE MODE: manually need to establish the fragment for this activity
        if (savedInstanceState != null) {

            clearAllFragments();

            // Reload fragment if saved
            fragmentID = savedInstanceState.getInt("fragmentId");
            positionOfRecipeStep = RecipeDataUtils.getPositionOfStep();

            // Call method to load appropriate fragment
            loadFragment();

        } else {
            // Default fragment ID value for RecipeListFragment
            fragmentID = 0;
            loadFragment(); // This will load the RecipeListFragment
        }

    }

    /**
     * This method is associated to the RecipeListFragment
     * <p>
     * Need to process the step that was selected and load the RecipeDetailFragment to
     * play the video
     *
     * @param recipe
     * @param view                     - This is the specific list item that needs to be highlighted if selected
     * @param positionOfRecipeStepList
     */
    @Override
    public void onItemSelected(RecipeStep recipe, View view, int positionOfRecipeStepList) {
        // When a user selects an item from the recipe details list (with the steps)

        // Reset highlight previously selected item
        if (savedListView != null) {
            savedListView.setSelected(false);
            savedListView = null;
        }

        // Highlight the selected item
        savedListView = view;
        savedListView.setSelected(true);

        fragmentID = 1; // unique indentifier for RecipeDetailsFragment

        positionOfRecipeStep = positionOfRecipeStepList;

        // Updates the position of the step to display in the details fragment
        RecipeDataUtils.setPositionOfStep(positionOfRecipeStep);

        // Two-pane mode logic, create the Recipe passing in the video that needs to be loaded
        if (getResources().getBoolean(R.bool.twoPaneMode)) {

            // This is to ensure that Details Fragment is loaded ONCE
            if (recipeDetailFragment != null) {
                recipeDetailFragment.updateView();
            } else {
                // Initialize Details Fragment
                recipeDetailFragment = (RecipeDetailFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.recipeDetailFragment);
            }

        } else {
            // Single pane mode, replace fragment container with RecipeDetailFragment
            loadFragment();
        }
    }

    /**
     * getMoreReviews is called when the "View Recipe Ingredients" button is clicked
     * A Dialog will open up displaying list of all ingredients for the recipe
     */
    public void getRecipeIngredients(View view) {

        String bullet = "\u25CF" + " ";

        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_recipe_ingredients);
        dialog.setTitle(getResources().getString(R.string.recipe_ingredients));

        TextView text = (TextView) dialog.findViewById(R.id.dialog_ingredients_list);

        // Loop through list of ingredients and append to dialog text view
        for (int x = 0; x < mRecipe.getRecipeIngredients().size(); x++) {

            RecipeIngredients recipeIngredients = mRecipe.getRecipeIngredients().get(x);

            text.append(bullet);
            text.append(recipeIngredients.getQuantity() + " ");
            text.append(recipeIngredients.getMeasure() + " ");
            text.append(recipeIngredients.getIngredient());
            text.append("\n");
        }

        dialog.show();

    }

    /**
     * This method is called when the Previous button is clicked on the Detail fragment (single
     * pane, portrait mode only). Logic is similiar to what happens when a step item is selected from the
     * list fragment. Displaying of the Next button is handled within the DetailsFragment.
     *
     * @param view
     */
    public void onPreviousBtnClick(View view) {

        RecipeDataUtils.setPositionOfStep(--positionOfRecipeStep);

        fragmentID = 1; // unique indentifier for RecipeDetailsFragment

        // Single pane mode, replace fragment container with RecipeDetailFragment
        loadFragment();
    }

    /**
     * This method is called when the Next button is clicked on the Detail fragment (single
     * pane, portrait mode only). Logic is similiar to what happens when a step item is selected from the
     * list fragment. Displaying of the Next button is handled within the DetailsFragment.
     *
     * @param view
     */
    public void onNextBtnClick(View view) {

        RecipeDataUtils.setPositionOfStep(++positionOfRecipeStep);

        fragmentID = 1; // unique indentifier for RecipeDetailsFragment

        // Single pane mode, replace fragment container with RecipeDetailFragment
        loadFragment();
    }

    /**
     * This method is executed when the back button is pressed from Android device
     * Depending on the type of fragment that is currently displayed, the back button should back out
     * in a logical manner
     */
    @Override
    public void onBackPressed() {

        // If status bar  and action bar were hidden due to full screen mode, restore it
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().show();

        if (getResources().getBoolean(R.bool.twoPaneMode)) {
            super.onBackPressed();
        } else if (fragmentID == 0) {
            finish();
        } else if (fragmentID == 1) {

            // If full screen mode due to Youtube player
            if (RecipeDataUtils.isFullScreen()) {
                RecipeDetailFragment.getYPlayer().setFullscreen(false);
            }

            else {
                // Clear all fragments
                fragmentID = 0;
                clearAllFragments();
                loadFragment();
            }
        }
    }

    /**
     * On orientation change, use the following fragment id integer to determine which fragment
     * needs to be loaded and replaced in the single pane view
     */
    private void loadFragment() {

        FragmentManager fragmentManager = getSupportFragmentManager();

        if (fragmentID == 0) {

            // Create new RecipeListFragment for displaying in this single pane view
            RecipeListFragment recipeListFragment = new RecipeListFragment();

            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, recipeListFragment).commit();

        } else if (fragmentID == 1) {

            // Load RecipeDetailsFragment with the given ID
            RecipeDetailFragment recipeDetailFragment = new RecipeDetailFragment();
            recipeDetailFragment.setRecipeStepPosition(positionOfRecipeStep);

            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, recipeDetailFragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

    public void clearAllFragments() {

        FragmentManager fragmentManager = getSupportFragmentManager();

        // cleanup any existing fragments in case we are in detailed mode
        fragmentManager.executePendingTransactions();

        Fragment fragmentById = fragmentManager.findFragmentById(R.id.fragment_container);

        if (fragmentById != null) {
            fragmentManager.beginTransaction()
                    .remove(fragmentById).commit();
        }
    }
}
