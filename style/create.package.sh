#!/bin/bash

DIR=$(dirname $0)
cd "$DIR/default"

FILE="../../app/src/main/assets/style-default.zip"
zip -r -FS "$FILE" classes.xml labels.xml patterns/ symbols/
