# Pkdx
A retro-themed dynamic Pokédex for Android.

<p align="center">
<a href="https://github.com/do-adams/Pkdx"><img src="/Pkdx_Poster.png" height="473" width="357"></a>
</p>

<b>Pkdx</b> (pronounced "Pikadex") is a unique Pokédex app for Android that implements the main game mechanic from the original Pokémon series of videogames. It encourages the user to continually use the app both to increase his understanding of the Pokémon world and to catch all of the available Pokémon and complete his Pokédex.

Upon use, <b>Pkdx</b> presents its user with a caught wild Pokémon. The user can then swipe down to catch another one, among other things. Interaction with the app comes naturally to users who are familiar with the original Pokémon videogame releases.

<b>User-features include:</b>

* All 721 (as of the time of original development) Pokémon make their appearance.
* Innovative, nostalgic, and responsive UI design based on the original Pokémon Red, Blue, and Yellow Gameboy Color games.
* Original sprites and sounds from the Pokémon universe.
* Ability to browse through collected Pokémon and compare description and ability data.
* Option to transfer and view favorite Pokémon to and from a PC Box.
* Custom app widget for catching hundreds of Pokémon directly from the home screen.

<b>Development features include:</b>

* Cached instance of scraped [PokeApi](http://pokeapi.co/) data for fast, native access to Pokémon content.
* Responsive UI practices for 7" Android tablets and similarly sized devices.
* Implementation of custom fonts and Action Bar titles for Views and Activities.
* Dynamically generated color palettes using the [Picasso](https://github.com/square/picasso) and [PicassoPalettes](https://github.com/florent37/PicassoPalette) libraries.
* Uses its own ContentProvider to store and access DB data including SQLite tables that keep track of the user's caught and favorited Pokémon.
* Novel use of Android's App Widgets to deliver cute Pokémon sprites to the Android home screen.

# Getting Started

To get started, simply clone this repository in Android Studio and run a debug or release build instance of this app in your device or emulator. No API keys or additional `.json` files needed.

The necessary Proguard rules are included in the `proguard-rules.pro` file for successful generation of APKs.

# Developer's Note

I made this app specifically for my little nephew. I recall introducing him to Pokémon Go in mid July and seeing him burst into cries of wonder and excitement as I got ready to take him out on our very own "Pokémon Safari" in my small neighborhood.

The experience really touched me and reminded me of my most purest aim with technology: to deliver a sort of `magic` that can radically enrich and improve other people's lives. <b>Pkdx</b> is an expression of that, and it has served its purpose well - providing me with hours of bonding and bliss with my own nephew.

If I can do the same for anyone else with my work then I will have considered it a massive success. If <b>Pkdx</b> managed to do it for you or if you simply want to get in touch, I'd love to hear from you. You can reach me [here](https://mianlabs.com/contact/).




