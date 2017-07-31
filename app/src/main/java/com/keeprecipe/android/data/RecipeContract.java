package com.keeprecipe.android.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by rubab on 7/4/17.
 */

public class RecipeContract  {

     /* Add content provider constants to the Contract  */

    // The authority, which is how your code knows which Content Provider to access
    public static final String AUTHORITY = "com.keeprecipe.android";

    // The base content URI = "content://" + <authority>
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    // Define the possible paths for accessing data in this contract
    // This is the path for the "recipe" directory
    public static final String RECIPE_TASKS = "recipe";
    public static final String RECIPE_INGREDIENTS_TASKS = "ingredients";
    public static final String RECIPE_STEPS_TASKS = "steps";

    /* MovieEntry is an inner class that defines the contents of the Movie table */
    public static final class RecipeEntry implements BaseColumns {

        // MovieEntry content URI = base content URI + path
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(RECIPE_TASKS).build();

        // Movie table and column names
        public static final String TABLE_NAME = "recipe";

        // Since RecipeEntry implements the interface "BaseColumns", it has an automatically produced
        // "_ID" column
        public static final String COLUMN_RECIPE_NAME = "RECIPE_NAME";
        public static final String COLUMN_RECIPE_SERVING = "SERVING_SIZE";
        public static final String COLUMN_IMAGE_URL = "IMAGE_URL";

    }

    /* RecipeIngredientsEntry is an inner class that defines the contents of the Ingredients table */
    public static final class RecipeIngredientsEntry implements BaseColumns {

        // RecipeIngredientsEntry content URI = base content URI + path
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(RECIPE_INGREDIENTS_TASKS).build();


        // Movie table and column names
        public static final String TABLE_NAME = "ingredients";

        // Since RecipeIngredientsEntry implements the interface "BaseColumns", it has an automatically produced
        // "_ID" column
        public static final String COLUMN_QUANTITY = "QUANTITY";
        public static final String COLUMN_MEASURE = "MEASURE";
        public static final String COLUMN_INGREDIENT = "INGREDIENT";
        public static final String COLUMN_RECIPE_ID = "RECIPE_ID";
    }


    /* RecipeStepsEntry is an inner class that defines the contents of the Ingredients table */
    public static final class RecipeStepsEntry implements BaseColumns {

        // RecipeIngredientsEntry content URI = base content URI + path
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(RECIPE_STEPS_TASKS).build();


        // Movie table and column names
        public static final String TABLE_NAME = "steps";

        // Since RecipeIngredientsEntry implements the interface "BaseColumns", it has an automatically produced
        // "_ID" column
        public static final String COLUMN_SHORT_DESC = "SHORT_DESC";
        public static final String COLUMN_DESCRIPTION = "DESCRIPTION";
        public static final String COLUMN_VIDEO_URL = "VIDEO_URL";
        public static final String COLUMN_THUMB_URL = "THUMB_URL";
        public static final String COLUMN_RECIPE_ID = "RECIPE_ID";


    }
}
