package com.yalematta.android.bakingproject.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.yalematta.android.bakingproject.R;
import com.yalematta.android.bakingproject.entities.Attribution;

import java.util.ArrayList;

/**
 * Created by yalematta on 2/18/18.
 */

public class AttributionsAdapter extends ArrayAdapter<Attribution> implements View.OnClickListener {

    private ArrayList<Attribution> dataSet;
    Context mContext;

    // View lookup cache
    private static class ViewHolder {
        TextView tvName;
        TextView tvCopyright;
        TextView tvGithubLink;
    }

    public AttributionsAdapter(ArrayList<Attribution> data, Context context) {
        super(context, R.layout.item_attribution, data);
        this.dataSet = data;
        this.mContext = context;

    }

    @Override
    public void onClick(View v) {

        int position= (Integer) v.getTag();
        Object object = getItem(position);
        Attribution attribution = (Attribution) object;

        switch (v.getId())
        {
            case R.id.tv_link:
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(attribution.getGithubLink()));
                mContext.startActivity(browserIntent);
                break;
        }
    }

    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Attribution dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_attribution, parent, false);
            viewHolder.tvName = (TextView) convertView.findViewById(R.id.tv_name);
            viewHolder.tvCopyright = (TextView) convertView.findViewById(R.id.tv_copyright);
            viewHolder.tvGithubLink = (TextView) convertView.findViewById(R.id.tv_link);

            result = convertView;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;
        }

        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;

        viewHolder.tvName.setText(dataModel.getLibraryName());
        viewHolder.tvCopyright.setText(dataModel.getCopyright());
        viewHolder.tvGithubLink.setText(dataModel.getGithubLink());

        SpannableString content = new SpannableString(dataModel.getGithubLink());
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        viewHolder.tvGithubLink.setText(content);
        viewHolder.tvGithubLink.setTag(position);
        viewHolder.tvGithubLink.setOnClickListener(this);

        // Return the completed view to render on screen
        return convertView;
    }
}