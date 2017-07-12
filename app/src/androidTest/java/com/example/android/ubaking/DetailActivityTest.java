package com.example.android.ubaking;

import android.content.Intent;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.example.android.ubaking.model.Recipe;
import com.example.android.ubaking.model.RecipeIngredients;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;

import static android.support.test.espresso.Espresso.onData;
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

    @Before
    public void testName() {

        mockRecipeObj = new Recipe();
        mockRecipeObj.setRecipeName("PIE");
        recipeIngredients = new RecipeIngredients();
        recipeIngredients.setIngredient("Chocolate");
        mockRecipeObj.setRecipeIngredients(Arrays.asList(recipeIngredients));

        Intent i = new Intent();
        i.putExtra("RecipeObj", mockRecipeObj);
        mActivityTestRule.launchActivity(i);

    }

    @Test
    public void clickIngredients_OpensDialog() {

        // Find the button and perform click operation
        onView(withId(R.id.loadMoreButton)).perform(click());

        // Verify action
        onView(withText("Recipe Ingredients")).check(matches(isDisplayed()));

    }
}
