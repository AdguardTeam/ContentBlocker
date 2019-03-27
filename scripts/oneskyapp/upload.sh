#!/bin/bash
apikey=$1
secretkey=$2
project=$3

echo "Uploading strings.xml for EN locale"
python upload.py -l en-US -p $project -f ../../adguard_cb/src/main/res/values/strings.xml -a $apikey -s $secretkey -r ANDROID_XML

echo "Upload finished"