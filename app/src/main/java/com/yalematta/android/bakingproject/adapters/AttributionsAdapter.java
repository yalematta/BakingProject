package com.yalematta.android.bakingproject.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.yalematta.android.bakingproject.R;
import com.yalematta.android.bakingproject.entities.Attribution;

import java.util.ArrayList;

/**
 * Created by yalematta on 2/18/18.
 */

public class AttributionsAdapter extends ArrayAdapter<Attribution> {

    public AttributionsAdapter(Context context, ArrayList<Attribution> attributions) {
        super(context, 0, attributions);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Attribution attribution = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_attribution, parent, false);
        }
        // Lookup view for data population
        TextView tvName = convertView.findViewById(R.id.tv_name);
        TextView tvCopyright = convertView.findViewById(R.id.tv_copyright);
        TextView tvLink = convertView.findViewById(R.id.tv_link);
        // Populate the data into the template view using the data object
        tvName.setText(attribution.libraryName);
        tvCopyright.setText(attribution.copyright);
        tvLink.setText(attribution.githubLink);
        // Return the completed view to render on screen
        return convertView;
    }
}
