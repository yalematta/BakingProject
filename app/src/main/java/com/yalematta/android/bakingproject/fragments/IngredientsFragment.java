package com.yalematta.android.bakingproject.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.support.v4.app.Fragment;

import com.yalematta.android.bakingproject.R;
import com.yalematta.android.bakingproject.adapters.IngredientsAdapter;
import com.yalematta.android.bakingproject.entities.Ingredient;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by yalematta on 3/4/18.
 */

public class IngredientsFragment extends Fragment {

    private IngredientsAdapter adapter;
    private ArrayList<Ingredient> ingredients;
    @BindView(R.id.lvIngredients) ListView lvIngredients;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_ingredients, container, false);
        ButterKnife.bind(this, v);

        Bundle bundle = getArguments();
        ingredients = bundle.getParcelableArrayList("INGREDIENTS");

        populateIngredients();

        return v;
    }

    private void populateIngredients() {
        adapter = new IngredientsAdapter(getContext(), ingredients);
        if (ingredients != null && ingredients.size() > 0) {
            lvIngredients.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
    }
}
