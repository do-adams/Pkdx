/*
 * Copyright (C) 2016 Damián Adams
 */
package com.mianlabs.pokeluv.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import me.sargunvohra.lib.pokekotlin.model.ChainLink;
import me.sargunvohra.lib.pokekotlin.model.EvolutionChain;
import me.sargunvohra.lib.pokekotlin.model.Pokemon;
import me.sargunvohra.lib.pokekotlin.model.PokemonSpecies;
import me.sargunvohra.lib.pokekotlin.model.PokemonSpeciesFlavorText;
import me.sargunvohra.lib.pokekotlin.model.PokemonType;

/**
 * Model class used for storing and accessing Pokemon data retrieved by API calls.
 */
public class PokeModel implements Parcelable {
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
    private String mSprite;

    // From Pokemon Species API Call
    private String mColor;
    private String mShape;
    private String mHabitat; // Habitat value can be null.
    private String mGeneration;
    private String mDescription;
    private ArrayList<String> mEvolutions = new ArrayList<>(); // Names of Pokemon in the evolution chain.

    /**
     * Use for debugging in the log.
     */
    @Override
    public String toString() {
        return "No. " + mPokedexNum + " " + mName + "\n" + mSprite + "\nHeight: " + mHeight +
                "\nWeight: " + mWeight + "\nTypes: " + mTypes + "\nColor: " + mColor
                + "\nShape: " + mShape + "\nHabitat: " + mHabitat +
                "\nGeneration: " + mGeneration + "\nDescription: " + mDescription
                + "\nEvolutions: " + mEvolutions;
    }

    /**
     * Capitalizes the lowercase Strings provided by the API.
     */
    public String capitalize(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    /**
     * Formats the height value provided by the API in units of decimeters.
     * Returns a String with the height in two popular units.
     */
    private String formatHeight(int height) {
        double meters = height * (1.0 / 10.0); // Converts from decimeters to meters.
        double feet = meters * 3.28084; // Converts meters to feet.
        return String.format(Locale.US, "%.1f m / %.1f ft", meters, feet);
    }

    /**
     * Formats the weight value provided by the API in units of hectograms.
     * Returns a String with the weight in two popular units.
     */
    private String formatWeight(int weight) {
        double kilograms = weight * (1.0 / 10.0); // Converts from hectograms to kilograms.
        double pounds = kilograms * 2.20462; // Converts from kilograms to pounds.
        return String.format(Locale.US, "%.1f kg / %.1f lbs", kilograms, pounds);
    }

    /**
     * Constructor for the class tasked with populating the PokeModel object
     * from three objects obtained by the PokeKotlin API. All three objects
     * must contain data on the same Pokemon.
     */
    public PokeModel(Pokemon pokemon, PokemonSpecies pokemonSpecies, EvolutionChain evolutionChain) {
        // Basic Pokemon Data
        mPokedexNum = pokemon.getId(); // Pokemon National Pokedex Num.
        mName = capitalize(pokemon.getName());
        mHeight = formatHeight(pokemon.getHeight()); // Pokemon height in decimeters.
        mWeight = formatWeight(pokemon.getWeight()); // Pokemon weight in hectograms.
        for (PokemonType t : pokemon.getTypes())
            mTypes.add(capitalize(t.getType().getName())); // Typing of Pokemon.
        mSprite = pokemon.getSprites().getFrontDefault();

        // Pokemon Species Data
        mColor = capitalize(pokemonSpecies.getColor().getName()); // Pokemon color.
        mShape = capitalize(pokemonSpecies.getShape().getName()); // Pokemon shape.
        if (pokemonSpecies.getHabitat() != null)
            mHabitat = capitalize(pokemonSpecies.getHabitat().getName()); // Pokemon habitat.
        mGeneration = capitalize(pokemonSpecies.getGeneration().getName()); // Pokemon generation.

        for (PokemonSpeciesFlavorText fv : pokemonSpecies.getFlavorTextEntries()) {
            // Flavor Text entries are in order from newest games to oldest games
            if (fv.getLanguage().getName().equals(LANG)) { // If description is in english.
                mDescription = fv.getFlavorText(); // Pokemon description.
                break;
            }
        }

        // Pokemon Evolutions
        ChainLink currentEvolution = evolutionChain.getChain(); // Starts with the first Pokemon in the evolution chain.
        mEvolutions.add(capitalize(currentEvolution.getSpecies().getName()));
        if (currentEvolution.getEvolvesTo().size() != 0) { // If the Pokemon has an evolutionary line.
            do {
                currentEvolution = currentEvolution.getEvolvesTo().get(0); // Gets next evolution in the chain (always located in position zero).
                mEvolutions.add(capitalize(currentEvolution.getSpecies().getName()));
            }
            while (!currentEvolution.getEvolvesTo().isEmpty()); // Stops if final evolution has been reached.
        }
    }

    /**
     * Formats the List objects from the PokeModel into
     * formatted Strings that are easily readable.
     */
    public static String formatListToString(List<String> list) {
        String result = "";
        for (String s : list)
            result += s + "\t\t";
        result = result.trim();
        return result.toUpperCase();
    }

    public int getPokedexNum() {
        return mPokedexNum;
    }

    public String getName() {
        return mName;
    }

    public String getHeight() {
        return mHeight;
    }

    public String getWeight() {
        return mWeight;
    }

    public ArrayList<String> getTypes() {
        return mTypes;
    }

    public String getSprite() {
        return mSprite;
    }

    public String getColor() {
        return mColor;
    }

    public String getShape() {
        return mShape;
    }

    public String getHabitat() {
        if (mHabitat != null)
            return mHabitat;
        else
            return null;
    }

    public String getGeneration() {
        return mGeneration;
    }

    public String getDescription() {
        return mDescription;
    }

    public ArrayList<String> getEvolutions() {
        return mEvolutions;
    }

    protected PokeModel(Parcel in) {
        mPokedexNum = in.readInt();
        mName = in.readString();
        mHeight = in.readString();
        mWeight = in.readString();
        if (in.readByte() == 0x01) {
            mTypes = new ArrayList<String>();
            in.readList(mTypes, String.class.getClassLoader());
        } else {
            mTypes = null;
        }
        mSprite = in.readString();
        mColor = in.readString();
        mShape = in.readString();
        mHabitat = in.readString();
        mGeneration = in.readString();
        mDescription = in.readString();
        if (in.readByte() == 0x01) {
            mEvolutions = new ArrayList<String>();
            in.readList(mEvolutions, String.class.getClassLoader());
        } else {
            mEvolutions = null;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mPokedexNum);
        dest.writeString(mName);
        dest.writeString(mHeight);
        dest.writeString(mWeight);
        if (mTypes == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(mTypes);
        }
        dest.writeString(mSprite);
        dest.writeString(mColor);
        dest.writeString(mShape);
        dest.writeString(mHabitat);
        dest.writeString(mGeneration);
        dest.writeString(mDescription);
        if (mEvolutions == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(mEvolutions);
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<PokeModel> CREATOR = new Parcelable.Creator<PokeModel>() {
        @Override
        public PokeModel createFromParcel(Parcel in) {
            return new PokeModel(in);
        }

        @Override
        public PokeModel[] newArray(int size) {
            return new PokeModel[size];
        }
    };
}