#!/bin/bash

DIR=$(dirname $0)
REPO="$DIR/../"
MATERIAL="$REPO/../material"
MARKERS="$MATERIAL/marker/vectorized-font"

SvgToBvg "$MARKERS/hydrant.svg" "$DIR/default/symbols/hydrant.bvg"
SvgToBvg "$MARKERS/hydrant-unklar.svg" "$DIR/default/symbols/hydrant-unklar.bvg"
SvgToBvg "$MARKERS/speicher.svg" "$DIR/default/symbols/speicher.bvg"
SvgToBvg "$MARKERS/tiefbrunnen.svg" "$DIR/default/symbols/tiefbrunnen.bvg"
SvgToBvg "$MARKERS/saugstelle.svg" "$DIR/default/symbols/saugstelle.bvg"
