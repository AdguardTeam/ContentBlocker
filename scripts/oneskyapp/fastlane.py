#!/usr/bin/python
import json
import md5
import optparse
import os.path
import re
import sys
import time
import urllib2
from exceptions import RuntimeError

parser = optparse.OptionParser(usage="%prog [options]. %prog -h for help.")
parser.add_option("-a", "--apikey", dest="apiKey", help="Oneskyapp API public key",
                  metavar="APIKEY")
parser.add_option("-s", "--secretkey", dest="secretKey", help="Oneskyapp API secretkey key",
                  metavar="SECRETKEY")
parser.add_option("-l", "--locale", dest="locale", help="Translation locale (two-character)",
                  metavar="LOCALE")
parser.add_option("-p", "--project", dest="projectId", help="Oneskyapp project ID",
                  metavar="PROJECT_ID")
parser.add_option("-o", "--output", dest="output", help="Output folder", metavar="FILE")
(options, args) = parser.parse_args(sys.argv)

if (not options.apiKey):
    parser.error('API public key not given')
if (not options.secretKey):
    parser.error('API secret key not given')
if (not options.locale):
    parser.error('Locale not given')
if (not options.projectId):
    parser.error('Project ID not given')
if (not options.output):
    parser.error('Output folder not given')

timestamp = str(int(time.time()))
devHash = md5.new(timestamp + options.secretKey).hexdigest()

url = "https://platform.api.onesky.io/1/projects/"
url += options.projectId
url += "/translations/app-descriptions?locale="
url += options.locale
url += "&api_key="
url += options.apiKey
url += "&timestamp="
url += timestamp
url += "&dev_hash="
url += devHash


def downloadAppDescriptions(url):
    print "Downloading the app description for " + options.locale + " from Oneskyapp"
    resp = ""
    for x in range(0, 5):
        if x > 0:
            print "    Retrying download (%d/5)..." % (x + 1)
        response = urllib2.urlopen(url)
        resp = response.read()
        if len(resp) > 0:
            break
        time.sleep(3)
    if len(resp) == 0:
        raise RuntimeError("Error downloading app description!")
    return resp


def removeGarbage(html):
    return re.sub('\{"code":500.*$', '', html)


response = downloadAppDescriptions(url)
response = removeGarbage(response).strip()

json = json.loads(response)
if (json["meta"]["status"] == 200):
    if not os.path.exists(options.output):
        os.makedirs(options.output)

    title_file = os.path.join(options.output, "title.txt")
    with open(title_file, "wb") as f:
        f.write(json["data"]["TITLE"].encode("UTF-8"))

    short_description_file = os.path.join(options.output, "short_description.txt")
    with open(short_description_file, "wb") as f:
        f.write(json["data"]["SHORT_DESCRIPTION"].encode("UTF-8"))

    full_description_file = os.path.join(options.output, "full_description.txt")
    with open(full_description_file, "wb") as f:
        f.write(json["data"]["DESCRIPTION"].encode("UTF-8"))

    print "App description has been successfully downloaded to " + options.output
else:
    print "Error downloading app description!"
