/*
 * Copyright (C) 2016 Damián Adams
 */
package com.mianlabs.pokeluv.ui.main;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;

import com.mianlabs.pokeluv.R;
import com.mianlabs.pokeluv.ui.generations.PokeListFragment;
import com.mianlabs.pokeluv.utilities.PokePicker;
import com.mianlabs.pokeluv.utilities.typeface.TypefaceUtils;

/**
 * Launches PokeFragments with a random, "caught" Pokemon
 * or a user-selected Pokemon from a PokeListFragment fragment.
 */
public class MainActivity extends AppCompatActivity {
    public static final String MAIN_KEY = "MainActivity";
    public static final String PKMN_CAUGHT_KEY = "PKMN_CAUGHT";
    private static final String TAG_POKE_FRAGMENT = "PKF";

    private PokeFragment mPokeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        int caughtPkmn = PokePicker.catchRandomPokemon();

        Intent intent = getIntent();
        Bundle bundle = new Bundle();
        bundle.putInt(MAIN_KEY, caughtPkmn);

        // Intent should always be not null (unless called from constructor).
        if (intent != null) {
            if (intent.hasExtra(PokeListFragment.POKE_LIST_KEY)) { // If Pokemon has been selected by the user.
                int selectedPkmn = intent.getIntExtra(PokeListFragment.POKE_LIST_KEY, 1);
                bundle.putInt(MAIN_KEY, selectedPkmn);
            } else {
                bundle.putBoolean(PKMN_CAUGHT_KEY, true); // If displaying caught Pokemon.
            }
        }

        // The PokeFragment class is retained across configuration changes to avoid
        // background thread leakage.
        FragmentManager fragmentManager = getFragmentManager();
        mPokeFragment = (PokeFragment) fragmentManager.findFragmentByTag(TAG_POKE_FRAGMENT);

        if (mPokeFragment == null) { // PokeFragment was not retained on configuration change.
            mPokeFragment = new PokeFragment();
            mPokeFragment.setArguments(bundle);
            getFragmentManager().beginTransaction()
                    .add(R.id.main_container, mPokeFragment, TAG_POKE_FRAGMENT).commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Sets the typeface of the activity for the retained fragment.
        TypefaceUtils.setActionBarTitle(this, getString(R.string.app_name));
        return super.onCreateOptionsMenu(menu);
    }
}
