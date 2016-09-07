/*
 * Copyright (C) 2016 Damián Adams
 */
package com.mianlabs.pokeluv.database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

/*
 * Great resource to use for implementing ContentProviders:
 * https://guides.codepath.com/android/Creating-Content-Providers#overview
 */
public class PokeProvider extends ContentProvider {
    private static final int CAUGHT_POKEMON = 100; // All rows.
    private static final int CAUGHT_POKEMON_ID = 101; // Individual row.
    private static final int FAVORITE_POKEMON = 200;
    private static final int FAVORITE_POKEMON_ID = 201;

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private PokeDBHelper mPokeDBHelper;

    @Override
    public boolean onCreate() {
        mPokeDBHelper = new PokeDBHelper(getContext());
        return true;
    }

    public static UriMatcher buildUriMatcher() {
        String content = PokeDBContract.CONTENT_AUTHORITY;
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(content, PokeDBContract.PATH_CAUGHT, CAUGHT_POKEMON);
        matcher.addURI(content, PokeDBContract.PATH_CAUGHT + "/#", CAUGHT_POKEMON_ID);
        matcher.addURI(content, PokeDBContract.PATH_FAVORITES, FAVORITE_POKEMON);
        matcher.addURI(content, PokeDBContract.PATH_FAVORITES + "/#", FAVORITE_POKEMON_ID);
        return matcher;
    }

    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case CAUGHT_POKEMON:
                return PokeDBContract.CaughtPokemonEntry.CONTENT_TYPE;
            case CAUGHT_POKEMON_ID:
                return PokeDBContract.CaughtPokemonEntry.CONTENT_ITEM_TYPE;
            case FAVORITE_POKEMON:
                return PokeDBContract.FavoritePokemonEntry.CONTENT_TYPE;
            case FAVORITE_POKEMON_ID:
                return PokeDBContract.FavoritePokemonEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        final SQLiteDatabase db = mPokeDBHelper.getWritableDatabase();
        Cursor retCursor;
        long _id;
        switch (sUriMatcher.match(uri)) {
            case CAUGHT_POKEMON:
                retCursor = db.query(
                        PokeDBContract.CaughtPokemonEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case CAUGHT_POKEMON_ID:
                _id = ContentUris.parseId(uri);
                retCursor = db.query(
                        PokeDBContract.CaughtPokemonEntry.TABLE_NAME,
                        projection,
                        PokeDBContract.CaughtPokemonEntry._ID + " = ?",
                        new String[]{String.valueOf(_id)},
                        null,
                        null,
                        sortOrder
                );
                break;
            case FAVORITE_POKEMON:
                retCursor = db.query(
                        PokeDBContract.FavoritePokemonEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case FAVORITE_POKEMON_ID:
                _id = ContentUris.parseId(uri);
                retCursor = db.query(
                        PokeDBContract.FavoritePokemonEntry.TABLE_NAME,
                        projection,
                        PokeDBContract.FavoritePokemonEntry._ID + " = ?",
                        new String[]{String.valueOf(_id)},
                        null,
                        null,
                        sortOrder
                );
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Registers a content observer to watch for changes to the cursor.
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mPokeDBHelper.getWritableDatabase();
        long _id;
        Uri returnUri;
        switch (sUriMatcher.match(uri)) {
            case CAUGHT_POKEMON:
                _id = db.insert(PokeDBContract.CaughtPokemonEntry.TABLE_NAME, null, values);
                if (_id > 0) {
                    returnUri = PokeDBContract.CaughtPokemonEntry.buildCaughtUri(_id);
                } else {
                    throw new UnsupportedOperationException("Unable to insert rows into: " + uri);
                }
                break;
            case FAVORITE_POKEMON:
                _id = db.insert(PokeDBContract.FavoritePokemonEntry.TABLE_NAME, null, values);
                if (_id > 0) {
                    returnUri = PokeDBContract.FavoritePokemonEntry.buildFavoritesUri(_id);
                } else {
                    throw new UnsupportedOperationException("Unable to insert rows into: " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Notify content observers.
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mPokeDBHelper.getWritableDatabase();
        int rows;
        switch (sUriMatcher.match(uri)) {
            case CAUGHT_POKEMON:
                rows = db.update(PokeDBContract.CaughtPokemonEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case FAVORITE_POKEMON:
                rows = db.update(PokeDBContract.FavoritePokemonEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rows != 0) { // If rows were updated.
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rows;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mPokeDBHelper.getWritableDatabase();
        int rows; // Number of rows targeted.
        switch (sUriMatcher.match(uri)) {
            case CAUGHT_POKEMON:
                rows = db.delete(PokeDBContract.CaughtPokemonEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case FAVORITE_POKEMON:
                rows = db.delete(PokeDBContract.FavoritePokemonEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (selection == null || rows != 0) { // If rows were deleted.
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rows;
    }
}
