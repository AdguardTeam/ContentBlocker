#!/bin/bash
apikey=$1
secretkey=$2
project=$3

echo "Download strings.xml for EN locale"
python download.py -l en-US -p $project -o strings.xml -f strings.xml -a $apikey -s $secretkey
echo -e "Moving string.xml for EN locale\n"
cp -f strings.xml ../../adguard_cb/src/main/res/values/strings.xml
rm strings.xml

echo "Download strings.xml for AR locale"
python download.py -l ar -p $project -o strings.xml -f strings.xml -a $apikey -s $secretkey
echo -e "Moving string.xml for AR locale\n"
cp -f strings.xml ../../adguard_cb/src/main/res/values-ar/strings.xml
rm strings.xml

echo "Download strings.xml for DA locale"
python download.py -l da -p $project -o strings.xml -f strings.xml -a $apikey -s $secretkey
echo -e "Moving string.xml for DA locale\n"
cp -f strings.xml ../../adguard_cb/src/main/res/values-da/strings.xml
rm strings.xml

echo "Download strings.xml for DE locale"
python download.py -l de -p $project -o strings.xml -f strings.xml -a $apikey -s $secretkey
echo -e "Moving string.xml for DE locale\n"
cp -f strings.xml ../../adguard_cb/src/main/res/values-de/strings.xml
rm strings.xml

echo "Download strings.xml for ES locale"
python download.py -l es -p $project -o strings.xml -f strings.xml -a $apikey -s $secretkey
echo -e "Moving string.xml for ES locale\n"
cp -f strings.xml ../../adguard_cb/src/main/res/values-es/strings.xml
rm strings.xml

echo "Download strings.xml for FA locale"
python download.py -l fa -p $project -o strings.xml -f strings.xml -a $apikey -s $secretkey
echo -e "Moving string.xml for FA locale\n"
cp -f strings.xml ../../adguard_cb/src/main/res/values-fa/strings.xml
rm strings.xml

echo "Download strings.xml for HR locale"
python download.py -l hr -p $project -o strings.xml -f strings.xml -a $apikey -s $secretkey
echo -e "Moving string.xml for HR locale\n"
cp -f strings.xml ../../adguard_cb/src/main/res/values-hr/strings.xml
rm strings.xml

echo "Download strings.xml for HU locale"
python download.py -l hu -p $project -o strings.xml -f strings.xml -a $apikey -s $secretkey
echo -e "Moving string.xml for HU locale\n"
cp -f strings.xml ../../adguard_cb/src/main/res/values-hu/strings.xml
rm strings.xml

echo "Download strings.xml for IT locale"
python download.py -l it -p $project -o strings.xml -f strings.xml -a $apikey -s $secretkey
echo -e "Moving string.xml for IT locale\n"
cp -f strings.xml ../../adguard_cb/src/main/res/values-it/strings.xml
rm strings.xml

echo "Download strings.xml for JA locale"
python download.py -l ja -p $project -o strings.xml -f strings.xml -a $apikey -s $secretkey
echo -e "Moving string.xml for JA locale\n"
cp -f strings.xml ../../adguard_cb/src/main/res/values-ja/strings.xml
rm strings.xml

echo "Download strings.xml for KO locale"
python download.py -l ko -p $project -o strings.xml -f strings.xml -a $apikey -s $secretkey
echo -e "Moving string.xml for KO locale\n"
cp -f strings.xml ../../adguard_cb/src/main/res/values-ko/strings.xml
rm strings.xml

echo "Download strings.xml for MK locale"
python download.py -l mk-MK -p $project -o strings.xml -f strings.xml -a $apikey -s $secretkey
echo -e "Moving string.xml for MK locale\n"
cp -f strings.xml ../../adguard_cb/src/main/res/values-mk/strings.xml
rm strings.xml

echo "Download strings.xml for NL locale"
python download.py -l nl -p $project -o strings.xml -f strings.xml -a $apikey -s $secretkey
echo -e "Moving string.xml for NL locale\n"
cp -f strings.xml ../../adguard_cb/src/main/res/values-nl/strings.xml
rm strings.xml

echo "Download strings.xml for NO locale"
python download.py -l no -p $project -o strings.xml -f strings.xml -a $apikey -s $secretkey
echo -e "Moving string.xml for NO locale\n"
cp -f strings.xml ../../adguard_cb/src/main/res/values-no/strings.xml
rm strings.xml

echo "Download strings.xml for PL locale"
python download.py -l pl -p $project -o strings.xml -f strings.xml -a $apikey -s $secretkey
echo -e "Moving string.xml for PL locale\n"
cp -f strings.xml ../../adguard_cb/src/main/res/values-pl/strings.xml
rm strings.xml

echo "Download strings.xml for PT-BR locale"
python download.py -l pt-BR -p $project -o strings.xml -f strings.xml -a $apikey -s $secretkey
echo -e "Moving string.xml for PT-BR locale\n"
cp -f strings.xml ../../adguard_cb/src/main/res/values-pt-rBR/strings.xml
rm strings.xml

