package com.mianlabs.pokeluv.ui;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
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

    private Activity mContext;
    private Typeface mCustomFont;

    private int mChosenPokemon;

    @BindView(R.id.pokemon_detail_container)
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
        mContext = getActivity(); // Grabs the context from the parent activity.
        mCustomFont = Typeface.createFromAsset(mContext.getAssets(), mContext.getString(R.string.font_path));
        setCustomTypefaceForViews();
        mContainer.setVisibility(View.INVISIBLE); // Hides the Views until properly set with Pokemon data.

        Bundle bundle = getArguments();
        mChosenPokemon = bundle.getInt(MainActivity.MAIN_KEY, 1);

        if (isNetworkAvailable()) {
            new Thread(new Runnable() { // Background thread for networking requests.
                @Override
                public void run() {
                    Log.d(TAG, "Attempting PokeAPI network request");

                    PokeApi pokeApi = new PokeApiClient();
                    Pokemon pokemon = pokeApi.getPokemon(mChosenPokemon);
                    PokemonSpecies pokemonSpecies = pokeApi.getPokemonSpecies(mChosenPokemon);
                    EvolutionChain evolutionChain =
                            pokeApi.getEvolutionChain(pokemonSpecies.getEvolutionChain().getId());

                    final PokeModel pokeModel = new PokeModel(pokemon, pokemonSpecies, evolutionChain);
                    Log.d(TAG, pokeModel.toString());

                    Handler handler = new Handler(Looper.getMainLooper()); // Grabs UI thread.
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            loadSpriteAndPalettes(pokeModel);
                            setPokemonData(pokeModel);
                            Log.d(TAG, "Pokemon data set");
                            mContainer.setVisibility(View.VISIBLE); // Views are ready to be displayed.
                        }
                    });
                }
            }).start();
        } else {
            TypefaceUtils.displayToast(mContext, getString(R.string.internet_connection_msg), 8);
        }

        return viewRoot;
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

    private void loadSpriteAndPalettes(PokeModel pokeModel) {
        Picasso.with(mContext).load(pokeModel.getSprite())
                .into(mPokemonSprite, PicassoPalette.with(pokeModel.getSprite(), mPokemonSprite)
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

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
