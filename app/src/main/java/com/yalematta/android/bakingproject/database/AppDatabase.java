package com.yalematta.android.bakingproject.database;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import com.yalematta.android.bakingproject.dao.RecipeDao;
import com.yalematta.android.bakingproject.entities.Recipe;

import java.text.DateFormat;
import java.util.Date;

/**
 * Created by yalematta on 1/21/18.
 */

@Database(entities = {Recipe.class}, version = 1)
@TypeConverters({RecipeTypeConverters.class})
public abstract class AppDatabase extends RoomDatabase {

    private static final String DATABASE_NAME = "recipes-database.db";
    private static volatile AppDatabase instance;

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = create(context);
        }
        return instance;
    }

    public static AppDatabase create(final Context context) {
        return Room.databaseBuilder(
                context,
                AppDatabase.class,
                DATABASE_NAME)
                .addCallback(rdc)
                .build();
    }

    public abstract RecipeDao getRecipeDao();

    //region Callbacks
    static RoomDatabase.Callback rdc = new RoomDatabase.Callback() {
        public void onCreate(SupportSQLiteDatabase db) {
//            String SQL_CREATE_TABLE = "CREATE TABLE recipes" +
//                    "(" +
//                    "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
//                    "cat_name TEXT, " +
//                    "cat_type TEXT)";
//            db.execSQL(SQL_CREATE_TABLE);
//            ContentValues contentValues = new ContentValues();
//            contentValues.put("cat_name", "electronics");
//            contentValues.put("cat_type", "commerce");
//            db.insert("categories", OnConflictStrategy.IGNORE, contentValues);
            Log.d("db create ", "table created when db created first time in  onCreate");
        }

        public void onOpen(SupportSQLiteDatabase db) {
//            ContentValues contentValues = new ContentValues();
//            contentValues.put("open_time", DateFormat.getDateTimeInstance().format(new Date()));
//            db.insert("dbusage", OnConflictStrategy.IGNORE, contentValues);
            Log.d("db open ", "adding db open date record");
        }
    };
    //endregion
}
