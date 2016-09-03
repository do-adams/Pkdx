package com.mianlabs.pokeluv.utilities;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.view.Menu;
import android.view.MenuItem;

import com.mianlabs.pokeluv.R;

public class BarTypefaceSetter {
    private static final String RELATIVE_PATH_TO_TYPEFACE = "Pokemon GB.ttf";

    public static void setActionBarTitle(AppCompatActivity context, String text) {
        // Applies the custom typeface to the Action Bar.
        SpannableString s = new SpannableString(text);
        // Don't use the full path of the text font, only the font filename.
        s.setSpan(new TypefaceSpan(context, RELATIVE_PATH_TO_TYPEFACE), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        ActionBar actionBar = context.getSupportActionBar();
        actionBar.setTitle(s);
    }

    public static void setActionBarOptionsText(AppCompatActivity context, Menu menu) {
        // Implements custom font for all Poke Luv menu items.
        SpannableStringBuilder title = new SpannableStringBuilder(context.getString(R.string.menu_more_pokemon));
        title.setSpan(new TypefaceSpan(context, RELATIVE_PATH_TO_TYPEFACE), 0, title.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        MenuItem menuItem = menu.findItem(R.id.menu_more_pokemon);
        menuItem.setTitle(title);
    }
}
