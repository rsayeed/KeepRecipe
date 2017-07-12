package com.example.android.ubaking.model;

import java.io.Serializable;

/**
 * Created by rubab on 6/25/17.
 */

public class RecipeStep implements Serializable {

    String recipeId;
    String stepId;
    String shortDesc;
    String desc;
    String videoURL;
    String thumbURL;

    public String getStepId() {
        return stepId;
    }

    public void setStepId(String stepId) {
        this.stepId = stepId;
    }

    public String getShortDesc() {
        return shortDesc;
    }

    public void setShortDesc(String shortDesc) {
        this.shortDesc = shortDesc;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getVideoURL() {
        return videoURL;
    }

    public void setVideoURL(String videoURL) {
        this.videoURL = videoURL;
    }

    public String getThumbURL() {
        return thumbURL;
    }

    public void setThumbURL(String thumbURL) {
        this.thumbURL = thumbURL;
    }

    public String getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(String recipeId) {
        this.recipeId = recipeId;
    }

    @Override
    public String toString() {
        return "RecipeStep{" +
                "recipeId='" + recipeId + '\'' +
                ", stepId='" + stepId + '\'' +
                ", shortDesc='" + shortDesc + '\'' +
                ", desc='" + desc + '\'' +
                ", videoURL='" + videoURL + '\'' +
                ", thumbURL='" + thumbURL + '\'' +
                '}';
    }
}
