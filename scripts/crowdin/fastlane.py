#!/usr/bin/python3

import sys
import optparse
import os
import xlrd
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

# https://twosky.adtidy.org/api/v1/download?format=json&language=sk&filename=strings.json&project=content_blocker_google_play

parser = optparse.OptionParser(usage="%prog [options]. %prog -h for help.")
parser.add_option("-l", "--locale", dest="locale", help="Translation locale (two-character)", metavar="LOCALE")
parser.add_option("-m", "--locale-mapping", dest="localeMapping", help="Translation locale mapping", metavar="LOCALE")
parser.add_option("-p", "--project", dest="projectId", help="Project ID", metavar="PROJECT_ID")
parser.add_option("-o", "--output", dest="output", help="Output directory name", metavar="DIRECTORY")
parser.add_option("-i", "--input", dest="input", help="Input file name", metavar="FILE")
parser.add_option("-c", "--cache-output", dest="cacheOutput", help="Cache output file name", metavar="FILE")
(options, args) = parser.parse_args(sys.argv)

if (not options.locale):
    parser.error('Locale not given')
if (not options.projectId):
    parser.error('Project ID not given')
if (not options.input):
    parser.error('Input file name not given')
if (not options.localeMapping):
    parser.error('Locale mapping name not given')
if (not options.output):
    parser.error('Output directory name not given')
if (not options.cacheOutput):
    parser.error('Output cache file name not given')

response = download_content("https://twosky.adtidy.org/api/v1/download?format=xml&language=%s&filename=%s&project=%s" % (options.locale, options.input, options.projectId))

#print('SERVER RESPONSE:')
#print(response)

if response != "":
    with open(options.cacheOutput, "wb") as f:
        f.write(response)
else:
    error("!!!Error downloading description file for locale: " + options.locale)

# Indexes for string IDs
short_desc_idx = -1
title_idx = -1
desc_idx = -1

# Open xlsx file and look for indexes
wb = xlrd.open_workbook(options.cacheOutput)
s = wb.sheet_by_index(0)
ids = s.col(0)
for i in range(len(ids)):
    val = ids[i].value
    if val == 'TITLE':
        title_idx = i
    elif val == 'DESCRIPTION':
        desc_idx = i
    elif val == 'SHORT_DESCRIPTION':
        short_desc_idx = i

if title_idx == -1 or desc_idx == -1 or short_desc_idx == -1:
    error("!!!Indexes are not valid!")

# Parse columns
utf8 = "UTF-8"
for i in range(s.ncols):
    column = s.col(i)
    locale = column[0].value
    if locale.replace(' ', '') == options.localeMapping or (locale == '' and column[1].value != ''):
        title_file = os.path.join(options.output, "title.txt")
        short_description_file = os.path.join(options.output, "short_description.txt")
        full_description_file = os.path.join(options.output, "full_description.txt")

        with open(title_file, "wb") as f:
            f.write(column[title_idx].value.encode(utf8))
        with open(short_description_file, "wb") as f:
            f.write(column[short_desc_idx].value.encode(utf8))
        with open(full_description_file, "wb") as f:
            f.write(column[desc_idx].value.encode(utf8))

        print("App description for " + options.locale + " has been successfully downloaded to " + options.output)
        break

os.remove(options.cacheOutput)