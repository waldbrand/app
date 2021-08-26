# About

This is the Android app of the Waldbrand project.
It is designed to work completely offline and allow fire fighters
to find hydrants and other sources of water supply.

Data is based on OpenStreetMap and should be updated regularly from
an online source in order to have current data on the device.

# License

This software is released under the terms of the GNU General Public
License. See [GPL.md](GPL.md) for details.

# Building from source

## Get the source

Clone the repository recursively (including submodules):

    git clone --recursive https://github.com/waldbrand/app.git

Afterwards go to the directory `waldbrand-app` for the following
steps.

## Add local map and database files

You need a few asset files that contain map data in order to build the app.
Those files need to be put in directory
`app/src/main/assets`.
You need a map file called `map.xmap.jet` for the base layers from
OpenStreetMap, a database called `map.sqlite.jet` for the search
functionality and another map file called `waldbrand.xmap.jet`
that contains hydrants, emergency access points etc.

Those files can be obtained or created as described in the
[waldbrand/website](https://github.com/waldbrand/website)
repository.

## Android SDK

Create a `local.properties` file:

    sdk.dir=/path/to/Android/Sdk/on/your/system

## Debug builds

Build the application:

    ./gradlew assembleDebug

Install:

    adb install app/build/outputs/apk/debug/app-debug.apk

## Release builds

To create release builds, create a `keystore.properties` file:

    storeFile=/path/to/your/release.keystore
    storePassword=your-store-password
    keyAlias=your-key-alias
    keyPassword=your-key-password

Build the application:

    ./gradlew assembleRelease

Install:

    adb install app/build/outputs/apk/release/app-release.apk
