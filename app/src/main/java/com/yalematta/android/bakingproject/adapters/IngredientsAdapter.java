package com.yalematta.android.bakingproject.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import com.yalematta.android.bakingproject.entities.Ingredient;

import java.util.ArrayList;

/**
 * Created by yalematta on 2/18/18.
 */

public class IngredientsAdapter extends ArrayAdapter<Ingredient> {

    private ArrayList<Ingredient> dataSet;
    Context mContext;

    // View lookup cache
    private static class ViewHolder {
        TextView mTitle;
        TextView mDescription;
    }

    public IngredientsAdapter(Context context, ArrayList<Ingredient> data) {
        super(context, R.layout.item_ingredient, data);
        this.dataSet = data;
        this.mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Ingredient dataModel = getItem(position);
        ViewHolder viewHolder;

        viewHolder = new ViewHolder();
        LayoutInflater inflater = LayoutInflater.from(getContext());
        convertView = inflater.inflate(R.layout.item_ingredient, parent, false);
        viewHolder.mTitle = (TextView) convertView.findViewById(R.id.tvIngredientName);
        viewHolder.mDescription = (TextView) convertView.findViewById(R.id.tvIngredientQuantity);
        convertView.setTag(viewHolder);

        viewHolder.mTitle.setText("\u25CF " + dataModel.getIngredientName());
        viewHolder.mDescription.setText(dataModel.getQuantity() + " " + dataModel.getMeasure());

        // Return the completed view to render on screen
        return convertView;
    }
}