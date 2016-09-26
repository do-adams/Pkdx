/*
 * Copyright (C) 2016 Damián Adams
 */
package com.mianlabs.pkdx.utilities;

/**
 * Class responsible for storing names and keys used by the SharedPreferences API.
 * <p/>
 * Contains the Filename and Key used for keeping track across Activities
 * of how many Pokemon the user has caught.
 */
public class PokeSharedPreferences {
    // Filename used for accessing the SharedPreferences app file.
    public static final String COUNT_CAUGHT_POKEMON_FILENAME = "caughtPokemon";
    // Key used for storing and retrieving the number of Pokemon the user has caught.
    public static final String COUNT_CAUGHT_POKEMON_KEY = "CAUGHT_POKEMON";
}
