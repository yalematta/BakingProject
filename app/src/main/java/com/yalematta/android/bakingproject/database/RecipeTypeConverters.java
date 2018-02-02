package com.yalematta.android.bakingproject.database;

import android.arch.persistence.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yalematta.android.bakingproject.entities.Ingredient;
import com.yalematta.android.bakingproject.entities.Step;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

/**
 * Created by yalematta on 1/23/18.
 */

public class RecipeTypeConverters {

    static Gson gson = new Gson();

    @TypeConverter
    public static List<Ingredient> stringToIngredientList(String data) {
        if (data == null) {
            return Collections.emptyList();
        }

        Type listType = new TypeToken<List<Ingredient>>() {
        }.getType();

        return gson.fromJson(data, listType);
    }

    @TypeConverter
    public static String ingredientListToString(List<Ingredient> ingredients) {
        return gson.toJson(ingredients);
    }

    @TypeConverter
    public static List<Step> stringToStepList(String data) {
        if (data == null) {
            return Collections.emptyList();
        }

        Type listType = new TypeToken<List<Step>>() {
        }.getType();

        return gson.fromJson(data, listType);
    }

    @TypeConverter
    public static String stepListToString(List<Step> steps) {
        return gson.toJson(steps);
    }
}

