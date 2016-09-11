/*
 * Copyright (C) 2016 Damián Adams
 */
package com.mianlabs.localpokeapi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class LocalPokeApi {
    // PokeApi can make calls to get Pokemon #s 1 to 721 (National Pokedex Number)
    public static final int NUM_OF_POKEMON = 721;

    // the file path matters depending on the way you load the text file (getClassLoader or getClass)
    public static final String PATH_TO_FILE = "/data/";

    public static PokeModel getPokemonData(int num) {
        PokeModel pokeModel = null;
        String data = null;
        InputStream inputStream = LocalPokeApi.class
                .getResourceAsStream(PATH_TO_FILE + num + ".txt"); // Gradle re-directs to resources dir.
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
