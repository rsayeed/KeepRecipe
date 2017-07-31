package com.keeprecipe.android;

import android.content.Intent;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.keeprecipe.android.R;
import com.keeprecipe.android.RecipeDetailsActivity;
import com.keeprecipe.android.model.Recipe;
import com.keeprecipe.android.model.RecipeIngredients;
import com.keeprecipe.android.model.RecipeStep;
import com.keeprecipe.android.utilities.RecipeDataUtils;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Created by rubab on 7/10/17.
 */

@RunWith(AndroidJUnit4.class)
public class DetailActivityTest {
    @Rule public ActivityTestRule<RecipeDetailsActivity> mActivityTestRule =
            new ActivityTestRule<>(RecipeDetailsActivity.class,false, false);

    private Recipe mockRecipeObj;
    private RecipeIngredients recipeIngredients;
    private RecipeStep recipeStep;

    @Before
    public void testName() {

        mockRecipeObj = new Recipe();
        mockRecipeObj.setRecipeName("PIE");
        mockRecipeObj.setRecipeId("1");
        recipeIngredients = new RecipeIngredients();
        recipeIngredients.setIngredient("Chocolate");
        recipeIngredients.setRecipeId("1");
        recipeStep = new RecipeStep();
        recipeStep.setShortDesc("Preparing");
        recipeStep.setVideoURL("");
        recipeStep.setThumbURL("");
        recipeStep.setDesc("Long Description");
        recipeStep.setRecipeId("1");
        recipeStep.setStepId("1");
        mockRecipeObj.setRecipeIngredients(Arrays.asList(recipeIngredients));
        mockRecipeObj.setRecipeSteps(Arrays.asList((recipeStep)));

        RecipeDataUtils recipeDataUtils = new RecipeDataUtils(mockRecipeObj,
                mockRecipeObj.getRecipeIngredients(), mockRecipeObj.getRecipeSteps());

        Intent i = new Intent();
        i.putExtra("RecipeObj", mockRecipeObj);
        mActivityTestRule.launchActivity(i);

    }

    /**
     * Verify that the dialog opens up from the fragment list
     */
    @Test
    public void clickIngredients_OpensDialog() {

        // Find the button and perform click operation
        onView(withId(R.id.viewRecipeIngredientsBtn)).perform(click());

        // Verify that Recipe Ingredients dialog opens up
        onView(withText("Recipe Ingredients")).check(matches(isDisplayed()));

    }

    /**
     * Clicks on a row item, which opens up the detail fragment
     * Verifies the description within the textView of the fragment
     */
    @Test
    public void listItemClick_loadFragment() {

        // Click on the RecyclerView item at position 0 (first positon)
        onView(withId(R.id.fragment_recycler_view_list)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

        // Verify the description in the detail fragment
        onView(withId((R.id.detail_step))).check(matches(withText("Long Description")));

    }
}
