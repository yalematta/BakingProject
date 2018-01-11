package com.yalematta.android.bakingproject.Adapters;

import android.content.Context;
import android.os.Parcelable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.yalematta.android.bakingproject.Models.Recipe;
import com.yalematta.android.bakingproject.R;
import com.yalematta.android.bakingproject.Views.RecipesFragment;

import java.util.List;

/**
 * Created by yalematta on 1/6/18.
 */

public class RecipesAdapter extends RecyclerView.Adapter<RecipesAdapter.RecipeViewHolder> {

    private Context mContext;
    private List<Recipe> recipeList;
    final private ListRecipeClickListener mOnClickListener;

    public class RecipeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public CardView cardView;
        public TextView title, count;
        public ImageView thumbnail, overflow;

        public RecipeViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.title);
            count = view.findViewById(R.id.count);
            cardView = view.findViewById(R.id.card_view);
            thumbnail = view.findViewById(R.id.thumbnail);

            cardView.setOnClickListener(this);
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
    }

    @Override
    public int getItemCount() {
        return recipeList.size();
    }

    public interface ListRecipeClickListener {
        void onListRecipeClick(int clickedRecipeIndex);

        /* Save and Restore RecyclerView Scroll Position */
        Parcelable onSaveInstanceState();
        void onRestoreInstanceState(Parcelable state);
    }
}
