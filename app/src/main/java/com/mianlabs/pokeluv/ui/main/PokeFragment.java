/*
 * Copyright (C) 2016 Damián Adams
 */
package com.mianlabs.pokeluv.ui.main;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.github.florent37.picassopalette.PicassoPalette;
import com.mianlabs.pokeluv.R;
import com.mianlabs.pokeluv.database.PokeCursorManager;
import com.mianlabs.pokeluv.model.PokeModel;
import com.mianlabs.pokeluv.ui.favorites.PokeFavorites;
import com.mianlabs.pokeluv.ui.generations.GenActivity;
import com.mianlabs.pokeluv.utilities.sound.SoundUtils;
import com.mianlabs.pokeluv.utilities.typeface.TypefaceUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.sargunvohra.lib.pokekotlin.client.PokeApi;
import me.sargunvohra.lib.pokekotlin.client.PokeApiClient;
import me.sargunvohra.lib.pokekotlin.model.EvolutionChain;
import me.sargunvohra.lib.pokekotlin.model.Pokemon;
import me.sargunvohra.lib.pokekotlin.model.PokemonSpecies;

/**
 * Fragment that safely queries the API for Pokemon data and displays it to the user.
 * Receives the Pokemon ID to load data for from MainActivity.
 * <p/>
 * Its launching activity must make sure this fragment instance
 * is retained properly across configuration changes through the use
 * of a tag in order to avoid memory leaks.
 * <p/>
 * See: http://www.androiddesignpatterns.com/2013/04/retaining-objects-across-config-changes.html
 */
public class PokeFragment extends Fragment implements PokeCursorManager.LoaderCall {
    private static final String TAG = PokeFragment.class.getSimpleName();
    private static final int NO_INTERNET_MSG_DURATION = 8;

    // Keys for saving state in case of configuration changes.
    private static final String POKE_MODEL_STATE_KEY = "POKE_MODEL";
    private static final String PKMN_CAUGHT_STATE_KEY = "POKEMON_CAUGHT";

    private AppCompatActivity mContext;
    private Typeface mCustomFont;

    @BindView(R.id.poke_fragment_swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.poke_fragment_scroll_view)
    ScrollView mScrollView;

    private final int LOADER_ID = new Random().nextInt();
    private ArrayList<Integer> mListOfFavPokemon; // List of Pokemon data from the db.

    private int mChosenPokemon;
    private boolean mHasPokemonBeenCaught;
    private PokeModel mPokeModel;

    @BindView(R.id.pokemon_sprite)
    ImageView mPokemonSprite;
    @BindView(R.id.pokemon_number_border)
    TextView mPokemonNumBorder;
    @BindView(R.id.pokemon_text_name)
    TextView mPokemonName;
    @BindView(R.id.pokemon_height)
    TextView mPokemonHeight;
    @BindView(R.id.pokemon_weight)
    TextView mPokemonWeight;
    @BindView(R.id.pokemon_types)
    TextView mPokemonTypes;
    @BindView(R.id.pokemon_color)
    TextView mPokemonColor;
    @BindView(R.id.pokemon_shape)
    TextView mPokemonShape;
    @BindView(R.id.pokemon_habitat)
    TextView mPokemonHabitat;
    @BindView(R.id.pokemon_generation)
    TextView mPokemonGeneration;
    @BindView(R.id.pokemon_description)
    TextView mPokemonDescription;
    @BindView(R.id.pokemon_evolutions)
    TextView mPokemonEvolutions;
    @BindView(R.id.pokemon_evo_line)
    TextView mPokemonEvoLine;

