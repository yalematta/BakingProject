package com.yalematta.android.bakingproject.fragments;

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

import com.yalematta.android.bakingproject.activities.MainActivity;
import com.yalematta.android.bakingproject.adapters.StepsAdapter;
import com.yalematta.android.bakingproject.entities.Recipe;
import com.yalematta.android.bakingproject.entities.Step;
import com.yalematta.android.bakingproject.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * Created by yalematta on 1/8/18.
 */

public class StepsFragment extends Fragment implements ViewPager.OnPageChangeListener {

    private Step clickedStep;
    private Recipe clickedRecipe;
    private StepsAdapter stepsAdapter;

    @BindView(R.id.tvNext) TextView tvNext;
    @BindView(R.id.llNext) LinearLayout llNext;
    @BindView(R.id.vpSteps) ViewPager stepsPager;
    @BindView(R.id.tvPrevious) TextView tvPrevious;
    @BindView(R.id.llPrevious) LinearLayout llPrevious;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_steps, container, false);
        ButterKnife.bind(this, v);

        Bundle bundle = getArguments();
        clickedRecipe = bundle.getParcelable("CLICKED_RECIPE");
        clickedStep = bundle.getParcelable("CLICKED_STEP");

        List<Fragment> fragments = getFragments();
        stepsAdapter = new StepsAdapter(getActivity().getSupportFragmentManager(), fragments);
        stepsPager.setAdapter(stepsAdapter);
        stepsPager.beginFakeDrag();

        if (isFirstPage(stepsPager.getCurrentItem())) llPrevious.setVisibility(View.INVISIBLE);

        stepsPager.addOnPageChangeListener(this);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(clickedRecipe.getName());

        if (MainActivity.mTwoPane){
            stepsPager.setCurrentItem(0);
        }
        else {
            stepsPager.setCurrentItem(clickedStep.getStepId());
        }

        setRetainInstance(true);

        return v;
    }

    private List<Fragment> getFragments() {
        List<Fragment> fList = new ArrayList<>();

        for (int i = 0; i < clickedRecipe.getSteps().size(); i++) {
            fList.add(StepFragment.newInstance(clickedRecipe.getSteps().get(i)));
        }

        return fList;
    }

    @OnClick(R.id.llNext)
    public void onNextClick(View view) {
        int currPos = stepsPager.getCurrentItem();
        int nextPos = currPos + 1;
        if (isLastPage(currPos)){
            FragmentManager mFragmentManager = ((FragmentActivity) getContext()).getSupportFragmentManager();
            mFragmentManager.popBackStackImmediate();
        }
        else{
            stepsPager.setCurrentItem(nextPos);
        }
    }

    @OnClick(R.id.llPrevious)
    public void onPreviousClick(View view) {
        int currPos = stepsPager.getCurrentItem();
        int previousPos = currPos - 1;
        stepsPager.setCurrentItem(previousPos);
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
