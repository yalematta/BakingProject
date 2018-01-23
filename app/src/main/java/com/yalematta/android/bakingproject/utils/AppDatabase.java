package com.yalematta.android.bakingproject.utils;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;

import com.yalematta.android.bakingproject.dao.RecipeDao;
import com.yalematta.android.bakingproject.entities.Recipe;

/**
 * Created by yalematta on 1/21/18.
 */

@Database(entities = {Recipe.class}, version = 1)
@TypeConverters({RecipeTypeConverters.class})
public abstract class AppDatabase extends RoomDatabase {
    public abstract RecipeDao getRecipeDao();
}
