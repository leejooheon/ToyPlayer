<div align="center">
    <img src="./app/src/main/res/mipmap-hdpi/ic_launcher.png" width="128" height="128" style="display: block; margin: 0 auto"/>
    <h1>ToyPlayer</h1>
    <p>An Android application for play music</p>
</div>

This project is my digital playground where I am learning.
I'm testing new technologies and ideas here.
If you like to contribute, that is very appreciated.

---

## Features
- play (almost) any song (local, remote, assets)
- android Auto
- background playback
- cache audio with AES encryption and decrypt and play the file.
- late init media url when preparing playback. (ResolvingDataSource.Resolver)
- using Media3 - MediaLibraryService
- local playlist management
- light/dark/dynamic theme
- multiple language (eng, kor)

## Preview
<p float="left">
  <img src="./screenshot/screen_main.png" height="600" width="270" >
  <img src="./screenshot/screen_full_player.png" height="600" width="270" >
  <img src="./screenshot/screen_playing_queue.png" height="600" width="270" >
  <img src="./screenshot/screen_album.png" height="600" width="270" >
  <img src="./screenshot/screen_artist.png" height="600" width="270" >
</p>

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