/*
 * Copyright (C) 2016 Damián Adams
 */
package com.mianlabs.pokeluv.adapters;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.mianlabs.pokeluv.R;
import com.mianlabs.pokeluv.ui.main.MainActivity;
import com.mianlabs.pokeluv.ui.generations.PokeListFragment;
import com.mianlabs.pokeluv.utilities.PokePicker;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Adapter for use with populating a list of Pokemon.
 * Pokemon to be shown in the list will depend on the numbers
 * present in the array of Pokemon numbers provided.
 *
 * Sets an OnClickListener for each Pokemon sprite that launches an intent to
 * MainActivity with the Pokemon's number id.
 *
 * Relies heavily on the PokePicker.GenerationNumbers.getDrawableResourceFromNumber method.
 */
public class PokeListAdapter extends RecyclerView.Adapter<PokeListAdapter.PokeViewHolder> {
    private AppCompatActivity mContext;
    private int[] mPokemon;

    public PokeListAdapter(AppCompatActivity context, int[] pokemon) {
        mContext = context;
        mPokemon = pokemon;
    }

    @Override
    public PokeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.pokemon_list_item, parent, false); // Change layout to custom.
        return new PokeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PokeViewHolder holder, int position) {
        holder.bindPokemonImg(mPokemon[position]);
    }

    @Override
    public int getItemCount() {
        return mPokemon.length;
    }

    public class PokeViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.pokemon_list_img)
        ImageView mImageView;

        public PokeViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        /**
         * Sets the image with the provided resId and sets the onClickListener for the View
         * to launch the MainActivity and its PokeFragment with this Pokemon's number id.
         */
        public void bindPokemonImg(final int pokemonNumber) {
            int resId = PokePicker.GenerationNumbers.getDrawableResourceFromNumber(mContext, pokemonNumber);
            Picasso.with(mContext).load(resId).into(mImageView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, MainActivity.class);
                    intent.putExtra(PokeListFragment.POKE_LIST_FRAG_KEY, pokemonNumber);
                    mContext.startActivity(intent);
                }
            });
        }

        /**
         * Returns the View assigned to its corresponding ViewHolder.
         */
        public View getView() {
            return itemView;
        }
    }
}
