package com.yalematta.android.bakingproject.adapters;

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
import com.yalematta.android.bakingproject.entities.Recipe;
import com.yalematta.android.bakingproject.R;

import java.util.List;

/**
 * Created by yalematta on 1/6/18.
 */

public class RecipesAdapter extends RecyclerView.Adapter<RecipesAdapter.RecipeViewHolder> {

    private Context mContext;
    private List<Recipe> recipeList;
    private final ListRecipeClickListener clickListener;

    public RecipesAdapter(Context mContext, List<Recipe> recipeList, ListRecipeClickListener clickListener) {
        this.mContext = mContext;
        this.recipeList = recipeList;
        this.clickListener = clickListener;
    }

    @Override
    public RecipeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new RecipeViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recipe_card, parent, false));
    }

    @Override
    public void onBindViewHolder(final RecipeViewHolder holder, int position) {
        Recipe recipe = recipeList.get(position);
        holder.title.setText(recipe.getName());
        holder.count.setText(recipe.getServings() + " servings");

        if (recipe.isFavorite())
            holder.heart.setImageResource(R.drawable.ic_favorite_full);
        else
            holder.heart.setImageResource(R.drawable.ic_favorite_empty);

        // loading album cover using Glide library
        Glide.with(mContext).load(recipe.getImage()).placeholder(R.drawable.placeholder).into(holder.thumbnail);
    }

    @Override
    public int getItemCount() {
        return recipeList.size();
    }

    public void addItems(List<Recipe> recipeList) {
        this.recipeList = recipeList;
        notifyDataSetChanged();
    }

    class RecipeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private CardView cardView;
        private TextView title, count;
        private ImageView thumbnail, heart;

        RecipeViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.title);
            count = view.findViewById(R.id.count);
            heart = view.findViewById(R.id.heart);
            cardView = view.findViewById(R.id.card_view);
            thumbnail = view.findViewById(R.id.thumbnail);

            cardView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int clickedPosition = getAdapterPosition();
            clickListener.onListRecipeClick(clickedPosition);
        }
    }

    public interface ListRecipeClickListener {
        void onListRecipeClick(int clickedRecipeIndex);

        /* Save and Restore RecyclerView Scroll Position */
        Parcelable onSaveInstanceState();
        void onRestoreInstanceState(Parcelable state);
    }
}
