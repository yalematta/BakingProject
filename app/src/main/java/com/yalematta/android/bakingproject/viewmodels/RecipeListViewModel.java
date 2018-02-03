package com.yalematta.android.bakingproject.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import com.yalematta.android.bakingproject.database.AppDatabase;
import com.yalematta.android.bakingproject.entities.Recipe;

import java.util.List;

/**
 * Created by yalematta on 2/3/18.
 */

public class RecipeListViewModel extends AndroidViewModel {

    private final LiveData<List<Recipe>> recipeList;

    private final LiveData<List<Recipe>> favoriteList;

    private AppDatabase appDatabase;

    public RecipeListViewModel(Application application) {
        super(application);

        appDatabase = AppDatabase.getInstance(this.getApplication());

        recipeList = appDatabase.getRecipeDao().getAllRecipes();

        favoriteList = appDatabase.getRecipeDao().getFavoriteRecipes();
    }

    public LiveData<List<Recipe>> getRecipeList() {
        return recipeList;
    }

    public LiveData<List<Recipe>> getFavoriteList() {
        return favoriteList;
    }

    public void updateRecipe(Recipe recipe) {
        new updateAsyncTask(appDatabase).execute(recipe);
    }

    private static class updateAsyncTask extends AsyncTask<Recipe, Void, Void> {

        private AppDatabase db;

        updateAsyncTask(AppDatabase appDatabase) {
            db = appDatabase;
        }

        @Override
        protected Void doInBackground(final Recipe... params) {
            db.getRecipeDao().updateRecipe(params[0]);
            return null;
        }
    }
}
