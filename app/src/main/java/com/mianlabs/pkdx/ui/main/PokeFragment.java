/*
 * Copyright (C) 2016 Damián Adams
 */
package com.mianlabs.pkdx.ui.main;

import android.app.Fragment;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.AnyRes;
import android.support.annotation.NonNull;
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
import com.google.gson.Gson;
import com.mianlabs.localpokeapi.LocalPokeApi;
import com.mianlabs.localpokeapi.PokeModel;
import com.mianlabs.pkdx.R;
import com.mianlabs.pkdx.database.PokeCursorManager;
import com.mianlabs.pkdx.database.PokeDBContract;
import com.mianlabs.pkdx.ui.about.AboutActivity;
import com.mianlabs.pkdx.ui.favorites.PokeFavorites;
import com.mianlabs.pkdx.ui.generations.GenActivity;
import com.mianlabs.pkdx.utilities.PokePicker;
import com.mianlabs.pkdx.utilities.sound.SoundUtils;
import com.mianlabs.pkdx.utilities.typeface.TypefaceUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Fragment that safely queries the API for Pokemon data and displays it to the user.
 * Receives the Pokemon ID to load data for from MainActivity.
 * <p/>
 * Its launching activity must make sure this fragment instance
 * is retained properly across configuration changes through the use
 * of a tag in order to avoid memory leaks and make sure this class
 * retrieves data properly.
 * <p/>
 * See: http://www.androiddesignpatterns.com/2013/04/retaining-objects-across-config-changes.html
 */
public class PokeFragment extends Fragment implements PokeCursorManager.LoaderCall {
    private static final String TAG = PokeFragment.class.getSimpleName();

    // Keys for saving state in case of configuration changes.
    private static final String POKE_MODEL_STATE_KEY = "POKE_MODEL";

    private AppCompatActivity mContext;
    private Typeface mCustomFont;

    // Parent container layouts.
    @BindView(R.id.poke_fragment_swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.poke_fragment_scroll_view)
    ScrollView mScrollView;

    private final int LOADER_ID = new Random().nextInt();
    private ArrayList<Integer> mListOfFavPokemon; // List of favorite Pokemon data from the db.

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
        setRetainInstance(true); // Retain this fragment across configuration changes.
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View viewRoot = inflater.inflate(R.layout.fragment_poke, container, false);
        ButterKnife.bind(this, viewRoot);
        mContext = (AppCompatActivity) getActivity(); // Grabs the context from the current parent activity.
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

        if (savedInstanceState == null) {
            // Creates a background thread to fetch and set API data.
            getPokemonData(mChosenPokemon, mHasPokemonBeenCaught);
        }
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
            mPokeModel = new Gson()
                    .fromJson(savedInstanceState.getString(POKE_MODEL_STATE_KEY), PokeModel.class);
            if (mPokeModel != null) {
                loadSpriteAndPalettes(mPokeModel);
                setPokemonData(mPokeModel);
            } else {
                // BG thread is tied to the retained instance of this Fragment.
                Log.d(TAG, "Waiting for background thread to load Pokemon data.");
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Sets the title of the Action Bar.
        TypefaceUtils.setActionBarTitle(mContext, getString(R.string.app_name));

        // Initiates or restarts a loader for retrieving data from the db (resists config changes).
        PokeCursorManager pokeCursorManager = new PokeCursorManager(mContext, this,
                PokeDBContract.FavoritePokemonEntry.TABLE_NAME);
        if (mContext.getSupportLoaderManager().getLoader(LOADER_ID) == null) {
            mContext.getSupportLoaderManager().initLoader(LOADER_ID, new Bundle(), pokeCursorManager);
        } else {
            mContext.getSupportLoaderManager().restartLoader(LOADER_ID, new Bundle(), pokeCursorManager);
        }
    }

    /**
     * Retrieves the list of favorite Pokemon from the db using the loader.
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        Log.d(TAG, "Obtained cursor.");
        // If the fragment has not been destroyed by the user (back button).
        if (isAdded()) {
            mListOfFavPokemon = PokeCursorManager.getPokemonInDb(cursor,
                    PokeDBContract.FavoritePokemonEntry.TABLE_NAME,
                    PokeDBContract.FavoritePokemonEntry.COLUMN_NUMBER);
        }
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
                Log.d(TAG, "Fetching PokeAPI data.");
                mPokeModel = LocalPokeApi.getPokemonData(chosenPokemon);
                Log.d(TAG, mPokeModel.toString());

                Handler handler = new Handler(Looper.getMainLooper()); // Grabs UI thread.
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        // If the fragment has not been destroyed by the user (back button).
                        if (isAdded()) {
                            if (mPokeModel != null) {
                                loadSpriteAndPalettes(mPokeModel);
                                setPokemonData(mPokeModel);
                                displayCaughtMsg(mPokeModel, hasPokemonBeenCaught);
                                Log.d(TAG, "Pokemon data set.");
                            } else {
                                Log.e(TAG, "Error while retrieving data. PokeModel is null");
                            }
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
     * layout views.
     */
    private void setPokemonData(PokeModel pokeModel) {
        mPokemonNumBorder.setText(getString(R.string.poke_fragment_num_borders) + pokeModel.getPokedexNum());
        mPokemonName.setText(pokeModel.getName());
        mPokemonHeight.setText(getString(R.string.poke_fragment_height) + pokeModel.getHeight());
        mPokemonWeight.setText(getString(R.string.poke_fragment_weight) + pokeModel.getWeight());
        mPokemonTypes.setText(PokeModel.formatListToString(pokeModel.getTypes()));
        mPokemonColor.setText(getString(R.string.poke_fragment_color) + pokeModel.getColor());
        mPokemonShape.setText(getString(R.string.poke_fragment_shape) + pokeModel.getShape());
        if (pokeModel.getHabitat() != null)
            mPokemonHabitat.setText(getString(R.string.poke_fragment_habitat) + pokeModel.getHabitat());
        mPokemonGeneration.setText(pokeModel.getGeneration().toUpperCase());
        mPokemonDescription.setText(pokeModel.getDescription());
        mPokemonEvolutions.setText(mContext.getString(R.string.poke_fragment_evolutions_header));
        mPokemonEvoLine.setText(PokeModel.formatListToString(pokeModel.getEvolutions()));
        // Lowercases the evolutions for the content desc. for proper pronunciation.
        mPokemonEvoLine.setContentDescription(PokeModel.formatListToString(pokeModel.getEvolutions()).toLowerCase());
    }

