package com.example.android.ubaking;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
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
        RecipeMainAdapter.RecipeMenuItemOnClickHandler,
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = RecipeMainActivity.class.getSimpleName();

    @BindView(R.id.my_toolbar)
    Toolbar toolbar;
    @BindView(R.id.fab)
    FloatingActionButton fab;

    @BindView(R.id.coordinatorLayout)
    CoordinatorLayout coordinatorLayout;

    @BindView(R.id.main_recycler_view)
    RecyclerView mRecyclerView;
    LinearLayoutManager mLayoutManager;
    RecipeMainAdapter recipeMainAdapter;

    // Lists holding onto Recipe Data
    private List<Recipe> mRecipeData;

    // Loaders to pull data from the database
    private static final int RECIPE_LOADER_ID = 0;

    // Request code for calling Add Recipe activity
    private static final int ADD_REQUEST_CODE = 10;

    // Shared preferences used only when application is run for first time
    private SharedPreferences sharedPreferences;


    public RecipeMainActivity() {
    }


    /**
     * Used to process callbacks from async network calls
     */
    public interface OnNetworkTaskCompleted {
        void onTaskCompleted();
    }

    OnNetworkTaskCompleted onNetworkTaskCompleted;

    /**
     * Preserve the latest listing of recipes
     *
     * @param outState
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {

        Log.v(TAG, "Saving recipe list");
        ArrayList<Recipe> listofMainRecipes = (ArrayList) mRecipeData;
        outState.putParcelableArrayList("mainRecipeList", listofMainRecipes);
    }

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

        if (savedInstanceState != null) {
            Log.v(TAG, "Retrieving saved data");
            mRecipeData = savedInstanceState.getParcelableArrayList("mainRecipeList");
        } else {
            Log.v(TAG, "Empty Recipe main list");
            // Initialize recipe data
            mRecipeData = new ArrayList<>();
        }

        mRecipeData = new ArrayList<>();

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
        recipeMainAdapter = new RecipeMainAdapter(this, mRecipeData, this, this);
        mRecyclerView.setAdapter(recipeMainAdapter);

        // Hide the FAB on scrolling down and display when scrolling up
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0)
                    fab.hide();
                else if (dy < 0)
                    fab.show();
            }
        });

        // If we have to load data from the network, this is the callback method to be
        // executed after data has been fetched
        onNetworkTaskCompleted = new OnNetworkTaskCompleted() {
            @Override
            public void onTaskCompleted() {
                restartLoader();
            }
        };

        /*
         Ensure a loader is initialized and active. If the loader doesn't already exist, one is
         created, otherwise the last created loader is re-used.
         */
        if (mRecipeData.isEmpty()) {
            getSupportLoaderManager().initLoader(RECIPE_LOADER_ID, null, this);
        } else {
            restartLoader();
        }
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

                        recipe.setRecipeId(data.getString(data.getColumnIndex(RecipeContract.RecipeEntry._ID)));
                        recipe.setRecipeName(data.getString(data.getColumnIndex(RecipeContract.RecipeEntry.COLUMN_RECIPE_NAME)));
                        recipe.setServingSize(data.getString(data.getColumnIndex(RecipeContract.RecipeEntry.COLUMN_RECIPE_SERVING)));
                        recipe.setImageURL(data.getString(data.getColumnIndex(RecipeContract.RecipeEntry.COLUMN_IMAGE_URL)));

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

            // database is empty, load pre-loaded recipes only if this is the first time that the
            // user is running the application
            Log.v(TAG, "DATABASE EMPTY");

            sharedPreferences = getPreferences(Context.MODE_PRIVATE);

            if (sharedPreferences.getBoolean("firstrun", true)) {
                // Only load recipe data from the internet the first time app is run
                loadRecipeData();
                sharedPreferences.edit().putBoolean("firstrun", false).commit();
            }

            // TEST MODE ONLY
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
            displayErrorMessage(null);

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
                displayErrorMessage(null);
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

    /**
     * Process the click of the Add button on main activity to open Add Recipe activity
     */
    @OnClick(R.id.fab)
    public void onClickFab() {

        Intent intent = new Intent(this, RecipeAddActivity.class);
        startActivityForResult(intent, ADD_REQUEST_CODE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {

            Log.v(TAG, "BACK TO MAIN ACTIVITY");

            if (requestCode == ADD_REQUEST_CODE) {

                // Clear array of existing recipes
                mRecipeData.clear();

                recipeMainAdapter.notifyDataSetChanged();

                // Fetch data from DB again
                restartLoader();

            }
        }
    }

    @Override
    public void onClickRecipeMenuItem(Recipe recipe, View view) {
        displayRecipeItemMenu(recipe, view);

    }

    /**
     * Showing popup menu when tapping on 3 dots
     */
    private void displayRecipeItemMenu(Recipe recipe, View view) {
        // inflate menu
        PopupMenu popup = new PopupMenu(this, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.recipe_item_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new MyRecipeMenuItemClickListener(recipe));
        popup.show();
    }

    /**
     * Determine what action needs to be taken as per user selection on recipe item menu (3 dots)
     */
    class MyRecipeMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

        Recipe recipeToDelete;

        public MyRecipeMenuItemClickListener(Recipe recipe) {

            recipeToDelete = recipe;
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_delete:
                    Log.v(TAG, "DELETE THIS RECIPE WITH ID: " + recipeToDelete.getRecipeId());
                    // Remove recipe object from list
                    if (removeRecipeFromDataSet(recipeToDelete.getRecipeId())) {
                        displayRecipeRemoved(recipeToDelete.getRecipeName());
                    } else {
                        displayErrorMessage(getString(R.string.contact_admin));
                    }
                    break;
            }

            return false;
        }

    }

    /**
     * Find the recipe by ID and delete it from the database
     * Reload the cursor
     *
     * @return
     */
    public boolean removeRecipeFromDataSet(String recipeId) {

        // Delete the selected recipe off the DB
        Uri uri = ContentUris.withAppendedId(
                RecipeContract.RecipeEntry.CONTENT_URI, Long.valueOf(recipeId));

        if (getContentResolver().delete(uri, null, null) == 1) {
            Log.v(TAG, "DELETED RECORD");

            // Clear array of existing recipes
            mRecipeData.clear();

            recipeMainAdapter.notifyDataSetChanged();

            // Fetch data from DB again
            restartLoader();

            return true;
        }

        return false;
    }


    /* Inflate the menu for the application */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /* Use AppCompatActivity's method getMenuInflater to get a handle on the menu inflater */
        MenuInflater inflater = getMenuInflater();
        /* Use the inflater's inflate method to inflate our menu layout to this menu */
        inflater.inflate(R.menu.main_activity_menu, menu);
        /* Return true so that the menu is displayed in the Toolbar */
        return true;
    }

    /* Add behavior to the options that are found in the application menu */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {

            case R.id.action_contact:
              // Create an intent to send an email to support
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{getString(R.string.email_to)});
                intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.email_subject));
                Intent mailer = Intent.createChooser(intent, null);
                startActivity(mailer);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * This method will display a snackbar to the user notifying that a particular Recipe was
     * deleted
     *
     * @param recipeName
     */
    public void displayRecipeRemoved(String recipeName) {

        Snackbar snackbar = Snackbar
                .make(coordinatorLayout, "Removed " + recipeName, Snackbar.LENGTH_LONG);

        snackbar.show();

    }

    /**
     * Display a toast to the user
     *
     * @param errorText
     */
    public void displayErrorMessage(String errorText) {

        if (errorText != null) {
            Toast.makeText(this, errorText, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, getString(R.string.error_internet), Toast.LENGTH_LONG).show();
        }

    }

}
