package com.keeprecipe.android.utilities;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.View;

import com.keeprecipe.android.data.RecipeContract;
import com.keeprecipe.android.model.Recipe;
import com.keeprecipe.android.model.RecipeIngredients;
import com.keeprecipe.android.model.RecipeStep;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by rubab on 6/28/17.
 */

public class RecipeDataUtils {

    private static final String TAG = RecipeDataUtils.class.getSimpleName();


    private static Recipe recipe;
    private static List<RecipeStep> recipeStepList;
    private static List<RecipeIngredients> recipeIngredientsList;
    private static List<Recipe> recipeListForWidget;
    private static int positionOfStep = 0;
    private static boolean isFullScreen;

    private static View listFragmentViewItem;

    public RecipeDataUtils() {
    }

    public RecipeDataUtils(Recipe recipe, List<RecipeIngredients> recipeIngredients, List<RecipeStep> recipeSteps) {

        this.setRecipe(recipe);
        this.setRecipeIngredientsList(recipeIngredients);
        this.setRecipeStepList(recipeSteps);
    }

    public static Recipe getRecipe() {
        return recipe;
    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }

    public static List<RecipeStep> getRecipeStepList() {
        return recipeStepList;
    }

    public void setRecipeStepList(List<RecipeStep> recipeStepList) {
        this.recipeStepList = recipeStepList;
    }

    public static List<RecipeIngredients> getRecipeIngredientsList() {
        return recipeIngredientsList;
    }

    public void setRecipeIngredientsList(List<RecipeIngredients> recipeIngredientsList) {
        this.recipeIngredientsList = recipeIngredientsList;
    }

    public static int getPositionOfStep() {
        return positionOfStep;
    }

    public static void setPositionOfStep(int positionOfStep) {
        RecipeDataUtils.positionOfStep = positionOfStep;
    }

    public static View getListFragmentViewItem() {
        return listFragmentViewItem;
    }

    public static void setListFragmentViewItem(View listFragmentViewItem) {
        RecipeDataUtils.listFragmentViewItem = listFragmentViewItem;
    }

    public static List<Recipe> getRecipeListForWidget() {
        return recipeListForWidget;
    }

    public static void setRecipeListForWidget(List<Recipe> recipeListForWidget) {
        RecipeDataUtils.recipeListForWidget = recipeListForWidget;
    }

    public static boolean isFinalPosition() {

        return (getPositionOfStep() >= getRecipeStepList().size() - 1);
    }

    public static boolean isFullScreen() {
        return isFullScreen;
    }

    public static void setFullScreen(boolean fullScreen) {
        isFullScreen = fullScreen;
    }

