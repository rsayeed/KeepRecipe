package com.example.android.ubaking.model;

/**
 * Created by rubab on 6/25/17.
 */

import java.io.Serializable;

/**
 * POJO class to represent ingredients for a recipe
 */
public class RecipeIngredients implements Serializable {

    String recipeId;
    String quantity;
    String measure;
    String ingredient;

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getMeasure() {
        return measure;
    }

    public void setMeasure(String measure) {
        this.measure = measure;
    }

    public String getIngredient() {
        return ingredient;
    }

    public void setIngredient(String ingredient) {
        this.ingredient = ingredient;
    }

    public String getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(String recipeId) {
        this.recipeId = recipeId;
    }

    @Override
    public String toString() {
        return "RecipeIngredients{" +
                "recipeId='" + recipeId + '\'' +
                ", quantity='" + quantity + '\'' +
                ", measure='" + measure + '\'' +
                ", ingredient='" + ingredient + '\'' +
                '}';
    }
}
