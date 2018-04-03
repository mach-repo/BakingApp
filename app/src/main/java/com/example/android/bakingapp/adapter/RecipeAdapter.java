package com.example.android.bakingapp.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.bakingapp.R;
import com.example.android.bakingapp.data.Recipe;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by merouane on 27/01/2018.
 */

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeAdapterViewHolder>{

    private static final int VIEW_TYPE_SIMPLE = 0;
    private static final int VIEW_TYPE_WITH_IMAGE = 1;


    final private RecipeAdapterOnClickHandler mClickHandler;
    /**
     * The interface that receives onClick messages.
     */
    public interface RecipeAdapterOnClickHandler {
        void onClick(Recipe recipe);
    }

    private final Context mContext;
    private List<Recipe> mRecipes;

    private final String TAG ="Adapter";

    /* done */
    public RecipeAdapter(@NonNull Context context, List<Recipe> myDataSet, RecipeAdapterOnClickHandler clickHandler) {
        mContext = context;
        mRecipes = myDataSet;
        mClickHandler = clickHandler;
    }

    /* done */
    @Override
    public RecipeAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        int layoutId;

        switch (viewType) {

            case VIEW_TYPE_SIMPLE: {
                layoutId = R.layout.recipe_list_item;
                break;
            }

            case VIEW_TYPE_WITH_IMAGE: {
                layoutId = R.layout.recipe_list_item2;
                break;
            }

            default:
                throw new IllegalArgumentException("Invalid view type, value of " + viewType);
        }

        View view = LayoutInflater.from(mContext).inflate(layoutId, parent, false);

        return new RecipeAdapterViewHolder(view);
    }

    /* done */
    @Override
    public void onBindViewHolder(RecipeAdapter.RecipeAdapterViewHolder holder, int position) {

        int viewType = getItemViewType(position);

        switch (viewType) {

            case VIEW_TYPE_SIMPLE:
                holder.recipeNameTextView.setText(mRecipes.get(position).getName());
                holder.recipeServingsTextView.setText(mContext.getResources().getString(R.string.servings)
                        + " " + mRecipes.get(position).getServings());
                break;

            case VIEW_TYPE_WITH_IMAGE:
                holder.recipeNameTextView.setText(mRecipes.get(position).getName());
                Picasso.with(mContext).load(mRecipes.get(position).getImage()).into(holder.recipeImageView);
                break;

            default:
                throw new IllegalArgumentException("Invalid view type, value of " + viewType);
        }


    }

    /* done */
    @Override
    public int getItemCount() {
        if (null == mRecipes){
            return 0;
        } else {
            return mRecipes.size();
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (TextUtils.isEmpty(mRecipes.get(position).getImage())) {
            return VIEW_TYPE_SIMPLE;
        } else {
            return VIEW_TYPE_WITH_IMAGE;
        }
    }

    /* done */
    // stores and recycles views as they are scrolled off screen
    public class RecipeAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @Nullable
        @BindView(R.id.recipe_imageView)
        ImageView recipeImageView;

        @BindView(R.id.recipe_name_textview)
        TextView recipeNameTextView;

        @BindView(R.id.recipe_serving_textview)
        TextView recipeServingsTextView;

        RecipeAdapterViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            Recipe recipe = mRecipes.get(adapterPosition);
            mClickHandler.onClick(recipe);
        }
    }

    public void swapDataset(List<Recipe> newData){
        mRecipes = newData;
        notifyDataSetChanged();
    }
}
