package com.yalematta.android.bakingproject.Views;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yalematta.android.bakingproject.Adapters.StepsAdapter;
import com.yalematta.android.bakingproject.Models.Recipe;
import com.yalematta.android.bakingproject.Models.Step;
import com.yalematta.android.bakingproject.R;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by yalematta on 1/8/18.
 */

public class StepsFragment extends Fragment implements View.OnClickListener, ViewPager.OnPageChangeListener {

    private Step clickedStep;
    private Recipe clickedRecipe;
    private ViewPager stepsPager;
    private StepsAdapter stepsAdapter;
    private TextView tvNext, tvPrevious;
    private LinearLayout llPrevious, llNext;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_steps, container, false);

        llNext = v.findViewById(R.id.llNext);
        tvNext = v.findViewById(R.id.tvNext);
        stepsPager = v.findViewById(R.id.vpSteps);
        llPrevious = v.findViewById(R.id.llPrevious);
        tvPrevious = v.findViewById(R.id.tvPrevious);

        Bundle bundle = getArguments();
        clickedRecipe = bundle.getParcelable("CLICKED_RECIPE");
        clickedStep = bundle.getParcelable("CLICKED_STEP");

        List<Fragment> fragments = getFragments();
        stepsAdapter = new StepsAdapter(getActivity().getSupportFragmentManager(), fragments);
        stepsPager.setAdapter(stepsAdapter);
        stepsPager.beginFakeDrag();

        stepsPager.setCurrentItem(clickedStep.getStepId());

        if (isFirstPage(stepsPager.getCurrentItem())) llPrevious.setVisibility(View.INVISIBLE);

        stepsPager.addOnPageChangeListener(this);

        llPrevious.setOnClickListener(this);
        llNext.setOnClickListener(this);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(clickedRecipe.getName());


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

                if (isLastPage(currPos)){
                    FragmentManager mFragmentManager = ((FragmentActivity) getContext()).getSupportFragmentManager();
                    mFragmentManager.popBackStackImmediate();
                }
                else{
                    stepsPager.setCurrentItem(nextPos);
                }

                break;

            case R.id.llPrevious:
                int previousPos = currPos - 1;
                stepsPager.setCurrentItem(previousPos);

                break;
        }
    }

    private boolean isLastPage(int position) {
        if (position == clickedRecipe.getSteps().size() - 1)
            return true;
        return false;
    }

    private boolean isFirstPage(int position) {
        if (position == 0)
            return true;
        return false;
    }

    //region addOnPageChangeListener for ViewPager
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if (isLastPage(position)) tvNext.setText(R.string.view_recipe);
        else tvNext.setText(R.string.next);
        if (isFirstPage(position)) llPrevious.setVisibility(View.INVISIBLE);
        else llPrevious.setVisibility(View.VISIBLE);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
    //endregion

}
