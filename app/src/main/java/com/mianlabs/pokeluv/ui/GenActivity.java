package com.mianlabs.pokeluv.ui;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.mianlabs.pokeluv.R;
import com.mianlabs.pokeluv.utilities.PokePicker;
import com.mianlabs.pokeluv.utilities.TypefaceUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GenActivity extends AppCompatActivity {
    private static final String TAG = GenActivity.class.getSimpleName();

    public static String GEN_KEY = "GenActivity";
    private Typeface mCustomFont;

    @BindView(R.id.button_gen_i)
    Button mGenIButton;
    @BindView(R.id.button_gen_ii)
    Button mGenIIButton;
    @BindView(R.id.button_gen_iii)
    Button mGenIIIButton;
    @BindView(R.id.button_gen_iv)
    Button mGenIVButton;
    @BindView(R.id.button_gen_v)
    Button mGenVButton;
    @BindView(R.id.button_gen_vi)
    Button mGenVIButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gen);
        ButterKnife.bind(this);
        TypefaceUtils.setActionBarTitle(this, getString(R.string.app_name));

        mCustomFont = Typeface.createFromAsset(getAssets(), getString(R.string.font_path));
        setCustomTypefaceForViews();
    }

    private void setCustomTypefaceForViews() {
        mGenIButton.setTypeface(mCustomFont);
        mGenIIButton.setTypeface(mCustomFont);
        mGenIIIButton.setTypeface(mCustomFont);
        mGenIVButton.setTypeface(mCustomFont);
        mGenVButton.setTypeface(mCustomFont);
        mGenVIButton.setTypeface(mCustomFont);
    }

    /**
     * onClickListener for all of the Generations buttons.
     */
    public void launchPokeList(View view) {
        PokePicker.Generations genVal;
        switch (view.getId()) {
            case R.id.button_gen_i:
                genVal = PokePicker.Generations.GEN_I;
                break;
            case R.id.button_gen_ii:
                genVal = PokePicker.Generations.GEN_II;
                break;
            case R.id.button_gen_iii:
                genVal = PokePicker.Generations.GEN_III;
                break;
            case R.id.button_gen_iv:
                genVal = PokePicker.Generations.GEN_IV;
                break;
            case R.id.button_gen_v:
                genVal = PokePicker.Generations.GEN_V;
                break;
            case R.id.button_gen_vi:
                genVal = PokePicker.Generations.GEN_VI;
                break;
            default:
                genVal = null;
                Log.e(TAG, "Error retrieving generation button ids.");
                break;
        }
        if (genVal != null) {
            PokeList pokeList = new PokeList();
            Bundle bundle = new Bundle();
            bundle.putParcelable(GEN_KEY, genVal);
            pokeList.setArguments(bundle);
            getFragmentManager().beginTransaction().add(R.id.generations_container, pokeList)
                    .addToBackStack(null).commit();
        }
    }
}
