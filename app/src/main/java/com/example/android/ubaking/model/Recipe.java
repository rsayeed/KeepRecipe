package com.example.android.ubaking.model;

/**
 * Created by rubab on 6/25/17.
 */

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.List;

/**
 * Bean class to hold all data pertaining to the Recipe JSON object
 */
public class Recipe implements Parcelable {

    String recipeId;
    String recipeName;
    List<RecipeIngredients> recipeIngredients;
    List<RecipeStep> recipeSteps;
    String servingSize;
    String imageURL;

    public Recipe() {}


    protected Recipe(Parcel in) {
        recipeId = in.readString();
        recipeName = in.readString();
        recipeIngredients = in.createTypedArrayList(RecipeIngredients.CREATOR);
        recipeSteps = in.createTypedArrayList(RecipeStep.CREATOR);
        servingSize = in.readString();
        imageURL = in.readString();
    }

    public static final Creator<Recipe> CREATOR = new Creator<Recipe>() {
        @Override
        public Recipe createFromParcel(Parcel in) {
            return new Recipe(in);
        }

        @Override
        public Recipe[] newArray(int size) {
            return new Recipe[size];
        }
    };

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

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    @Override
    public String toString() {
        return "Recipe{" +
                "recipeId='" + recipeId + '\'' +
                ", recipeName='" + recipeName + '\'' +
                ", recipeIngredients=" + recipeIngredients +
                ", recipeSteps=" + recipeSteps +
                ", servingSize='" + servingSize + '\'' +
                ", imageURL='" + imageURL + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(recipeId);
        dest.writeString(recipeName);
        dest.writeTypedList(recipeIngredients);
        dest.writeTypedList(recipeSteps);
        dest.writeString(servingSize);
        dest.writeString(imageURL);
    }


}
