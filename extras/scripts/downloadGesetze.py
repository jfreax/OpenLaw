#!/usr/bin/env python
# -*- coding: utf-8 -*-

import os
import sys

import lxml.etree
import lxml.html

output_dir = sys.argv[1]
base_url = "http://www.gesetze-im-internet.de/"
root_alphabet = lxml.html.parse(base_url+"aktuell.html").getroot()

def getAllLaws():
    ret = []

    alphabet = root_alphabet.cssselect("#container .alphabet")
    for el in alphabet:
        if el.attrib.has_key('href'):
            alphabet_elem = lxml.html.parse(base_url+el.attrib['href']).getroot()

            title_elem = alphabet_elem.cssselect("#paddingLR12 p a")

            for el_title in title_elem:
                if el_title.attrib.has_key('href') and not el_title.attrib['href'].endswith('.pdf'):
                    ret.append(el_title.attrib['href'][2:-11])
                    #print el_title.getchildren()[0].text, el_title.attrib['href']

    return ret


def getLawText(law):
    law_root = lxml.html.parse(base_url+law+"/index.html").getroot()
    head_elem = law_root.cssselect("#paddingLR12 td a")

    i = 0
    for el in head_elem:
        if el.attrib.has_key('href'):
            head_link = el.attrib['href']
            head_root = lxml.html.parse(base_url+law+"/"+head_link).getroot()

            for bad in head_root.xpath("//a[text()='Nichtamtliches Inhaltsverzeichnis']"):
                bad.getparent().remove(bad)
            for bad in head_root.xpath("//div[contains(@class, 'jnheader')]"):
                bad.getparent().remove(bad)

            headHtml_elem = head_root.cssselect("#paddingLR12")

            directory = output_dir + '/' + law.replace('-','_') + '/'

            if not os.path.exists(directory):
                os.makedirs(directory)

            with open(directory + str(i), 'w') as lawFile:
                lawFile.write(lxml.etree.tostring(headHtml_elem[0]))

            i = i+1



laws = getAllLaws()
for law in laws:
    getLawText(law)
