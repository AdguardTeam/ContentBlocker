#!/bin/bash
project=content_blocker_google_play
cache=../../fastlane/metadata/android/strings-cache.json
input=strings.json

# Moving to a folder with scripts
cd $(dirname $0)

# Installing missing python packages
python3 -c "import requests" &> /dev/null || pip3 install requests

locales=("ru" "fr" "de" "zh-TW" "zh-CN")
for i in ${locales[@]}
do
    python3 fastlane.py -l $i -p $project -o ../../fastlane/metadata/android/$i -c $cache -i $input
done

# Exception

python3 fastlane.py -l en -p $project -o ../../fastlane/metadata/android/en-US -c $cache -i $input