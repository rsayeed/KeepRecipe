package com.keeprecipe.android.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by rubab on 6/25/17.
 */

public class RecipeStep implements Parcelable {

    String recipeId;
    String stepId;
    String shortDesc;
    String desc;
    String videoURL;
    String thumbURL;

    public RecipeStep() {}

    protected RecipeStep(Parcel in) {
        recipeId = in.readString();
        stepId = in.readString();
        shortDesc = in.readString();
        desc = in.readString();
        videoURL = in.readString();
        thumbURL = in.readString();
    }

    public static final Creator<RecipeStep> CREATOR = new Creator<RecipeStep>() {
        @Override
        public RecipeStep createFromParcel(Parcel in) {
            return new RecipeStep(in);
        }

        @Override
        public RecipeStep[] newArray(int size) {
            return new RecipeStep[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(recipeId);
        dest.writeString(stepId);
        dest.writeString(shortDesc);
        dest.writeString(desc);
        dest.writeString(videoURL);
        dest.writeString(thumbURL);

    }
}
