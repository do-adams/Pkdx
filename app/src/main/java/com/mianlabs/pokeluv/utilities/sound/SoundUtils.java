/*
 * Copyright (C) 2016 Damián Adams
 */
package com.mianlabs.pokeluv.utilities.sound;

import android.content.Context;
import android.media.MediaPlayer;

import com.mianlabs.pokeluv.R;

/**
 * Utility class for playing sound effects.
 */
public class SoundUtils {

    private static void playSound(Context context, int soundResId) {
        final MediaPlayer mediaPlayer = MediaPlayer.create(context, soundResId);
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.reset();
                mp.release();
                mp = null;
            }
        });
        mediaPlayer.start();
    }

    public static void playPokemonCaughtSound(Context context) {
        playSound(context, R.raw.pokemon_caught);
    }

    public static void playMenuItemSound(Context context) {
        playSound(context, R.raw.menu_item_selected);
    }

    public static void playFavoritesSound(Context context) {
        playSound(context, R.raw.favorites_pc_box);
    }
}
