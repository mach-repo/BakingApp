package com.example.android.bakingapp.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Movie;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.example.android.bakingapp.R;
import com.example.android.bakingapp.data.Ingredient;
import com.example.android.bakingapp.data.Recipe;
import com.example.android.bakingapp.data.Step;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import static android.content.ContentValues.TAG;
import static com.example.android.bakingapp.MainActivity.RECIPES_JSON;
import static com.example.android.bakingapp.widget.RecipeWidget.FAVORITE_RECIPE;
import static com.example.android.bakingapp.widget.RecipeWidget.FAVORITE_RECIPE_DEFAULT;

/**
 * Created by merouane on 27/01/2018.
 */

public class NetworkUtils {

    private static final String TAG = "NetworkUtils";

    public static final String RECIPES_URL = "https://d17h27t6h515a5.cloudfront.net/topher/2017/May/59121517_baking/baking.json";


    private static URL buildUrl() {

        try {
            URL queryUrl = new URL(RECIPES_URL);
            return queryUrl;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Log.e(TAG, "malformatted URL, builUrl function");
            return null;
        } finally {
            Log.d(TAG, "buildurl function is fine");
        }
    }


    /* gets the raw JSON from the given url server */
    private static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            String response = null;
            if (hasInput) {
                response = scanner.next();
            }
            scanner.close();
            return response;
        } finally {
            urlConnection.disconnect();
            Log.d(TAG, "we have the JSON data now getResponseFromHttpUrl function is fine");
        }
    }

    /* returns list of recipes from the RAW JSON response we got */
    public static ArrayList<Recipe> getListRecipes(String rawJsonResponse) throws JSONException {

        ArrayList<Recipe> listRecipes = new ArrayList<>();

        JSONArray recipesArray = new JSONArray(rawJsonResponse);

        for(int i = 0; i < recipesArray.length(); i++){
            JSONObject aSingleRecipe = recipesArray.getJSONObject(i);

            // here we get the simple values first
            String id = aSingleRecipe.getString("id");
            String name = aSingleRecipe.getString("name");
            String servings = aSingleRecipe.getString("servings");
            String image = aSingleRecipe.getString("image");

            // here we get the ingredients array
            JSONArray ingredientsArray = aSingleRecipe.getJSONArray("ingredients");
            ArrayList<Ingredient> ingredients = new ArrayList<>();
            for(int j = 0; j < ingredientsArray.length(); j++) {
                JSONObject aSingleIngredient = ingredientsArray.getJSONObject(j);

                String quantity = aSingleIngredient.getString("quantity");
                String measure = aSingleIngredient.getString("measure");
                String ingredient = aSingleIngredient.getString("ingredient");

                Ingredient newIngredient = new Ingredient(quantity, measure, ingredient);

                ingredients.add(newIngredient);
            }

            // here we get the steps array
            JSONArray stepsArray = aSingleRecipe.getJSONArray("steps");
            ArrayList<Step> steps = new ArrayList<>();
            for(int k = 0; k < stepsArray.length(); k++) {
                JSONObject aSingleStep = stepsArray.getJSONObject(k);

                String stepId = aSingleStep.getString("id");
                String shortDescription = aSingleStep.getString("shortDescription");
                String description = aSingleStep.getString("description");
                String videoURL = aSingleStep.getString("videoURL");
                String thumbnailURL = aSingleStep.getString("thumbnailURL");

                Step step = new Step(stepId, shortDescription, description, videoURL, thumbnailURL);

                steps.add(step);
            }

            Recipe newRecipe = new Recipe(id, name, ingredients, steps, servings, image);
            listRecipes.add(newRecipe);
        }

        Log.d(TAG, "getListRecipes is fine");
        return listRecipes;
    }


    public static ArrayList<Recipe> getRecipes(Context context){

        ArrayList<Recipe> recipes = new ArrayList<>();

        // build the URL first
        URL queryUrl = buildUrl();

        // get the JSON response
        String rawJsonResponse = "";
        try{
            rawJsonResponse = getResponseFromHttpUrl(queryUrl);
        }catch (IOException e){
            Log.e(TAG, "can't get the raw json yet");
        }

        // put the json the shared preference
        // ( so when the first time a widget is
        // instantiated it displays the ingredients of the first recipe )
        if(!TextUtils.isEmpty(rawJsonResponse)){
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = prefs.edit();

            // the JSON for the whole recipes
            editor.putString(RECIPES_JSON, rawJsonResponse);
            // favorite recipe
            if(!prefs.contains(FAVORITE_RECIPE)){
                editor.putInt(FAVORITE_RECIPE, FAVORITE_RECIPE_DEFAULT);
            }


            editor.commit();
        }


        // get the list of movies from the JSON response
        try{
            recipes = getListRecipes(rawJsonResponse);
        } catch(JSONException e){
            Log.e(TAG, "can't get the data from JSON");
        }

        return recipes;
    }
}
