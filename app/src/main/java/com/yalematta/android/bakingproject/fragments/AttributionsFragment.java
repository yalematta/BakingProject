package com.yalematta.android.bakingproject.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.yalematta.android.bakingproject.R;
import com.yalematta.android.bakingproject.adapters.AttributionsAdapter;
import com.yalematta.android.bakingproject.entities.Attribution;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by yalematta on 2/18/18.
 */

public class AttributionsFragment extends Fragment {

    @BindView(R.id.lv_attributions) ListView listView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_attributions, container, false);
        ButterKnife.bind(this, v);

        populateAttributions();

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.attributions));

        return v;
    }

    private void populateAttributions() {
        // Add item to adapter
        ArrayList<Attribution> attributionsList = new ArrayList<>();
        attributionsList.add(new Attribution("Glide", "Copyright Sam Judd", "https://github.com/bumptech/glide"));
        attributionsList.add(new Attribution("Volley", "Copyright Google, Inc.", "https://github.com/google/volley"));
        attributionsList.add(new Attribution("ExoPlayer", "Copyright Google, Inc.", "https://github.com/google/ExoPlayer"));
        attributionsList.add(new Attribution("Room", "Copyright The Android Open Source Project, Inc.", "https://github.com/googlesamples/android-architecture-components"));
        attributionsList.add(new Attribution("ButterKnife", "Copyright 2013 Jake Wharton", "https://jakewharton.github.io/butterknife/"));

        AttributionsAdapter adapter = new AttributionsAdapter(attributionsList, getContext());
        listView.setAdapter(adapter);
    }
}
