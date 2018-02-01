package com.yalematta.android.bakingproject.utils;

import android.content.Context;
import android.content.res.Configuration;

/**
 * Created by yalematta on 2/1/18.
 */

public class AppUtilities {

    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }




}
