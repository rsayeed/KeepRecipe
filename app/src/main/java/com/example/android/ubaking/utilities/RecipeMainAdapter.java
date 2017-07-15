package com.example.android.ubaking.utilities;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.ubaking.R;
import com.example.android.ubaking.model.Recipe;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by rubab on 6/25/17.
 */

public class RecipeMainAdapter extends RecyclerView.Adapter<RecipeMainAdapter.RecipeViewHolder> {

    private static final String TAG = RecipeMainAdapter.class.getSimpleName();

    private List<Recipe> mData;
    private Context mContext;

    private final RecipeMainOnClickHandler mClickHandler;

    /**
     * The interface that receives onClick messages.
     */
    public interface RecipeMainOnClickHandler {
        void onClick(Recipe recipe);
    }
    /**
     * Constructor
     *
     * @param mContext
     * @param recipeData
     */
    public RecipeMainAdapter(Context mContext, List<Recipe> recipeData, RecipeMainOnClickHandler clickHandler) {

        this.mContext = mContext;
        this.mData = recipeData;
        mClickHandler = clickHandler;
    }


    @Override
    public RecipeMainAdapter.RecipeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(mContext)
                .inflate(R.layout.recipe_list_main, parent, false);

        RecipeViewHolder vh = new RecipeViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(RecipeMainAdapter.RecipeViewHolder holder, int position) {

        Recipe recipe = mData.get(position);
        Log.v(TAG, "OnBindViewHolder");
        holder.recipeNameTextView.setText(recipe.getRecipeName());

        // Check if we have imageURL, if so, load the image using Picasso
        if (recipe.getImageURL()!= null && recipe.getImageURL().length() > 0) {

            // Download image from url and display it onto the imageview
            Picasso.with(mContext).load(recipe.getImageURL()).into(holder.imageUrlView);
        }
        else {
            switch (recipe.getRecipeName()) {
                case "Nutella Pie":
                    holder.imageUrlView.setImageResource(R.drawable.nutella);
                    break;
                case "Brownies":
                    holder.imageUrlView.setImageResource(R.drawable.brownies);
                    break;
                case "Yellow Cake":
                    holder.imageUrlView.setImageResource(R.drawable.yellowcake);
                    break;
                case "Cheesecake":
                    holder.imageUrlView.setImageResource(R.drawable.cheesecake);
                    break;
            }
        }

    }

    @Override
    public int getItemCount() {

        if (mData == null) {
            Log.v(TAG, "DATA IS NULL");
            return 0;
        }
        return mData.size();
    }

    /**
     * Update the list of Trailer Items when new data has been laoded
     */
    public void swapData(List<Recipe> list) {

        mData = list;
        notifyDataSetChanged();
    }


    /**
     * A ViewHolder is a required part of the pattern for RecyclerViews. It mostly behaves as
     * a cache of the child views for a forecast item. It's also a convenient place to set an
     * OnClickListener, since it has access to the adapter and the views.
     */
    class RecipeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final TextView recipeNameTextView;
        final ImageView imageUrlView;

        RecipeViewHolder(View view) {
            super(view);

            recipeNameTextView = (TextView) view.findViewById(R.id.recipe_name);
            imageUrlView = (ImageView) view.findViewById(R.id.recipeImageView);
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
            Recipe recipe = mData.get(getAdapterPosition());
            mClickHandler.onClick(recipe);
        }
    }
}
