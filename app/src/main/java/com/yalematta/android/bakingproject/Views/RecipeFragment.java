package com.yalematta.android.bakingproject.Views;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.yalematta.android.bakingproject.Adapters.RecipeAdapter;
import com.yalematta.android.bakingproject.Models.Ingredient;
import com.yalematta.android.bakingproject.Models.Recipe;
import com.yalematta.android.bakingproject.Models.Step;
import com.yalematta.android.bakingproject.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yalematta on 1/7/18.
 */

public class RecipeFragment extends Fragment {

    private TextView tvErrorMessage1, tvErrorMessage2;
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
        clickedRecipe = (Recipe) bundle.getParcelable("CLICKED_RECIPE");
        Log.e("RECIPE TEST", "" + clickedRecipe.getName());

        Map<Integer, Object> map = createHashMap(clickedRecipe.getIngredients(), clickedRecipe.getSteps());
        populateView(map);

        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    private Map<Integer, Object> createHashMap(List<Ingredient> ingredients, List<Step> steps) {

        Map<Integer, Object> map = new HashMap<>();

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
        adapter = new RecipeAdapter(getContext(), clickedRecipe.getIngredients().size(), map);

        rvRecipe.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        pbIndicator.setVisibility(View.GONE);
        tvErrorMessage1.setVisibility(View.GONE);
        tvErrorMessage2.setVisibility(View.GONE);
        rvRecipe.setVisibility(View.VISIBLE);
    }
}
