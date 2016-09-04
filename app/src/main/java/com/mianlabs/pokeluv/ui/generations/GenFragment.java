package com.mianlabs.pokeluv.ui.generations;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.mianlabs.pokeluv.R;
import com.mianlabs.pokeluv.utilities.PokePicker;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GenFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = GenFragment.class.getSimpleName();
    public static final String GEN_FRAG_KEY = "GenFragment";

    private Context mContext;
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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_gen, container, false);
        ButterKnife.bind(this, rootView);
        mContext = getActivity();
        mCustomFont = Typeface.createFromAsset(mContext.getAssets(), getString(R.string.font_path));
        setCustomTypefaceForViews();

        mGenIButton.setOnClickListener(this);
        mGenIIButton.setOnClickListener(this);
        mGenIIIButton.setOnClickListener(this);
        mGenIVButton.setOnClickListener(this);
        mGenVButton.setOnClickListener(this);
        mGenVIButton.setOnClickListener(this);

        return rootView;
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
    @Override
    public void onClick(View v) {
        PokePicker.Generations genVal;
        switch (v.getId()) {
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
            bundle.putParcelable(GEN_FRAG_KEY, genVal);
            pokeList.setArguments(bundle);
            getFragmentManager().beginTransaction().replace(R.id.generations_container, pokeList)
                    .addToBackStack(null).commit();
        }
    }
}
