package com.mianlabs.pokeluv.ui;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.mianlabs.pokeluv.R;
import com.mianlabs.pokeluv.model.PokeModel;
import com.mianlabs.pokeluv.ui.generations.GenActivity;
import com.mianlabs.pokeluv.ui.generations.PokeList;
import com.mianlabs.pokeluv.utilities.TypefaceUtils;

import java.util.Random;

public class MainActivity extends AppCompatActivity {
    public static final String MAIN_KEY = "MainActivity";
    private static final String TAG_POKE_FRAGMENT = "PKF";

    private PokeFragment mPokeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TypefaceUtils.setActionBarTitle(this, getString(R.string.app_name));

        // PokeApi can make calls to get Pokemon #s 1 to 721 (National Pokedex Number)
        int randPkmn = new Random().nextInt(PokeModel.NUM_OF_POKEMON + 1);

        Intent intent = getIntent();
        Bundle bundle = new Bundle();
        bundle.putInt(MAIN_KEY, randPkmn);
        if (intent != null) {
            if (intent.hasExtra(PokeList.POKE_LIST_KEY)) {
                int pokeNum = intent.getIntExtra(PokeList.POKE_LIST_KEY, 1);
                bundle.putInt(MAIN_KEY, pokeNum);
            }
        }

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
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        TypefaceUtils.setActionBarOptionsText(this, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_more_pokemon:
                startActivity(new Intent(this, GenActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
