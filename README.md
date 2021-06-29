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

## Add local map and database files

You need a map file and database to build the app in directory
`app/src/main/assets`. The map file is called `map.xmap.jet` and the
database is called `map.sqlite.jet`.

You can download example files for Berlin using this script:

    ./data/get-berlin-data.sh

There a few more scripts for other cities:

    ./data/get-bayreuth-data.sh
    ./data/get-cambridge-data.sh
    ./data/get-cottbus-data.sh

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
