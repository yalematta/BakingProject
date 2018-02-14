package com.yalematta.android.bakingproject.fragments;

import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.yalematta.android.bakingproject.activities.MainActivity;
import com.yalematta.android.bakingproject.adapters.RecipeAdapter;
import com.yalematta.android.bakingproject.entities.Ingredient;
import com.yalematta.android.bakingproject.entities.Recipe;
import com.yalematta.android.bakingproject.entities.Step;
import com.yalematta.android.bakingproject.R;
import com.yalematta.android.bakingproject.database.AppDatabase;
import com.yalematta.android.bakingproject.widgets.IngredientListService;
import com.yalematta.android.bakingproject.widgets.RecipeIngredientWidgetProvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

/**
 * Created by yalematta on 1/7/18.
 */

public class RecipeFragment extends Fragment implements RecipeAdapter.ListStepClickListener, View.OnClickListener {

    private static final int KEY_CODE = 111;
    private TextView tvErrorMessage1, tvErrorMessage2;
    private FloatingActionButton fab;
    private Map<Integer, Object> map;
    private ProgressBar pbIndicator;
    private RecyclerView rvRecipe;
    private RecipeAdapter adapter;
    private Recipe clickedRecipe;

    public static String recipeTitle = "Add your ingredients";
    public static List<Ingredient> ingredientsModelList = new ArrayList<>();

    private int appWidgetId;
    boolean hasWidget = false;
    private Intent resultValue;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_recipe, container, false);

        fab = v.findViewById(R.id.fab);
        rvRecipe = v.findViewById(R.id.rvRecipe);
        pbIndicator = v.findViewById(R.id.pbLoadingIndicator);
        tvErrorMessage1 = v.findViewById(R.id.tvErrorMessage1);
        tvErrorMessage2 = v.findViewById(R.id.tvErrorMessage2);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvRecipe.setLayoutManager(mLayoutManager);
        rvRecipe.setHasFixedSize(true);
        rvRecipe.setItemAnimator(new DefaultItemAnimator());

        pbIndicator.setVisibility(View.VISIBLE);

        Bundle bundle = getArguments();
        clickedRecipe = bundle.getParcelable("CLICKED_RECIPE");

        //region FAB methods
