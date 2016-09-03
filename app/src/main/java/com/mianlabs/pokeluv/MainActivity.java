package com.mianlabs.pokeluv;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;

import com.mianlabs.pokeluv.utilities.TypefaceSpan;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Applies the custom typeface to the Action Bar.
        SpannableString s = new SpannableString("Poke Luv");
        // Don't use the full path of the text font, only the font filename.
        s.setSpan(new TypefaceSpan(this, "Pokemon GB.ttf"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(s);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction().add(R.id.main_frame, new PokeFragment()).commit();
        }
    }
}
