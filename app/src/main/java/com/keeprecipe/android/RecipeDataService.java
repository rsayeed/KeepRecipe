package com.keeprecipe.android;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.Nullable;

import com.keeprecipe.android.data.RecipeContract;
import com.keeprecipe.android.model.Recipe;
import com.keeprecipe.android.model.RecipeIngredients;
import com.keeprecipe.android.model.RecipeStep;
import com.keeprecipe.android.utilities.RecipeDataUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rubab on 7/9/17.
 */

/**
 * This intent service is used by the RecipeWidgetProvider and theso that recipe information can be
 * displayed in the corresponding widget
 */
public class RecipeDataService extends IntentService {

    private static final String TAG = RecipeDataService.class.getSimpleName();

    public static final String ACTION_QUERY_RECIPES = "com.example.android.recipe.action.query_recipes";

    public RecipeDataService() {
        super("RecipeDataService");
    }

    /**
     * Starts this service to perform UpdatePlantWidgets action with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionQueryRecipes(Context context) {
        Intent intent = new Intent(context, RecipeDataService.class);
        intent.setAction(ACTION_QUERY_RECIPES);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_QUERY_RECIPES.equals(action)) {
                handleActionQueryRecipes();
            }
        }
    }

    /**
     * Handle action UpdatePlantWidgets in the provided background thread
     */
    private void handleActionQueryRecipes() {

        List<Recipe> recipeList = new ArrayList<>();

        //Query to get a list of all Recipe's from the database
        Cursor cursor = getContentResolver().query(RecipeContract.RecipeEntry.CONTENT_URI,
                null,
                null,
                null,
                null);

        // Check to see if there are any Recipes
        if (cursor.moveToFirst()) {

            do {

                Recipe recipe = new Recipe();

                recipe.setRecipeId(cursor.getString(0));
                recipe.setRecipeName(cursor.getString(1));
                recipe.setServingSize(cursor.getString(2));

                // Get Recipe Ingredients
                List<RecipeIngredients> recipeIngredientsList = RecipeDataUtils.queryforIngredientsData(recipe.getRecipeId(), this);

                // Get Steps Ingredients
                List<RecipeStep> recipeStepList = RecipeDataUtils.queryforStepsData(recipe.getRecipeId(), this);

                recipe.setRecipeIngredients(recipeIngredientsList);
                recipe.setRecipeSteps(recipeStepList);

                // Add to master list of recipes
                recipeList.add(recipe);

            } while (cursor.moveToNext());

        }

        // Save the collection of Recipes
        RecipeDataUtils.setRecipeListForWidget(recipeList);

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, RecipeWidgetProvider.class));

        //Now update all widgets
        RecipeWidgetProvider.updateRecipeWidgets(this, appWidgetManager, appWidgetIds);
    }

}
