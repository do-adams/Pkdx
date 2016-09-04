package com.mianlabs.pokeluv.utilities;

import android.os.Parcel;
import android.os.Parcelable;

public class PokePicker {
    /**
     * Warning: only use this enum for passing around between activities/fragments, NOT for db storage.
     */
    public enum Generations implements Parcelable {
        GEN_I, GEN_II, GEN_III, GEN_IV, GEN_V, GEN_VI;

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(final Parcel dest, final int flags) {
            dest.writeInt(ordinal());
        }

        public static final Creator<Generations> CREATOR = new Creator<Generations>() {
            @Override
            public Generations createFromParcel(final Parcel source) {
                return Generations.values()[source.readInt()];
            }

            @Override
            public Generations[] newArray(final int size) {
                return new Generations[size];
            }
        };
    }
}
