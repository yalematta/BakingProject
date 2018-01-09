package com.yalematta.android.bakingproject.Views;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yalematta.android.bakingproject.R;

/**
 * Created by yalematta on 1/9/18.
 */

public class StepFragment extends Fragment {

    public static final String EXTRA_MESSAGE = "EXTRA_MESSAGE";

    public static final StepFragment newInstance(String message) {
        StepFragment f = new StepFragment();
        Bundle bdl = new Bundle(1);
        bdl.putString(EXTRA_MESSAGE, message);
        f.setArguments(bdl);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        String message = getArguments().getString(EXTRA_MESSAGE);
        View v = inflater.inflate(R.layout.fragment_step, container, false);
        TextView messageTextView = v.findViewById(R.id.textView);
        messageTextView.setText(message);
        return v;
    }
}
