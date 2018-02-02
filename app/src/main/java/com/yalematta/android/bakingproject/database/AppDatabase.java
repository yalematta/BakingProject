package com.yalematta.android.bakingproject.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

import com.yalematta.android.bakingproject.dao.RecipeDao;
import com.yalematta.android.bakingproject.entities.Recipe;

/**
 * Created by yalematta on 1/21/18.
 */

@Database(entities = {Recipe.class}, version = 1)
@TypeConverters({RecipeTypeConverters.class})
public abstract class AppDatabase extends RoomDatabase {

    private static final String DATABASE_NAME = "recipes-database.db";
    private static volatile AppDatabase instance;

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = create(context);
        }
        return instance;
    }

    public static AppDatabase create(final Context context) {
        return Room.databaseBuilder(
                context,
                AppDatabase.class,
                DATABASE_NAME)
                .build();
    }

    public abstract RecipeDao getRecipeDao();
}
