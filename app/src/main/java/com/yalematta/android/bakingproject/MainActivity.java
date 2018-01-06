package com.yalematta.android.bakingproject;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.yalematta.android.bakingproject.Models.Recipe;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends Activity {

    private static final String ENDPOINT = "https://d17h27t6h515a5.cloudfront.net/topher/2017/May/59121517_baking/baking.json";
    private RequestQueue requestQueue;
    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestQueue = Volley.newRequestQueue(getApplicationContext());

        GsonBuilder gsonBuilder = new GsonBuilder();
        gson = gsonBuilder.create();

        fetchRecipes();
    }

    private void fetchRecipes() {

        StringRequest request = new StringRequest(Request.Method.GET, ENDPOINT, onRecipesLoaded, onRecipesError);
        requestQueue.add(request);
    }

    private final Response.Listener<String> onRecipesLoaded = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {

            List<Recipe> recipes = Arrays.asList(gson.fromJson(response, Recipe[].class));

            Log.i("MainActivity", recipes.size() + " recipes loaded.");
            for (Recipe recipe : recipes) {
                Log.i("MainActivity", recipe.recipeId + ": " + recipe.name);
            }
        }
    };

    private final Response.ErrorListener onRecipesError = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.e(" MainActivity", error.toString());
        }
    };
}