//        rvRecipe.addOnScrollListener(new RecyclerView.OnScrollListener(){
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy){
//                if (dy<0 && !fab.isShown())
//                    fab.show();
//                else if(dy>0 && fab.isShown())
//                    fab.hide();
//            }
//
//            @Override
//            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//            }
//        });


        if (clickedRecipe.isFavorite())
            fab.setImageResource(R.drawable.ic_favorite_white_full);
        else
            fab.setImageResource(R.drawable.ic_favorite_white_empty);

        fab.setOnClickListener(this);
        //endregion

        Map<Integer, Object> map = createHashMap(clickedRecipe.getIngredients(), clickedRecipe.getSteps());
        populateView(map);

        recipeTitle = clickedRecipe.getName();
        ingredientsModelList = clickedRecipe.getIngredients();

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(clickedRecipe.getName());

        setHasOptionsMenu(true);

        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // get the appwidget id from the intent
        Intent intent = getActivity().getIntent();
        appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);

        // make the result intent and set the result to canceled
        resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        getActivity().setResult(RESULT_CANCELED, resultValue);

        // if we weren't started properly, finish here
        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finishConfigure();
        }

    }

    private void finishConfigure() {
    /* finish configuring appwidget ... */
        getActivity().setResult(RESULT_OK, resultValue);

        String key = String.format("appwidget%d_configured", appWidgetId);
        SharedPreferences prefs = getActivity().getSharedPreferences("widget_prefs", 0);
        prefs.edit().putBoolean(key, true).commit();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_recipe_share, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_share:
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_SUBJECT, "Sharing recipes");
                String sAux = "\nCheck out this " + clickedRecipe.getName() + " recipe:\n\n";
                sAux = sAux + "https://play.google.com/store/apps/details?id=com.yalematta.android.bakingproject \n\n";
                i.putExtra(Intent.EXTRA_TEXT, sAux);
                startActivity(Intent.createChooser(i, "Share Recipe"));
                return true;

            case R.id.action_add:
                int itemId = item.getItemId();
                boolean recipeAdded;

                checkPhantomAppWidget();

                if (itemId == R.id.action_add) {
                    recipeTitle = clickedRecipe.getName();
                    ingredientsModelList = clickedRecipe.getIngredients();

                    if (hasWidget) {

                        recipeAdded = IngredientListService.startActionChangeIngredientList(this.getContext());

                        if (recipeAdded)
                            Toast.makeText(getContext(), R.string.widget_added_text, Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(getContext(), R.string.widget_not_added_text, Toast.LENGTH_SHORT).show();
                    }

                    else {
                        Intent pickIntent = new Intent(AppWidgetManager.ACTION_APPWIDGET_PICK);
                        pickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
                        startActivityForResult(pickIntent, KEY_CODE);
                    }


                    return true;
                }
        }
        return super.onOptionsItemSelected(item);
    }

    private Map<Integer, Object> createHashMap(List<Ingredient> ingredients, List<Step> steps) {

        map = new HashMap<>();

        map.put(0, getString(R.string.ingredients));

        for (int i = 0; i < ingredients.size(); i++) {
            map.put(i + 1, ingredients.get(i));
        }

        map.put(ingredients.size() + 1, getString(R.string.steps));

        for (int i = 0; i < steps.size(); i++) {
            map.put(ingredients.size() + i + 2, steps.get(i));
        }

        return map;
    }

    private void populateView(Map<Integer, Object> map) {
        adapter = new RecipeAdapter(getContext(), clickedRecipe.getIngredients().size(), map, this);

        rvRecipe.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        pbIndicator.setVisibility(View.GONE);
        tvErrorMessage1.setVisibility(View.GONE);
        tvErrorMessage2.setVisibility(View.GONE);
        rvRecipe.setVisibility(View.VISIBLE);
    }

    @Override
    public void onListStepClick(int clickedStepIndex) {
        Bundle args = new Bundle();
        args.putParcelable("CLICKED_RECIPE", clickedRecipe);
        args.putParcelable("CLICKED_STEP", (Parcelable) map.get(clickedStepIndex));

        StepsFragment stepsFragment = new StepsFragment();
        stepsFragment.setArguments(args);
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, stepsFragment)
                .addToBackStack(stepsFragment.getClass().getSimpleName())
                .commit();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab:

                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... voids) {
                        clickedRecipe.setFavorite(!clickedRecipe.isFavorite());
                        AppDatabase.getInstance(getContext()).getRecipeDao().updateRecipe(clickedRecipe);
                        MainActivity.viewModel.updateRecipe(clickedRecipe);
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void result) {
                        super.onPostExecute(result);

                        if (clickedRecipe.isFavorite()) {
                            fab.setImageResource(R.drawable.ic_favorite_white_full);
                        } else {
                            fab.setImageResource(R.drawable.ic_favorite_white_empty);
                        }
                    }

                }.execute();
                break;
        }
    }

    private void checkPhantomAppWidget(){
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getContext());
        AppWidgetHost appWidgetHost = new AppWidgetHost(getContext(), 1); // for removing phantoms
        SharedPreferences prefs = getActivity().getSharedPreferences("widget_prefs", 0);

        int[] appWidgetIDs = appWidgetManager.getAppWidgetIds(new ComponentName(getContext(), RecipeIngredientWidgetProvider.class));
        for (int i = 0; i < appWidgetIDs.length; i++) {
            int id = appWidgetIDs[i];
            String key = String.format("appwidget%d_configured", id);
            if (prefs.getBoolean(key, false)) {
                hasWidget = true;
            } else {
                // delete the phantom appwidget
                appWidgetHost.deleteAppWidgetId(id);
            }
        }
    }
}
