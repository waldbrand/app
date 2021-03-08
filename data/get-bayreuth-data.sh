#!/bin/bash

DIR=$(dirname $0)

OUT="$DIR/../app/src/main/assets"
MAP="$OUT/map.xmap.jet"
DB="$OUT/map.sqlite.jet"
AC="$DIR/../app/src/main/java/de/topobyte/apps/viewer/AppConstants.java"

curl http://osmtestdata.topobyte.de/stadtplan/191007/Bayreuth.xmap > "$MAP"
curl http://osmtestdata.topobyte.de/stadtplan/191007/Bayreuth.sqlite | gzip > "$DB"
cp "$DIR/app-constants/Bayreuth.java" "$AC"
