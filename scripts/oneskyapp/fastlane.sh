#!/bin/bash
apikey=$1
secretkey=$2
project=$3

python fastlane.py -l en-US -p $project -o ../../fastlane/metadata/android/en-US -a $apikey -s $secretkey
python fastlane.py -l ru -p $project -o ../../fastlane/metadata/android/ru -a $apikey -s $secretkey
python fastlane.py -l fr -p $project -o ../../fastlane/metadata/android/fr -a $apikey -s $secretkey
python fastlane.py -l de -p $project -o ../../fastlane/metadata/android/de -a $apikey -s $secretkey
python fastlane.py -l zh-TW -p $project -o ../../fastlane/metadata/android/zh-TW -a $apikey -s $secretkey
python fastlane.py -l zh-CN -p $project -o ../../fastlane/metadata/android/zh-CN -a $apikey -s $secretkey