    /**
     * This method is called only once when the Fragment is first created.
     * If retained, it will not be called again across configuration changes.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = (AppCompatActivity) getActivity(); // Grabs the context from the parent activity.
        setRetainInstance(true); // Retain this fragment across configuration changes.
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View viewRoot = inflater.inflate(R.layout.fragment_poke, container, false);
        ButterKnife.bind(this, viewRoot);
        setUpSwipeRefreshLayout(mSwipeRefreshLayout, mScrollView);
        mCustomFont = Typeface.createFromAsset(mContext.getAssets(), mContext.getString(R.string.font_path));
        setCustomTypefaceForViews(mCustomFont);
        return viewRoot;
    }

    /**
     * Cleanly sets up the two layout View parent's
     * (SwipeRefreshLayout and ScrollView)
     * scrolling behaviors for the PokeFragment layout.
     * Technique implemented from: http://stackoverflow.com/a/26296897
     */
    private void setUpSwipeRefreshLayout(SwipeRefreshLayout swipeRefreshLayout, ScrollView scrollView) {
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(false);
                // Catches a new Pokemon!
                startActivity(new Intent(mContext, MainActivity.class));
            }
        });
        mScrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                int scrollY = mScrollView.getScrollY();
                if (scrollY == 0)
                    mSwipeRefreshLayout.setEnabled(true);
                else
                    mSwipeRefreshLayout.setEnabled(false);
            }
        });
    }

    /**
     * Sets the custom typeface for all of the applicable views
     * in the layout.
     */
    private void setCustomTypefaceForViews(Typeface customFont) {
        mPokemonNumBorder.setTypeface(customFont);
        mPokemonName.setTypeface(customFont);
        mPokemonHeight.setTypeface(customFont);
        mPokemonWeight.setTypeface(customFont);
        mPokemonTypes.setTypeface(customFont);
        mPokemonColor.setTypeface(customFont);
        mPokemonShape.setTypeface(customFont);
        mPokemonHabitat.setTypeface(customFont);
        mPokemonGeneration.setTypeface(customFont);
        mPokemonDescription.setTypeface(customFont);
        mPokemonEvolutions.setTypeface(customFont);
        mPokemonEvoLine.setTypeface(customFont);
    }

    /**
     * Called after onCreateView has inflated the views.
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Bundle bundle = getArguments();
        mChosenPokemon = bundle.getInt(MainActivity.MAIN_KEY, 1);
        mHasPokemonBeenCaught = bundle.getBoolean(MainActivity.PKMN_CAUGHT_KEY, false);

        if (isNetworkAvailable()) {
            if (savedInstanceState == null) {
                // Creates a background thread when class is first instantiated.
                getPokemonData(mChosenPokemon, mHasPokemonBeenCaught);
            }
        } else // Display "no internet connection found" msg.
            TypefaceUtils.displayToast(mContext,
                    getString(R.string.internet_connection_msg), NO_INTERNET_MSG_DURATION);
    }

    /**
     * Restores and sets the Pokemon data across configuration changes
     * to prevent unnecessary API calls.
     */
    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            Log.d(TAG, "Retrieving saved state Pokemon.");
            mPokeModel = savedInstanceState.getParcelable(POKE_MODEL_STATE_KEY);
            mHasPokemonBeenCaught = savedInstanceState.getBoolean(PKMN_CAUGHT_STATE_KEY);
            if (mPokeModel != null) {
                loadSpriteAndPalettes(mPokeModel.getSprite());
                setPokemonData(mPokeModel);
                displayCaughtMsg(mPokeModel, mHasPokemonBeenCaught);
            } else
                Log.d(TAG, "Waiting for background thread to load Pokemon data.");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Sets the title of the Action Bar.
        TypefaceUtils.setActionBarTitle(mContext, getString(R.string.app_name));

        // Initiates a loader for retrieving data from the db.
        PokeCursorManager pokeCursorManager = new PokeCursorManager(mContext, this);
        if (mContext.getSupportLoaderManager().getLoader(LOADER_ID) == null) {
            mContext.getSupportLoaderManager().initLoader(LOADER_ID, new Bundle(), pokeCursorManager);
        } else {
            mContext.getSupportLoaderManager().restartLoader(LOADER_ID, new Bundle(), pokeCursorManager);
        }
    }

    /**
     * Retrieves the list of favorite Pokemon from the db.
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        Log.d(TAG, "Obtained cursor.");
        mListOfFavPokemon = PokeCursorManager.getPokemonInDb(cursor);
    }


    /**
     * Checks to ensure the user can connect to the network,
     * as per Google's Android Developer guidelines.
     */
    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        // This method requires permission ACCESS_NETWORK_STATE.
        boolean isAvailable = false;
        if (networkInfo != null && networkInfo.isConnected()) {
            // Checks if a network is present and connected.
            isAvailable = true;
        }
        return isAvailable;
    }

    /**
     * Makes an API request to fetch and store the Pokemon data.
     * Then makes the calls to set, load, and display the data on
     * the fragment layout.
     */
    private void getPokemonData(final int chosenPokemon, final boolean hasPokemonBeenCaught) {
        new Thread(new Runnable() { // Background thread for networking requests.
            @Override
            public void run() {
                Log.d(TAG, "Attempting PokeAPI network request.");
                PokeApi pokeApi = new PokeApiClient();
                Pokemon pokemon = pokeApi.getPokemon(chosenPokemon);
                PokemonSpecies pokemonSpecies = pokeApi.getPokemonSpecies(chosenPokemon);
                EvolutionChain evolutionChain =
                        pokeApi.getEvolutionChain(pokemonSpecies.getEvolutionChain().getId());

                mPokeModel = new PokeModel(pokemon, pokemonSpecies, evolutionChain);
                Log.d(TAG, mPokeModel.toString());

                Handler handler = new Handler(Looper.getMainLooper()); // Grabs UI thread.
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (isAdded()) { // If the fragment has not been destroyed by the user (back button).
                            loadSpriteAndPalettes(mPokeModel.getSprite());
                            setPokemonData(mPokeModel);
                            displayCaughtMsg(mPokeModel, hasPokemonBeenCaught);
                            Log.d(TAG, "Pokemon data set.");
                        } else {
                            Log.d(TAG, "PokeFragment is detached.");
                        }
                    }
                });
            }
        }).start();
    }

    /**
     * Sets the Pokemon data from a valid PokeModel object into the
     * layout views and updates their content descriptions.
     */
    private void setPokemonData(PokeModel pokeModel) {
        mPokemonNumBorder.setText(getString(R.string.poke_fragment_num_borders) + pokeModel.getPokedexNum());
        mPokemonNumBorder.setContentDescription(getString(R.string.poke_fragment_num_borders) + pokeModel.getPokedexNum());
        mPokemonName.setText(pokeModel.getName());
        mPokemonName.setContentDescription(pokeModel.getName());
        mPokemonWeight.setText(getString(R.string.poke_fragment_weight) + pokeModel.getWeight());
        mPokemonWeight.setContentDescription(getString(R.string.poke_fragment_weight) + pokeModel.getWeight());
        mPokemonHeight.setText(getString(R.string.poke_fragment_height) + pokeModel.getHeight());
        mPokemonHeight.setContentDescription(getString(R.string.poke_fragment_height) + pokeModel.getHeight());
        mPokemonTypes.setText(PokeModel.formatListToString(pokeModel.getTypes()));
        mPokemonTypes.setContentDescription(PokeModel.formatListToString(pokeModel.getTypes()));
        mPokemonColor.setText(getString(R.string.poke_fragment_color) + pokeModel.getColor());
        mPokemonColor.setContentDescription(getString(R.string.poke_fragment_color) + pokeModel.getColor());
        mPokemonShape.setText(getString(R.string.poke_fragment_shape) + pokeModel.getShape());
        mPokemonShape.setContentDescription(getString(R.string.poke_fragment_shape) + pokeModel.getShape());
        if (pokeModel.getHabitat() != null) {
            mPokemonHabitat.setText(getString(R.string.poke_fragment_habitat) + pokeModel.getHabitat());
            mPokemonHabitat.setContentDescription(getString(R.string.poke_fragment_habitat) + pokeModel.getHabitat());
        }
        mPokemonGeneration.setText(pokeModel.getGeneration().toUpperCase());
        mPokemonGeneration.setContentDescription(pokeModel.getGeneration().toUpperCase());
        mPokemonDescription.setText(pokeModel.getDescription());
        mPokemonDescription.setContentDescription(pokeModel.getDescription());
        mPokemonEvolutions.setText(mContext.getString(R.string.poke_fragment_evolutions_header));
        mPokemonEvolutions.setContentDescription(mContext.getString(R.string.poke_fragment_evolutions_header));
        mPokemonEvoLine.setText(PokeModel.formatListToString(pokeModel.getEvolutions()));
        // Lowercases the evolutions for the content desc. for proper pronunciation.
        mPokemonEvoLine.setContentDescription(PokeModel.formatListToString(pokeModel.getEvolutions()).toLowerCase());
    }

    /**
     * Downloads the Sprite image from the provided URL, loads it into the
     * appropriate ImageView, extracts a color palette from the image,
     * and sets the background and text colors of several views
     * based on it.
     */
    private void loadSpriteAndPalettes(String pokemonSpriteURL) {
        Picasso.with(mContext).load(pokemonSpriteURL)
                .into(mPokemonSprite, PicassoPalette.with(pokemonSpriteURL, mPokemonSprite)
                        .use(PicassoPalette.Profile.VIBRANT)
                        .intoBackground(mPokemonSprite) // Background color for Sprite.
                        .intoTextColor(mPokemonNumBorder, PicassoPalette.Swatch.BODY_TEXT_COLOR) // Text color for Number.
                        .use(PicassoPalette.Profile.VIBRANT_LIGHT)
                        .intoBackground(mPokemonNumBorder) // Background color for Number.
                        .intoBackground(mPokemonGeneration) // Background color for Generation.
                        .intoBackground(mPokemonEvolutions) // Background color for Evolutions.
                        .use(PicassoPalette.Profile.MUTED_LIGHT)
                        .intoBackground(mPokemonTypes) // Background color for Types.
                );
    }

    /**
     * If the Pokemon has been "caught"
     * it plays a sound and displays a message to let the user know.
     */
    private void displayCaughtMsg(PokeModel pokeModel, boolean hasPokemonBeenCaught) {
        if (hasPokemonBeenCaught) {
            SoundUtils.playPokemonCaughtSound(mContext);
            TypefaceUtils.displayToast(mContext,
                    pokeModel.getName().toUpperCase() + " was caught!",
                    TypefaceUtils.TOAST_SHORT_DURATION);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(POKE_MODEL_STATE_KEY, mPokeModel);
        outState.putBoolean(PKMN_CAUGHT_STATE_KEY, mHasPokemonBeenCaught);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu, menu);
        TypefaceUtils.setActionBarOptionsText(mContext, menu);
        TypefaceUtils.setActionBarTitle(mContext, getString(R.string.app_name));
        // Parent activity must call to set the custom action bar title
        // on its onCreateOptionsMenu method to preserve the custom
        // font across retained configuration changes.
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        SoundUtils.playMenuItemSound(mContext); // Plays the menu sound.
        switch (item.getItemId()) {
            case R.id.menu_more_pokemon:
                startActivity(new Intent(mContext, GenActivity.class));
                return true;
            case R.id.menu_catch_pokemon:
                startActivity(new Intent(mContext, MainActivity.class));
                return true;
            case R.id.menu_add_to_favs:
                addPokemonToFavs(mPokeModel, mListOfFavPokemon);
                return true;
            case R.id.menu_favorites:
                startActivity(new Intent(mContext, PokeFavorites.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Adds the Pokemon to favorites if applicable
     * and displays a message to the user.
     */
    private void addPokemonToFavs(PokeModel pokeModel, ArrayList<Integer> listOfFavPokemon) {
        if (pokeModel != null && listOfFavPokemon != null) {
            int pokeNum = pokeModel.getPokedexNum();
            if (listOfFavPokemon.contains(pokeNum))  // If already a favorite.
                TypefaceUtils.displayToast(mContext, getString(R.string.redundant_fav_pokemon_msg),
                        TypefaceUtils.TOAST_SHORT_DURATION);
            else {
                PokeCursorManager.insertPokemonInDb(mContext, pokeNum);
                TypefaceUtils.displayToast(mContext, getString(R.string.add_pokemon_to_favs_msg),
                        TypefaceUtils.TOAST_SHORT_DURATION);
            }
        }
    }
}
