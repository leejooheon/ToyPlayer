<div align="center">
    <img src="./app/src/main/res/mipmap-hdpi/ic_launcher.png" width="128" height="128" style="display: block; margin: 0 auto"/>
    <h1>MusicToyProject</h1>
    <p>An Android application for play music</p>
</div>

---

## Features
- Play (almost) any song (local, remote, assets)
- Background playback
- Cache audio with AES encryption and decrypt and play the file.
- load the URL of the MediaItem when preparing for playback. (ResolvingDataSource.Resolver)
- using Media3 - MediaLibraryService
- Local playlist management
- Light/Dark/Dynamic theme
- Android Auto (it will be later)

## Architecture
Alkaa architecture is strongly based on
the [Hexagonal Architecture](https://alistair.cockburn.us/hexagonal-architecture/) by Alistair
Cockburn.

* **app** - The Application module. It contains all the initialization logic for the Android
  environment.
* **features** - The module/folder containing all the features (visual or not) from the application.
* **domain** - The modules containing the most important part of the application: the business
  logic. This module depends only on itself and all interaction it does is via _dependency
  inversion_.
* **data** - The module containing the data (local, remote, light etc) from the app.

## Disclaimer
This project is an app that only simplifies playback.