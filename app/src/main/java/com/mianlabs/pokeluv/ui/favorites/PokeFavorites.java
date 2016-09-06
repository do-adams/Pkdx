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
import com.mianlabs.pokeluv.utilities.TypefaceUtils;

import java.util.ArrayList;
import java.util.Random;

/**
 * Activity for displaying the user's favorite Pokemon.
 */
public class PokeFavorites extends AppCompatActivity implements PokeCursorManager.LoaderCall {
    private final int LOADER_ID = new Random().nextInt();

    private RecyclerView mPokemonList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_poke_list);
        mPokemonList = (RecyclerView) findViewById(R.id.pokemon_list);
        getSupportLoaderManager().initLoader(LOADER_ID, new Bundle(), new PokeCursorManager(this, this));
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        ArrayList<Integer> list = PokeCursorManager.getPokemonInDb(cursor);
        int[] pokemon = new int[list.size()];
        int i = list.size() - 1; // Last element.
        for (int z : list)
            pokemon[i--] = z; // Display more recent Pokemon first.
        mPokemonList.setAdapter(new PokeListAdapter(this, pokemon));
        mPokemonList.setLayoutManager(new GridLayoutManager(this, 3));
        mPokemonList.setHasFixedSize(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        TypefaceUtils.setActionBarTitle(this, getString(R.string.favorites_name));
    }
}
