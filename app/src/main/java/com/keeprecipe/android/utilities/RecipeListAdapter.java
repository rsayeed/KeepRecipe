package com.keeprecipe.android.utilities;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.keeprecipe.android.model.RecipeStep;
import com.keeprecipe.android.R;

import java.util.List;

/**
 * Created by rubab on 6/27/17.
 */

public class RecipeListAdapter extends RecyclerView.Adapter<RecipeListAdapter.RecipeListViewHolder>  {

    private static final String TAG = RecipeListAdapter.class.getSimpleName();

    private List<RecipeStep> mData;
    private Context mContext;

    private final RecipeListAdapter.RecipeListOnClickHandler mClickHandler;

    /**
     * The interface that receives onClick messages.
     */
    public interface RecipeListOnClickHandler {
        void onClick(RecipeStep recipe, View view, int position);
    }
    /**
     * Constructor
     *
     * @param mContext
     * @param recipeData
     */
    public RecipeListAdapter(Context mContext, List<RecipeStep> recipeData, RecipeListAdapter.RecipeListOnClickHandler clickHandler) {

        this.mContext = mContext;
        this.mData = recipeData;
        mClickHandler = clickHandler;

    }


    @Override
    public RecipeListAdapter.RecipeListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(mContext)
                .inflate(R.layout.recipe_ingredients, parent, false);

        RecipeListAdapter.RecipeListViewHolder vh = new RecipeListAdapter.RecipeListViewHolder(v);

        return vh;
    }

    @Override
    public void onBindViewHolder(RecipeListAdapter.RecipeListViewHolder holder, int position) {

        RecipeStep recipe = mData.get(position);
        holder.recipeShortDescTextView.setText(recipe.getShortDesc());

    }

    @Override
    public int getItemCount() {

        if (mData == null) {
            return 0;
        }
        return mData.size();
    }


    /**
     * A ViewHolder is a required part of the pattern for RecyclerViews. It mostly behaves as
     * a cache of the child views for a forecast item. It's also a convenient place to set an
     * OnClickListener, since it has access to the adapter and the views.
     */
    class RecipeListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final TextView recipeShortDescTextView;

        RecipeListViewHolder(View view) {
            super(view);

            recipeShortDescTextView = (TextView) view.findViewById(R.id.recipeShortDesc);
            view.setOnClickListener(this);

        }

        /**
         * This gets called by the child views during a click. We fetch the date that has been
         * selected, and then call the onClick handler registered with this adapter, passing that
         * date.
         *
         * @param v the View that was clicked
         */
        @Override
        public void onClick(View v) {
            RecipeStep recipe = mData.get(getAdapterPosition());
            mClickHandler.onClick(recipe, v, getAdapterPosition());
        }
    }
}

