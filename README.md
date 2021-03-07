# About

stadtplan-app is an Android app that provides offline maps for a
geographic region such as a city or an island. It works completely offline
and offers maps and a search index.

# License

This library is released under the terms of the GNU General Public
License. See [GPL.md](GPL.md) for details.

# Building from source

Clone the repository recursively (including submodules):

    git clone --recursive https://github.com/topobyte/android-stadtplan.git

You need a map file and database to build the app in directory
`app/src/main/assets`. The map file is called `map.xmap.jet` and the
database is called `map.sqlite.jet`.

You can download example files for Berlin using this script:

    ./get-berlin-data.sh

Create a `local.properties` file:

    sdk.dir=/path/to/Android/Sdk/on/your/system

To create release builds, create a `keystore.properties` file:

    storeFile=/path/to/your/release.keystore
    storePassword=your-store-password
    keyAlias=your-key-alias
    keyPassword=your-key-password

Build the application:

    ./gradlew assembleGooglePaidRelease

Install:

    adb install app/build/outputs/apk/googlePaid/release/app-google-paid-release.apk
