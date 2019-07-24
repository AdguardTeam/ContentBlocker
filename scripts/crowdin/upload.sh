#!/bin/bash
project=content_blocker

# Moving to a folder with scripts
cd $(dirname $0)

# Installing missing python packages
python3 -c "import requests" &> /dev/null || pip3 install requests

echo "Uploading strings.xml for EN locale"
python3 upload.py -l en -p $project -f ../../adguard_cb/src/main/res/values/strings.xml

echo "Upload finished"