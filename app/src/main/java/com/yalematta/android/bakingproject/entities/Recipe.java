package com.yalematta.android.bakingproject.entities;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;


/**
 * Created by yalematta on 1/5/18.
 */

public class Recipe implements Parcelable {

    @SerializedName("id")
    public int recipeId;

    public String name;

    public List<Ingredient> ingredients = null;

    public List<Step> steps = null;

    public int servings;

    public String image;

    public Recipe(int recipeId, String name, List<Ingredient> ingredients, List<Step> steps, int servings, String image) {
        this.recipeId = recipeId;
        this.name = name;
        this.ingredients = ingredients;
        this.steps = steps;
        this.servings = servings;
        this.image = image;
    }

    public int getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(int recipeId) {
        this.recipeId = recipeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    public List<Step> getSteps() {
        return steps;
    }

    public void setSteps(List<Step> steps) {
        this.steps = steps;
    }

    public int getServings() {
        return servings;
    }

    public void setServings(int servings) {
        this.servings = servings;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.recipeId);
        dest.writeString(this.name);
        dest.writeTypedList(this.ingredients);
        dest.writeTypedList(this.steps);
        dest.writeInt(this.servings);
        dest.writeString(this.image);
    }

    protected Recipe(Parcel in) {
        this.recipeId = in.readInt();
        this.name = in.readString();
        this.ingredients = in.createTypedArrayList(Ingredient.CREATOR);
        this.steps = in.createTypedArrayList(Step.CREATOR);
        this.servings = in.readInt();
        this.image = in.readString();
    }

    public static final Parcelable.Creator<Recipe> CREATOR = new Parcelable.Creator<Recipe>() {
        @Override
        public Recipe createFromParcel(Parcel source) {
            return new Recipe(source);
        }

        @Override
        public Recipe[] newArray(int size) {
            return new Recipe[size];
        }
    };
}