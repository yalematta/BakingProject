package com.yalematta.android.bakingproject.fragments;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Rect;
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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.yalematta.android.bakingproject.BuildConfig;
import com.yalematta.android.bakingproject.activities.MainActivity;
import com.yalematta.android.bakingproject.activities.RecipeActivity;
import com.yalematta.android.bakingproject.adapters.RecipesAdapter;
import com.yalematta.android.bakingproject.entities.Recipe;
import com.yalematta.android.bakingproject.R;
import com.yalematta.android.bakingproject.database.AppDatabase;
import com.yalematta.android.bakingproject.utils.AppUtilities;
import com.yalematta.android.bakingproject.utils.GridSpacingItemDecoration;
import com.yalematta.android.bakingproject.viewmodels.RecipeListViewModel;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by yalematta on 1/7/18.
 */

public class RecipesFragment extends Fragment implements RecipesAdapter.ListRecipeClickListener, SwipeRefreshLayout.OnRefreshListener {

    private static final String SAVED_LAYOUT_MANAGER = "SAVED_LAYOUT_MANAGER";
    private Parcelable layoutManagerSavedState;
    private RequestQueue requestQueue;
    private Gson gson;

    @BindView(R.id.swipe_layout) SwipeRefreshLayout refreshLayout;
    @BindView(R.id.pbLoadingIndicator) ProgressBar pbIndicator;
    @BindView(R.id.tvErrorMessage1) TextView tvErrorMessage1;
    @BindView(R.id.tvErrorMessage2) TextView tvErrorMessage2;
    @BindView(R.id.ivErrorImage) ImageView failedImage;
    @BindView(R.id.rvRecipes) RecyclerView rvRecipes;

    private List<Recipe> recipeList;
    private RecipesAdapter adapter;
    private Recipe anyRecipe;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_recipes, container, false);
        ButterKnife.bind(this, v);

        refreshLayout.setOnRefreshListener(this);

        refreshLayout.setColorSchemeResources(R.color.colorPrimaryDark);

        rvRecipes.addItemDecoration(new GridSpacingItemDecoration(1, dpToPx(10), true));

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.app_name));

        pbIndicator.setVisibility(View.VISIBLE);

        setRetainInstance(true);

        populateView();

        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    private void initializeDataFromAPI() {
        requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        GsonBuilder gsonBuilder = new GsonBuilder();
        gson = gsonBuilder.create();
        fetchRecipes();
    }

    private void fetchRecipes() {
        StringRequest request = new StringRequest(Request.Method.GET, BuildConfig.ENDPOINT , onRecipesLoaded, onRecipesError);
        requestQueue.add(request);
    }

    //region Volley Response
    private final Response.Listener<String> onRecipesLoaded = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            recipeList = Arrays.asList(gson.fromJson(response, Recipe[].class));

            if (recipeList.size() > 0) {
                createDatabase();
            } else {
                rvRecipes.setVisibility(View.GONE);
                pbIndicator.setVisibility(View.GONE);
                failedImage.setVisibility(View.VISIBLE);
                tvErrorMessage1.setVisibility(View.VISIBLE);
                tvErrorMessage2.setVisibility(View.VISIBLE);
            }
        }
    };

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

    private void createDatabase() {
        AppDatabase.create(getContext());
        insertRecipes();
        populateView();
    }

    public void insertRecipes() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                AppDatabase.getInstance(getContext()).getRecipeDao().deleteAll(recipeList);
                AppDatabase.getInstance(getContext()).getRecipeDao().insertAll(recipeList);
                return null;
            }
        }.execute();
    }

    private void populateView() {

        if (getActivity() != null) {

            adapter = new RecipesAdapter(getContext(), new ArrayList<Recipe>(), this);

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

            MainActivity.viewModel.getRecipeList().observe(this, new Observer<List<Recipe>>() {
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

        Intent myIntent = new Intent(getActivity(), RecipeActivity.class);
        myIntent.putExtra("CLICKED_RECIPE", MainActivity.viewModel.getRecipeList().getValue().get(clickedRecipeIndex));
        this.startActivity(myIntent);
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
        getActivity().setTitle(R.string.title_activity_main);
    }

    private void initializeData() {

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {

                anyRecipe = AppDatabase.getInstance(getContext()).getRecipeDao().getAnyRecipe();
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                super.onPostExecute(result);

                if (anyRecipe != null) {
                    populateView();
                } else {
                    initializeDataFromAPI();
                }

            }

        }.execute();
    }

    @OnClick(R.id.tvErrorMessage2)
    public void onErrorMessageClick(View view) {
        RecipesFragment recipesFragment = new RecipesFragment();
        getActivity().getSupportFragmentManager().beginTransaction()
                .add(R.id.content_frame, recipesFragment)
                .commit();
    }
}
