package com.mianlabs.pokeluv;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import com.mianlabs.pokeluv.utilities.BarTypefaceSetter;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GenActivity extends AppCompatActivity {
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
        BarTypefaceSetter.setActionBarText(this, getString(R.string.app_name));
    }
}
