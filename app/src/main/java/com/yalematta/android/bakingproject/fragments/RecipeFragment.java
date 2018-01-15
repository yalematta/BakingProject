package com.yalematta.android.bakingproject.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
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

import com.yalematta.android.bakingproject.adapters.RecipeAdapter;
import com.yalematta.android.bakingproject.entities.Ingredient;
import com.yalematta.android.bakingproject.entities.Recipe;
import com.yalematta.android.bakingproject.entities.Step;
import com.yalematta.android.bakingproject.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yalematta on 1/7/18.
 */

public class RecipeFragment extends Fragment implements RecipeAdapter.ListStepClickListener {

    private TextView tvErrorMessage1, tvErrorMessage2;
    private Map<Integer, Object> map;
    private ProgressBar pbIndicator;
    private RecyclerView rvRecipe;
    private RecipeAdapter adapter;
    private Recipe clickedRecipe;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_recipe, container,false);

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

        Map<Integer, Object> map = createHashMap(clickedRecipe.getIngredients(), clickedRecipe.getSteps());
        populateView(map);

        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(clickedRecipe.getName());

        setHasOptionsMenu(true);

        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_recipe_share, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_share:
                try {
                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("text/plain");
                    i.putExtra(Intent.EXTRA_SUBJECT, "Sharing recipes");
                    String sAux = "\nCheck out this " + clickedRecipe.getName() + " recipe:\n\n";
                    sAux = sAux + "https://play.google.com/store/apps/details?id=com.yalematta.android.bakingproject \n\n";
                    i.putExtra(Intent.EXTRA_TEXT, sAux);
                    startActivity(Intent.createChooser(i, "Share Recipe"));
                } catch(Exception e) {

                }
                return true;
        }
        return false;
    }

    private Map<Integer, Object> createHashMap(List<Ingredient> ingredients, List<Step> steps) {

        map = new HashMap<>();

        map.put(0, getString(R.string.ingredients));

        for (int i = 0; i < ingredients.size(); i++){
            map.put(i+1, ingredients.get(i));
        }

        map.put(ingredients.size()+1, getString(R.string.steps));

        for (int i = 0; i < steps.size(); i++){
            map.put(ingredients.size()+i+2, steps.get(i));
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
}