echo "Download strings.xml for PT-PT locale"
python download.py -l pt-PT -p $project -o strings.xml -f strings.xml -a $apikey -s $secretkey
echo -e "Moving string.xml for PT-PT locale\n"
cp -f strings.xml ../../adguard_cb/src/main/res/values-pt-rPT/strings.xml
rm strings.xml

echo "Download strings.xml for RU locale"
python download.py -l ru -p $project -o strings.xml -f strings.xml -a $apikey -s $secretkey
echo -e "Moving string.xml for RU locale\n"
cp -f strings.xml ../../adguard_cb/src/main/res/values-ru/strings.xml
rm strings.xml

echo "Download strings.xml for SK locale"
python download.py -l sk -p $project -o strings.xml -f strings.xml -a $apikey -s $secretkey
echo -e "Moving string.xml for SK locale\n"
cp -f strings.xml ../../adguard_cb/src/main/res/values-sk/strings.xml
rm strings.xml

echo "Download strings.xml for SV locale"
python download.py -l sv -p $project -o strings.xml -f strings.xml -a $apikey -s $secretkey
echo -e "Moving string.xml for SV locale\n"
cp -f strings.xml ../../adguard_cb/src/main/res/values-sv/strings.xml
rm strings.xml

echo "Download strings.xml for TR locale"
python download.py -l tr -p $project -o strings.xml -f strings.xml -a $apikey -s $secretkey
echo -e "Moving string.xml for TR locale\n"
cp -f strings.xml ../../adguard_cb/src/main/res/values-tr/strings.xml
rm strings.xml

echo "Download strings.xml for UK locale"
python download.py -l uk -p $project -o strings.xml -f strings.xml -a $apikey -s $secretkey
echo -e "Moving string.xml for UK locale\n"
cp -f strings.xml ../../adguard_cb/src/main/res/values-uk/strings.xml
rm strings.xml

echo "Download strings.xml for ZH-TW locale"
python download.py -l zh-TW -p $project -o strings.xml -f strings.xml -a $apikey -s $secretkey
echo -e "Moving string.xml for ZH-TW locale\n"
cp -f strings.xml ../../adguard_cb/src/main/res/values-zh-rTW/strings.xml
rm strings.xml

echo "Download strings.xml for ZH-CN locale"
python download.py -l zh-CN -p $project -o strings.xml -f strings.xml -a $apikey -s $secretkey
echo -e "Moving string.xml for ZH-CN locale\n"
cp -f strings.xml ../../adguard_cb/src/main/res/values-zh-rCN/strings.xml
rm strings.xml

echo "Download strings.xml for BE locale"
python download.py -l be-BY -p $project -o strings.xml -f strings.xml -a $apikey -s $secretkey
echo -e "Moving string.xml for BE locale\n"
cp -f strings.xml ../../adguard_cb/src/main/res/values-be/strings.xml
rm strings.xml

echo "Download strings.xml for CS locale"
python download.py -l cs -p $project -o strings.xml -f strings.xml -a $apikey -s $secretkey
echo -e "Moving string.xml for CS locale\n"
cp -f strings.xml ../../adguard_cb/src/main/res/values-cs/strings.xml
rm strings.xml

echo "Download strings.xml for BN locale"
python download.py -l bn -p $project -o strings.xml -f strings.xml -a $apikey -s $secretkey
echo -e "Moving string.xml for BN locale\n"
cp -f strings.xml ../../adguard_cb/src/main/res/values-bn/strings.xml
rm strings.xml

echo "Download strings.xml for ID locale"
python download.py -l id -p $project -o strings.xml -f strings.xml -a $apikey -s $secretkey
echo -e "Moving string.xml for ID locale\n"
cp -f strings.xml ../../adguard_cb/src/main/res/values-in/strings.xml
rm strings.xml

echo "Download strings.xml for FR locale"
python download.py -l fr -p $project -o strings.xml -f strings.xml -a $apikey -s $secretkey
echo -e "Moving string.xml for FR locale\n"
cp -f strings.xml ../../adguard_cb/src/main/res/values-fr/strings.xml
rm strings.xml

echo "Download strings.xml for zh-TW locale"
python download.py -l zh-TW -p $project -o strings.xml -f strings.xml -a $apikey -s $secretkey
echo -e "Moving string.xml for zh-TW locale\n"
cp -f strings.xml ../../adguard_cb/src/main/res/values-zh-rTW/strings.xml
rm strings.xml

echo "Download strings.xml for LV locale"
python download.py -l lt-LT -p $project -o strings.xml -f strings.xml -a $apikey -s $secretkey
echo -e "Moving string.xml for LV locale\n"
cp -f strings.xml ../../adguard_cb/src/main/res/values-lv/strings.xml
rm strings.xml


echo "Import finished"