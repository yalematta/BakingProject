package com.yalematta.android.bakingproject.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.Relation;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yalematta on 1/5/18.
 */

@Entity(tableName = "recipe")
public class Recipe implements Parcelable {

    @PrimaryKey(autoGenerate = false)
    @SerializedName("id")
    public int recipeId;

    @ColumnInfo(name = "recipe_name")
    public String name;

    @Relation(parentColumn = "ingredientName", entityColumn = "quantity")
    public List<Ingredient> ingredients = null;

    @Relation(parentColumn = "stepId", entityColumn = "shortDescription")
    public List<Step> steps = null;

    @ColumnInfo(name = "recipe_servings")
    public int servings;

    @Ignore
    public String image;

    public Recipe(int recipeId, String name, List<Ingredient> ingredients, List<Step> steps, int servings, String image) {
        this.recipeId = recipeId;
        this.name = name;
        this.ingredients = ingredients;
        this.steps = steps;
        this.servings = servings;
        this.image = image;
    }

    //todo: fix this constructor asap
    public Recipe(int recipeId, String name) {
        this.recipeId = recipeId;
        this.name = name;
    }

    public int getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(int id) {
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
        dest.writeList(this.ingredients);
        dest.writeList(this.steps);
        dest.writeInt(this.servings);
        dest.writeString(this.image);
    }

    protected Recipe(Parcel in) {
        this.recipeId = in.readInt();
        this.name = in.readString();
        this.ingredients = new ArrayList<Ingredient>();
        in.readList(this.ingredients, Ingredient.class.getClassLoader());
        this.steps = new ArrayList<Step>();
        in.readList(this.steps, Step.class.getClassLoader());
        this.servings = in.readInt();
        this.image = in.readString();
    }

    public static final Creator<Recipe> CREATOR = new Creator<Recipe>() {
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
