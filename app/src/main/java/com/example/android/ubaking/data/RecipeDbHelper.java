package com.example.android.ubaking.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by rubab on 7/4/17.
 */

public class RecipeDbHelper extends SQLiteOpenHelper {

    // The name of the database
    private static final String DATABASE_NAME = "recipeDb.db";

    // If you change the database schema, you must increment the database version
    private static final int VERSION = 2;

    // Constructor
    public RecipeDbHelper(Context context) {

        super(context, DATABASE_NAME, null, VERSION);
    }

    /**
     * Called when the tasks database is created for the first time.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {

        // Create Recipe table (careful to follow SQL formatting rules)
        final String CREATE_RECIPE_TABLE = "CREATE TABLE "  + RecipeContract.RecipeEntry.TABLE_NAME + " (" +
                RecipeContract.RecipeEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                RecipeContract.RecipeEntry.COLUMN_RECIPE_NAME + " TEXT," +
                RecipeContract.RecipeEntry.COLUMN_IMAGE_URL + " TEXT," +
                RecipeContract.RecipeEntry.COLUMN_RECIPE_SERVING              + " TEXT);";

        // Create Recipe Ingredients table (careful to follow SQL formatting rules)
        final String CREATE_INGREDIENTS_TABLE = "CREATE TABLE "  + RecipeContract.RecipeIngredientsEntry.TABLE_NAME + " (" +
                RecipeContract.RecipeIngredientsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                RecipeContract.RecipeIngredientsEntry.COLUMN_INGREDIENT + " TEXT," +
                RecipeContract.RecipeIngredientsEntry.COLUMN_MEASURE + " TEXT," +
                RecipeContract.RecipeIngredientsEntry.COLUMN_QUANTITY + " TEXT," +
                RecipeContract.RecipeIngredientsEntry.COLUMN_RECIPE_ID + " INTEGER," +
                " FOREIGN KEY ("+RecipeContract.RecipeIngredientsEntry.COLUMN_RECIPE_ID+") REFERENCES " +
                RecipeContract.RecipeEntry.TABLE_NAME+"("+RecipeContract.RecipeEntry._ID+"));";

        final String CREATE_STEP_TABLE = "CREATE TABLE "  + RecipeContract.RecipeStepsEntry.TABLE_NAME + " (" +
                RecipeContract.RecipeStepsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                RecipeContract.RecipeStepsEntry.COLUMN_DESCRIPTION + " TEXT," +
                RecipeContract.RecipeStepsEntry.COLUMN_SHORT_DESC + " TEXT," +
                RecipeContract.RecipeStepsEntry.COLUMN_THUMB_URL + " TEXT," +
                RecipeContract.RecipeStepsEntry.COLUMN_VIDEO_URL + " TEXT," +
                RecipeContract.RecipeStepsEntry.COLUMN_RECIPE_ID + " INTEGER," +
                " FOREIGN KEY ("+RecipeContract.RecipeStepsEntry.COLUMN_RECIPE_ID+") REFERENCES " +
                RecipeContract.RecipeEntry.TABLE_NAME+"("+RecipeContract.RecipeEntry._ID+"));";

        db.execSQL(CREATE_RECIPE_TABLE);
        db.execSQL(CREATE_INGREDIENTS_TABLE);
        db.execSQL(CREATE_STEP_TABLE);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        db.execSQL("PRAGMA foreign_keys=ON");
    }

    /**
     * This method discards the old table of data and calls onCreate to recreate a new one.
     * This only occurs when the version number for this database (DATABASE_VERSION) is incremented.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + RecipeContract.RecipeEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + RecipeContract.RecipeIngredientsEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + RecipeContract.RecipeStepsEntry.TABLE_NAME);

        onCreate(db);
    }
}


