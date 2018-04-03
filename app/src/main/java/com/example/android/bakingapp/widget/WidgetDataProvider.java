package com.example.android.bakingapp.widget;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.android.bakingapp.R;
import com.example.android.bakingapp.data.Ingredient;
import com.example.android.bakingapp.data.Recipe;
import com.example.android.bakingapp.utilities.NetworkUtils;

import java.util.ArrayList;
import java.util.List;

import static com.example.android.bakingapp.MainActivity.RECIPES_JSON;
import static com.example.android.bakingapp.R.string.ingredients;
import static com.example.android.bakingapp.widget.RecipeWidget.FAVORITE_RECIPE;
import static com.example.android.bakingapp.widget.RecipeWidget.FAVORITE_RECIPE_DEFAULT;

/**
 * Created by merouane on 30/01/2018.
 *
 * WidgetDataProvider acts as the adapter for the collection view widget,
 * providing RemoteViews to the widget in the getViewAt method.
 */
public class WidgetDataProvider implements RemoteViewsService.RemoteViewsFactory {

    private static final String TAG = "WidgetDataProvider";

    private Context mContext;
    private Recipe mRecipe;
    private List<Ingredient> mIngredientsList;

    public WidgetDataProvider(Context context, Intent intent) {
        mContext = context;
    }

    @Override
    public void onCreate() {
        initData();
    }

    @Override
    public void onDataSetChanged() {
        initData();
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return mIngredientsList.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews view = new RemoteViews(mContext.getPackageName(),
                android.R.layout.simple_list_item_1);

        view.setTextViewText(android.R.id.text1,
                createLineFromIngredients(mIngredientsList.get(position)));

        return view;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }


    private void initData() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        String rawJSON = prefs.getString(RECIPES_JSON, "");
        int favoriteRecipe = prefs.getInt(FAVORITE_RECIPE, FAVORITE_RECIPE_DEFAULT);

        if(!TextUtils.isEmpty(rawJSON)){
            ArrayList<Recipe> recipes = new ArrayList<>();

            try{
                recipes = NetworkUtils.getListRecipes(rawJSON);
            }catch(Exception e){
                Log.e("widget", "error loading recipes from JSON for the widget");
            }

            //mIngredientsList.clear();

            mRecipe = recipes.get(favoriteRecipe - 1);
            mIngredientsList = mRecipe.getIngredients();

        }
    }

    /* one line of the ingredients */
    private String createLineFromIngredients(Ingredient ingredient){

        String newLine = "- " + ingredient.getIngredient() + " " + "(" + ingredient.getQuantity() + " " + ingredient.getMeasure() + ")";

        return newLine;
    }

}