package com.mianlabs.pokeluv.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.mianlabs.pokeluv.R;
import com.mianlabs.pokeluv.utilities.TypefaceUtils;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TypefaceUtils.setActionBarTitle(this, getString(R.string.app_name));

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction().add(R.id.main_frame, new PokeFragment()).commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        TypefaceUtils.setActionBarOptionsText(this, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_more_pokemon:
                startActivity(new Intent(this, GenActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
