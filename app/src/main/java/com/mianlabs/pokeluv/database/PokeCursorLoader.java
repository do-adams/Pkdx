package com.mianlabs.pokeluv.database;

import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;

/**
 * Queries the ContentProvider in the background
 * for a cursor referencing the PokeDBContract.FavoritePokemonEntry
 * table in the DB.
 */
public class PokeCursorLoader implements LoaderManager.LoaderCallbacks<Cursor> {

    public interface Callback {
        void onLoadFinished(Loader<Cursor> loader, Cursor cursor);
    }

    private Context mContext;
    private Callback mCallback;

    public PokeCursorLoader(Context context, Callback callback) {
        mContext = context;
        mCallback = callback;
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
        mCallback.onLoadFinished(loader, data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
