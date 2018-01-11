package com.yalematta.android.bakingproject.Adapters;

import android.content.Context;
import android.os.Parcelable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yalematta.android.bakingproject.Models.Ingredient;
import com.yalematta.android.bakingproject.Models.Step;
import com.yalematta.android.bakingproject.R;

import java.util.List;
import java.util.Map;

/**
 * Created by yalematta on 1/7/18.
 */

public class RecipeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private int ingredientsCount;
    private Map<Integer, Object> map;
    private static RecipeAdapter.ListStepClickListener mOnClickListener;

    private final static int HEADER_VIEW = 1;
    private final static int INGREDIENT_VIEW = 2;
    private final static int STEP_VIEW = 3;

    public RecipeAdapter(Context mContext, int ingredientsCount, Map<Integer, Object> map, ListStepClickListener listener) {
        this.map = map;
        this.mContext = mContext;
        this.mOnClickListener = listener;
        this.ingredientsCount = ingredientsCount;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case HEADER_VIEW:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_header, parent, false);
                return new HeaderViewHolder(view);
            case INGREDIENT_VIEW:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ingredient, parent, false);
                return new IngredientViewHolder(view);
            case STEP_VIEW:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_step, parent, false);
                return new StepViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeaderViewHolder) {
            Object mObject = map.get(position);
            if (mObject.equals("Ingredients"))
                ((HeaderViewHolder) holder).mImage.setImageResource(R.drawable.ingredients);
            else
                ((HeaderViewHolder) holder).mImage.setImageResource(R.drawable.steps);
            ((HeaderViewHolder) holder).mTitle.setText((String) mObject);
        } else if (holder instanceof IngredientViewHolder) {
            Object mObject = map.get(position);
            Ingredient ingredient = (Ingredient) mObject;
            ((IngredientViewHolder) holder).mTitle.setText("\u25CF " + ingredient.getIngredientName());
            if ((ingredient.getQuantity() % 1) == 0) {
                int quantity = (int) Math.round(ingredient.getQuantity());
                ((IngredientViewHolder) holder).mDescription.setText(quantity + " " + ingredient.getMeasure());
            }
            else {
                ((IngredientViewHolder) holder).mDescription.setText(ingredient.getQuantity() + " " + ingredient.getMeasure());
            }
        } else if (holder instanceof StepViewHolder) {
            Object mObject = map.get(position);
            Step step = (Step) mObject;
            ((StepViewHolder) holder).mId.setText(String.valueOf(step.getStepId()));
            ((StepViewHolder) holder).mTitle.setText(step.getShortDescription());
        }
    }

    @Override
    public int getItemCount() {
        if (map == null)
            return 0;
        return map.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (map != null) {
            Object object = map.get(position);
            if (object != null) {
                if (object instanceof Ingredient) {
                    return INGREDIENT_VIEW;
                } else if (object instanceof Step) {
                    return STEP_VIEW;
                }
                else if (object instanceof String){
                    return HEADER_VIEW;
                }
            }
        }
        return 0;
    }

    //region ViewHolders methods

    public static class HeaderViewHolder extends RecyclerView.ViewHolder {
        private TextView mTitle;
        private ImageView mImage;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            mImage = itemView.findViewById(R.id.ivHeader);
            mTitle = itemView.findViewById(R.id.tvHeader);
        }
    }

    public static class IngredientViewHolder extends RecyclerView.ViewHolder {
        private TextView mTitle;
        private TextView mDescription;

        public IngredientViewHolder(View itemView) {
            super(itemView);
            mTitle = (TextView) itemView.findViewById(R.id.tvIngredientName);
            mDescription = (TextView) itemView.findViewById(R.id.tvIngredientQuantity);
        }
    }

    public static class StepViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mTitle;
        private TextView mId;

        public StepViewHolder(View itemView) {
            super(itemView);

            mId = itemView.findViewById(R.id.tvStepId);
            mTitle = itemView.findViewById(R.id.tvStepShortDescription);

            mTitle.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onListStepClick(clickedPosition);
        }
    }

    //endregion methods methods

    public interface ListStepClickListener {
        void onListStepClick(int clickedRecipeIndex);
    }
}