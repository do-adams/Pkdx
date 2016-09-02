package com.mianlabs.pokeluv.model;

import java.util.ArrayList;
import java.util.Locale;

import me.sargunvohra.lib.pokekotlin.model.ChainLink;
import me.sargunvohra.lib.pokekotlin.model.EvolutionChain;
import me.sargunvohra.lib.pokekotlin.model.Pokemon;
import me.sargunvohra.lib.pokekotlin.model.PokemonSpecies;
import me.sargunvohra.lib.pokekotlin.model.PokemonSpeciesFlavorText;
import me.sargunvohra.lib.pokekotlin.model.PokemonType;

// TODO: Implement Parcelable to save state across configuration changes.
public class PokeModel {
    private static final String TAG = PokeModel.class.getSimpleName();
    private static final String LANG = "en"; // Language for retrieving Pokemon data.

    // PokeApi can make calls to get Pokemon #s 1 to 721 (National Pokedex Number)
    public static final int NUM_OF_POKEMON = 721;

    // From Pokemon API Call
    private int mPokedexNum;
    private String mName;
    private String mHeight;
    private String mWeight;
    private ArrayList<String> mTypes = new ArrayList<>();

    // From Pokemon Species API Call
    private String mColor;
    private String mShape;
    private String mHabitat;
    private String mGeneration;
    private String mDescription;
    private ArrayList<String> mEvolutions = new ArrayList<>(); // Names of Pokemon in the evolution chain.

    @Override
    public String toString() {
        return "No. " + mPokedexNum + " " + mName + "\nHeight: " + mHeight + "\nWeight: " + mWeight
                + "\nTypes: " + mTypes + "\nColor: " + mColor + "\nShape: " + mShape + "\nHabitat: "
                + mHabitat + "\nGeneration: " + mGeneration + "\nDescription: "
                + mDescription + "\nEvolutions: " + mEvolutions;
    }

    private String formatHeight(int height) {
        double meters = height * (1.0 / 10.0); // Converts from decimeters to meters.
        double feet = meters * 3.28084; // Converts meters to feet.
        return String.format(Locale.US, "%.1f m / %.1f ft", meters, feet);
    }

    private String formatWeight(int weight) {
        double kilograms = weight * (1.0 / 10.0); // Converts from hectograms to kilograms.
        double pounds = kilograms * 2.20462; // Converts from kilograms to feet.
        return String.format(Locale.US, "%.1f kg / %.1f lbs", kilograms, pounds);
    }

    public PokeModel(Pokemon pokemon, PokemonSpecies pokemonSpecies, EvolutionChain evolutionChain) {
        // Basic Pokemon Data
        mPokedexNum = pokemon.getId(); // Pokemon National Pokedex Num.
        mName = pokemon.getName();
        mHeight = formatHeight(pokemon.getHeight()); // Pokemon height in decimeters.
        mWeight = formatWeight(pokemon.getWeight()); // Pokemon weight in hectograms.
        for (PokemonType t : pokemon.getTypes())
            mTypes.add(t.getType().getName()); // Typing of Pokemon.

        // Pokemon Species Data
        mColor = pokemonSpecies.getColor().getName(); // Pokemon color.
        mShape = pokemonSpecies.getShape().getName(); // Pokemon shape.
        if (pokemonSpecies.getHabitat() != null)
            mHabitat = pokemonSpecies.getHabitat().getName(); // Pokemon habitat.
        mGeneration = pokemonSpecies.getGeneration().getName(); // Pokemon generation.

        for (PokemonSpeciesFlavorText fv : pokemonSpecies.getFlavorTextEntries()) {
            // Flavor Text entries are in order from newest games to oldest games
            if (fv.getLanguage().getName().equals(LANG)) { // If description is in english.
                mDescription = fv.getFlavorText(); // Pokemon description.
                break;
            }
        }

        // Pokemon Evolutions
        ChainLink currentEvolution = evolutionChain.getChain(); // Starts with the first Pokemon in the evolution chain.
        mEvolutions.add(currentEvolution.getSpecies().getName());
        if (currentEvolution.getEvolvesTo().size() != 0) { // If the Pokemon has an evolutionary line.
            do {
                currentEvolution = currentEvolution.getEvolvesTo().get(0); // Gets next evolution in the chain (always located in position zero).
                mEvolutions.add(currentEvolution.getSpecies().getName());
            }
            while (!currentEvolution.getEvolvesTo().isEmpty()); // Stops if final evolution has been reached.
        }
    }
}
