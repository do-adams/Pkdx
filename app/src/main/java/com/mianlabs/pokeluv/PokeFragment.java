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
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.sargunvohra.lib.pokekotlin.client.PokeApi;
import me.sargunvohra.lib.pokekotlin.client.PokeApiClient;
import me.sargunvohra.lib.pokekotlin.model.ChainLink;
import me.sargunvohra.lib.pokekotlin.model.EvolutionChain;
import me.sargunvohra.lib.pokekotlin.model.Genus;
import me.sargunvohra.lib.pokekotlin.model.Pokemon;
import me.sargunvohra.lib.pokekotlin.model.PokemonSpecies;
import me.sargunvohra.lib.pokekotlin.model.PokemonSpeciesFlavorText;
import me.sargunvohra.lib.pokekotlin.model.PokemonType;

public class PokeFragment extends Fragment {
    private static final String TAG = PokeFragment.class.getSimpleName();
    private Activity mContext;

    @BindView(R.id.pokemon)
    TextView mPokemonTextView;

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
            Log.d(TAG, "Making network request");
            new Thread(new Runnable() { // Makes background thread for networking request.
                @Override
                public void run() {
                    // PokeApi can make calls to get Pokémon #s 1 to 721 (National Pokédex Number)
                    PokeApi pokeApi = new PokeApiClient();
                    final Pokemon pokemon = pokeApi.getPokemon(1);
                    final PokemonSpecies pokemonSpecies = pokeApi.getPokemonSpecies(1);

                    final String LANG = "en"; // Language for retrieving Pokémon data.

                    // Pokémon Data

                    String name = pokemon.getName();
                    pokemon.getId(); // Pokémon National Pokedex Num.
                    Log.d(TAG, "Pokémon: " + pokemon.getName() + "# " + pokemon.getId());
                    pokemon.getHeight(); // Pokémon height in decimeters.
                    pokemon.getWeight(); // Pokémon weight in hectograms.

                    pokemon.getTypes().size(); // Number of types for the Pokémon.
                    for(PokemonType t : pokemon.getTypes()) {
                        t.getType().getName(); // Typing of Pokémon.
                        Log.d(TAG, t.getType().getName());
                    }

                    // Pokémon Species Data

                    pokemonSpecies.getColor().getName(); // Pokémon color.
                    pokemonSpecies.getShape().getName(); // Pokémon shape.

                    if (pokemonSpecies.getEvolvesFromSpecies() != null) {
                        pokemonSpecies.getEvolvesFromSpecies().getName(); // TODO: Test for Pokémon evolutions.
                        Log.d(TAG, "Evolves from: " + pokemonSpecies.getEvolvesFromSpecies().getName());
                    }

                    EvolutionChain evolutionChain = pokeApi.getEvolutionChain(pokemonSpecies.getEvolutionChain().getId());
                    Log.d(TAG, "Evolution chain: " + evolutionChain);

                    ArrayList<String> evolutions = new ArrayList<String>(); // Names of Pokemon in the evolution chain.
                    ChainLink currentEvolution = evolutionChain.getChain(); // Starts with the first Pokemon in the evolution chain.
                    evolutions.add(currentEvolution.getSpecies().getName());

                    if (currentEvolution.getEvolvesTo().size() != 0) { // If the Pokemon has an evolutionary line.
                        do {
                            currentEvolution = currentEvolution.getEvolvesTo().get(0); // Gets next evolution in the chain (always located in position zero).
                            evolutions.add(currentEvolution.getSpecies().getName());
                        } while (!currentEvolution.getEvolvesTo().isEmpty()); // Stops if final evolution has been reached.
                    }
                    Log.d(TAG, "Evolution names: " + evolutions.toString());


                    if (pokemonSpecies.getHabitat() != null)
                        pokemonSpecies.getHabitat().getName(); // Pokémon habitat.

                    pokemonSpecies.getGeneration().getName(); // Pokémon generation.

                    for (Genus gn : pokemonSpecies.getGenera()) {
                        if (gn.getLanguage().getName().equals(LANG)) { // If genus is in english.
                            gn.getGenus(); // Pokémon genus.
                            break;
                        }
                    }

                    // Flavor Text entries are in order from newest games to oldest games
                    for (PokemonSpeciesFlavorText fv : pokemonSpecies.getFlavorTextEntries()) {
                        if (fv.getLanguage().getName().equals(LANG)) { // If description is in english.
                            fv.getFlavorText(); // Pokémon description.
                            break;
                        }
                    }

                    Handler handler = new Handler(Looper.getMainLooper()); // grabs UI thread.
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            mPokemonTextView.setText(pokemon.toString());
                            Log.d(TAG, "Posting data to text field");
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

        // This method requires permission ACCESS_NETWORK_STATE
        boolean isAvailable = false;
        if (networkInfo != null && networkInfo.isConnected()) {
            // Checks if a network is present and connected
            isAvailable = true;
        }
        return isAvailable;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
