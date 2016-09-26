package com.mianlabs.pkdx.ui.about;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.mianlabs.pkdx.R;
import com.mianlabs.pkdx.utilities.typeface.TypefaceUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AboutActivity extends AppCompatActivity {
    Typeface mCustomFont;

    @BindView(R.id.about_title)
    TextView mTitle;
    @BindView(R.id.about_tagline)
    TextView mTagline;
    @BindView(R.id.about_legal_disclaimer_title)
    TextView mLegalDisclaimerTitle;
    @BindView(R.id.about_legal_disclaimer)
    TextView mLegalDisclaimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);
        mCustomFont = Typeface.createFromAsset(getAssets(), getString(R.string.font_path));
        setCustomTypefaceForViews(mCustomFont);
    }

    @Override
    protected void onResume() {
        super.onResume();
        TypefaceUtils.setActionBarTitle(this, getString(R.string.about_name));
    }

    private void setCustomTypefaceForViews(Typeface customFont) {
        mTitle.setTypeface(customFont);
        mTagline.setTypeface(customFont);
        mLegalDisclaimerTitle.setTypeface(customFont);
        mLegalDisclaimer.setTypeface(customFont);
    }
}
