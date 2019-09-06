#!/bin/bash

# Moving to a folder with scripts
cd $(dirname $0)

# Installing missing python packages
python3 -c "import requests" &> /dev/null || pip3 install requests
python3 -c "import xlrd" &> /dev/null || pip3 install xlrd

python3 fastlane.py -p content_blocker_google_play -o ../../fastlane/metadata/android -i AppDescription.xlsx -c ../../.twosky.json
