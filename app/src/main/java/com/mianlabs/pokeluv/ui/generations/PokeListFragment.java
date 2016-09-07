/*
 * Copyright (C) 2016 Damián Adams
 */
package com.mianlabs.pokeluv.ui.generations;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mianlabs.pokeluv.R;
import com.mianlabs.pokeluv.adapters.PokeListAdapter;
import com.mianlabs.pokeluv.utilities.PokePicker;
import com.mianlabs.pokeluv.utilities.typeface.TypefaceUtils;

/**
 * Loads a list of a Pokemon generation using the PokeListAdapter.
 */
public class PokeListFragment extends Fragment {
    private static final String TAG = PokeListFragment.class.getSimpleName();
    public static final String POKE_LIST_FRAG_KEY = "PokeListFragment";
    public static final int NUMBER_OF_POKEMON_PER_ROW = 3;

    private AppCompatActivity mContext;
    private RecyclerView mPokemonList;
    private PokePicker.Generations mPokemonGeneration;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.layout_poke_list, container, false);
        mContext = (AppCompatActivity) getActivity();

        Bundle bundle = getArguments();
        if (bundle != null) {
            mPokemonGeneration = (PokePicker.Generations) bundle.get(GenFragment.GEN_FRAG_KEY);
            if (mPokemonGeneration != null) {
                // Get the gen. numbers array for the adapter.
                int[] gen;
                switch (mPokemonGeneration) {
                    case GEN_I:
                        gen = PokePicker.GenerationNumbers.getGenOne();
                        break;
                    case GEN_II:
                        gen = PokePicker.GenerationNumbers.getGenTwo();
                        break;
                    case GEN_III:
                        gen = PokePicker.GenerationNumbers.getGenThree();
                        break;
                    case GEN_IV:
                        gen = PokePicker.GenerationNumbers.getGenFour();
                        break;
                    case GEN_V:
                        gen = PokePicker.GenerationNumbers.getGenFive();
                        break;
                    case GEN_VI:
                        gen = PokePicker.GenerationNumbers.getGenSix();
                        break;
                    default:
                        gen = null;
                        Log.e(TAG, "Error while retrieving Pokemon generation numbers.");
                        break;
                }

                if (gen != null) {
                    mPokemonList = (RecyclerView) rootView.findViewById(R.id.pokemon_list);
                    PokeListAdapter pokeListAdapter = new PokeListAdapter(mContext, gen);
                    mPokemonList.setAdapter(pokeListAdapter);
                    mPokemonList.setLayoutManager(new GridLayoutManager(mContext, NUMBER_OF_POKEMON_PER_ROW));
                    mPokemonList.setHasFixedSize(true);
                }
            }
        }
        Log.d(TAG, "Poke List view set.");
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Sets the title of the Action Bar.
        if (mPokemonGeneration != null)
            TypefaceUtils.setActionBarTitle(mContext, mPokemonGeneration.getName());
    }
}
