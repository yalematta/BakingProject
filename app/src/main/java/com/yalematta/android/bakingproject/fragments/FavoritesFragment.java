package com.yalematta.android.bakingproject.fragments;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.yalematta.android.bakingproject.R;
import com.yalematta.android.bakingproject.activities.MainActivity;
import com.yalematta.android.bakingproject.adapters.RecipesAdapter;
import com.yalematta.android.bakingproject.entities.Recipe;
import com.yalematta.android.bakingproject.database.AppDatabase;
import com.yalematta.android.bakingproject.utils.AppUtilities;
import com.yalematta.android.bakingproject.utils.GridSpacingItemDecoration;
import com.yalematta.android.bakingproject.viewmodels.RecipeListViewModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by yalematta on 2/2/18.
 */

public class FavoritesFragment extends Fragment implements RecipesAdapter.ListRecipeClickListener, View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    private static final String SAVED_LAYOUT_MANAGER = "SAVED_LAYOUT_MANAGER";
    private Parcelable layoutManagerSavedState;

    @BindView(R.id.swipe_layout) SwipeRefreshLayout refreshLayout;
    @BindView(R.id.pbLoadingIndicator) ProgressBar pbIndicator;
    @BindView(R.id.tvErrorMessage1) TextView tvErrorMessage1;
    @BindView(R.id.tvErrorMessage2) TextView tvErrorMessage2;
    @BindView(R.id.ivErrorImage) ImageView failedImage;
    @BindView(R.id.rvRecipes) RecyclerView rvRecipes;

    private RecipesAdapter adapter;
    private Recipe anyRecipe;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_recipes, container, false);
        ButterKnife.bind(this, v);

        refreshLayout.setOnRefreshListener(this);

        refreshLayout.setColorSchemeResources(R.color.colorPrimary,
                R.color.colorPrimaryLight,
                R.color.colorPrimaryDark);

        rvRecipes.addItemDecoration(new GridSpacingItemDecoration(1, dpToPx(10), true));

        pbIndicator.setVisibility(View.VISIBLE);
        tvErrorMessage2.setOnClickListener(this);

        setRetainInstance(true);

        populateFavorites();

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.favorites));

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        onRefresh();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private final Response.ErrorListener onRecipesError = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            rvRecipes.setVisibility(View.GONE);
            pbIndicator.setVisibility(View.GONE);
            failedImage.setVisibility(View.VISIBLE);
            tvErrorMessage1.setVisibility(View.VISIBLE);
            tvErrorMessage2.setVisibility(View.VISIBLE);
        }
    };
    //endregion

    private void populateFavorites() {

        if (getActivity() != null) {

            adapter = new RecipesAdapter(this.getContext(), new ArrayList<Recipe>(), this);

            final RecyclerView.LayoutManager mLayoutManager;

            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                mLayoutManager = new GridLayoutManager(getContext(), 1);
                rvRecipes.setLayoutManager(mLayoutManager);
            } else {
                mLayoutManager = new GridLayoutManager(getContext(), 3);
                rvRecipes.setLayoutManager(mLayoutManager);
                rvRecipes.setHasFixedSize(true);
            }
            rvRecipes.setItemAnimator(new DefaultItemAnimator());

            rvRecipes.setAdapter(adapter);

            MainActivity.viewModel = ViewModelProviders.of(this).get(RecipeListViewModel.class);

            MainActivity.viewModel.getFavoriteList().observe(this, new Observer<List<Recipe>>() {
                @Override
                public void onChanged(@Nullable List<Recipe> recipes) {
                    adapter.addItems(recipes);
                }
            });

            adapter.notifyDataSetChanged();

            pbIndicator.setVisibility(View.GONE);
            failedImage.setVisibility(View.GONE);
            rvRecipes.setVisibility(View.VISIBLE);
            tvErrorMessage1.setVisibility(View.GONE);
            tvErrorMessage2.setVisibility(View.GONE);
        }
    }

    @Override
    public void onListRecipeClick(int clickedRecipeIndex) {
        Bundle args = new Bundle();
        args.putParcelable("CLICKED_RECIPE", MainActivity.viewModel.getFavoriteList().getValue().get(clickedRecipeIndex));

        RecipeFragment recipeFragment = new RecipeFragment();
        recipeFragment.setArguments(args);
        getActivity().getSupportFragmentManager().beginTransaction()
                .add(R.id.content_frame, recipeFragment)
                .addToBackStack(recipeFragment.getClass().getSimpleName())
                .commit();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tvErrorMessage2:
                MainActivity.navigationView.getMenu().getItem(0).setChecked(true);
                RecipesFragment recipesFragment = new RecipesFragment();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .add(R.id.content_frame, recipesFragment)
                        .commit();
        }
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setRefreshing(false);
                initializeData();
                if (layoutManagerSavedState != null) {
                    rvRecipes.getLayoutManager().onRestoreInstanceState(layoutManagerSavedState);
                }
            }
        }, 3000);
    }

    //region Designing the CardViews

    /* Converting dp to pixel */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    //endregion

    //region Save and Restore RecyclerView Scroll Position
    @Override
    public Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(SAVED_LAYOUT_MANAGER, rvRecipes.getLayoutManager().onSaveInstanceState());
        return bundle;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            layoutManagerSavedState = ((Bundle) state).getParcelable(SAVED_LAYOUT_MANAGER);
        }
    }

    //endregion

    @Override
    public void onResume() {
        super.onResume();

        initializeData();
        if (layoutManagerSavedState != null) {
            rvRecipes.getLayoutManager().onRestoreInstanceState(layoutManagerSavedState);
        }
        getActivity().setTitle(R.string.favorites);
    }

    private void initializeData() {

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                anyRecipe = AppDatabase.getInstance(getContext()).getRecipeDao().getAnyFavoriteRecipe();
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                super.onPostExecute(result);

                if (getActivity() != null) {

                    if (anyRecipe != null) {
                        populateFavorites();
                    } else {
                        rvRecipes.setVisibility(View.GONE);
                        failedImage.setVisibility(View.VISIBLE);

                        tvErrorMessage1.setText(getString(R.string.no_favorite));
                        tvErrorMessage2.setText(getString(R.string.favorite_some));

                        tvErrorMessage1.setVisibility(View.VISIBLE);
                        tvErrorMessage2.setVisibility(View.VISIBLE);
                    }
                }
            }

        }.execute();
    }
}
