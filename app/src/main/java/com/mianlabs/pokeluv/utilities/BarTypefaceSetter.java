package com.mianlabs.pokeluv.utilities;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;

public class BarTypefaceSetter {
    private static final String RELATIVE_PATH_TO_TYPEFACE = "Pokemon GB.ttf";

    public static void setActionBarText(AppCompatActivity context, String text) {
        // Applies the custom typeface to the Action Bar.
        SpannableString s = new SpannableString(text);
        // Don't use the full path of the text font, only the font filename.
        s.setSpan(new TypefaceSpan(context, RELATIVE_PATH_TO_TYPEFACE), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        ActionBar actionBar = context.getSupportActionBar();
        actionBar.setTitle(s);
    }
}
