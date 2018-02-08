package com.yalematta.android.bakingproject.widgets;

import android.content.Context;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.yalematta.android.bakingproject.R;
import com.yalematta.android.bakingproject.entities.Ingredient;
import com.yalematta.android.bakingproject.fragments.RecipeFragment;

import java.util.List;


public class ListRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private Context mContext;
    private List<Ingredient> ingredientsModelList;

    public ListRemoteViewsFactory(Context context) {
        mContext = context;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {
        ingredientsModelList = RecipeFragment.ingredientsModelList;
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        if (ingredientsModelList == null) return 0;
        return ingredientsModelList.size();
    }

    @Override
    public RemoteViews getViewAt(int i) {
        RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(), R.layout.widget_list_view_item);
        remoteViews.setTextViewText(R.id.widget_list_view_text_ingredient, ingredientsModelList.get(i).getIngredientName());
        remoteViews.setTextViewText(R.id.widget_list_view_text_measure, ingredientsModelList.get(i).getMeasure());
        remoteViews.setTextViewText(R.id.widget_list_view_text_quantity, ingredientsModelList.get(i).getQuantity() + "");
        return remoteViews;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 0;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }
}
