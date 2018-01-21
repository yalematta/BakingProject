package com.yalematta.android.bakingproject.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.yalematta.android.bakingproject.entities.Step;

import java.util.List;

/**
 * Created by yalematta on 1/22/18.
 */

@Dao
public interface StepDao {

    // Adds a step to the database
    @Insert
    void insertAll(List<Step> steps);

    @Delete
    void deleteAll(List<Step> steps);

    // Gets all steps in the database
    @Query("SELECT * FROM step")
    List<Step> getAllStep();

    @Query("SELECT * FROM step WHERE stepRecipeId IS :stepRecipeId")
    List<Step> getStepsForRecipe(int stepRecipeId);
}
