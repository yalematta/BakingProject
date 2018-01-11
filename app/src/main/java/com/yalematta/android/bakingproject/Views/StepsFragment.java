package com.yalematta.android.bakingproject.Views;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yalematta.android.bakingproject.Adapters.StepsAdapter;
import com.yalematta.android.bakingproject.Models.Recipe;
import com.yalematta.android.bakingproject.Models.Step;
import com.yalematta.android.bakingproject.R;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by yalematta on 1/8/18.
 */

public class StepsFragment extends Fragment implements View.OnClickListener {

    private Step clickedStep;
    private Recipe clickedRecipe;
    private ViewPager stepsPager;
    private TextView tvPagination;
    private StepsAdapter stepsAdapter;
    private LinearLayout llPrevious, llNext;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_steps, container, false);

        llNext = v.findViewById(R.id.llNext);
        stepsPager = v.findViewById(R.id.vpSteps);
        llPrevious = v.findViewById(R.id.llPrevious);

        Bundle bundle = getArguments();
        clickedRecipe = bundle.getParcelable("CLICKED_RECIPE");
        clickedStep = bundle.getParcelable("CLICKED_STEP");

        List<Fragment> fragments = getFragments();
        stepsAdapter = new StepsAdapter(getActivity().getSupportFragmentManager(), fragments);
        stepsPager.setAdapter(stepsAdapter);
        stepsPager.beginFakeDrag();

        stepsPager.setCurrentItem(clickedStep.getStepId());

        llPrevious.setOnClickListener(this);
        llNext.setOnClickListener(this);

        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(clickedRecipe.getName());

        return v;
    }

    private List<Fragment> getFragments() {
        List<Fragment> fList = new ArrayList<>();

        for (int i = 0; i < clickedRecipe.getSteps().size(); i++) {
            fList.add(StepFragment.newInstance(clickedRecipe.getSteps().get(i)));
        }

        return fList;
    }

    @Override
    public void onClick(View view) {
        int currPos = stepsPager.getCurrentItem();

        switch (view.getId()) {

            case R.id.llNext:
                int nextPos = currPos + 1;
                stepsPager.setCurrentItem(nextPos);
                break;
            case R.id.llPrevious:
                int previousPos = currPos - 1;
                stepsPager.setCurrentItem(previousPos);
                break;
        }
    }
}
