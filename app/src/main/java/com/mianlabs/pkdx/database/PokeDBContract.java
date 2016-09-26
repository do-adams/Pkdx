/*
 * Copyright (C) 2016 Damián Adams
 */
package com.mianlabs.pkdx.database;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Contract class for DB that stores the Pokedex numbers of
 * caught Pokemon and the Pokedex numbers of the user's favorite Pokemon.
 * <p/>
 * Note: If adding or removing a subclass you must also make sure to update its methods.
 */
public class PokeDBContract {
    public static final String CONTENT_AUTHORITY = "com.mianlabs.pkdx";
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_CAUGHT = "caughtPokemon";
    public static final String PATH_FAVORITES = "favoritePokemon";


    // Note: When updating this class you must also make sure to update its methods.
    public static final class CaughtPokemonEntry implements BaseColumns {
        // Location URI for table.
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_CAUGHT).build();

        // MIME Types.
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/" + CONTENT_URI + "/" + PATH_CAUGHT;
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/" + CONTENT_URI + "/" + PATH_CAUGHT;

        // Table Schema.
        public static final String TABLE_NAME = "caughtPokemonTable";
        public static final String COLUMN_NUMBER = "caughtPokemonNumber";

        // Returns URI to find a specific favorite entry by its table id.
        public static Uri buildCaughtUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        /**
         * Returns an SQLite projection for all the columns in this table.
         */
        public static String[] getProjectionAll() {
            return new String[]{COLUMN_NUMBER};
        }

        /**
         * Safety method for determining if a table has a
         * specific column.
         */
        public static boolean hasColumn(String tableName) {
            switch (tableName) {
                case COLUMN_NUMBER:
                    return true;
                default:
                    return false;
            }
        }
    }

    // Note: When updating this class you must also make sure to update its methods.
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

        /**
         * Returns an SQLite projection for all the columns in this table.
         */
        public static String[] getProjectionAll() {
            return new String[]{COLUMN_NUMBER};
        }

        /**
         * Safety method for determining if a table has a
         * specific column.
         */
        public static boolean hasColumn(String tableName) {
            switch (tableName) {
                case COLUMN_NUMBER:
                    return true;
                default:
                    return false;
            }
        }
    }

    /**
     * Utility method:
     * Grabs Uri of the table to query based on the table name.
     */
    public static Uri getTableContentUri(String tableName) {
        switch (tableName) {
            case CaughtPokemonEntry.TABLE_NAME:
                return CaughtPokemonEntry.CONTENT_URI;
            case FavoritePokemonEntry.TABLE_NAME:
                return FavoritePokemonEntry.CONTENT_URI;
            default:
                throw new IllegalArgumentException("Entered invalid table name");
        }
    }

    /**
     * Utility method:
     * Returns an SQLite projection for all columns based on the table name.
     */
    public static String[] getProjectionForTable(String tableName) {
        switch (tableName) {
            case CaughtPokemonEntry.TABLE_NAME:
                return CaughtPokemonEntry.getProjectionAll();
            case FavoritePokemonEntry.TABLE_NAME:
                return FavoritePokemonEntry.getProjectionAll();
            default:
                throw new IllegalArgumentException("No projection found for table name.");
        }
    }

    /**
     * Safety method for ensuring that a given table
     * has a given column.
     */
    public static boolean doesTableHaveColumn(String tableName, String columnName) {
        switch (tableName) {
            case CaughtPokemonEntry.TABLE_NAME:
                return CaughtPokemonEntry.hasColumn(columnName);
            case FavoritePokemonEntry.TABLE_NAME:
                return FavoritePokemonEntry.hasColumn(columnName);
            default:
                return false;
        }
    }
}
