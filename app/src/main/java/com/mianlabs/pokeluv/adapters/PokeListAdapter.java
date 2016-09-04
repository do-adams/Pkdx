package com.mianlabs.pokeluv.adapters;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.mianlabs.pokeluv.R;
import com.mianlabs.pokeluv.ui.MainActivity;
import com.mianlabs.pokeluv.ui.generations.PokeList;
import com.mianlabs.pokeluv.utilities.PokePicker;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PokeListAdapter extends RecyclerView.Adapter<PokeListAdapter.PokeViewHolder> {
    private AppCompatActivity mContext;
    private int[] mPokemon;

    public PokeListAdapter(AppCompatActivity context, int[] pokemons) {
        mContext = context;
        mPokemon = pokemons;
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
            // mImageView = (ImageView) itemView.findViewById(R.id.pokemon_list_img);
        }

        /**
         * Sets the image with the provided resId and sets the onClickListener for the View.
         */
        public void bindPokemonImg(final int pokemonNumber) {
            int resId = PokePicker.GenNumbers.getDrawableResourceFromNumber(mContext, pokemonNumber);
            Picasso.with(mContext).load(resId).into(mImageView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, MainActivity.class);
                    intent.putExtra(PokeList.POKE_LIST_KEY, pokemonNumber);
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
