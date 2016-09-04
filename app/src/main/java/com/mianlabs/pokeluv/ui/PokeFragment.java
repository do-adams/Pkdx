package com.mianlabs.pokeluv.ui;

import android.app.Fragment;
import android.content.Context;
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
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.github.florent37.picassopalette.PicassoPalette;
import com.mianlabs.pokeluv.R;
import com.mianlabs.pokeluv.model.PokeModel;
import com.mianlabs.pokeluv.utilities.TypefaceUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.sargunvohra.lib.pokekotlin.client.PokeApi;
import me.sargunvohra.lib.pokekotlin.client.PokeApiClient;
import me.sargunvohra.lib.pokekotlin.model.EvolutionChain;
import me.sargunvohra.lib.pokekotlin.model.Pokemon;
import me.sargunvohra.lib.pokekotlin.model.PokemonSpecies;

// TODO: Add documentation.
public class PokeFragment extends Fragment {
    private static final String TAG = PokeFragment.class.getSimpleName();

    // Keys for saving state in case of configuration changes.
    private static final String POKE_MODEL_STATE_KEY = "POKE_MODEL";
    private static final String POKEMON_DAY_STATE_KEY = "POKEMON_DAY";

    private AppCompatActivity mContext;
    private Typeface mCustomFont;

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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Retain this fragment across configuration changes.
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View viewRoot = inflater.inflate(R.layout.fragment_poke, container, false);
        ButterKnife.bind(this, viewRoot);
        mContext = (AppCompatActivity) getActivity(); // Grabs the context from the parent activity.
        mCustomFont = Typeface.createFromAsset(mContext.getAssets(), mContext.getString(R.string.font_path));
        setCustomTypefaceForViews();
        mContainer.setVisibility(View.INVISIBLE); // Hides the Views until properly set with Pokemon data.

        Bundle bundle = getArguments();
        final int chosenPokemon = bundle.getInt(MainActivity.MAIN_KEY, 1);
        mIsPokemonOfTheDay = bundle.getBoolean(MainActivity.POKEMON_OF_THE_DAY, false);

        if (savedInstanceState == null) {
            if (isNetworkAvailable()) {
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
                                displayViewsAfterDataIsSet();
                                Log.d(TAG, "Pokemon data set");
                            }
                        });
                    }
                }).start();
            } else {
                // Display "no internet connection found" msg.
                TypefaceUtils.displayToast(mContext, getString(R.string.internet_connection_msg), 8);
            }
        }
        return viewRoot;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Sets the title of the Action Bar.
        TypefaceUtils.setActionBarTitle(mContext, getString(R.string.app_name));
    }

    private void setCustomTypefaceForViews() {
        mPokemonNumBorder.setTypeface(mCustomFont);
        mPokemonName.setTypeface(mCustomFont);
        mPokemonHeight.setTypeface(mCustomFont);
        mPokemonWeight.setTypeface(mCustomFont);
        mPokemonTypes.setTypeface(mCustomFont);
        mPokemonColor.setTypeface(mCustomFont);
        mPokemonShape.setTypeface(mCustomFont);
        mPokemonHabitat.setTypeface(mCustomFont);
        mPokemonGeneration.setTypeface(mCustomFont);
        mPokemonDescription.setTypeface(mCustomFont);
        mPokemonEvolutions.setTypeface(mCustomFont);
        mPokemonEvoLine.setTypeface(mCustomFont);
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            Log.d(TAG, "Retrieving saved state Pokemon");
            mPokeModel = savedInstanceState.getParcelable(POKE_MODEL_STATE_KEY);
            mIsPokemonOfTheDay = savedInstanceState.getBoolean(POKEMON_DAY_STATE_KEY);
            loadSpriteAndPalettes(mPokeModel.getSprite());
            setPokemonData(mPokeModel);
            displayViewsAfterDataIsSet();
        }
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

    private void setPokemonData(PokeModel pokeModel) {
        mPokemonNumBorder.setText("No. " + pokeModel.getPokedexNum());
        mPokemonName.setText(pokeModel.getName());
        mPokemonWeight.setText("Weight: " + pokeModel.getWeight());
        mPokemonHeight.setText("Height: " + pokeModel.getHeight());
        mPokemonTypes.setText(formatListToString(pokeModel.getTypes()));
        mPokemonColor.setText("Color: " + pokeModel.getColor());
        mPokemonShape.setText("Shape: " + pokeModel.getShape());
        if (pokeModel.getHabitat() != null)
            mPokemonHabitat.setText("Habitat: " + pokeModel.getHabitat());
        mPokemonGeneration.setText(pokeModel.getGeneration().toUpperCase());
        mPokemonDescription.setText(pokeModel.getDescription());
        mPokemonEvoLine.setText(formatListToString(pokeModel.getEvolutions()));
    }

    private String formatListToString(List<String> list) {
        String result = "";
        for (String s : list)
            result += s + "\t\t";
        result = result.trim();
        return result.toUpperCase();
    }

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

    private void displayViewsAfterDataIsSet() {
        mContainer.setVisibility(View.VISIBLE); // Views are ready to be displayed.
        if (mIsPokemonOfTheDay)
            TypefaceUtils.displayToastTop(mContext, mContext.getString(R.string.pokemon_of_the_day_msg), 2,
                    (int) mContext.getResources().getDimension(R.dimen.daily_toast_y_offset));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(POKE_MODEL_STATE_KEY, mPokeModel);
        outState.putBoolean(POKEMON_DAY_STATE_KEY, mIsPokemonOfTheDay);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
