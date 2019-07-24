#!/bin/bash
project=content_blocker

cd $(dirname $0)

python3 -c "import requests" &> /dev/null || pip3 install requests

locales=("ar" "da" "de" "es" "fa" "hr" "hu" "it"
        "ja" "ko"  "fr" "lv" "mk" "nl" "no" "pl"
        "ru" "sk" "sv" "tr" "uk" "be" "cs" "bn")

locales_exceptions=("pt-BR:pt-rBR" "pt-PT:pt-rPT" "zh-TW:zh-rTW"
                    "zh-CN:zh-rCN" "id:in" "zh-TW:zh-rTW")

for i in ${locales[@]}
do
    echo "Download strings.xml for $i locale"
    python3 download.py -l $i -p $project -o strings.xml
    echo -e "Moving string.xml for $i locale\n"
    cp -f strings.xml ../../adguard_cb/src/main/res/values-$i/strings.xml
    rm strings.xml
done

# Exceptions

for i in ${locales_exceptions[@]}
do
    KEY=${i%%:*}
    VALUE=${i##*:}

    echo "Download strings.xml for $KEY locale"
    python3 download.py -l $KEY -p $project -o strings.xml
    echo -e "Moving string.xml for $KEY locale\n"
    cp -f strings.xml ../../adguard_cb/src/main/res/values-$VALUE/strings.xml
    rm strings.xml
done

echo "Download strings.xml for EN locale"
python3 download.py -l en -p $project -o strings.xml
echo -e "Moving string.xml for EN locale\n"
cp -f strings.xml ../../adguard_cb/src/main/res/values/strings.xml
rm strings.xml

echo "Import finished"