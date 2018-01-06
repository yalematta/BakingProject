package com.yalematta.android.bakingproject.Views;

import android.app.Activity;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.View;
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
import com.yalematta.android.bakingproject.Adapters.RecipesAdapter;
import com.yalematta.android.bakingproject.Models.Recipe;
import com.yalematta.android.bakingproject.R;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements RecipesAdapter.ListRecipeClickListener {

    private static final String ENDPOINT = "https://d17h27t6h515a5.cloudfront.net/topher/2017/May/59121517_baking/baking.json";
    private static final String SAVED_LAYOUT_MANAGER = "SAVED_LAYOUT_MANAGER";
    private Parcelable layoutManagerSavedState;
    private RequestQueue requestQueue;
    private Gson gson;

    private TextView tvErrorMessage1, tvErrorMessage2;
    private ProgressBar pbIndicator;
    private RecyclerView rvRecipes;
    private RecipesAdapter adapter;
    private List<Recipe> recipeList;
    private  Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeView();
        initializeData();

        if (layoutManagerSavedState != null) {
            rvRecipes.getLayoutManager().onRestoreInstanceState(layoutManagerSavedState);
        }
    }

    private void initializeData() {
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        GsonBuilder gsonBuilder = new GsonBuilder();
        gson = gsonBuilder.create();
        fetchRecipes();
    }

    private void initializeView() {
        toolbar = findViewById(R.id.toolbar);
        rvRecipes = findViewById(R.id.rvRecipes);
        pbIndicator = findViewById(R.id.pbLoadingIndicator);
        tvErrorMessage1 = findViewById(R.id.tvErrorMessage1);
        tvErrorMessage2 = findViewById(R.id.tvErrorMessage2);

        setSupportActionBar(toolbar);
        pbIndicator.setVisibility(View.VISIBLE);
        tvErrorMessage2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fetchRecipes();
            }
        });
    }

    private void fetchRecipes() {
        StringRequest request = new StringRequest(Request.Method.GET, ENDPOINT, onRecipesLoaded, onRecipesError);
        requestQueue.add(request);
    }


    /* Volley Response */
    private final Response.Listener<String> onRecipesLoaded = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            recipeList = Arrays.asList(gson.fromJson(response, Recipe[].class));

            if(recipeList.size() > 0){
                populateView();
            }
            else{
                rvRecipes.setVisibility(View.GONE);
                pbIndicator.setVisibility(View.GONE);
                tvErrorMessage1.setVisibility(View.VISIBLE);
                tvErrorMessage2.setVisibility(View.VISIBLE);
            }

//          Log.i("MainActivity", recipeList.size() + " recipes loaded.");
//          for (Recipe recipe : recipeList) {
//              Log.i("MainActivity", recipe.recipeId + ": " + recipe.name);
//          }
        }
    };

    private final Response.ErrorListener onRecipesError = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            rvRecipes.setVisibility(View.GONE);
            pbIndicator.setVisibility(View.GONE);
            tvErrorMessage1.setVisibility(View.VISIBLE);
            tvErrorMessage2.setVisibility(View.VISIBLE);

//          Log.e("MainActivity", error.toString());
        }
    };

    private void populateView() {

        adapter = new RecipesAdapter(this, recipeList, this);

        RecyclerView.LayoutManager mLayoutManager;

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            mLayoutManager = new GridLayoutManager(this, 1);
            rvRecipes.setLayoutManager(mLayoutManager);
            rvRecipes.setHasFixedSize(true);
            rvRecipes.addItemDecoration(new GridSpacingItemDecoration(1, dpToPx(10), true));
        }
        else {
            mLayoutManager = new GridLayoutManager(this, 3);
            rvRecipes.setLayoutManager(mLayoutManager);
            rvRecipes.setHasFixedSize(true);
            rvRecipes.addItemDecoration(new GridSpacingItemDecoration(3, dpToPx(10), true));
        }
        rvRecipes.setItemAnimator(new DefaultItemAnimator());
        rvRecipes.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        pbIndicator.setVisibility(View.GONE);
        tvErrorMessage1.setVisibility(View.GONE);
        tvErrorMessage2.setVisibility(View.GONE);
        rvRecipes.setVisibility(View.VISIBLE);

    }

    @Override
    public void onListRecipeClick(int clickedRecipeIndex) {
        //TODO: Implement the rest of the Application here
    }

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

    /* Save and Restore RecyclerView Scroll Position */
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
        super.onRestoreInstanceState((Bundle) state);
    }

}
