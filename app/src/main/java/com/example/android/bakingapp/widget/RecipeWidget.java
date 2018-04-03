package com.example.android.bakingapp.widget;

import android.annotation.TargetApi;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.widget.RemoteViews;

import com.example.android.bakingapp.R;
import com.example.android.bakingapp.data.Ingredient;
import com.example.android.bakingapp.data.Recipe;
import com.example.android.bakingapp.utilities.NetworkUtils;

import java.util.ArrayList;
import java.util.List;

import static com.example.android.bakingapp.MainActivity.RECIPES_JSON;
import static com.example.android.bakingapp.RecipeDetailActivity.RECIPE;

/**
 * Implementation of App Widget functionality.
 */
public class RecipeWidget extends AppWidgetProvider {

    public static final String FAVORITE_RECIPE = "favorite-recipe";
    public static final int FAVORITE_RECIPE_DEFAULT = 1;

    private Recipe mRecipe;

    // here we receive the intent
    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        if(intent != null){
            if(intent.getAction().equals("android.appwidget.action.APPWIDGET_UPDATE")){

                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                ComponentName thisWidget = new ComponentName(context.getPackageName(), RecipeWidget.class.getName());
                int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
                //Log.d("khorti", "rani hna machi lhih");
                onUpdate(context, appWidgetManager, appWidgetIds);
            }
        }

    }

    private void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
        initData(context);


        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.recipe_widget);
        views.setTextViewText(R.id.widget_recipe_name, mRecipe.getName());

        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.widget_list);

        // Set up the collection
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            setRemoteAdapter(context, views);
        } else {
            setRemoteAdapterV11(context, views);
        }
        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    /**
     * Sets the remote adapter used to fill in the list items
     *
     * @param views RemoteViews to set the RemoteAdapter
     */
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private static void setRemoteAdapter(Context context, @NonNull final RemoteViews views) {
        views.setRemoteAdapter(R.id.widget_list,
                new Intent(context, WidgetService.class));
    }

    /**
     * Sets the remote adapter used to fill in the list items
     *
     * @param views RemoteViews to set the RemoteAdapter
     */
    @SuppressWarnings("deprecation")
    private static void setRemoteAdapterV11(Context context, @NonNull final RemoteViews views) {
        views.setRemoteAdapter(0, R.id.widget_list,
                new Intent(context, WidgetService.class));
    }


    private void initData(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String rawJSON = prefs.getString(RECIPES_JSON, "");
        int favoriteRecipe = prefs.getInt(FAVORITE_RECIPE, FAVORITE_RECIPE_DEFAULT);

        if(!TextUtils.isEmpty(rawJSON)){
            ArrayList<Recipe> recipes = new ArrayList<>();

            try{
                recipes = NetworkUtils.getListRecipes(rawJSON);
            }catch(Exception e){
                Log.e("widget", "error loading recipes from JSON for the widget");
            }
            //Log.d("khorti", "the new favorite is = " + favoriteRecipe);
            mRecipe = recipes.get(favoriteRecipe - 1);
        }
    }
}



