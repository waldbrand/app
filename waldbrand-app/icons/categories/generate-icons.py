#!/usr/bin/python3

import os
import re
import functools
import shlex, subprocess
import tempfile
import shutil

colors = ["#734A08","#0092DA", "#ffa024", "#ac39ac", "#39ac39", "#999999", "#DA0092", "#FFd806", "#000000"]
allnames = [
    ["attraction", "bar", "biergarten", "cafe", "cinema", "fastfood", \
    "museum", "nightclub", "pub", "restaurant", "theatre", "worship", \
    "memorial", "library", "embassy", "casino", "playground", "townhall", \
    "prison", "bakery", "view_point", "firestation", "post_office"], \
    ["busstop", "busstation", "railstation", "tramstop", "hotel", "hostel", \
    "guesthouse", "camping", "helicopter", "airport", "information", "bikerental"], \
    ["university", "school", "kindergarten"], \
    ["shoes", "jewelry", "clothes", "supermarket", "book", "kiosk", "shop_other"], \
    ["street", "pharmacy", "park", "cemetery", "scrub", "florist", \
    "peak", "volcano", "shelter"], \
    ["place", "misc", "christian", "islamic", "jewish", "atm", "bank", "hairdresser"], \
    ["hospital", "doctors", "dentist", "veterinary", "opticians"], \
    ["bicycle", "car", "diy", "fuel"], \
    []]

sizes = [48, 72]
dirs = ["drawable", "drawable-hdpi"]

dirSvgInput = "svg/"
dirColoredSvgs = "svg-colored/"
dirPngs = "../../app/src/main/res/"

os.makedirs(dirColoredSvgs, exist_ok=True)

for x in range(len(colors)):
    color = colors[x]
    names = allnames[x]
    print(colors[x])
    for name in names:
        fInput = dirSvgInput + name + ".svg"
        fColored = dirColoredSvgs + name + ".svg"

        print(fInput + " -> " + fColored)
        f = open(fInput)
        svg = f.read()
        f.close()

        # c1 background fill
        # c2 background stroke
        # c3 forground
        c1 = color
        c2 = "none"
        c3 = "#ffffff"

        svg = re.sub("fill:#111111", "fill:" + c1, svg)
        svg = re.sub("fill:#111", "fill:" + c1, svg)
        svg = re.sub("stroke:#eeeeee", "stroke:" + c2, svg)
        svg = re.sub("stroke:#eee", "stroke:" + c2, svg)
        svg = re.sub("fill:white", "fill:" + c3, svg)
        svg = re.sub("stroke:white", "stroke:" + c3, svg)
        svg = re.sub("fill:#ffffff", "fill:" + c3, svg)
        svg = re.sub("stroke:#ffffff", "stroke:" + c3, svg)

        f = open(fColored, "w")
        f.write(svg)
        f.close()

names = functools.reduce(lambda x,y: x+y, allnames)

for name in names:
    fColored = dirColoredSvgs + name + ".svg"
    for s in range(len(sizes)):
        size = sizes[s]
        outputDir = dirPngs + dirs[s] + "/"
        output = outputDir + "cat_" + name + ".png"
        if not os.path.isdir(outputDir):
            os.makedirs(outputDir, exist_ok=True)
        print(fColored + " -> " + output)

        cmd = "rsvg-convert -f png -w " + str(size) + " -h " + str(size) \
            + " -o " + output + " " + fColored
        cmd = "inkscape -C -e " + output + " -h " + str(size) \
            + " -w " + str(size) + " " + fColored
        args = shlex.split(cmd)
        p = subprocess.call(args)

        tmp = tempfile.mkstemp(".png")[1]
        shutil.copyfile(output, tmp)

        cmd = "pngcrush " + tmp + " " + output
        args = shlex.split(cmd)
        print(args)
        p = subprocess.call(args)

        os.remove(tmp)
