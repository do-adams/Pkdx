package com.mianlabs.pokeluv;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
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
import android.widget.TextView;

import com.github.florent37.picassopalette.PicassoPalette;
import com.mianlabs.pokeluv.model.PokeModel;
import com.squareup.picasso.Picasso;

import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.sargunvohra.lib.pokekotlin.client.PokeApi;
import me.sargunvohra.lib.pokekotlin.client.PokeApiClient;
import me.sargunvohra.lib.pokekotlin.model.EvolutionChain;
import me.sargunvohra.lib.pokekotlin.model.Pokemon;
import me.sargunvohra.lib.pokekotlin.model.PokemonSpecies;

public class PokeFragment extends Fragment {
    private static final String TAG = PokeFragment.class.getSimpleName();
    private Activity mContext;

    @BindView(R.id.pokemon_sprite)
    ImageView mPokemonSprite;
    @BindView(R.id.pokemon_number_border)
    TextView mPokemonNumBorder;
    @BindView(R.id.pokemon_text_name)
    TextView mPokemonName;
    @BindView(R.id.pokemon_weight)
    TextView mPokemonWeight;
    @BindView(R.id.pokemon_height)
    TextView mPokemonHeight;
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

        if (isNetworkAvailable()) {
            new Thread(new Runnable() { // Background thread for networking requests.
                @Override
                public void run() {
                    Log.d(TAG, "Attempting PokeAPI network request");
                    // PokeApi can make calls to get Pokemon #s 1 to 721 (National Pokedex Number)
                    int randPkmn = new Random().nextInt(PokeModel.NUM_OF_POKEMON + 1);

                    PokeApi pokeApi = new PokeApiClient();
                    final Pokemon pokemon = pokeApi.getPokemon(randPkmn);
                    final PokemonSpecies pokemonSpecies = pokeApi.getPokemonSpecies(randPkmn);
                    EvolutionChain evolutionChain =
                            pokeApi.getEvolutionChain(pokemonSpecies.getEvolutionChain().getId());

                    final PokeModel pokeModel = new PokeModel(pokemon, pokemonSpecies, evolutionChain);
                    Log.d(TAG, pokeModel.toString());

                    Handler handler = new Handler(Looper.getMainLooper()); // grabs UI thread.
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            loadSpriteAndPalettes(pokeModel);
                            setPokemonData(pokeModel);
                            Log.d(TAG, "Pokemon data set");
                        }
                    });
                }
            }).start();
        }
        return viewRoot;
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

        // Get the Pokemon type or types from the List.
        String typing = pokeModel.getTypes().toString();
        String types = "";
        for (int i = 0; i < typing.length(); i++) {
            char ch = typing.charAt(i);
            if (ch != '[' && ch != ']') {
                if (ch == ',')
                    types += "\t\t"; // Put space between multiple types.
                else
                    types += Character.toUpperCase(ch);
            }
        }
        mPokemonTypes.setText(types);
        mPokemonColor.setText("Color: " + pokeModel.getColor());
        mPokemonShape.setText("Shape: " + pokeModel.getShape());
        if (pokeModel.getHabitat() != null)
            mPokemonHabitat.setText("Habitat: " + pokeModel.getHabitat());
        mPokemonGeneration.setText(pokeModel.getGeneration().toUpperCase());
        mPokemonDescription.setText(pokeModel.getDescription());
    }

    private void loadSpriteAndPalettes(PokeModel pokeModel) {
        Picasso.with(mContext).load(pokeModel.getSprite())
                .into(mPokemonSprite, PicassoPalette.with(pokeModel.getSprite(), mPokemonSprite)
                        .use(PicassoPalette.Profile.VIBRANT)
                        .intoBackground(mPokemonSprite) // Background color for Sprite.
                        .intoTextColor(mPokemonNumBorder, PicassoPalette.Swatch.BODY_TEXT_COLOR) // Text color for Number.
                        .intoTextColor(mPokemonTypes, PicassoPalette.Swatch.BODY_TEXT_COLOR) // Text color for Types.
                        .intoTextColor(mPokemonGeneration, PicassoPalette.Swatch.BODY_TEXT_COLOR) // Text color for Generation.
                        .use(PicassoPalette.Profile.VIBRANT_LIGHT)
                        .intoBackground(mPokemonNumBorder) // Background color for Number.
                        .intoBackground(mPokemonGeneration) // Background color for Generation.
                        .use(PicassoPalette.Profile.MUTED_LIGHT)
                        .intoBackground(mPokemonTypes) // Background color for Types.
                );
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
