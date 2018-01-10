package com.yalematta.android.bakingproject.Views;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
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
        tvPagination = v.findViewById(R.id.tvPagination);

        Bundle bundle = getArguments();
        clickedRecipe = bundle.getParcelable("CLICKED_RECIPE");
        clickedStep = bundle.getParcelable("CLICKED_STEP");

        List<Fragment> fragments = getFragments();
        stepsAdapter = new StepsAdapter(getActivity().getSupportFragmentManager(), fragments);
        stepsPager.setAdapter(stepsAdapter);
        stepsPager.beginFakeDrag();

        stepsPager.setCurrentItem(clickedStep.getStepId());
        tvPagination.setText(clickedStep.getStepId() + 1 + "/" + clickedRecipe.getSteps().size());

        llPrevious.setOnClickListener(this);
        llNext.setOnClickListener(this);

        return v;
    }

    private List<Fragment> getFragments() {
        List<Fragment> fList = new ArrayList<>();

        for (int i = 0; i < clickedRecipe.getSteps().size(); i++) {
            String stepId = String.valueOf(clickedRecipe.getSteps().get(i).getStepId());
            String shortDescription = clickedRecipe.getSteps().get(i).getShortDescription();
            fList.add(StepFragment.newInstance(stepId + ". " + shortDescription));
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
                if (nextPos < clickedRecipe.getSteps().size())
                    tvPagination.setText(nextPos + 1 + "/" + clickedRecipe.getSteps().size());
                break;
            case R.id.llPrevious:
                int previousPos = currPos - 1;
                stepsPager.setCurrentItem(previousPos);
                if (previousPos+2 > 1)
                    tvPagination.setText(previousPos + 1 + "/" + clickedRecipe.getSteps().size());
                break;
        }
    }

}
