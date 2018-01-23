package com.yalematta.android.bakingproject.utils;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;

import com.yalematta.android.bakingproject.dao.IngredientDao;
import com.yalematta.android.bakingproject.dao.RecipeDao;
import com.yalematta.android.bakingproject.dao.StepDao;
import com.yalematta.android.bakingproject.entities.Ingredient;
import com.yalematta.android.bakingproject.entities.Recipe;
import com.yalematta.android.bakingproject.entities.Step;

/**
 * Created by yalematta on 1/21/18.
 */

@Database(entities = {Recipe.class, Ingredient.class, Step.class}, version = 1)
@TypeConverters({RecipeTypeConverters.class})
public abstract class AppDatabase extends RoomDatabase {
    public abstract StepDao getStepDao();
    public abstract RecipeDao getRecipeDao();
    public abstract IngredientDao getIngredientDao();
}
