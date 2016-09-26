/*
 * Copyright (C) 2016 Damián Adams
 */
package com.mianlabs.pkdx.ui.generations;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.mianlabs.pkdx.R;
import com.mianlabs.pkdx.utilities.typeface.TypefaceUtils;

/**
 * Used for displaying GenFragments and PokeListFragment fragments.
 */
public class GenActivity extends AppCompatActivity {
    private static final String TAG = GenActivity.class.getSimpleName();

    private static boolean mIsTwoPane; // Tied to the class, not the instance.

    public static boolean isTwoPane() {
        return mIsTwoPane;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gen);
        TypefaceUtils.setActionBarTitle(this, getString(R.string.app_name));

        // Sets the two pane value.
        mIsTwoPane = (findViewById(R.id.poke_list_container) != null ?
                true : false);
        Log.d(TAG, "Value of isTwoPane: " + mIsTwoPane);

        // Important to check for null states, otherwise you can end up with
        // multiple instances of the same fragment on top of each other in case
        // of frequent configuration changes.
        if (savedInstanceState == null)
            getFragmentManager().beginTransaction()
                    .add(R.id.generations_container, new GenFragment()).commit();
    }
}
