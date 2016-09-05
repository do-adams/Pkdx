/*
 * Copyright (C) 2016 Damián Adams
 */
package com.mianlabs.pokeluv.ui;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.github.florent37.picassopalette.PicassoPalette;
import com.mianlabs.pokeluv.R;
import com.mianlabs.pokeluv.model.PokeModel;
import com.mianlabs.pokeluv.ui.generations.GenActivity;
import com.mianlabs.pokeluv.utilities.TypefaceUtils;
import com.squareup.picasso.Picasso;

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
 * <p>
 * Its launching activity must make sure this fragment instance
 * is retained properly across configuration changes through the use
 * of a tag in order to avoid memory leaks.
 * <p>
 * See: http://www.androiddesignpatterns.com/2013/04/retaining-objects-across-config-changes.html
 */
public class PokeFragment extends Fragment {
    private static final String TAG = PokeFragment.class.getSimpleName();
    private static final int NO_INTERNET_MSG_DURATION = 8;
    private static final int PKM_DAY_MSG_DURATION = 2;

    // Keys for saving state in case of configuration changes.
    private static final String POKE_MODEL_STATE_KEY = "POKE_MODEL";
    private static final String POKEMON_OF_THE_DAY_STATE_KEY = "POKEMON_DAY";

    private AppCompatActivity mContext;
    private Typeface mCustomFont;

    // Represents whether this fragment has been instantiated or retained.
    private boolean mHasBeenRetained;

    private int mChosenPokemon;
    private boolean mIsPokemonOfTheDay;
    private PokeModel mPokeModel;

    @BindView(R.id.poke_fragment_container)
    ScrollView mContainer;
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
        mCustomFont = Typeface.createFromAsset(mContext.getAssets(), mContext.getString(R.string.font_path));
        setCustomTypefaceForViews(mCustomFont);
        return viewRoot;
    }

    /**
     * Called after onCreateView has inflated the views.
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Bundle bundle = getArguments();
        mChosenPokemon = bundle.getInt(MainActivity.MAIN_KEY, 1);
        mIsPokemonOfTheDay = bundle.getBoolean(MainActivity.POKEMON_OF_THE_DAY_KEY, false);

        if (isNetworkAvailable()) {
            if (!mHasBeenRetained) {  // Creates a background thread.
                getPokemonData(mChosenPokemon, mIsPokemonOfTheDay, mContainer);
                mHasBeenRetained = true;
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
            Log.d(TAG, "Retrieving saved state Pokemon");
            mPokeModel = savedInstanceState.getParcelable(POKE_MODEL_STATE_KEY);
            mIsPokemonOfTheDay = savedInstanceState.getBoolean(POKEMON_OF_THE_DAY_STATE_KEY);
            if (mPokeModel != null) {
                loadSpriteAndPalettes(mPokeModel.getSprite());
                setPokemonData(mPokeModel);
                displayDailyMsg(mIsPokemonOfTheDay);
            } else
                Log.d(TAG, "Waiting for background thread to load Pokemon data");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Sets the title of the Action Bar.
        TypefaceUtils.setActionBarTitle(mContext, getString(R.string.app_name));
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
    private void getPokemonData(final int chosenPokemon, final boolean isPokemonOfTheDay,
                                final View container) {
        new Thread(new Runnable() { // Background thread for networking requests.
            @Override
            public void run() {
                Log.d(TAG, "Attempting PokeAPI network request");
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
                        loadSpriteAndPalettes(mPokeModel.getSprite());
                        setPokemonData(mPokeModel);
                        displayDailyMsg(isPokemonOfTheDay);
                        Log.d(TAG, "Pokemon data set");
                    }
                });
            }
        }).start();
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
     * Sets the Pokemon data from a valid PokeModel object into the
     * layout views.
     */
    private void setPokemonData(PokeModel pokeModel) {
        mPokemonNumBorder.setText("No. " + pokeModel.getPokedexNum());
        mPokemonName.setText(pokeModel.getName());
        mPokemonWeight.setText("Weight: " + pokeModel.getWeight());
        mPokemonHeight.setText("Height: " + pokeModel.getHeight());
        mPokemonTypes.setText(PokeModel.formatListToString(pokeModel.getTypes()));
        mPokemonColor.setText("Color: " + pokeModel.getColor());
        mPokemonShape.setText("Shape: " + pokeModel.getShape());
        if (pokeModel.getHabitat() != null)
            mPokemonHabitat.setText("Habitat: " + pokeModel.getHabitat());
        mPokemonGeneration.setText(pokeModel.getGeneration().toUpperCase());
        mPokemonDescription.setText(pokeModel.getDescription());
        mPokemonEvolutions.setText(mContext.getString(R.string.poke_fragment_evolutions_header));
        mPokemonEvoLine.setText(PokeModel.formatListToString(pokeModel.getEvolutions()));
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
     * To be used when all the calls for loading and setting PokeModel data
     * into views have been made. Signals for the parent layout of the PokeFragment
     * to become visible. If the Pokemon being displayed is the Pokemon of the Day,
     * it signals to display a toast to let the user know.
     */
    private void displayDailyMsg(boolean isPokemonOfTheDay) {
        if (isPokemonOfTheDay)
            TypefaceUtils.displayToastTop(mContext,
                    mContext.getString(R.string.pokemon_of_the_day_msg), PKM_DAY_MSG_DURATION,
                    (int) mContext.getResources().getDimension(R.dimen.daily_toast_y_offset));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(POKE_MODEL_STATE_KEY, mPokeModel);
        outState.putBoolean(POKEMON_OF_THE_DAY_STATE_KEY, mIsPokemonOfTheDay);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu, menu);
        TypefaceUtils.setActionBarOptionsText(mContext, menu);
        // Parent activity must call to set the action bar title on its onCreateOptionsMenu method.
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_more_pokemon:
                startActivity(new Intent(mContext, GenActivity.class));
                return true;
            case R.id.menu_pokemon_of_the_day:
                startActivity(new Intent(mContext, MainActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
