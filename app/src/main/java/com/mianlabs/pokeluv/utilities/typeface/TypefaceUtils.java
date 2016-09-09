/*
 * Copyright (C) 2016 Damián Adams
 */
package com.mianlabs.pokeluv.utilities.typeface;

import android.content.Context;
import android.os.CountDownTimer;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.mianlabs.pokeluv.R;

/**
 * Utility class for implementing a custom typeface into the Action Bar title and menu options.
 * Also provides Toasts with custom typeface and duration abilities.
 */
public class TypefaceUtils {
    private static final String TAG = TypefaceUtils.class.getSimpleName();
    public static final int TOAST_SHORT_DURATION = 2; // Duration in seconds.

    // Relative path to typeface from the /assets/fonts dir.
    private static final String RELATIVE_PATH_TO_TYPEFACE = "Pokemon GB.ttf";

    /**
     * Sets the Action Bar title with a custom typeface.
     */
    public static void setActionBarTitle(AppCompatActivity context, String text) {
        // Applies the custom typeface to the Action Bar.
        SpannableString s = new SpannableString(text);
        // Don't use the full path of the text font, only the font filename.
        s.setSpan(new TypefaceSpan(context, RELATIVE_PATH_TO_TYPEFACE), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        ActionBar actionBar = context.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(s);
        } else {
            Log.e(TAG, "Action bar reference is null");
        }
    }

    /**
     * Sets the options for the activity menu for
     * the Poke Luv app with a custom typeface.
     * Menu must be inflated before calling this method.
     */
    public static void setActionBarOptionsText(AppCompatActivity context, Menu menu) {
        // Implements custom fonts for all Poke Luv menu items.
        SpannableStringBuilder pokedexTitle =
                new SpannableStringBuilder(context.getString(R.string.menu_more_pokemon));
        pokedexTitle
                .setSpan(new TypefaceSpan(context, RELATIVE_PATH_TO_TYPEFACE), 0, pokedexTitle.length(),
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        SpannableStringBuilder pokemonOfTheDayTitle =
                new SpannableStringBuilder(context.getString(R.string.menu_catch_pokemon));
        pokemonOfTheDayTitle
                .setSpan(new TypefaceSpan(context, RELATIVE_PATH_TO_TYPEFACE), 0, pokemonOfTheDayTitle.length(),
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        SpannableStringBuilder addToFavsTitle =
                new SpannableStringBuilder(context.getString(R.string.menu_add_to_favs));
        addToFavsTitle
                .setSpan(new TypefaceSpan(context, RELATIVE_PATH_TO_TYPEFACE), 0, addToFavsTitle.length(),
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        SpannableStringBuilder favoritesTitle =
                new SpannableStringBuilder(context.getString(R.string.menu_favorites));
        favoritesTitle
                .setSpan(new TypefaceSpan(context, RELATIVE_PATH_TO_TYPEFACE), 0, favoritesTitle.length(),
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        MenuItem dexItem = menu.findItem(R.id.menu_more_pokemon);
        dexItem.setTitle(pokedexTitle);

        MenuItem dailyItem = menu.findItem(R.id.menu_catch_pokemon);
        dailyItem.setTitle(pokemonOfTheDayTitle);

        MenuItem addToFavsItem = menu.findItem(R.id.menu_add_to_favs);
        addToFavsItem.setTitle(addToFavsTitle);

        MenuItem favoritesItem = menu.findItem(R.id.menu_favorites);
        favoritesItem.setTitle(favoritesTitle);
    }

    /**
     * Runs or displays a customized toast for a specified duration of time.
     * Implemented using code from: http://blog.cindypotvin.com/toast-specific-duration-android/
     */
    private static void runCustomToast(final Toast toast,
                                       int toastDurationInMilliSeconds,
                                       int toastRefreshRateInMilliSeconds) {
        CountDownTimer toastCountDown;
        toastCountDown = new CountDownTimer(toastDurationInMilliSeconds, toastRefreshRateInMilliSeconds) {
            public void onTick(long millisUntilFinished) {
                toast.show();
            }

            public void onFinish() {
                toast.cancel();
            }
        };
        toast.show();
        toastCountDown.start();
    }

    /**
     * Displays a toast msg for a specified amount of time (seconds).
     */
    public static void displayToast(Context context, String msg, int durationInSeconds) {
        int toastDurationInMilliSeconds = durationInSeconds * 1000;
        int toastRefreshRateInMilliSeconds = 2 * 1000;

        SpannableString s = new SpannableString(msg);
        s.setSpan(new TypefaceSpan(context, RELATIVE_PATH_TO_TYPEFACE), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        final Toast toast = Toast.makeText(context, s, Toast.LENGTH_LONG);

        runCustomToast(toast, toastDurationInMilliSeconds, toastRefreshRateInMilliSeconds);
    }

    /**
     * Displays a toast msg for a specified amount of time (seconds) in the Gravity.TOP
     * position along with a y-offset.
     */
    public static void displayToastTop(Context context, String msg, int durationInSeconds, int yOffset) {
        int toastDurationInMilliSeconds = durationInSeconds * 1000;
        int toastRefreshRateInMilliSeconds = 2 * 1000;

        SpannableString s = new SpannableString(msg);
        s.setSpan(new TypefaceSpan(context, RELATIVE_PATH_TO_TYPEFACE), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        final Toast toast = Toast.makeText(context, s, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.TOP, 0, yOffset);

        runCustomToast(toast, toastDurationInMilliSeconds, toastRefreshRateInMilliSeconds);
    }
}
