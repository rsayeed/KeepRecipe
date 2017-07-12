package com.example.android.ubaking;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import com.example.android.ubaking.model.RecipeIngredients;
import com.example.android.ubaking.utilities.RecipeDataUtils;

/**
 * Created by rubab on 7/8/17.
 */

public class RecipeWidgetProvider extends AppWidgetProvider {

    private static final String TAG = RecipeWidgetProvider.class.getSimpleName();

    private static final String ACTION_NEXT_CLICK =
            "com.example.android.ubaking.action.NEXT_CLICK";

    private static final String defaultTitle = "NO RECIPE FOUND";

    private static final String defaultText = "Hey there. Looks like you need to add some Recipe's first! Click here to add some Recipes.";

    private static int mRecipePosition = -1;

    public static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                       int appWidgetId) {

        final String bullet = "\u25CF" + " ";

        // Create an Intent to launch MainActivity when clicked
        Intent intentMain = new Intent(context, RecipeMainActivity.class);
        PendingIntent pendingIntentMain = PendingIntent.getActivity(context, 0, intentMain, PendingIntent.FLAG_CANCEL_CURRENT);

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.recipe_widget);

        if (RecipeDataUtils.getRecipeListForWidget().size() > 0) {

            if ((++mRecipePosition) >= RecipeDataUtils.getRecipeListForWidget().size()) {
                mRecipePosition = 0;
            }

            // Create an intent to launch DetailActivity when clicked
            Intent intentDetail = new Intent(context, RecipeDetailsActivity.class);

            intentDetail.putExtra("RecipeObj", RecipeDataUtils.getRecipeListForWidget().get(mRecipePosition));
            PendingIntent pendingIntentDetail = PendingIntent.getActivity(context, 0, intentDetail, PendingIntent.FLAG_CANCEL_CURRENT);

            // Set title of Recipe
            views.setTextViewText(R.id.widget_recipe_textView, RecipeDataUtils.getRecipeListForWidget()
                    .get(mRecipePosition).getRecipeName());

            StringBuilder recipeIngredientString = new StringBuilder();

            // Set the ingredients for the given recipe
            for (int x = 0; x < RecipeDataUtils.getRecipeListForWidget()
                    .get(mRecipePosition).getRecipeIngredients().size(); x++) {

                // We don't have enough space to list out all the ingredients for this recipe
                if (x == 5) {
                    recipeIngredientString.append("...");
                    break;
                }

                RecipeIngredients recipeIngredients = RecipeDataUtils.getRecipeListForWidget()
                        .get(mRecipePosition).getRecipeIngredients().get(x);

                recipeIngredientString.append(bullet);
                recipeIngredientString.append(recipeIngredients.getQuantity() + " ");
                recipeIngredientString.append(recipeIngredients.getMeasure() + " ");
                recipeIngredientString.append(recipeIngredients.getIngredient());
                recipeIngredientString.append("\n");
            }

            // Set ingredients
            views.setTextViewText(R.id.widget_ingredients_list, recipeIngredientString.toString());

            // Click handler for "Next Recipe" button
            views.setOnClickPendingIntent(R.id.WidgetNextButton, getPendingSelfIntent(context,
                    ACTION_NEXT_CLICK));

            // Hide the refresh button since it will be taken care of periodically
            views.setViewVisibility(R.id.refreshWidgetBtn, View.GONE);

            // Display the Next button if we have data
            views.setViewVisibility(R.id.WidgetNextButton, View.VISIBLE);

            // On Click Handler for Details
            views.setOnClickPendingIntent(R.id.widget_ingredients_list, pendingIntentDetail);

        }

        else {

            // No Recipes loaded, display refresh button after user has loaded Main Activity
            views.setTextViewText(R.id.widget_recipe_textView, defaultTitle);
            views.setTextViewText(R.id.widget_ingredients_list, defaultText);
            views.setViewVisibility(R.id.WidgetNextButton, View.GONE);
            views.setViewVisibility(R.id.refreshWidgetBtn, View.VISIBLE);

            // Click handler for "Next Recipe" button
            views.setOnClickPendingIntent(R.id.refreshWidgetBtn, getPendingSelfIntent(context,
                    ACTION_NEXT_CLICK));

            // Set the Main intent on the Ingredients textView
            views.setOnClickPendingIntent(R.id.widget_ingredients_list, pendingIntentMain);

        }

        // Widgets allow click handlers to only launch pending intents
        views.setOnClickPendingIntent(R.id.widget_recipe_textView, pendingIntentMain);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    private static PendingIntent getPendingSelfIntent(Context context, String action) {
        // An explicit intent directed at the current class (the "self").
        Intent intent = new Intent(context, RecipeWidgetProvider.class);
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        //Start the intent service update widget action, the service takes care of updating the widgets UI
        RecipeDataService.startActionQueryRecipes(context);
    }

    public static void updateRecipeWidgets(Context context, AppWidgetManager appWidgetManager,
                                           int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context.getPackageName(), getClass().getName()));

        if (ACTION_NEXT_CLICK.equals(intent.getAction())) {
            Log.v(TAG, "Next button clicked");
            onUpdate(context, appWidgetManager, appWidgetIds);
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // Perform any action when one or more AppWidget instances have been deleted
    }

    @Override
    public void onEnabled(Context context) {
        // Perform any action when an AppWidget for this provider is instantiated
    }

    @Override
    public void onDisabled(Context context) {
        // Perform any action when the last AppWidget instance for this provider is deleted
    }
}
