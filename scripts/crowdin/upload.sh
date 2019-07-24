#!/bin/bash
project=content_blocker

# Moving to a folder with scripts
cd $(dirname $0)

# Installing missing python packages
python3 -c "import requests" &> /dev/null || pip3 install requests

# Upload current translations. Uncomment if necessary.
#
#locales=("ar" "da" "de" "es" "fa" "hr" "hu" "it"
#        "ko" "fr" "lv" "mk" "nl" "no" "pl" "ru"
#        "sv" "tr" "uk" "be" "cs" "bn" "sk" "ja")

#locales_exceptions=( "pt-BR:pt-rBR" "pt-PT:pt-rPT" "zh-TW:zh-rTW" "zh-CN:zh-rCN" "id:in" "zh-TW:zh-rTW")

#for i in ${locales[@]}
#do
#    echo "Start uploading strings.xml for $i locale"
#    python3 upload.py -l $i -p  $project -f ../../adguard_cb/src/main/res/values-$i/strings.xml
#    echo "Finish uploading strings.xml for $i locale"
#done
#
#for i in ${locales_exceptions[@]}
#do
#    KEY=${i%%:*}
#    VALUE=${i##*:}
#
#    echo "Start uploading strings.xml for $KEY locale"
#    python3 upload.py -l $KEY -p $project -f ../../adguard_cb/src/main/res/values-$VALUE/strings.xml
#done

echo "Uploading strings.xml for EN locale"
python3 upload.py -l en -p $project -f ../../adguard_cb/src/main/res/values/strings.xml

echo "Upload finished"