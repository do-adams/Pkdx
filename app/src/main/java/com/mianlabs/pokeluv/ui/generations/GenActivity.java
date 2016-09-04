package com.mianlabs.pokeluv.ui.generations;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.mianlabs.pokeluv.R;
import com.mianlabs.pokeluv.utilities.TypefaceUtils;

public class GenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gen);
        TypefaceUtils.setActionBarTitle(this, getString(R.string.app_name));

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.generations_container, new GenFragment()).commit();
        }
    }
}
