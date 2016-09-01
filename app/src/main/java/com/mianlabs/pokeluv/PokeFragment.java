package com.mianlabs.pokeluv;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.sargunvohra.lib.pokekotlin.client.PokeApi;
import me.sargunvohra.lib.pokekotlin.client.PokeApiClient;
import me.sargunvohra.lib.pokekotlin.model.PokemonSpecies;

public class PokeFragment extends Fragment {
    private static final String TAG = PokeFragment.class.getSimpleName();
    private Activity mContext;

    @BindView(R.id.pokemon)
    TextView mPokemonTextView;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Retain this fragment across configuration changes.
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View viewRoot = inflater.inflate(R.layout.fragment_poke, container, false);
        ButterKnife.bind(this, viewRoot);

        mContext = getActivity(); // grabs the context from the parent activity.

        if (isNetworkAvailable()) {
            Log.d(TAG, "Making network request");
            new Thread(new Runnable() { // makes background thread for networking.
                @Override
                public void run() {
                    PokeApi pokeApi = new PokeApiClient();
                    final PokemonSpecies bulbasaur = pokeApi.getPokemonSpecies(1);

                    Handler handler = new Handler(Looper.getMainLooper()); // grabs UI thread.
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            mPokemonTextView.setText(bulbasaur.toString());
                            Log.d(TAG, "Posting data to text field");
                        }
                    });
                }
            }).start();
        }

        return viewRoot;
    }

    /**
     * Checks to ensure the user can connect to the network,
     * as per Google's Android Developer guidelines.
     */
    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        //This method requires permission ACCESS_NETWORK_STATE
        boolean isAvailable = false;
        if (networkInfo != null && networkInfo.isConnected()) {
            //Checks if a network is present and connected
            isAvailable = true;
        }
        return isAvailable;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
