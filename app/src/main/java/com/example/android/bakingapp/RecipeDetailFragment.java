package com.example.android.bakingapp;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.android.bakingapp.data.Ingredient;
import com.example.android.bakingapp.data.Recipe;
import com.example.android.bakingapp.data.Step;

import java.util.List;

import static com.example.android.bakingapp.RecipeDetailActivity.RECIPE;

/**
 * A placeholder fragment containing a simple view.
 */
public class RecipeDetailFragment extends Fragment {

    private static final String TAG = "RecipeDetailFragment";

    OnStepClickListener mCallback;

    public interface OnStepClickListener {
        void onStepSelected(List<Step> steps, int clickedStep);
    }

    public RecipeDetailFragment() {
    }

    // Override onAttach to make sure that the container activity has implemented the callback
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the host activity has implemented the callback interface
        // If not, it throws an exception
        try {
            mCallback = (OnStepClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnStepClickListener");
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // make the fragment retainable
        setRetainInstance(true);

        // the recipe
        Recipe currentRecipe = this.getArguments().getParcelable(RECIPE);

        // inflating the view
        View rootView = inflater.inflate(R.layout.fragment_recipe_detail, container, false);

        // assigning the ingredients to their view
        TextView ingredientsTextview = (TextView) rootView.findViewById(R.id.recipe_ingredients_textview);
        ingredientsTextview.setText(createSummaryFromIngredients(currentRecipe.getIngredients()));

        // the recyclerview that holds the recipe's steps
        RecyclerView stepsRecyclerView = (RecyclerView) rootView.findViewById(R.id.steps_recyclerview);
        //mLayoutManager = new LinearLayoutManager(this.getActivity());
        //Log.d("debugMode", "The application stopped after this");
        stepsRecyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        //mAdapter = new RecyclerAdapter(getNames());
        stepsRecyclerView.setAdapter(new StepsAdapter(this.getActivity(), currentRecipe.getSteps()));


        return rootView;
    }

    /* summary of the ingredients */
    private String createSummaryFromIngredients(List<Ingredient> ingredients){

        String summary = "";

        for(int i = 0; i < ingredients.size(); i++){

            Ingredient currentIngredient = ingredients.get(i);

            String quantity = currentIngredient.getQuantity();
            String measure = currentIngredient.getMeasure();
            String ingredient = currentIngredient.getIngredient();

            String newLine = "- " + ingredient + " " + "(" + quantity + " " + measure + ")\n";

            summary += newLine;
            //Log.d(TAG, "newline is = " + newLine);
        }

        return summary;
    }




    // the adapder for the steps
    public class StepsAdapter extends RecyclerView.Adapter<StepsAdapter.StepsAdapterViewHolder> {

        private List<Step> mSteps;
        private Context mContext;

        private final String TAG ="StepsAdapter";


        public StepsAdapter(Context context, List<Step> myDataset) {
            mContext = context;
            mSteps = myDataset;
        }

        @Override
        public StepsAdapter.StepsAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(mContext).inflate(R.layout.steps_list_item, parent, false);
            return new StepsAdapter.StepsAdapterViewHolder(view);
        }

        @Override
        public void onBindViewHolder(StepsAdapter.StepsAdapterViewHolder holder, int position) {

            Step currentStep = mSteps.get(position);

            holder.mStepNumberTextView.setText(currentStep.getId());
            holder.mStepTextView.setText(currentStep.getShortDescription());
        }


        @Override
        public int getItemCount() {
            if (null == mSteps){
                return 0;
            } else {
                return mSteps.size();
            }
        }

        // stores and recycles views as they are scrolled off screen
        public class StepsAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            TextView mStepNumberTextView;
            TextView mStepTextView;

            StepsAdapterViewHolder(View itemView) {
                super(itemView);

                mStepNumberTextView = (TextView) itemView.findViewById(R.id.step_number);
                mStepTextView = (TextView) itemView.findViewById(R.id.single_step_textview);
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {
                int adapterPosition = getAdapterPosition();
                //Step step = mSteps.get(adapterPosition);
                //mClickHandler.onClick(trailer);
                mCallback.onStepSelected(mSteps, adapterPosition);
            }
        }

    }
}
