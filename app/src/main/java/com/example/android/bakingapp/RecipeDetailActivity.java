package com.example.android.bakingapp;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ScrollView;

import com.example.android.bakingapp.data.Recipe;
import com.example.android.bakingapp.data.Step;
import com.example.android.bakingapp.widget.RecipeWidget;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.android.bakingapp.StepActivity.CURRENT_STEP;

import static com.example.android.bakingapp.StepActivity.STEPS;
import static com.example.android.bakingapp.widget.RecipeWidget.FAVORITE_RECIPE;

public class RecipeDetailActivity extends AppCompatActivity
        implements RecipeDetailFragment.OnStepClickListener{

    private boolean mIsTwoPane = false;

    public static final String CURRENT_SELECTED_STEP = "current-step";


    private Recipe mCurrentRecipe;
    private int mCurrentStep = 0;
    public static final String RECIPE = "recipe";


    @BindView(R.id.recipe_detail_scrollview)
    ScrollView mScrollView;

    public static final String SCROLL_POSITION ="scroll-position";


    // retain the created fragment/fragments to avoid recreation
    private static final String RECIPE_FRAGMENT = "RecipeFragment";
    private static final String STEP_FRAGMENT = "DetailFragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ButterKnife.bind(this);

        if(getIntent() != null){
            mCurrentRecipe = getIntent().getParcelableExtra(RECIPE);
        }


        // the arguments for the RecipeDetailFragment
        Bundle bundle = new Bundle();
        bundle.putParcelable(RECIPE, mCurrentRecipe);


        if(findViewById(R.id.step_details_container) != null){

            mIsTwoPane = true;

            // the arguments for the StepDetailFragment
            Bundle otherBundle = new Bundle();
            otherBundle.putParcelable(CURRENT_STEP, mCurrentRecipe.getSteps().get(0));

            if(savedInstanceState == null){
                // create first fragment
                RecipeDetailFragment recipeDetailFragment = new RecipeDetailFragment();
                recipeDetailFragment.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().
                        replace(R.id.recipe_details_container, recipeDetailFragment, RECIPE_FRAGMENT).commit();

                // create second fragment
                StepDetailFragment stepDetailFragment = new StepDetailFragment();
                stepDetailFragment.setArguments(otherBundle);
                getSupportFragmentManager().beginTransaction().
                        replace(R.id.step_details_container, stepDetailFragment, STEP_FRAGMENT).commit();

            }

        }else{

            if(savedInstanceState == null){
                // create only first fragment because one pane mode
                RecipeDetailFragment recipeDetailFragment = new RecipeDetailFragment();
                recipeDetailFragment.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().
                        replace(R.id.recipe_detail_container, recipeDetailFragment, RECIPE_FRAGMENT).commit();
            }

            mIsTwoPane = false;
        }

        if(savedInstanceState != null){
            restoreScrollView(savedInstanceState);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_recipe_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        }else if(id == R.id.action_add_widget){
            updateWidget();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStepSelected(List<Step> steps, int clickedStep) {

        ArrayList<Step> stepsArray = new ArrayList<>();
        stepsArray.addAll(steps);

        mCurrentStep = clickedStep;

        if(mIsTwoPane){
            // give the pressed step to the fragment
            Bundle bundle = new Bundle();
            // for the step activity
            bundle.putParcelableArrayList(STEPS, stepsArray);
            // for step fragmenr
            bundle.putParcelable(CURRENT_STEP, stepsArray.get(clickedStep));

            StepDetailFragment newStepDetailFragment = new StepDetailFragment();
            newStepDetailFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.step_details_container, newStepDetailFragment)
                    .commit();


            //Log.d("Recipe", "rani hna ya zouba");
        }else{
            // launch the step activity
            Intent intent = new Intent(this, StepActivity.class);
            intent.putExtra(STEPS, stepsArray);
            intent.putExtra(CURRENT_STEP, clickedStep);
            startActivity(intent);
        }
    }

    /* sends a broadcast after the user presses the menu action */
    public void updateWidget() {
        // write to shared preference then send the broadcast to update
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        int newFavoriteRecipe = Integer.parseInt(mCurrentRecipe.getId());
        //Log.d("khorti", "1 new favorite is = " + newFavoriteRecipe);
        editor.putInt(FAVORITE_RECIPE, newFavoriteRecipe);
        editor.commit();
        // send the broadcast
        Intent intent = new Intent(this, RecipeWidget.class);
        intent.setAction("android.appwidget.action.APPWIDGET_UPDATE");
        sendBroadcast(intent);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putIntArray(SCROLL_POSITION, new int[]{ mScrollView.getScrollX(), mScrollView.getScrollY()});
    }

    private void restoreScrollView(Bundle savedInstanceState){

        final int[] position = savedInstanceState.getIntArray(SCROLL_POSITION);
        if(position != null)
            mScrollView.post(new Runnable() {
                public void run() {
                    mScrollView.scrollTo(position[0], position[1]);
                }
            });
    }
}
