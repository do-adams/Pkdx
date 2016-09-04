package com.mianlabs.pokeluv.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.mianlabs.pokeluv.R;
import com.mianlabs.pokeluv.adapters.PokeListAdapter;
import com.mianlabs.pokeluv.utilities.PokePicker;
import com.mianlabs.pokeluv.utilities.TypefaceUtils;

public class PokeList extends AppCompatActivity {
    private static final String TAG = PokeList.class.getSimpleName();

    private RecyclerView mPokemonList;
    private PokePicker.Generations mPokemonGeneration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poke_list);

        String title; // Title for the Action Bar.
        if (getIntent().hasExtra(GenActivity.GEN_KEY)) {
            Bundle bundle = getIntent().getExtras();
            mPokemonGeneration = (PokePicker.Generations) bundle.get(GenActivity.GEN_KEY);
            title = mPokemonGeneration.getName();

            // Get the gen. numbers array for the adapter.
            int[] gen;
            switch (mPokemonGeneration) {
                case GEN_I:
                    gen = PokePicker.GenNumbers.getGenOne();
                    break;
                case GEN_II:
                    gen = PokePicker.GenNumbers.getGenTwo();
                    break;
                case GEN_III:
                    gen = PokePicker.GenNumbers.getGenThree();
                    break;
                case GEN_IV:
                    gen = PokePicker.GenNumbers.getGenFour();
                    break;
                case GEN_V:
                    gen = PokePicker.GenNumbers.getGenFive();
                    break;
                case GEN_VI:
                    gen = PokePicker.GenNumbers.getGenSix();
                    break;
                default:
                    gen = null;
                    Log.e(TAG, "Error while retrieving the generations number array");
                    break;
            }

            if (gen != null) {
                mPokemonList = (RecyclerView) findViewById(R.id.pokemon_list);
                mPokemonList.setLayoutManager(new GridLayoutManager(this, 3));
                PokeListAdapter pokeListAdapter = new PokeListAdapter(this, gen);
                mPokemonList.setAdapter(pokeListAdapter);
            }
        } else {
            title = getString(R.string.app_name);
        }
        TypefaceUtils.setActionBarTitle(this, title);
    }
}
