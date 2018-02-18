package com.yalematta.android.bakingproject.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yalematta.android.bakingproject.BuildConfig;
import com.yalematta.android.bakingproject.R;

/**
 * Created by yalematta on 2/17/18.
 */

public class AboutFragment extends Fragment implements View.OnClickListener {

    private TextView tvVersion, tvAttributions;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_about, container, false);

        tvVersion = v.findViewById(R.id.tv_app_version);
        tvAttributions = v.findViewById(R.id.tv_attributions);

        tvVersion.setText(String.format(getString(R.string.app_version), BuildConfig.VERSION_NAME));
        tvAttributions.setOnClickListener(this);

        return v;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_attributions:
                AttributionsFragment attributionsFragment = new AttributionsFragment();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.content_frame, attributionsFragment)
                        .commit();
                break;
        }
    }
}
