package com.yalematta.android.bakingproject.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.yalematta.android.bakingproject.entities.Ingredient;

import java.util.List;

/**
 * Created by yalematta on 1/22/18.
 */

@Dao
public interface IngredientDao {

    // Adds an ingredient to the database
    @Insert
    void insertAll(List<Ingredient> ingredients);

    @Delete
    void deleteAll(List<Ingredient> ingredients);

    // Gets all ingredients in the database
    @Query("SELECT * FROM ingredients")
    List<Ingredient> getAllIngredient();
}