    /**
     * Get the video ID from any youtube link
     * @param youtubeUrl
     * @return
     */
    public static String getYoutubeVideoId(String youtubeUrl)
    {
        String video_id="";
        if (youtubeUrl != null && youtubeUrl.trim().length() > 0 && youtubeUrl.startsWith("http"))
        {

            String expression = "^.*((youtu.be"+ "\\/)" + "|(v\\/)|(\\/u\\/w\\/)|(embed\\/)|(watch\\?))\\??v?=?([^#\\&\\?]*).*"; // var regExp = /^.*((youtu.be\/)|(v\/)|(\/u\/\w\/)|(embed\/)|(watch\?))\??v?=?([^#\&\?]*).*/;
            CharSequence input = youtubeUrl;
            Pattern pattern = Pattern.compile(expression,Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(input);
            if (matcher.matches())
            {
                String groupIndex1 = matcher.group(7);
                if(groupIndex1!=null && groupIndex1.length()==11)
                    video_id = groupIndex1;
            }
        }
        return video_id;
    }

    /**
     * This method will query the Ingredients table to get all the associated records
     * for a given Recipe ID
     *
     * @param recipeId
     */
    public static List<RecipeIngredients> queryforIngredientsData(String recipeId, Context context) {

        List<RecipeIngredients> listOfRecipeIngredients = new ArrayList<>();

        // Query for the Ingredients table using the recipe ID
        Cursor c = null;
        try {

            Uri uri = ContentUris.withAppendedId(
                    RecipeContract.RecipeIngredientsEntry.CONTENT_URI, Long.valueOf(recipeId));

            c = context.getContentResolver().query(uri,
                    null,
                    null,
                    null,
                    null);

        } catch (Exception e) {
            e.printStackTrace();
        }

        // Loop through the Steps cursor
        if (c.moveToFirst()) {

            do {
                RecipeIngredients recipeIngredients = new RecipeIngredients();

                recipeIngredients.setIngredient(c.getString(1));
                recipeIngredients.setMeasure(c.getString(2));
                recipeIngredients.setQuantity(c.getString(3));
                recipeIngredients.setRecipeId(c.getString(4));

                // Add to the list
                listOfRecipeIngredients.add(recipeIngredients);

            } while (c.moveToNext());
        }

        return listOfRecipeIngredients;
    }

    /**
     * This method will query the Steps table to get all the associated records
     * for a given Recipe ID
     *
     * @param recipeId
     */
    public static List<RecipeStep> queryforStepsData(String recipeId, Context context) {

        List<RecipeStep> listOfRecipeStep = new ArrayList<>();

        // Query for the Steps table using the recipe ID
        Cursor c = null;
        try {

            Uri uri = ContentUris.withAppendedId(
                    RecipeContract.RecipeStepsEntry.CONTENT_URI, Long.valueOf(recipeId));

            c = context.getContentResolver().query(uri,
                    null,
                    null,
                    null,
                    null);

        } catch (Exception e) {
            e.printStackTrace();
        }


        // Loop through the Steps cursor
        if (c.moveToFirst()) {

            do {
                RecipeStep recipeStep = new RecipeStep();

                recipeStep.setStepId(c.getString(0));
                recipeStep.setDesc(c.getString(1));
                recipeStep.setShortDesc(c.getString(2));
                recipeStep.setThumbURL(c.getString(3));
                recipeStep.setVideoURL(c.getString(4));
                recipeStep.setRecipeId(c.getString(5));

                // Add to the list
                listOfRecipeStep.add(recipeStep);

            } while (c.moveToNext());
        }

        return listOfRecipeStep;
    }

    /**
     * This method will read in a list of Recipe objects and load appropriate records to the
     * database tables
     *
     * @param recipeData
     */
    public static void insertData(List<Recipe> recipeData, Context context) {

        // Loop through recipe data, extract fields and insert to database
        for (int x = 0; x < recipeData.size(); x++) {

            Recipe recipe = recipeData.get(x);

            // Insert to RECIPE table
            ContentValues contentValuesRecipe = new ContentValues();
            contentValuesRecipe.put(RecipeContract.RecipeEntry.COLUMN_RECIPE_NAME, recipe.getRecipeName());
            contentValuesRecipe.put(RecipeContract.RecipeEntry.COLUMN_RECIPE_SERVING, recipe.getServingSize());
            contentValuesRecipe.put(RecipeContract.RecipeEntry._ID, recipe.getRecipeId());

            context.getContentResolver().insert(RecipeContract.RecipeEntry.CONTENT_URI, contentValuesRecipe);

            // Insert to INGREDIENTS TABLE
            for (int i = 0; i < recipe.getRecipeIngredients().size(); i++) {

                RecipeIngredients recipeIngredients = recipe.getRecipeIngredients().get(i);

                ContentValues contentValuesIngredients = new ContentValues();
                contentValuesIngredients.put(RecipeContract.RecipeIngredientsEntry.COLUMN_INGREDIENT, recipeIngredients.getIngredient());
                contentValuesIngredients.put(RecipeContract.RecipeIngredientsEntry.COLUMN_MEASURE, recipeIngredients.getMeasure());
                contentValuesIngredients.put(RecipeContract.RecipeIngredientsEntry.COLUMN_QUANTITY, recipeIngredients.getQuantity());
                contentValuesIngredients.put(RecipeContract.RecipeIngredientsEntry.COLUMN_RECIPE_ID, recipe.getRecipeId());

                context.getContentResolver().insert(RecipeContract.RecipeIngredientsEntry.CONTENT_URI, contentValuesIngredients);

            }

            // Insert to STEPS TABLE
            for (int j = 0; j < recipe.getRecipeSteps().size(); j++) {

                RecipeStep recipeStep = recipe.getRecipeSteps().get(j);

                ContentValues contentValuesSteps = new ContentValues();
                contentValuesSteps.put(RecipeContract.RecipeStepsEntry.COLUMN_DESCRIPTION, recipeStep.getDesc());
                contentValuesSteps.put(RecipeContract.RecipeStepsEntry.COLUMN_SHORT_DESC, recipeStep.getShortDesc());
                contentValuesSteps.put(RecipeContract.RecipeStepsEntry.COLUMN_THUMB_URL, recipeStep.getThumbURL());
                contentValuesSteps.put(RecipeContract.RecipeStepsEntry.COLUMN_VIDEO_URL, recipeStep.getVideoURL());
                contentValuesSteps.put(RecipeContract.RecipeStepsEntry.COLUMN_RECIPE_ID, recipe.getRecipeId());

                context.getContentResolver().insert(RecipeContract.RecipeStepsEntry.CONTENT_URI, contentValuesSteps);

            }

        }

    }

    /**
     * This method will insert one recipe object and return the recipe ID
     *
     * @param recipe
     */
    public static int insertRecipeAndGetId(Recipe recipe, Context context) {

        // Insert to RECIPE table
        ContentValues contentValuesRecipe = new ContentValues();
        contentValuesRecipe.put(RecipeContract.RecipeEntry.COLUMN_RECIPE_NAME, recipe.getRecipeName());
        contentValuesRecipe.put(RecipeContract.RecipeEntry.COLUMN_IMAGE_URL, recipe.getImageURL());

        context.getContentResolver().insert(RecipeContract.RecipeEntry.CONTENT_URI, contentValuesRecipe);

        // Get all recipe's, find the recipe matching by name and pull the ID, return the ID
        // Query for the Ingredients table using the recipe ID
        Cursor c = null;
        try {

            Uri uri = RecipeContract.RecipeEntry.CONTENT_URI;

            c = context.getContentResolver().query(uri,
                    null,
                    null,
                    null,
                    null);

        } catch (Exception e) {
            e.printStackTrace();
        }

        // Loop through the Steps cursor
        if (c.moveToFirst()) {

            do {

                if (c.getString(c.getColumnIndex(RecipeContract.RecipeEntry.COLUMN_RECIPE_NAME)).equalsIgnoreCase(recipe.getRecipeName())) {

                    return c.getInt(0);

                }

            } while (c.moveToNext());
        }

        return -1;
    }

    /**
     * This method will read in a list of Recipe Ingredient and step objects and store them to the database
     * with the appropriate recipe id
     *
     */
    public static void insertRecipeIngredientsAndSteps(List<RecipeIngredients> recipeIngredList, List<RecipeStep> recipeStepList, Context context) {

            // Insert to INGREDIENTS TABLE
            for (int i = 0; i < recipeIngredList.size(); i++) {

                RecipeIngredients recipeIngredients = recipeIngredList.get(i);

                ContentValues contentValuesIngredients = new ContentValues();
                contentValuesIngredients.put(RecipeContract.RecipeIngredientsEntry.COLUMN_INGREDIENT, recipeIngredients.getIngredient());
                contentValuesIngredients.put(RecipeContract.RecipeIngredientsEntry.COLUMN_MEASURE, recipeIngredients.getMeasure());
                contentValuesIngredients.put(RecipeContract.RecipeIngredientsEntry.COLUMN_QUANTITY, recipeIngredients.getQuantity());
                contentValuesIngredients.put(RecipeContract.RecipeIngredientsEntry.COLUMN_RECIPE_ID, recipeIngredients.getRecipeId());

                context.getContentResolver().insert(RecipeContract.RecipeIngredientsEntry.CONTENT_URI, contentValuesIngredients);

            }

            // Insert to STEPS TABLE
            for (int j = 0; j < recipeStepList.size(); j++) {

                RecipeStep recipeStep = recipeStepList.get(j);

                ContentValues contentValuesSteps = new ContentValues();
                contentValuesSteps.put(RecipeContract.RecipeStepsEntry.COLUMN_DESCRIPTION, recipeStep.getDesc());
                contentValuesSteps.put(RecipeContract.RecipeStepsEntry.COLUMN_SHORT_DESC, recipeStep.getShortDesc());
                contentValuesSteps.put(RecipeContract.RecipeStepsEntry.COLUMN_THUMB_URL, recipeStep.getThumbURL());
                contentValuesSteps.put(RecipeContract.RecipeStepsEntry.COLUMN_VIDEO_URL, recipeStep.getVideoURL());
                contentValuesSteps.put(RecipeContract.RecipeStepsEntry.COLUMN_RECIPE_ID, recipeStep.getRecipeId());


                context.getContentResolver().insert(RecipeContract.RecipeStepsEntry.CONTENT_URI, contentValuesSteps);

            }

        }

}
