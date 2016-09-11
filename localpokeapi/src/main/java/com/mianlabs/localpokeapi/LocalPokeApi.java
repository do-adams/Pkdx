/*
 * Copyright (C) 2016 Damián Adams
 */
package com.mianlabs.localpokeapi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Use this class for making a request for Pokemon data.
 */
public class LocalPokeApi {
    // the file path matters depending on the way you load the text file (getClassLoader or getClass)
    public static final String PATH_TO_FILE = "/data/";

    /**
     * Returns Pokemon data in the form of a PokeModel object.
     * Returns null if something went wrong while fetching the data.
     */
    public static PokeModel getPokemonData(int pokemonNumber) {
        PokeModel pokeModel = null;
        String data = null;
        InputStream inputStream = LocalPokeApi.class
                .getResourceAsStream(PATH_TO_FILE + pokemonNumber + ".txt"); // Gradle re-directs to resources dir.
        if (inputStream != null) {
            BufferedReader bufferedReader = null;
            try {
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                data = "";
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    data += line + "\n"; // Includes newlines because of the Description entry.
                }
            } catch (IOException e) {
                System.out.println("Error while reading Pokemon data from file.");
                e.printStackTrace();
            } finally {
                if (bufferedReader != null) {
                    try {
                        bufferedReader.close();
                    } catch (IOException e) {
                        System.out.println("Error while closing file of Pokemon data.");
                        e.printStackTrace();
                    }
                }
            }
        } else {
            System.out.println("Error: Input stream is null");
        }
        if (data != null) {
            pokeModel = new PokeModel(data);
        }
        return pokeModel;
    }
}
