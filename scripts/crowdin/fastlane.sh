#!/bin/bash
project=content_blocker_google_play
input=AppDescription.xlsx

# Moving to a folder with scripts
cd $(dirname $0)

# Installing missing python packages
python3 -c "import requests" &> /dev/null || pip3 install requests
python3 -c "import xlrd" &> /dev/null || pip3 install xlrd

locales=("ru:Russian" "fr:French" "de:German" "zh-TW:ChineseTraditional" "zh-CN:ChineseSimplified")
for i in ${locales[@]}
do
    KEY=${i%%:*}
    VALUE=${i##*:}
    python3 fastlane.py -l $KEY -p $project -o ../../fastlane/metadata/android/$KEY -c ../../fastlane/metadata/android/$KEY/cache.xlsx -i $input -m $VALUE
done

# Exception

python3 fastlane.py -l en -p $project -o ../../fastlane/metadata/android/en-US -c ../../fastlane/metadata/android/en-US/cache.xlsx -i $input -m English