#!/usr/bin/python3

import sys
import optparse
import urllib.request, urllib.error, urllib.parse

def download_content(url):
    request = urllib.request.Request(url, headers={
        "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome"
    })

    resp = urllib.request.urlopen(request)
    content = resp.read()
    if len(content) == 0:
        raise RuntimeError("Error downloading content from %s" % url)
    resp.close()
    return content

# https://twosky.adtidy.org/api/v1/download?format=xml&language=sk&filename=strings.xml&project=android

parser = optparse.OptionParser(usage="%prog [options]. %prog -h for help.")
parser.add_option("-l", "--locale", dest="locale", help="Translation locale (two-character)", metavar="LOCALE")
parser.add_option("-p", "--project", dest="projectId", help="Project ID", metavar="PROJECT_ID")
parser.add_option("-o", "--output", dest="output", help="Output file name", metavar="FILE")
(options, args) = parser.parse_args(sys.argv)

if (not options.locale):
    parser.error('Locale not given')
if (not options.projectId):
    parser.error('Project ID not given')
if (not options.output):
    parser.error('Output file name not given')

response = download_content("https://twosky.adtidy.org/api/v1/download?format=xml&language=%s&filename=%s&project=%s" % (options.locale, options.output, options.projectId))

#print('SERVER RESPONSE:')
#print(response)

if response != "":
    print("File has been successfully downloaded to " + options.output)
    with open(options.output, "wb") as f:
        f.write(response)
else:
    print("!!!Error downloading file for ", options.locale)