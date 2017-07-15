package com.example.android.ubaking.utilities;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.example.android.ubaking.model.Recipe;
import com.example.android.ubaking.model.RecipeIngredients;
import com.example.android.ubaking.model.RecipeStep;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rubab on 6/25/17.
 */

public class RecipeJsonUtils {

    private static final String TAG = RecipeJsonUtils.class.getSimpleName();

    /**
     * This method parses JSON from a web response and returns a list of Recipe Items
     *
     * @param recipeJsonStr JSON response from server
     * @return List of GridItems containing image URL and movie ID
     * @throws JSONException If JSON data cannot be properly parsed
     */
    public static List<Recipe> parseJSON(Context context, String recipeJsonStr)
            throws JSONException {

        /* List of GridItems to hold each movie's poster URL and movie ID */
        List<Recipe> parsedRecipeList = new ArrayList<>();
        List<RecipeIngredients> parsedRecipeIngredientsList;
        List<RecipeStep> parsedRecipeStepsList;

        JSONArray recipeJsonArray = new JSONArray(recipeJsonStr);

        Log.v(TAG, "recipeJsonArray: " + recipeJsonArray.length());

        for (int x = 0; x < recipeJsonArray.length(); x++) {

            Recipe recipe = new Recipe();
            parsedRecipeIngredientsList = new ArrayList<>();
            parsedRecipeStepsList = new ArrayList<>();

            JSONObject recipeJson = recipeJsonArray.getJSONObject(x);

            recipe.setRecipeId(recipeJson.getString("id"));
            recipe.setRecipeName(recipeJson.getString("name"));
            recipe.setImageURL(recipeJson.getString("image"));

            JSONArray ingredientsArray = recipeJson.getJSONArray("ingredients");

            for (int i = 0; i < ingredientsArray.length(); i++) {

                RecipeIngredients recipeIngredients = new RecipeIngredients();

                JSONObject ingredients = ingredientsArray.getJSONObject(i);

                recipeIngredients.setQuantity(ingredients.getString("quantity"));
                recipeIngredients.setMeasure(ingredients.getString("measure"));
                recipeIngredients.setIngredient(ingredients.getString("ingredient"));

                // Add to master list of Recipe ingredients
                parsedRecipeIngredientsList.add(recipeIngredients);
            }

            // Add recipe ingredients to recipe object
            recipe.setRecipeIngredients(parsedRecipeIngredientsList);

            JSONArray recipeStepsArray = recipeJson.getJSONArray("steps");

            for (int j = 0; j < recipeStepsArray.length(); j++) {

                RecipeStep recipeStep = new RecipeStep();

                JSONObject steps = recipeStepsArray.getJSONObject(j);

                recipeStep.setStepId(steps.getString("id"));
                recipeStep.setShortDesc(steps.getString("shortDescription"));
                recipeStep.setDesc(steps.getString("description"));
                recipeStep.setVideoURL(steps.getString("videoURL"));
                recipeStep.setThumbURL(steps.getString("thumbnailURL"));

                parsedRecipeStepsList.add(recipeStep);

            }

            // Add recipe steps to recipe object
            recipe.setRecipeSteps(parsedRecipeStepsList);

            recipe.setServingSize(recipeJson.getString("servings"));

            // Add recipe object to master list
            parsedRecipeList.add(recipe);

            Log.v(TAG, recipe.toString());

        }
        return parsedRecipeList;
    }

}

