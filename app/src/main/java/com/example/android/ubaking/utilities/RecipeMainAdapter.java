package com.example.android.ubaking.utilities;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.ubaking.R;
import com.example.android.ubaking.model.Recipe;
import com.squareup.picasso.Picasso;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
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

    private final RecipeMenuItemOnClickHandler mRecipeItemClickHandler;

    /**
     * The interface that receives onClick messages
     */
    public interface RecipeMainOnClickHandler {
        void onClick(Recipe recipe);
    }

    /**
     * The interface that receives onClick messages for the three dot menu option in each recipe
     */
    public interface RecipeMenuItemOnClickHandler {
        void onClickRecipeMenuItem(Recipe recipe, View view);
    }

    /**
     * Constructor
     *
     * @param mContext
     * @param recipeData
     */
    public RecipeMainAdapter(Context mContext, List<Recipe> recipeData,
                             RecipeMainOnClickHandler clickHandler,
                             RecipeMenuItemOnClickHandler itemClickHandler) {

        this.mContext = mContext;
        this.mData = recipeData;
        mClickHandler = clickHandler;
        mRecipeItemClickHandler = itemClickHandler;
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

        final Recipe recipe = mData.get(position);
        holder.recipeNameTextView.setText(recipe.getRecipeName());

        // Check if we have imageURL, if so, load the image using Picasso
        if (recipe.getImageURL() != null && recipe.getImageURL().length() > 0) {

            Uri uri = Uri.parse(recipe.getImageURL());

            Picasso.with(mContext).load(uri).into(holder.imageUrlView);

        } else {
            switch (recipe.getRecipeName()) {
                case "Nutella Pie":
                    try {
                        holder.imageUrlView.setImageBitmap(getBitmapFromAsset("nutella.jpg"));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case "Brownies":
                    try {
                        holder.imageUrlView.setImageBitmap(getBitmapFromAsset("brownies.jpg"));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case "Yellow Cake":
                    try {
                        holder.imageUrlView.setImageBitmap(getBitmapFromAsset("yellowcake.jpg"));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case "Cheesecake":
                    try {
                        holder.imageUrlView.setImageBitmap(getBitmapFromAsset("cheesecake.jpg"));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }

        // Apply onClick listener to 3 dots
        holder.recipeOptionMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRecipeItemClickHandler.onClickRecipeMenuItem(recipe, v);
            }
        });

    }


    public AssetManager getAssets() {
        // Ensure we're returning assets with the correct configuration.
        return mContext.getAssets();
    }

    /**
     * This method is used to retrieve asset images
     *
     * @param strName
     * @return
     * @throws IOException
     */
    private Bitmap getBitmapFromAsset(String strName) throws IOException {
        AssetManager assetManager = getAssets();
        InputStream istr = assetManager.open(strName);
        Bitmap bitmap = BitmapFactory.decodeStream(istr);
        return bitmap;
    }

    @Override
    public int getItemCount() {

        if (mData == null) {
            Log.v(TAG, "DATA IS NULL");
            return 0;
        }
        return mData.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
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
        final ImageView recipeOptionMenu;

        RecipeViewHolder(View view) {
            super(view);

            recipeNameTextView = (TextView) view.findViewById(R.id.recipe_name);
            imageUrlView = (ImageView) view.findViewById(R.id.recipeImageView);
            recipeOptionMenu = (ImageView) view.findViewById(R.id.recipe_options_menu);

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
