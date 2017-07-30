package com.example.android.ubaking.model;

/**
 * Created by rubab on 6/25/17.
 */

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * POJO class to represent ingredients for a recipe
 */
public class RecipeIngredients implements Parcelable {

    String recipeId;
    String quantity;
    String measure;
    String ingredient;

    public RecipeIngredients() {}

    protected RecipeIngredients(Parcel in) {
        recipeId = in.readString();
        quantity = in.readString();
        measure = in.readString();
        ingredient = in.readString();
    }

    public static final Creator<RecipeIngredients> CREATOR = new Creator<RecipeIngredients>() {
        @Override
        public RecipeIngredients createFromParcel(Parcel in) {
            return new RecipeIngredients(in);
        }

        @Override
        public RecipeIngredients[] newArray(int size) {
            return new RecipeIngredients[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(recipeId);
        dest.writeString(quantity);
        dest.writeString(measure);
        dest.writeString(ingredient);
    }
}
