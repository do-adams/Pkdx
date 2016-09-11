/*
 * Copyright (C) 2016 Damián Adams
 */
package com.mianlabs.localpokeapi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Model class used for storing and accessing Pokemon data retrieved by API calls.
 */
public class PokeModel {
    private static final String TAG = PokeModel.class.getSimpleName();

    // File delimiter character used in the Pokemon data files.
    private static final String FILE_DELIMITER = "!~";

    private int mPokedexNum;
    private String mName;
    private String mHeight;
    private String mWeight;
    private List<String> mTypes = new ArrayList<>();
    private String mSprite;

    private String mColor;
    private String mShape;
    private String mHabitat; // Habitat value can be null.
    private String mGeneration;
    private String mDescription;
    private List<String> mEvolutions = new ArrayList<>(); // Names of Pokemon in the evolution chain.

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
     * Constructor for a PokeModel. Must be fed a string containing
     * all the text (with newlines) in a Pokemon data file.
     */
    public PokeModel(String data) {
        int i = 0;
        String[] pokemon = data.split(FILE_DELIMITER);
        for (String s : pokemon) {
            pokemon[i++] = s.trim(); // Trims the entries in the Pokemon data array.
        }

        mPokedexNum = Integer.parseInt(pokemon[0]);
        mName = pokemon[1];
        mHeight = pokemon[2];
        mWeight = pokemon[3];
        mTypes = Arrays.asList(pokemon[4].substring(1, pokemon[4].length() - 1).split(", "));
        mSprite = pokemon[5];

        mColor = pokemon[6];
        mShape = pokemon[7];
        if (!pokemon[8].equals("null"))
            mHabitat = pokemon[8];
        mGeneration = pokemon[9];
        mDescription = pokemon[10];
        mEvolutions = Arrays.asList(pokemon[11].substring(1, pokemon[11].length() - 1).split(", "));
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

    public List<String> getTypes() {
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

    public List<String> getEvolutions() {
        return mEvolutions;
    }
}
