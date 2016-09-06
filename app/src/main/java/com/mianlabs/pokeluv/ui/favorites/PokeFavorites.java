package com.mianlabs.pokeluv.ui.favorites;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.mianlabs.pokeluv.R;
import com.mianlabs.pokeluv.adapters.PokeListAdapter;
import com.mianlabs.pokeluv.database.PokeCursorManager;
import com.mianlabs.pokeluv.ui.generations.PokeList;
import com.mianlabs.pokeluv.utilities.TypefaceUtils;

import java.util.ArrayList;
import java.util.Random;

/**
 * Activity for displaying the user's favorite Pokemon.
 */
public class PokeFavorites extends AppCompatActivity implements PokeCursorManager.LoaderCall {
    private static final String LIST_STATE_KEY = "LIST";
    private final int LOADER_ID = new Random().nextInt();

    private ArrayList<Integer> mPokemonNumbers;
    private RecyclerView mPokemonList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_poke_list);
        mPokemonList = (RecyclerView) findViewById(R.id.pokemon_list);

        if (savedInstanceState == null)
            getSupportLoaderManager().initLoader(LOADER_ID, new Bundle(), new PokeCursorManager(this, this));
        else { // No need to query the cursor again across configuration changes.
            mPokemonNumbers = savedInstanceState.getIntegerArrayList(LIST_STATE_KEY);
            setPokemonList(mPokemonNumbers, mPokemonList);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        TypefaceUtils.setActionBarTitle(this, getString(R.string.favorites_name));
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mPokemonNumbers = PokeCursorManager.getPokemonInDb(cursor);
        setPokemonList(mPokemonNumbers, mPokemonList);

    }

    private void setPokemonList(ArrayList<Integer> pokemonNumbers, RecyclerView pokemonList) {
        int[] pokemon = new int[pokemonNumbers.size()];
        int i = pokemonNumbers.size() - 1; // Last element.
        for (int z : pokemonNumbers)
            pokemon[i--] = z; // Display more recent Pokemon first.
        pokemonList.setAdapter(new PokeListAdapter(this, pokemon));
        pokemonList.setLayoutManager(new GridLayoutManager(this, PokeList.NUMBER_OF_POKEMON_PER_ROW));
        pokemonList.setHasFixedSize(true);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putIntegerArrayList(LIST_STATE_KEY, mPokemonNumbers);
    }
}
