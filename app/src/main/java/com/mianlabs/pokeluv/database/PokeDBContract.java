/*
 * Copyright (C) 2016 Damián Adams
 */
package com.mianlabs.pokeluv.database;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Contract class for DB that stores the Pokedex Numbers
 * of the user's favorite Pokemon.
 */
public class PokeDBContract {
    public static final String CONTENT_AUTHORITY = "com.mianlabs.pokeluv";
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_FAVORITES = "favoritePokemon";

    public static final class FavoritePokemonEntry implements BaseColumns {
        // Location URI for table.
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVORITES).build();

        // MIME Types.
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/" + CONTENT_URI + "/" + PATH_FAVORITES;
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/" + CONTENT_URI + "/" + PATH_FAVORITES;

        // Table Schema.
        public static final String TABLE_NAME = "favoritePokemonTable";
        public static final String COLUMN_NUMBER = "favoritePokemonNumber";

        // Returns URI to find a specific favorite entry by its table id.
        public static Uri buildFavoritesUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}
