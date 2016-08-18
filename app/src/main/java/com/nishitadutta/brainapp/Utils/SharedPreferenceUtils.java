package com.nishitadutta.brainapp.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.nishitadutta.brainapp.Global.Constants;

/**
 * Created by Nishita on 18-08-2016.
 */
public class SharedPreferenceUtils {

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private static SharedPreferenceUtils sInstance;
    private Context mContext;

    private SharedPreferenceUtils(Context context){
        mContext=context;
        sharedPreferences=context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
        editor=sharedPreferences.edit();
    }

    public static SharedPreferenceUtils getInstance(Context context){
        if (sInstance == null) {
            sInstance = new SharedPreferenceUtils(context.getApplicationContext());
        }
        return sInstance;
    }

    public void setValue(String key, String value){
        editor.putString(key, value);
        editor.apply();
    }
}
