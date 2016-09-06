/*
 * Copyright (C) 2016 Damián Adams
 */
package com.mianlabs.pokeluv.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;

import java.util.ArrayList;

/**
 * Loader class that queries the ContentProvider in the bg
 * for a cursor referencing the PokeDBContract.FavoritePokemonEntry
 * table in the DB.
 * Provides utility methods for retrieving and storing Pokemon data in the
 * db.
 * Uses the support library implementation to avoid configuration bugs.
 */
public class PokeCursorManager implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = PokeCursorManager.class.getSimpleName();

    public interface LoaderCall {
        /**
         * Callback for onLoadFinished.
         * No need to close the cursor as this class
         * will close it on its own.
         */
        void onLoadFinished(Loader<Cursor> loader, Cursor cursor);
    }

    private Context mContext;
    private LoaderCall mLoaderCall;

    public PokeCursorManager(Context context, LoaderCall loaderCall) {
        mContext = context;
        mLoaderCall = loaderCall;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Queries the cursor in the background.
        return new CursorLoader(mContext,
                PokeDBContract.FavoritePokemonEntry.CONTENT_URI,
                new String[]{PokeDBContract.FavoritePokemonEntry.COLUMN_NUMBER},
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mLoaderCall.onLoadFinished(loader, data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    /**
     * Returns a List containing all of the numbers
     * in the COLUMN_NUMBER of the favoritePokemon
     * table in the DB. Must be passed an
     * intact cursor reference loaded by
     * this very same class.
     */
    public static ArrayList<Integer> getPokemonInDb(Cursor cursor) {
        ArrayList<Integer> list = new ArrayList<>();
        if (cursor != null) { // Checks if the cursor is null.
            if (cursor.moveToFirst()) { // Checks if the cursor is empty.
                do {
                    int numIndex = cursor.getColumnIndex(PokeDBContract.FavoritePokemonEntry.COLUMN_NUMBER);
                    int dbNum = cursor.getInt(numIndex);
                    list.add(dbNum);
                }
                while (cursor.moveToNext());
            } else
                Log.d(TAG, "Cursor is empty");
        } else
            Log.e(TAG, "Cursor is null");
        Log.d(TAG, "Pokemon in the db: " + list.toString());
        return list;
    }

    /**
     * Inserts a Pokemon into the db (favoritePokemon table).
     */
    public static void insertPokemonInDb(Context context, int pokemonNumber) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(PokeDBContract.FavoritePokemonEntry.COLUMN_NUMBER, pokemonNumber);
        context.getContentResolver().insert(PokeDBContract.FavoritePokemonEntry.CONTENT_URI, contentValues);
        Log.d(TAG, "Successfully inserted Pokemon #" + pokemonNumber + " in db.");
    }
}
