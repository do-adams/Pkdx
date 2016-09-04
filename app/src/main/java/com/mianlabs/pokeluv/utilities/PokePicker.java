package com.mianlabs.pokeluv.utilities;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.Arrays;

public class PokePicker {
    private static final String TAG = PokePicker.class.getSimpleName();

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

    /**
     * Class for retrieving an array of numbers that includes all Pokemons introduced in
     * any Generation.
     */
    public static class GenNums {
        /**
         * Use if you need to debug this class.
         */
        public static void writeTestLogs() {
            Log.d(TAG, "Gen I: " + Arrays.toString(getGenOne()));
            Log.d(TAG, "Gen II: " + Arrays.toString(getGenTwo()));
            Log.d(TAG, "Gen III: " + Arrays.toString(getGenThree()));
            Log.d(TAG, "Gen IV: " + Arrays.toString(getGenFour()));
            Log.d(TAG, "Gen V: " + Arrays.toString(getGenFive()));
            Log.d(TAG, "Gen VI: " + Arrays.toString(getGenSix()));
        }

        public static int[] getNumsArray(int startingNum, int finalNum) {
            int size = finalNum - startingNum + 1;
            int[] gen = new int[size];
            for (int i = 0; i < size; i++) {
                gen[i] = startingNum++;
            }
            return gen;
        }

        public static int[] getGenOne() {
            return getNumsArray(1, 151);
        }

        public static int[] getGenTwo() {
            return getNumsArray(152, 251);
        }

        public static int[] getGenThree() {
            return getNumsArray(252, 386);
        }

        public static int[] getGenFour() {
            return getNumsArray(387, 493);
        }

        public static int[] getGenFive() {
            return getNumsArray(494, 649);
        }

        public static int[] getGenSix() {
            return getNumsArray(650, 721);
        }
    }
}
