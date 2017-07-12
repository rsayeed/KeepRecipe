package com.example.android.ubaking.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

/**
 * Created by rubab on 7/4/17.
 */

public class RecipeContentProvider extends ContentProvider {

    private static final String TAG = RecipeContentProvider.class.getSimpleName();

    // Constants for defining specific URI
    public static final int RECIPE = 100;
    public static final int RECIPE_WITH_ID = 101;

    public static final int INGREDIENTS = 200;
    public static final int INGREDIENTS_WITH_ID = 201;

    public static final int STEPS = 300;
    public static final int STEPS_WITH_ID = 301;


    // CDeclare a static variable for the Uri matcher that you construct
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    // Define a static buildUriMatcher method that associates URI's with their int match
    /**
     Initialize a new matcher object without any matches,
     then use .addURI(String authority, String path, int match) to add matches
     */
    public static UriMatcher buildUriMatcher() {

        // Initialize a UriMatcher with no matches by passing in NO_MATCH to the constructor
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        /*
          All paths added to the UriMatcher have a corresponding int.
          For each kind of uri you may want to access, add the corresponding match with addURI.
          The two calls below add matches for the task directory and a single item by ID.
         */
        uriMatcher.addURI(RecipeContract.AUTHORITY, RecipeContract.RECIPE_TASKS, RECIPE);
        uriMatcher.addURI(RecipeContract.AUTHORITY, RecipeContract.RECIPE_TASKS + "/#", RECIPE_WITH_ID);

        uriMatcher.addURI(RecipeContract.AUTHORITY, RecipeContract.RECIPE_INGREDIENTS_TASKS, INGREDIENTS);
        uriMatcher.addURI(RecipeContract.AUTHORITY, RecipeContract.RECIPE_INGREDIENTS_TASKS + "/#", INGREDIENTS_WITH_ID);

        uriMatcher.addURI(RecipeContract.AUTHORITY, RecipeContract.RECIPE_STEPS_TASKS, STEPS);
        uriMatcher.addURI(RecipeContract.AUTHORITY, RecipeContract.RECIPE_STEPS_TASKS + "/#", STEPS_WITH_ID);

        return uriMatcher;
    }

    // Member variable for a TaskDbHelper that's initialized in the onCreate() method
    private RecipeDbHelper mTaskDbHelper;

    /* onCreate() is where you should initialize anything you’ll need to setup
    your underlying data source.
    In this case, you’re working with a SQLite database, so you’ll need to
    initialize a DbHelper to gain access to it.
     */
    @Override
    public boolean onCreate() {
        // Complete onCreate() and initialize a TaskDbhelper on startup
        // [Hint] Declare the DbHelper as a global variable

        Context context = getContext();
        mTaskDbHelper = new RecipeDbHelper(context);
        return true;
    }


