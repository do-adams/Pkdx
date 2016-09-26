/*
 * Copyright (C) 2016 Damián Adams
 */
package com.mianlabs.pkdx.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class PokeDBHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "pkdx.db";

    public PokeDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        addFavoritesTable(db);
    }

    private void addFavoritesTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + PokeDBContract.CaughtPokemonEntry.TABLE_NAME + " (" +
                PokeDBContract.CaughtPokemonEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                PokeDBContract.CaughtPokemonEntry.COLUMN_NUMBER + " INTEGER NOT NULL);"
        );
        db.execSQL("CREATE TABLE " + PokeDBContract.FavoritePokemonEntry.TABLE_NAME + " (" +
                PokeDBContract.FavoritePokemonEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                PokeDBContract.FavoritePokemonEntry.COLUMN_NUMBER + " INTEGER NOT NULL);"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Deletes the db and creates a new one.
        // In case of expanding the db, implement a better upgrade solution that store's
        // the user's favorite Pokemon.
        db.execSQL("DROP TABLE IF EXISTS " + PokeDBContract.FavoritePokemonEntry.TABLE_NAME);
        onCreate(db);
        db.execSQL("DROP TABLE IF EXISTS " + PokeDBContract.CaughtPokemonEntry.TABLE_NAME);
        onCreate(db);
    }
}
