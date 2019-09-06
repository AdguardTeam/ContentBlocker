#!/usr/bin/python3

import sys
import optparse
import os
import xlrd
import urllib.request, urllib.error, urllib.parse
import json

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

def parse_args():
    parser = optparse.OptionParser(usage="%prog [options]. %prog -h for help.")
    parser.add_option("-p", "--project", dest="project", help="Project ID", metavar="PROJECT_ID")
    parser.add_option("-o", "--output", dest="output", help="Output directory name", metavar="DIRECTORY")
    parser.add_option("-i", "--input", dest="input", help="Input file name", metavar="FILE")
    parser.add_option("-c", "--config", dest="config", help="Config file name", metavar="FILE")
    (values, args) = parser.parse_args(sys.argv)

    if (not values.project):
        parser.error('Project ID not given')
    if (not values.input):
        parser.error('Input file name not given')
    if (not values.output):
        parser.error('Output directory name not given')
    return values

def save_book(response, values):
    book_file_name = values.output + "/book.xlsx"
    if response != "":
        with open(book_file_name, "wb") as f:
            f.write(response)
        return book_file_name
    else:
        raise RuntimeError("!!!Error downloading description file")

def get_indexes(sheet):
    ids = sheet.col(0)
    title_idx = desc_idx = short_desc_idx = -1
    for i in range(len(ids)):
        val = ids[i].value
        if val == 'TITLE':
            title_idx = i
        elif val == 'DESCRIPTION':
            desc_idx = i
        elif val == 'SHORT_DESCRIPTION':
            short_desc_idx = i

    if title_idx == -1 or desc_idx == -1 or short_desc_idx == -1:
        raise RuntimeError("!!!Indexes are not valid!")
    return title_idx, desc_idx, short_desc_idx

def get_project_config(values):
    with open(values.config, 'r') as file:
        for project_config in json.load(file):
            if project_config["project_id"] == values.project:
                return project_config
        raise RuntimeError("!!! Error parsing json file")

values = parse_args()
response = download_content("https://twosky.adtidy.org/api/v1/download?format=xml&language=en&filename=%s&project=%s" % (values.input, values.project))
config = get_project_config(values)
book_file = save_book(response, values)
sheet = xlrd.open_workbook(book_file).sheet_by_index(0)
(title_idx, desc_idx, short_desc_idx) = get_indexes(sheet)
locale_idx = 0

utf8 = "UTF-8"
languages = config["languages"]
for i in range(1, sheet.ncols - 1):
    column = sheet.col(i)
    locale = column[locale_idx].value

    if locale not in languages.values():
        print("Locale " + locale + " doesn't exist")
        continue

    lang_key = [k for k, v in languages.items() if v == locale][0]
    if lang_key in config["mapping"]:
        lang_key = config["mapping"][lang_key]
    lang_dir = values.output + "/" + lang_key
    if not os.path.exists(lang_dir):
        os.mkdir(lang_dir)

    if column[title_idx].value != '':
        title_file = os.path.join(lang_dir, "title.txt")
        short_description_file = os.path.join(lang_dir, "short_description.txt")
        full_description_file = os.path.join(lang_dir, "full_description.txt")

        with open(title_file, "wb") as f:
            f.write(column[title_idx].value.encode(utf8))
        with open(short_description_file, "wb") as f:
            f.write(column[short_desc_idx].value.encode(utf8))
        with open(full_description_file, "wb") as f:
            f.write(column[desc_idx].value.encode(utf8))

        print("App description for " + locale + " has been successfully downloaded to " + lang_dir)

os.remove(book_file)
