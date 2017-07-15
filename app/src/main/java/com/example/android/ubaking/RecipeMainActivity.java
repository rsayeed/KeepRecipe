package com.example.android.ubaking;

import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.example.android.ubaking.data.RecipeContract;
import com.example.android.ubaking.model.Recipe;
import com.example.android.ubaking.utilities.NetworkUtils;
import com.example.android.ubaking.utilities.RecipeDataUtils;
import com.example.android.ubaking.utilities.RecipeJsonUtils;
import com.example.android.ubaking.utilities.RecipeMainAdapter;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class RecipeMainActivity extends AppCompatActivity implements
        RecipeMainAdapter.RecipeMainOnClickHandler,
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = RecipeMainActivity.class.getSimpleName();

    @BindView(R.id.my_toolbar) Toolbar toolbar;

    @BindView(R.id.main_recycler_view) RecyclerView mRecyclerView;
    LinearLayoutManager mLayoutManager;
    RecipeMainAdapter recipeMainAdapter;

    // Lists holding onto Recipe Data
    private List<Recipe> mRecipeData;

    // Loaders to pull data from the database
    private static final int RECIPE_LOADER_ID = 0;

    public RecipeMainActivity() {
    }

    /**
     * Used to process callbacks from async network calls
     */
    public interface OnNetworkTaskCompleted {
        void onTaskCompleted();
    }

    OnNetworkTaskCompleted onNetworkTaskCompleted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_main);

        // Bind the views
        ButterKnife.bind(this);

        //Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        // Change the title of ActionBar to reflect this activity
        getSupportActionBar().setTitle(R.string.actionBar_main);

        // Initialize recipe data
        mRecipeData = new ArrayList<>();

        //mRecyclerView = (RecyclerView) findViewById(R.id.main_recycler_view);

        // Use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // Use GridLayout manager for Tablet-lanscape mode, Set number of columns to 4
        if (getResources().getBoolean(R.bool.twoPaneMode)) {
            mLayoutManager = new GridLayoutManager(this, 4);
            // Use a linear layout manager for mobile portrait/landscape mode
        } else {
            mLayoutManager = new LinearLayoutManager(this);
        }

        mRecyclerView.setLayoutManager(mLayoutManager);

        // Initialize adapter with context, recipe data, and context again for onClick implementation
        recipeMainAdapter = new RecipeMainAdapter(this, mRecipeData, this);
        mRecyclerView.setAdapter(recipeMainAdapter);

        // If we have to load data from the network, this is the callback method to be
        // executed after data has been fetched
        onNetworkTaskCompleted = new OnNetworkTaskCompleted() {
            @Override
            public void onTaskCompleted() {
                restartLoader();
            }
        };

        // Load data from Recipe table
        /*
         Ensure a loader is initialized and active. If the loader doesn't already exist, one is
         created, otherwise the last created loader is re-used. This loader will be used to
         query for a user's movies
         */
        getSupportLoaderManager().initLoader(RECIPE_LOADER_ID, null, this);

    }

    /**
     * This method serves as the callback for when the Network task has finished
     * fetching data. Loader will be restarted to fetch Recipe data from the database
     */
    public void restartLoader() {

        Log.v(TAG, "RESTART LOADER");
        getSupportLoaderManager().restartLoader(RECIPE_LOADER_ID, null, this);

    }

    @Override
    public Loader<Cursor> onCreateLoader(final int id, final Bundle args) {

        return new AsyncTaskLoader<Cursor>(this) {

            // Initialize a Cursor, this will hold all the task data
            Cursor mRecipeCursor = null;

            // onStartLoading() is called when a loader first starts loading data
            @Override
            protected void onStartLoading() {
                if (mRecipeCursor != null) {
                    // Delivers any previously loaded data immediately
                    deliverResult(mRecipeCursor);
                } else {
                    // Force a new load
                    forceLoad();
                }
            }

            @Override
            public Cursor loadInBackground() {

                // Query and load all task data in the background; sort by priority
                // [Hint] use a try/catch block to catch any errors in loading data
                Cursor c = null;

                switch (id) {

                    case RECIPE_LOADER_ID:
                        try {
                            c = getContentResolver().query(RecipeContract.RecipeEntry.CONTENT_URI,
                                    null,
                                    null,
                                    null,
                                    null);

                        } catch (Exception e) {
                            Log.e(TAG, "Failed to asynchronously load data.");
                            e.printStackTrace();
                            return null;
                        }
                        break;
                }
                return c;
            }

            // deliverResult sends the result of the load, a Cursor, to the registered listener
            public void deliverResult(Cursor data) {

                mRecipeCursor = data;
                super.deliverResult(mRecipeCursor);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        // Populate a list of Recipes found
        if (data.moveToFirst()) {
            // loop through cursor and store the results in a new Recipe object
            do {
                switch (loader.getId()) {

                    case RECIPE_LOADER_ID:

                        Recipe recipe = new Recipe();

                        recipe.setRecipeId(data.getString(0));
                        recipe.setRecipeName(data.getString(1));
                        recipe.setServingSize(data.getString(2));

                        Log.v(TAG, "New recipe: " + recipe.toString());

                        // GET STEPS DATA
                        recipe.setRecipeSteps(RecipeDataUtils.queryforStepsData(recipe.getRecipeId(), this));

                        // GET INGREDIENTS DATA
                        recipe.setRecipeIngredients(RecipeDataUtils.queryforIngredientsData(recipe.getRecipeId(), this));

                        // At this point, the Recipe object should have everything
                        mRecipeData.add(recipe);

                        break;
                }

            } while (data.moveToNext());

            recipeMainAdapter.swapData(mRecipeData);

        } else {

            // database is empty, load pre-loaded recipes
            Log.v(TAG, "DATABASE EMPTY");
            loadRecipeData();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        recipeMainAdapter.swapData(null);
    }


    /**
     * This method will load the URL from the NetworkUtils class and execute
     * the AsyncTask to obtain recipe data
     */
    private void loadRecipeData() {

        // Verify that we have internet connection
        if (NetworkUtils.isOnline(getApplicationContext())) {
            Log.v(TAG, "ONLINE");

            new FetchRecipeInfoTask(onNetworkTaskCompleted).execute();

        } else {
            // No internet connection available
            displayErrorMessage();

        }
    }

    public class FetchRecipeInfoTask extends AsyncTask<Void, Void, List<Recipe>> {

        private OnNetworkTaskCompleted listener;

        public FetchRecipeInfoTask(OnNetworkTaskCompleted listener) {

            this.listener = listener;
        }

        @Override
        protected List<Recipe> doInBackground(Void... params) {

            URL recipeInfoRequestUrl =
                    NetworkUtils.buildUrlForRecipeData();
            try {

                String jsonRecipeResponse = NetworkUtils
                        .getResponseFromHttpUrl(recipeInfoRequestUrl);

                // Get list of Recipe items
                List<Recipe> recipeDataList = RecipeJsonUtils.
                        parseJSON(RecipeMainActivity.this, jsonRecipeResponse);

                return recipeDataList;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }


        @Override
        protected void onPostExecute(List<Recipe> recipeData) {

            if (recipeData != null) {
                Log.v(TAG, "Insert Data");
                // Insert Recipe Data
                RecipeDataUtils.insertData(recipeData, getApplicationContext());
                // Register callback
                listener.onTaskCompleted();
            } else {
                displayErrorMessage();
            }
        }
    }


    /**
     * This method will handle clicks whenever a recipe is selected. It will create an
     * explicit intent to take the user to the details screen
     *
     * @param recipe
     */

    @Override
    public void onClick(Recipe recipe) {

        //Pass the selected recipe object to the detail screen
        Intent intent = new Intent(this, RecipeDetailsActivity.class);
        intent.putExtra("RecipeObj", recipe);

        // Store the data values for retrieval by Detail activity
        RecipeDataUtils recipeDataUtils = new RecipeDataUtils(recipe, recipe.getRecipeIngredients(), recipe.getRecipeSteps());

        // Reset the the recipe step position to zero
        RecipeDataUtils.setPositionOfStep(0);

        //Start details activity
        startActivity(intent);
    }

    public void displayErrorMessage() {
        Toast.makeText(this, getString(R.string.error_internet), Toast.LENGTH_LONG).show();

    }
}
