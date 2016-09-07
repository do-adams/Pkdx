/*
 * Copyright (C) 2016 Damián Adams
 */
package com.mianlabs.pokeluv.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.widget.RemoteViews;

import com.mianlabs.pokeluv.R;
import com.mianlabs.pokeluv.ui.main.MainActivity;
import com.mianlabs.pokeluv.utilities.PokePicker;

/**
 * Widget "catches" a different Pokemon every time it is updated,
 * sets the widget layout with that Pokemon and sets it to launch
 * a PokeFragment for that Pokemon when clicked.
 */
public class PokeWidget extends AppWidgetProvider {
    private static final String TAG = PokeWidget.class.getSimpleName();
    public static final String POKE_WIDGET_KEY = "PokeWidget";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.d(TAG, "Widget onUpdate called.");
        final int N = appWidgetIds.length;
        for (int i = 0; i < N; i++) {
            int appWidgetId = appWidgetIds[i];
            int caughtPkmn = PokePicker.catchRandomPokemon();

            Intent intent = new Intent(context, MainActivity.class);
            intent.putExtra(POKE_WIDGET_KEY, caughtPkmn);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                    intent, PendingIntent.FLAG_UPDATE_CURRENT);

            Drawable drawable = ResourcesCompat.getDrawable(context.getResources(),
                    PokePicker.GenerationNumbers.getDrawableResourceFromNumber(context, caughtPkmn),
                    null);
            if (drawable != null) {
                Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.pokemon_list_item);
                views.setImageViewBitmap(R.id.pokemon_list_img, bitmap);
                views.setOnClickPendingIntent(R.id.pokemon_list_img, pendingIntent);

                appWidgetManager.updateAppWidget(appWidgetId, views);
            } else
                Log.e(TAG, "Widget drawable is null");
        }
    }
}
