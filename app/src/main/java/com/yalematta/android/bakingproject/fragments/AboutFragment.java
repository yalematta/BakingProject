package com.yalematta.android.bakingproject.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yalematta.android.bakingproject.BuildConfig;
import com.yalematta.android.bakingproject.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by yalematta on 2/17/18.
 */

public class AboutFragment extends Fragment {

    @BindView(R.id.tv_app_version) TextView tvVersion;
    @BindView(R.id.tv_attributions) TextView tvAttributions;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_about, container, false);
        ButterKnife.bind(this, v);

        tvVersion.setText(String.format(getString(R.string.app_version), BuildConfig.VERSION_NAME));

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.about));

        setRetainInstance(true);

        return v;
    }

    @OnClick(R.id.tv_attributions)
    public void onAttributionsClick(View view) {
        AttributionsFragment attributionsFragment = new AttributionsFragment();
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, attributionsFragment)
                .commit();
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(R.string.about);
    }
}
