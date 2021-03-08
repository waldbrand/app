# About

stadtplan-app is an Android app that provides offline maps for a
geographic region such as a city or an island. It works completely offline
and offers maps and a search index.

# License

This library is released under the terms of the GNU General Public
License. See [GPL.md](GPL.md) for details.

# Building from source

## Get the source

Clone the repository recursively (including submodules):

    git clone --recursive https://github.com/topobyte/android-stadtplan.git

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

    ./gradlew assembleGooglePaidDebug

Install:

    adb install app/build/outputs/apk/googlePaid/debug/app-google-paid-debug.apk

## Release builds

To create release builds, create a `keystore.properties` file:

    storeFile=/path/to/your/release.keystore
    storePassword=your-store-password
    keyAlias=your-key-alias
    keyPassword=your-key-password

Build the application:

    ./gradlew assembleGooglePaidRelease

Install:

    adb install app/build/outputs/apk/googlePaid/release/app-google-paid-release.apk
