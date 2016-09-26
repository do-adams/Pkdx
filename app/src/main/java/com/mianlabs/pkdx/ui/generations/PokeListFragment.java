/*
 * Copyright (C) 2016 Damián Adams
 */
package com.mianlabs.pkdx.ui.generations;

import android.app.Fragment;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mianlabs.pkdx.R;
import com.mianlabs.pkdx.adapters.PokeListAdapter;
import com.mianlabs.pkdx.database.PokeCursorManager;
import com.mianlabs.pkdx.database.PokeDBContract;
import com.mianlabs.pkdx.utilities.PokePicker;
import com.mianlabs.pkdx.utilities.PokeSharedPreferences;
import com.mianlabs.pkdx.utilities.typeface.TypefaceUtils;

import java.util.ArrayList;
import java.util.Random;

/**
 * Loads a list of a Pokemon generation using the PokeListAdapter.
 */
public class PokeListFragment extends Fragment implements PokeCursorManager.LoaderCall {
    private static final String TAG = PokeListFragment.class.getSimpleName();

    private static boolean sDisplayFavoriteMsg = true; // Flag for telling the user how to use this Activity.

    public static final String POKE_LIST_FRAG_KEY = "PokeListFragment";
    public static final int NUMBER_OF_POKEMON_PER_ROW = 3;

    private final int LOADER_ID = new Random().nextInt();

    private AppCompatActivity mContext;

    private PokePicker.Generations mPokemonGeneration;
    private int[] mGen;

    private RecyclerView mPokemonList;
    private PokeListAdapter mPokeListAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // All right to grab context here since this fragment is not retained.
        mContext = (AppCompatActivity) getActivity();
        int countOfCaughtPokemon = mContext
                .getSharedPreferences(PokeSharedPreferences.COUNT_CAUGHT_POKEMON_FILENAME, Context.MODE_PRIVATE)
                .getInt(PokeSharedPreferences.COUNT_CAUGHT_POKEMON_KEY, 0);
        if (sDisplayFavoriteMsg && countOfCaughtPokemon < PokePicker.NUM_OF_POKEMON) {
            TypefaceUtils.displayToast(mContext, getString(R.string.poke_list_fragment_msg),
                    TypefaceUtils.TOAST_SHORT_DURATION);
            sDisplayFavoriteMsg = false;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.layout_poke_list, container, false);
        mPokemonList = (RecyclerView) rootView.findViewById(R.id.pokemon_list);

        Bundle bundle = getArguments();
        if (bundle != null) {
            mPokemonGeneration = (PokePicker.Generations) bundle.get(GenFragment.GEN_FRAG_KEY);
            if (mPokemonGeneration != null) {
                // Get the gen. numbers array for the adapter.
                switch (mPokemonGeneration) {
                    case GEN_I:
                        mGen = PokePicker.GenerationNumbers.getGenOne();
                        break;
                    case GEN_II:
                        mGen = PokePicker.GenerationNumbers.getGenTwo();
                        break;
                    case GEN_III:
                        mGen = PokePicker.GenerationNumbers.getGenThree();
                        break;
                    case GEN_IV:
                        mGen = PokePicker.GenerationNumbers.getGenFour();
                        break;
                    case GEN_V:
                        mGen = PokePicker.GenerationNumbers.getGenFive();
                        break;
                    case GEN_VI:
                        mGen = PokePicker.GenerationNumbers.getGenSix();
                        break;
                    default:
                        mGen = null;
                        break;
                }
                if (mGen != null) {
                    // Initially sets the list of Pokemon to no data (avoids a logging error).
                    mPokeListAdapter = new PokeListAdapter(mContext, new int[]{});
                    mPokemonList.setAdapter(mPokeListAdapter);
                    mPokemonList.setLayoutManager(new GridLayoutManager(mContext, NUMBER_OF_POKEMON_PER_ROW));
                    mPokemonList.setHasFixedSize(true);

                    // Starts the loader to fill the List up.
                    mContext.getSupportLoaderManager().initLoader(LOADER_ID, new Bundle(),
                            new PokeCursorManager(mContext, this,
                                    PokeDBContract.CaughtPokemonEntry.TABLE_NAME));
                } else {
                    Log.e(TAG, "Error while retrieving Pokemon generation numbers.");
                }
            } else {
                Log.e(TAG, "No Pokemon Generation enum found for PokeListFragment");
            }
        } else {
            Log.e(TAG, "No bundle found for PokeListFragment");
        }
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Sets the title of the Action Bar.
        if (mPokemonGeneration != null)
            TypefaceUtils.setActionBarTitle(mContext, mPokemonGeneration.getName());
    }

    /**
     * Displays the list of caught Pokemon that are from the given Generation.
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        ArrayList<Integer> listOfCaughtPokemon = PokeCursorManager.getPokemonInDb(cursor,
                PokeDBContract.CaughtPokemonEntry.TABLE_NAME, PokeDBContract.CaughtPokemonEntry.COLUMN_NUMBER);

        // Determines if any of the caught Pokemon are from this Gen.
        ArrayList<Integer> listOfDisplayPokemon = new ArrayList<>();
        for (int n : mGen)
            if (listOfCaughtPokemon.contains(n))
                listOfDisplayPokemon.add(n);

        // Converts from List to Array.
        int[] displayPokemon = new int[listOfDisplayPokemon.size()];
        for (int i = 0; i < displayPokemon.length; i++)
            displayPokemon[i] = listOfDisplayPokemon.get(i);

        mPokemonList.setAdapter(new PokeListAdapter(mContext, displayPokemon));
        Log.d(TAG, "Poke List view set.");
    }
}
