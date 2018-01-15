package com.yalematta.android.bakingproject.utils;

import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.yalematta.android.bakingproject.database.AppDatabase;
import com.yalematta.android.bakingproject.entities.Recipe;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by yalematta on 1/15/18.
 */

public class DatabaseInitializer {

    private static final String TAG = DatabaseInitializer.class.getName();

    public static void populateAsync(@NonNull final AppDatabase db) {
        PopulateDbAsync task = new PopulateDbAsync(db);
        task.execute();
    }

    private static Recipe addRecipe(final AppDatabase db, Recipe recipe) {
        db.recipeDao().insertAll(recipe);
        return recipe;
    }

    private static class PopulateDbAsync extends AsyncTask<Void, Void, Void> {

        private final AppDatabase mDb;

        PopulateDbAsync(AppDatabase db) {
            mDb = db;
        }

        @Override
        protected Void doInBackground(final Void... params) {
            //todo: fix this asap
            if (mDb.recipeDao().countRecipes() == 0) {
                List<Recipe> recipes = new ArrayList<>();
                recipes.add(new Recipe(0, "Pasta"));
                recipes.add(new Recipe(1, "Pasta 2"));
                recipes.add(new Recipe(2, "Pasta 3"));

                mDb.recipeDao()
                        .insertAll(
                                recipes.toArray(new Recipe[recipes.size()]));
            }

            return null;
        }

        //todo: we still need to use those methods...

    }
}