package com.yalematta.android.bakingproject.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.crashlytics.android.Crashlytics;
import com.yalematta.android.bakingproject.R;
import com.yalematta.android.bakingproject.entities.Recipe;
import com.yalematta.android.bakingproject.fragments.AboutFragment;
import com.yalematta.android.bakingproject.fragments.FavoritesFragment;
import com.yalematta.android.bakingproject.fragments.IngredientsFragment;
import com.yalematta.android.bakingproject.fragments.RecipeFragment;
import com.yalematta.android.bakingproject.fragments.RecipesFragment;
import com.yalematta.android.bakingproject.fragments.StepsFragment;

import java.util.ArrayList;

import io.fabric.sdk.android.Fabric;

/**
 * Created by yalematta on 3/11/18.
 */

public class RecipeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, FragmentManager.OnBackStackChangedListener {

    public static FrameLayout secondFrame;

    public static NavigationView navigationView;

    public static boolean mTwoPane;

    private Recipe clickedRecipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_recipe);

        mTwoPane = findViewById(R.id.second_frame) != null;

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(0).setChecked(true);

        secondFrame = findViewById(R.id.second_frame);

        clickedRecipe = getIntent().getParcelableExtra("CLICKED_RECIPE");
        Bundle args = new Bundle();
        Bundle _args = new Bundle();
        args.putParcelable("CLICKED_RECIPE", clickedRecipe);
        _args.putParcelableArrayList("INGREDIENTS", (ArrayList<? extends Parcelable>) clickedRecipe.getIngredients());

        if (null == savedInstanceState) {
            // set your initial fragment object

            if (mTwoPane) {
                RecipeFragment recipeFragment = new RecipeFragment();
                recipeFragment.setArguments(args);
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.content_frame, recipeFragment)
                        .addToBackStack(recipeFragment.getClass().getSimpleName())
                        .commit();

                IngredientsFragment ingredientsFragment = new IngredientsFragment();
                ingredientsFragment.setArguments(_args);
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.second_frame, ingredientsFragment)
                        .addToBackStack(ingredientsFragment.getClass().getSimpleName())
                        .commit();

            } else {
                RecipeFragment recipeFragment = new RecipeFragment();
                recipeFragment.setArguments(args);
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.content_frame, recipeFragment)
                        .addToBackStack(recipeFragment.getClass().getSimpleName())
                        .commit();
            }

            getSupportFragmentManager().addOnBackStackChangedListener(this);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
            finish();
        } else {
            super.onBackPressed();
        }

        if (mTwoPane) {
            finish();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_explore) {

            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("NAV_CLICKED", "nav_explore");
            startActivity(intent);

        } else if (id == R.id.nav_favorites) {

            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("NAV_CLICKED", "nav_favorites");
            startActivity(intent);

        } else if (id == R.id.nav_about) {

            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("NAV_CLICKED", "nav_about");
            startActivity(intent);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackStackChanged() {
        int lastBackStackEntryCount = getSupportFragmentManager().getBackStackEntryCount() - 1;

        if (lastBackStackEntryCount == -1) {
            getSupportActionBar().setTitle(R.string.title_activity_main);
            navigationView.getMenu().getItem(0).setChecked(true);
        } else if (getSupportFragmentManager().getBackStackEntryAt(lastBackStackEntryCount).getName().equals(FavoritesFragment.class.getSimpleName())) {
            getSupportActionBar().setTitle(R.string.favorites);
        }
    }
}

