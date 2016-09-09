/*
 * Copyright (C) 2016 Damián Adams
 */
package com.mianlabs.pokeluv.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;

import java.util.ArrayList;

/**
 * Loader class that queries the ContentProvider in the bg
 * for a cursor referencing any given table in the DB.
 * <p/>
 * Provides utility methods for retrieving and storing Pokemon data in the
 * db.
 * <p/>
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
    private String mTableName;
    private Uri mTableUri;

    public PokeCursorManager(Context context, LoaderCall loaderCall, String tableName) {
        mContext = context.getApplicationContext(); // To avoid memory leaks (CursorLoader uses the App. Context).
        mLoaderCall = loaderCall;
        mTableName = tableName;
        mTableUri = PokeDBContract.getTableContentUri(tableName);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Grabs the projection for the query.
        return new CursorLoader(mContext,
                mTableUri,
                PokeDBContract.getProjectionForTable(mTableName),
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
     * of the Pokemon in a given DB table's numeric column.
     * <p/>
     * Must be passed a cursor reference loaded by
     * this very same class and the name of the table
     * and column you want to get the Pokemon numbers from.
     */
    public static ArrayList<Integer> getPokemonInDb(Cursor cursor, String tableName, String columnName) {
        if (PokeDBContract.doesTableHaveColumn(tableName, columnName)) {
            ArrayList<Integer> list = new ArrayList<>();
            if (cursor != null) { // Checks if the cursor is null.
                if (cursor.moveToFirst()) { // Checks if the cursor is empty.
                    do {
                        int numIndex = cursor.getColumnIndex(columnName);
                        int dbNum = cursor.getInt(numIndex);
                        list.add(dbNum);
                    }
                    while (cursor.moveToNext());
                } else {
                    Log.d(TAG, "Cursor for " + tableName + " is empty");
                }
            } else {
                Log.e(TAG, "Cursor for " + tableName + " is null");
            }
            Log.d(TAG, "Pokemon in table " + tableName + ": " + list.toString());
            return list;
        } else {
            throw new IllegalArgumentException("Column not found in table.");
        }
    }

    /**
     * Inserts a Pokemon into the db given the name of the table and the column name.
     */
    public static void insertPokemonInDb(Context context, int pokemonNumber,
                                         String tableName, String columnName) {
        if (PokeDBContract.doesTableHaveColumn(tableName, columnName)) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(columnName, pokemonNumber);
            context.getContentResolver().insert(PokeDBContract.getTableContentUri(tableName), contentValues);
            Log.d(TAG, "Successfully inserted Pokemon #" + pokemonNumber + " in table " + tableName);
        } else {
            throw new IllegalArgumentException("Column not found in table.");
        }
    }
}
