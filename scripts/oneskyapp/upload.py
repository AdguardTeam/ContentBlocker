#!/usr/bin/python3

import os
import sys
import optparse
import urllib3
import certifi

parser = optparse.OptionParser(usage="%prog [options]. %prog -h for help.")
parser.add_option("-f", "--file", dest="fileName", help="File name", metavar="FILE")
parser.add_option("-l", "--locale", dest="locale", help="Translation locale (two-character)", metavar="LOCALE")
parser.add_option("-p", "--project", dest="projectId", help="Oneskyapp project ID", metavar="PROJECT_ID")
(options, args) = parser.parse_args(sys.argv)
if (not options.fileName):
    parser.error('File name not given')
if (not options.locale):
    parser.error('Locale not given')
if (not options.projectId):
    parser.error('Project ID not given')

print("Uploading " + options.fileName + " for " + options.locale + " locale to Crowdin")

url = 'https://twosky.adtidy.org/api/v1/upload'

http = urllib3.PoolManager(cert_reqs='CERT_REQUIRED', ca_certs=certifi.where())
content = open(options.fileName, "rb").read()

fileName = os.path.basename(options.fileName)

params = {
    'filename': fileName,
    'format': 'xml',
    'language': options.locale,
    'project': 'content_blocker',
    'file': (fileName, content),
}

response = http.request('POST', 'https://twosky.adtidy.org/api/v1/upload', params)

#print('SERVER RESPONSE:')
#print(response.status, "\n", response.data)

if response.status in range(200, 299):
    print("Upload finished successfully.")
else:
    print("Error uploading file!")
    print(response.info(), "\n", response.data, "\n", response.status)