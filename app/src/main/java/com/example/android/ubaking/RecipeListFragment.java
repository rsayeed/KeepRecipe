package com.example.android.ubaking;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.ubaking.model.RecipeStep;
import com.example.android.ubaking.utilities.RecipeDataUtils;
import com.example.android.ubaking.utilities.RecipeListAdapter;

import java.util.List;

/**
 * Created by rubab on 6/27/17.
 */

public class RecipeListFragment extends Fragment implements RecipeListAdapter.RecipeListOnClickHandler {

    private static final String TAG = RecipeListFragment.class.getSimpleName();

    private OnClickListener mCallBack;
    private View savedListView;

    private RecyclerView mRecipeListRecyclerView;
    private LinearLayoutManager mRecipeListLayoutManager;

    public RecipeListFragment() {};

    /**
     * This will be used as the callback listener for the activity associated to this
     * fragment
     */
    public interface OnClickListener {

        void onItemSelected(RecipeStep recipe, View view, int position);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {

            mCallBack = (OnClickListener) context;
        } catch (ClassCastException e) {

            throw new ClassCastException(context.toString()
                    + " must implement onItemlickListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        RecipeListAdapter mRecipeListAdapter;

        // Inflate the fragment recipe list layout
        View rootView = inflater.inflate(R.layout.fragment_recipe_list, container, false);
        mRecipeListRecyclerView = (RecyclerView) rootView.findViewById(R.id.fragment_recycler_view_list);

        // Use a linear layout manager
        mRecipeListLayoutManager = new LinearLayoutManager(getContext());
        mRecipeListRecyclerView.setLayoutManager(mRecipeListLayoutManager);

        // Pass in the appropriate recipe object
        mRecipeListAdapter = new RecipeListAdapter(getContext(), getRecipeSteps(), this);

        mRecipeListRecyclerView.setAdapter(mRecipeListAdapter);

        // This will scroll through the recycler view to the previously selected item
        mRecipeListRecyclerView.post(new Runnable() {
            @Override
            public void run() {
                mRecipeListRecyclerView.smoothScrollToPosition(RecipeDataUtils.getPositionOfStep());
            }
        });

        // This is to highlight the appropriate list item when when device orientation is changed
        if (getResources().getBoolean(R.bool.twoPaneMode)) {

            mRecipeListRecyclerView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (mRecipeListRecyclerView.findViewHolderForAdapterPosition(
                            RecipeDataUtils.getPositionOfStep()) != null) {

                        // Set highlight on appropriate item
                        mRecipeListRecyclerView.findViewHolderForAdapterPosition(
                                RecipeDataUtils.getPositionOfStep()).itemView.performClick();

                    }
                }
            }, 150);
        }

        return rootView;
    }

    private List<RecipeStep> getRecipeSteps() {
        return RecipeDataUtils.getRecipeStepList();
    }

    /**
     * This is the onclick method that is defined in the recycler view adapter
     * @param recipe
     */
    @Override
    public void onClick(RecipeStep recipe, View view, int position) {

        mCallBack.onItemSelected(recipe, view, position);
    }
}
