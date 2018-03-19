package com.yalematta.android.bakingproject.activities;


import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.yalematta.android.bakingproject.R;
import com.crashlytics.android.Crashlytics;
import com.yalematta.android.bakingproject.fragments.AboutFragment;
import com.yalematta.android.bakingproject.fragments.FavoritesFragment;
import com.yalematta.android.bakingproject.fragments.RecipesFragment;
import com.yalematta.android.bakingproject.viewmodels.RecipeListViewModel;

import butterknife.BindView;
import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, FragmentManager.OnBackStackChangedListener {

    public static RecipeListViewModel viewModel;

    public static @BindView(R.id.nav_view) NavigationView navigationView;
    @BindView(R.id.toolbar) Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);

        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(0).setChecked(true);

        if (null == savedInstanceState) {
            // set you initial fragment object

            RecipesFragment recipesFragment = new RecipesFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_frame, recipesFragment, RecipesFragment.class.getSimpleName())
                    .commit();

            getSupportFragmentManager().addOnBackStackChangedListener(this);
        }

        if(getIntent().getStringExtra("NAV_CLICKED") != null){

            String navigation = getIntent().getStringExtra("NAV_CLICKED");

            FragmentManager fm = getSupportFragmentManager();
            int count = fm.getBackStackEntryCount();
            for (int i = 0; i < count; ++i) {
                fm.popBackStackImmediate();
            }

            if (navigation.equals("nav_explore")){

                navigationView.setCheckedItem(R.id.nav_explore);
                RecipesFragment recipesFragment = new RecipesFragment();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.content_frame, recipesFragment, RecipesFragment.class.getSimpleName())
                        .commit();

            } else if (navigation.equals("nav_favorites")){

                navigationView.setCheckedItem(R.id.nav_favorites);
                FavoritesFragment favoritesFragment = new FavoritesFragment();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.content_frame, favoritesFragment)
                        .addToBackStack(FavoritesFragment.class.getSimpleName())
                        .commit();

            } else if (navigation.equals("nav_about")){

                navigationView.setCheckedItem(R.id.nav_about);
                AboutFragment aboutFragment = new AboutFragment();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.content_frame, aboutFragment)
                        .addToBackStack(AboutFragment.class.getSimpleName())
                        .commit();
            }

            drawer.closeDrawer(GravityCompat.START);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_explore) {

            FragmentManager fm = getSupportFragmentManager();
            int count = fm.getBackStackEntryCount();
            for (int i = 0; i < count; ++i) {
                fm.popBackStackImmediate();
            }

            RecipesFragment recipesFragment = new RecipesFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_frame, recipesFragment, RecipesFragment.class.getSimpleName())
                    .commit();

        } else if (id == R.id.nav_favorites) {

            FragmentManager fm = getSupportFragmentManager();
            int count = fm.getBackStackEntryCount();
            for (int i = 0; i < count; ++i) {
                fm.popBackStackImmediate();
            }

            FavoritesFragment favoritesFragment = new FavoritesFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_frame, favoritesFragment)
                    .addToBackStack(FavoritesFragment.class.getSimpleName())
                    .commit();

        } else if (id == R.id.nav_about) {

            FragmentManager fm = getSupportFragmentManager();
            int count = fm.getBackStackEntryCount();
            for (int i = 0; i < count; ++i) {
                fm.popBackStackImmediate();
            }

            AboutFragment aboutFragment = new AboutFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_frame, aboutFragment)
                    .addToBackStack(AboutFragment.class.getSimpleName())
                    .commit();
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
        }
        else if (getSupportFragmentManager().getBackStackEntryAt(lastBackStackEntryCount).getName().equals(FavoritesFragment.class.getSimpleName())){
            getSupportActionBar().setTitle(R.string.favorites);
        }
    }
}
