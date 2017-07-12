package com.example.android.ubaking.model;

/**
 * Created by rubab on 6/25/17.
 */

import java.io.Serializable;
import java.util.List;

/**
 * Bean class to hold all data pertaining to the Recipe JSON object
 */
public class Recipe implements Serializable {

    String recipeId;
    String recipeName;
    List<RecipeIngredients> recipeIngredients;
    List<RecipeStep> recipeSteps;
    String servingSize;

    public String getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(String recipeId) {
        this.recipeId = recipeId;
    }

    public String getRecipeName() {
        return recipeName;
    }

    public void setRecipeName(String recipeName) {
        this.recipeName = recipeName;
    }

    public List<RecipeIngredients> getRecipeIngredients() {
        return recipeIngredients;
    }

    public void setRecipeIngredients(List<RecipeIngredients> recipeIngredients) {
        this.recipeIngredients = recipeIngredients;
    }

    public List<RecipeStep> getRecipeSteps() {
        return recipeSteps;
    }

    public void setRecipeSteps(List<RecipeStep> recipeSteps) {
        this.recipeSteps = recipeSteps;
    }

    public String getServingSize() {
        return servingSize;
    }

    public void setServingSize(String servingSize) {
        this.servingSize = servingSize;
    }

    @Override
    public String toString() {
        return "Recipe{" +
                "recipeId='" + recipeId + '\'' +
                ", recipeName='" + recipeName + '\'' +
                ", recipeIngredients=" + recipeIngredients +
                ", recipeSteps=" + recipeSteps +
                ", servingSize='" + servingSize + '\'' +
                '}';
    }
}
