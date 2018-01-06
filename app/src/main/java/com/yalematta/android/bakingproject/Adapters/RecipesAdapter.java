package com.yalematta.android.bakingproject.Adapters;

import android.content.Context;
import android.os.Parcelable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.yalematta.android.bakingproject.Models.Recipe;
import com.yalematta.android.bakingproject.R;

import java.util.List;

/**
 * Created by yalematta on 1/6/18.
 */

public class RecipesAdapter extends RecyclerView.Adapter<RecipesAdapter.RecipeViewHolder> {

    private Context mContext;
    private List<Recipe> recipeList;
    final private ListRecipeClickListener mOnClickListener;

    public class RecipeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView title, count;
        public ImageView thumbnail, overflow;

        public RecipeViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            count = (TextView) view.findViewById(R.id.count);
            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
            overflow = (ImageView) view.findViewById(R.id.overflow);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onListRecipeClick(clickedPosition);
        }
    }

    public RecipesAdapter(Context mContext, List<Recipe> recipeList, ListRecipeClickListener listener) {
        this.mContext = mContext;
        this.recipeList = recipeList;
        this.mOnClickListener = listener;
    }

    @Override
    public RecipeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recipe_card, parent, false);

        return new RecipeViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final RecipeViewHolder holder, int position) {
        Recipe recipe = recipeList.get(position);
        holder.title.setText(recipe.getName());
        holder.count.setText(recipe.getServings() + " servings");

        // loading album cover using Glide library
        Glide.with(mContext).load(recipe.getImage()).placeholder(R.drawable.recipeholder).into(holder.thumbnail);

        holder.overflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return recipeList.size();
    }

    public interface ListRecipeClickListener {
        void onListRecipeClick (int clickedRecipeIndex);

        Parcelable onSaveInstanceState();

        void onRestoreInstanceState(Parcelable state);
    }
}