    /**
     * Returns a URI for any given drawable resource id
     * present in this app.
     * Code implemented from: http://stackoverflow.com/a/36062748
     */
    private final Uri getUriToDrawable(@NonNull Context context, @AnyRes int drawableId) {
        Uri imageUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE +
                "://" + context.getResources().getResourcePackageName(drawableId)
                + '/' + context.getResources().getResourceTypeName(drawableId)
                + '/' + context.getResources().getResourceEntryName(drawableId));
        return imageUri;
    }

    /**
     * Downloads the Sprite image from the provided URL, loads it into the
     * appropriate ImageView, extracts a color palette from the image,
     * and sets the background and text colors of several views
     * based on it.
     */
    private void loadSpriteAndPalettes(PokeModel pokeModel) {
        int resId = PokePicker.GenerationNumbers
                .getDrawableResourceFromNumber(mContext, pokeModel.getPokedexNum());
        String url = getUriToDrawable(mContext, resId).toString(); // Gets the internal URL to the drawable.

        Picasso.with(mContext).load(resId)
                .into(mPokemonSprite, PicassoPalette.with(url, mPokemonSprite)
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

    /**
     * Saves Fragment state across configuration changes.
     * Stores the PokeModel object instance.
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(POKE_MODEL_STATE_KEY, new Gson().toJson(mPokeModel));
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
        switch (item.getItemId()) {
            case R.id.menu_more_pokemon:
                SoundUtils.playMenuItemSound(mContext);
                startActivity(new Intent(mContext, GenActivity.class));
                return true;
            case R.id.menu_catch_pokemon:
                SoundUtils.playMenuItemSound(mContext);
                startActivity(new Intent(mContext, MainActivity.class));
                return true;
            case R.id.menu_add_to_favs:
                SoundUtils.playMenuItemSound(mContext);
                addPokemonToFavs(mPokeModel, mListOfFavPokemon);
                return true;
            case R.id.menu_remove_from_favs:
                SoundUtils.playMenuItemSound(mContext);
                removePokemonFromFavs(mPokeModel, mListOfFavPokemon, mChosenPokemon);
                return true;
            case R.id.menu_favorites:
                SoundUtils.playFavoritesSound(mContext);
                startActivity(new Intent(mContext, PokeFavorites.class));
                return true;
            case R.id.menu_about:
                SoundUtils.playMenuItemSound(mContext);
                startActivity(new Intent(mContext, AboutActivity.class));
            default:
                return false;
        }
    }

    /**
     * Adds the Pokemon to favorites if applicable
     * and displays a message to the user.
     */
    private void addPokemonToFavs(PokeModel pokeModel, ArrayList<Integer> listOfFavPokemon) {
        if (pokeModel != null && listOfFavPokemon != null) {
            int pokeNum = pokeModel.getPokedexNum();

            if (listOfFavPokemon.contains(pokeNum)) { // If already a favorite.
                TypefaceUtils.displayToast(mContext, getString(R.string.redundant_fav_pokemon_msg),
                        TypefaceUtils.TOAST_SHORT_DURATION);
            } else {
                PokeCursorManager.insertPokemonInDb(mContext, pokeNum,
                        PokeDBContract.FavoritePokemonEntry.TABLE_NAME,
                        PokeDBContract.FavoritePokemonEntry.COLUMN_NUMBER);
                TypefaceUtils.displayToast(mContext, getString(R.string.add_pokemon_to_favs_msg),
                        TypefaceUtils.TOAST_SHORT_DURATION);
            }
        }
    }

    /**
     * Removes a Pokemon from the favorites table in the db.
     */
    private void removePokemonFromFavs(PokeModel pokeModel, ArrayList<Integer> listOfFavPokemon,
                                       int chosenPokemon) {
        if (pokeModel != null && listOfFavPokemon != null) {
            if (mListOfFavPokemon.contains(chosenPokemon)) {
                mContext.getContentResolver()
                        .delete(PokeDBContract.FavoritePokemonEntry.CONTENT_URI,
                                PokeDBContract.FavoritePokemonEntry.COLUMN_NUMBER + " = " + chosenPokemon,
                                null);
                TypefaceUtils.displayToast(mContext,
                        getString(R.string.remove_fav_pokemon_msg), TypefaceUtils.TOAST_SHORT_DURATION);
            } else {
                TypefaceUtils.displayToast(mContext,
                        getString(R.string.redundant_remove_fav_pokemon_msg),
                        TypefaceUtils.TOAST_SHORT_DURATION);
            }
        }
    }
}
