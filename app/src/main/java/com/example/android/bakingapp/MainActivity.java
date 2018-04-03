package com.example.android.bakingapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Movie;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.test.espresso.IdlingResource;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.bakingapp.adapter.RecipeAdapter;
import com.example.android.bakingapp.data.Recipe;
import com.example.android.bakingapp.idlingResource.SimpleIdlingResource;
import com.example.android.bakingapp.utilities.NetworkUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.View.VISIBLE;

public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<ArrayList<Recipe>>,
        RecipeAdapter.RecipeAdapterOnClickHandler{

    public static final String RECIPES_JSON = "recipes-json";

    private static final String TAG = "MainActivity";

    /* views */
    @BindView(R.id.my_recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.pb_loading_indicator)
    ProgressBar mProgressBar;
    @BindView(R.id.error_message_textview)
    TextView mNoInternetTextview;

    private RecipeAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private int mPosition = RecyclerView.NO_POSITION;

    /* for restoring data between reconfigurations */
    private ArrayList<Recipe> mRecipesList;
    private static final String RECIPES_LIST = "recipes-list";
    private static final String LAYOUT_MANAGER_STATE = "layout-state";

    /* loader id */
    private static final int ID_RECIPES_LOADER = 1992;

    // The Idling Resource which will be null in production.
    @Nullable
    private SimpleIdlingResource mIdlingResource;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);;
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new RecipeAdapter(this, null, this);
        mRecyclerView.setAdapter(mAdapter);




        if(savedInstanceState == null){
            mRecipesList = new ArrayList<>();
            if(isOnline()){

                // for testing purpose
                getIdlingResource();
                if (mIdlingResource != null) {
                    mIdlingResource.setIdleState(false);
                }

                // kick-off the loader
                getSupportLoaderManager().initLoader(ID_RECIPES_LOADER, null, this);

            }else{
                showErrorNoInternet();
            }
        }else{
            mRecipesList = savedInstanceState.getParcelableArrayList(RECIPES_LIST);
            mAdapter.swapDataset(mRecipesList);

            Parcelable state = savedInstanceState.getParcelable(LAYOUT_MANAGER_STATE);
            mLayoutManager.onRestoreInstanceState(state);
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelableArrayList(RECIPES_LIST, mRecipesList);
        outState.putParcelable(LAYOUT_MANAGER_STATE, mLayoutManager.onSaveInstanceState());
    }


    @Override
    public Loader<ArrayList<Recipe>> onCreateLoader(int loaderId, Bundle args) {
        switch (loaderId) {
            case ID_RECIPES_LOADER:
                showProgressBar();
                return new RecipesLoader(MainActivity.this, null);
            default:
                throw new RuntimeException("Loader Not Implemented: " + loaderId);
        }
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Recipe>> loader, ArrayList<Recipe> data) {

        mAdapter.swapDataset(data);
        if (mPosition == RecyclerView.NO_POSITION) {
            mPosition = 0;
        }

        mRecyclerView.smoothScrollToPosition(mPosition);
        hideProgressBar();

        mRecipesList.addAll(data);

        // for testing purpose
        if (mIdlingResource != null) {
            mIdlingResource.setIdleState(true);
        }
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Recipe>> loader) {
        mAdapter.swapDataset(null);
    }

    @Override
    public void onClick(Recipe recipe) {
        Intent intent = new Intent(this, RecipeDetailActivity.class);
        intent.putExtra(RecipeDetailActivity.RECIPE, recipe);
        startActivity(intent);
    }

    private void showErrorNoInternet(){
        mRecyclerView.setVisibility(View.INVISIBLE);
        mNoInternetTextview.setVisibility(View.VISIBLE);
    }
    private void showProgressBar(){
        mRecyclerView.setVisibility(View.GONE);
        mNoInternetTextview.setVisibility(View.GONE);
        mProgressBar.setVisibility(VISIBLE);
    }
    private void hideProgressBar(){
        mRecyclerView.setVisibility(View.VISIBLE);
        mNoInternetTextview.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.GONE);
    }

    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    // the task that gets the recipes from the internet
    private static class RecipesLoader extends AsyncTaskLoader<ArrayList<Recipe>> {

        ArrayList<Recipe> mRecipesResults;
        Context mContext;


        public RecipesLoader(Context context, ArrayList<Recipe> recipes){
            super(context);
            mContext = context;
            mRecipesResults = recipes;
        }

        @Override
        public ArrayList<Recipe> loadInBackground() {
            mRecipesResults = NetworkUtils.getRecipes(mContext);
            return mRecipesResults;
        }

        @Override
        protected void onStartLoading() {
            if (mRecipesResults != null) {
                Log.d(TAG, "recipes cached with success");
                deliverResult(mRecipesResults);
            } else {
                forceLoad();
            }
        }
        @Override
        public void deliverResult(ArrayList<Recipe> results) {
            mRecipesResults = results;
            super.deliverResult(results);
        }
    }


    /**
     * Only called from test, creates and returns a new {@link SimpleIdlingResource}.
     */
    @VisibleForTesting
    @NonNull
    public IdlingResource getIdlingResource() {
        if (mIdlingResource == null) {
            mIdlingResource = new SimpleIdlingResource();
        }
        return mIdlingResource;
    }
}
