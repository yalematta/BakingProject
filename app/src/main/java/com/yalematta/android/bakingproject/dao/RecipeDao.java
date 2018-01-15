package com.yalematta.android.bakingproject.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.yalematta.android.bakingproject.entities.Recipe;

import java.util.List;

/**
 * Created by yalematta on 1/15/18.
 */

@Dao
public interface RecipeDao {

    @Query("SELECT * FROM recipe")
    List<Recipe> getAll();

    @Query("SELECT * FROM recipe where recipe_name LIKE  :name")
    Recipe findByName(String name);

    @Query("SELECT COUNT(*) from recipe")
    int countRecipes();

    @Insert
    void insertAll(Recipe... recipes);

    @Delete
    void delete(Recipe recipe);
}
