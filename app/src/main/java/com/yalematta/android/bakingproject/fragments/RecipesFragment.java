package com.yalematta.android.bakingproject.fragments;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
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
import com.yalematta.android.bakingproject.adapters.RecipesAdapter;
import com.yalematta.android.bakingproject.entities.Recipe;
import com.yalematta.android.bakingproject.R;

import java.util.Arrays;
import java.util.List;

/**
 * Created by yalematta on 1/7/18.
 */

public class RecipesFragment extends Fragment implements RecipesAdapter.ListRecipeClickListener, View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    private static final String ENDPOINT = "https://d17h27t6h515a5.cloudfront.net/topher/2017/May/59121517_baking/baking.json";
    private static final String SAVED_LAYOUT_MANAGER = "SAVED_LAYOUT_MANAGER";
    private Parcelable layoutManagerSavedState;
    private RequestQueue requestQueue;
    private Gson gson;

    private TextView tvErrorMessage1, tvErrorMessage2;
    private SwipeRefreshLayout refreshLayout;
    private List<Recipe> recipeList;
    private ProgressBar pbIndicator;
    private RecyclerView rvRecipes;
    private RecipesAdapter adapter;
    private ImageView failedImage;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_recipes, container, false);

        rvRecipes = v.findViewById(R.id.rvRecipes);
        failedImage = v.findViewById(R.id.ivErrorImage);
        refreshLayout = v.findViewById(R.id.swipe_layout);
        pbIndicator = v.findViewById(R.id.pbLoadingIndicator);
        tvErrorMessage1 = v.findViewById(R.id.tvErrorMessage1);
        tvErrorMessage2 = v.findViewById(R.id.tvErrorMessage2);

        refreshLayout.setOnRefreshListener(this);

        refreshLayout.setColorSchemeResources(R.color.colorPrimary,
                R.color.colorPrimaryLight,
                R.color.colorPrimaryDark);

        rvRecipes.addItemDecoration(new GridSpacingItemDecoration(1, dpToPx(10), true));

        pbIndicator.setVisibility(View.VISIBLE);
        tvErrorMessage2.setOnClickListener(this);

        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    private void initializeData() {
        requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        GsonBuilder gsonBuilder = new GsonBuilder();
        gson = gsonBuilder.create();
        fetchRecipes();
    }

    private void fetchRecipes() {
        StringRequest request = new StringRequest(Request.Method.GET, ENDPOINT, onRecipesLoaded, onRecipesError);
        requestQueue.add(request);
    }

    //region Volley Response
    private final Response.Listener<String> onRecipesLoaded = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            recipeList = Arrays.asList(gson.fromJson(response, Recipe[].class));

            if (recipeList.size() > 0) {
                populateView();
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

    private void populateView() {

        adapter = new RecipesAdapter(getContext(), recipeList, this);

        final RecyclerView.LayoutManager mLayoutManager;

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            mLayoutManager = new GridLayoutManager(getContext(), 1);
            rvRecipes.setLayoutManager(mLayoutManager);
            rvRecipes.setHasFixedSize(true);
        } else {
            mLayoutManager = new GridLayoutManager(getContext(), 3);
            rvRecipes.setLayoutManager(mLayoutManager);
            rvRecipes.setHasFixedSize(true);
        }
        rvRecipes.setItemAnimator(new DefaultItemAnimator());
        rvRecipes.setAdapter(adapter);

        adapter.notifyDataSetChanged();

        pbIndicator.setVisibility(View.GONE);
        failedImage.setVisibility(View.GONE);
        rvRecipes.setVisibility(View.VISIBLE);
        tvErrorMessage1.setVisibility(View.GONE);
        tvErrorMessage2.setVisibility(View.GONE);
    }

    @Override
    public void onListRecipeClick(int clickedRecipeIndex) {
        Bundle args = new Bundle();
        args.putParcelable("CLICKED_RECIPE", (Parcelable) recipeList.get(clickedRecipeIndex));

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

    /* RecyclerView item decoration - give equal margin around grid item */
    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }

    }

    /* Converting dp to pixel */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    //endregion

    //region Save and Restore RecyclerView Scroll Position */
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
}
