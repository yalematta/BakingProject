package com.yalematta.android.bakingproject.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.yalematta.android.bakingproject.entities.Recipe;

import java.util.List;

/**
 * Created by yalematta on 1/21/18.
 */

@Dao
public interface RecipeDao {

    // Adds a recipe to the database
    @Insert
    void insertAll(List<Recipe> recipes);

    @Delete
    void deleteAll(List<Recipe> recipes);

    @Update
    void update(Recipe recipe);

    // Gets all recipes in the database
    @Query("SELECT * FROM recipes")
    List<Recipe> getAllRecipes();

    // Gets recipe in the database with a recipe Id
    @Query("SELECT * FROM recipes WHERE recipeId LIKE :recipeId")
    Recipe getRecipeWhereRecipeId(int recipeId);

    // Gets favorite recipes
    @Query("SELECT * FROM recipes WHERE isFavorite = 1")
    List<Recipe> getFavoriteRecipes();

}