    // Implement insert to handle requests to insert a single new row of data
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {

        // Write URI matching code to identify the match for the tasks directory
        int match = sUriMatcher.match(uri);
        Uri returnUri; // URI to be returned

        switch (match) {
            case RECIPE:
                // Inserting values into recipe table
                returnUri = insertRow(RecipeContract.RecipeEntry.TABLE_NAME, RecipeContract.RecipeEntry.CONTENT_URI, values);
                break;
            case INGREDIENTS:
                // Inserting values into ingredients table
                returnUri = insertRow(RecipeContract.RecipeIngredientsEntry.TABLE_NAME, RecipeContract.RecipeIngredientsEntry.CONTENT_URI, values);
                break;
            case STEPS:
                // Inserting values into steps table
                returnUri = insertRow(RecipeContract.RecipeStepsEntry.TABLE_NAME, RecipeContract.RecipeStepsEntry.CONTENT_URI, values);
                break;
            // Set the value for the returnedUri and write the default case for unknown URI's
            // Default case throws an UnsupportedOperationException
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Notify the resolver if the uri has been changed, and return the newly inserted URI
        getContext().getContentResolver().notifyChange(uri, null);

        // Return constructed uri (this points to the newly inserted row of data)
        return returnUri;
    }


    // Insert new values into the database
    private Uri insertRow(String tableName, Uri tableContentUri, ContentValues values) {

        final SQLiteDatabase db = mTaskDbHelper.getWritableDatabase();

        long id = db.insert(tableName, null, values);
        if ( id > 0 ) {
            return ContentUris.withAppendedId(tableContentUri, id);
        } else {
            throw new android.database.SQLException("Failed to insert row into " + tableContentUri);
        }
    }

    // Implement query to handle requests for data by URI
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        // Get access to underlying database (read-only for query)
        final SQLiteDatabase db = mTaskDbHelper.getReadableDatabase();

        // QUERIES FOR RECIPE TABLE
        String selectAllRecipeQuery = "SELECT  * FROM " + RecipeContract.RecipeEntry.TABLE_NAME;
        String selectRecipeQuery = "SELECT  * FROM " + RecipeContract.RecipeEntry.TABLE_NAME + " WHERE " +
                RecipeContract.RecipeEntry._ID + "=" + uri.getLastPathSegment();

        // QUERIES FOR INGREDIENTS TABLE
        String selectAllIngredientsQuery = "SELECT  * FROM " + RecipeContract.RecipeIngredientsEntry.TABLE_NAME;
        String selectIngredientsQuery = "SELECT  * FROM " + RecipeContract.RecipeIngredientsEntry.TABLE_NAME + " WHERE " +
                RecipeContract.RecipeIngredientsEntry.COLUMN_RECIPE_ID + "=" + uri.getLastPathSegment();

        // QUERIES FOR STEP TABLE
        String selectAllStepQuery = "SELECT  * FROM " + RecipeContract.RecipeStepsEntry.TABLE_NAME;
        String selectStepQuery = "SELECT  * FROM " + RecipeContract.RecipeStepsEntry.TABLE_NAME + " WHERE " +
                RecipeContract.RecipeStepsEntry.COLUMN_RECIPE_ID + "=" + uri.getLastPathSegment();

        // Write URI match code and set a variable to return a Cursor
        int match = sUriMatcher.match(uri);
        Cursor retCursor;

        // Query for the recipe directory and write a default case
        switch (match) {
            // Query for the tasks directory
            case RECIPE:
                Log.v(TAG, "QUERY RECIPE");
                retCursor =  db.rawQuery(selectAllRecipeQuery, null);
                break;
            case RECIPE_WITH_ID:
                Log.v(TAG, "QUERY RECIPE WITH ID");
                retCursor =  db.rawQuery(selectRecipeQuery, null);
                break;
            case INGREDIENTS:
                Log.v(TAG, "QUERY INGREDIENTS");
                retCursor =  db.rawQuery(selectAllIngredientsQuery, null);
                break;
            case INGREDIENTS_WITH_ID:
                Log.v(TAG, "QUERY INGREDIENTS WITH ID");
                retCursor =  db.rawQuery(selectIngredientsQuery, null);
                break;
            case STEPS:
                Log.v(TAG, "QUERY STEPS");
                retCursor =  db.rawQuery(selectAllStepQuery, null);
                break;
            case STEPS_WITH_ID:
                Log.v(TAG, "QUERY STEPS WITH ID");
                retCursor =  db.rawQuery(selectStepQuery, null);
                break;

            // Default exception
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Set a notification URI on the Cursor and return that Cursor
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);

        // Return the desired Cursor
        return retCursor;
    }


    // Implement delete to delete a single row of data from Recipe table
    // This should also delete all corresponding rows from the Ingredients and Steps table
    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {

        // Get access to the database and write URI matching code to recognize a single item
        final SQLiteDatabase db = mTaskDbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);
        // Keep track of the number of deleted tasks
        int tasksDeleted; // starts as 0

        // Write the code to delete a single row of data
        // [Hint] Use selections to delete an item by its row ID
        switch (match) {
            // Handle the single item case, recognized by the ID included in the URI path
            case RECIPE_WITH_ID:
                // Get the task ID from the URI path
                String id = uri.getPathSegments().get(1);
                // Use selections/selectionArgs to filter for this ID
                tasksDeleted = db.delete(RecipeContract.RecipeEntry.TABLE_NAME, RecipeContract.RecipeEntry._ID + "=?", new String[]{id});
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Notify the resolver of a change and return the number of items deleted
        if (tasksDeleted != 0) {
            // A task was deleted, set notification
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of tasks deleted
        return tasksDeleted;
    }


    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {

        throw new UnsupportedOperationException("Not yet implemented");
    }


    @Override
    public String getType(@NonNull Uri uri) {

        throw new UnsupportedOperationException("Not yet implemented");
    }
}
