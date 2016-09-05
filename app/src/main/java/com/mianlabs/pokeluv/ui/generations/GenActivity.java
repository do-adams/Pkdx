/*
 * Copyright (C) 2016 Damián Adams
 */
package com.mianlabs.pokeluv.ui.generations;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.mianlabs.pokeluv.R;
import com.mianlabs.pokeluv.utilities.TypefaceUtils;

/**
 * Used for displaying GenFragments and PokeList fragments.
 */
public class GenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gen);
        TypefaceUtils.setActionBarTitle(this, getString(R.string.app_name));

        // Important to check for null states, otherwise you can end up with
        // multiple instances of the same fragment on top of each other in case
        // of frequent configuration changes.
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.generations_container, new GenFragment()).commit();
        }
    }
}
