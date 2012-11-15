#!/usr/bin/env python
# -*- coding: utf-8 -*-

import os
import sys

import codecs
from itertools import chain

import lxml.etree
import lxml.html

output_dir = sys.argv[1]
base_url = "http://www.gesetze-im-internet.de/"
root_alphabet = lxml.html.parse(base_url+"aktuell.html").getroot()

# (name, fulltitle, slug/link)
def getAllLaws():
    ret = []

    alphabet = root_alphabet.cssselect("#container .alphabet")
    for el in alphabet:
        if el.attrib.has_key('href'):
            alphabet_elem = lxml.html.parse(base_url+el.attrib['href']).getroot()

            link_elem = alphabet_elem.cssselect("#paddingLR12 p a")
            title_elem = alphabet_elem.cssselect("#paddingLR12 p a abbr")

            for (el_link, el_title) in zip(link_elem, title_elem):
                if el_link.attrib.has_key('href') and not el_link.attrib['href'].endswith('.pdf'):
                    ret.append(
                            (el_title.text.lstrip(' '),
                            el_title.attrib['title'],
                            el_link.attrib['href'][2:-11])
                        )
    return ret


def writeLawHead(slug, html):
    first = True

    directory = output_dir + '/' + slug.replace('-','_') + '/'
    with codecs.open(directory + "heads", 'w', 'utf-8') as lawHead:
        for tr in html.xpath("//div[@id='paddingLR12']/table/tr"):
            tds = tr.xpath("child::td")
            text = tr.xpath("child::td/descendant::text()")[-1]

            depth = len(tds)
            if len(tds) < 3:
                colspan = tds[-1].xpath("attribute::colspan")[0]
                depth = 4 - int(colspan)

            if first:
                first = False
                if text[0] != u"§" and not text.startswith("Art"):
                    depth = 1

            #href = tds[-1].xpath("child::a/attribute::href")[0]
            #if '#' in href:
            #    continue
            lawHead.write(u"%i: %s\n" % (depth,text))


def writeLawText(slug, html):
    head_elem = html.cssselect("#paddingLR12 td a")

    # Fix for laws without headlines
    if len(head_elem) == 0:
        head_elem = html.cssselect("#paddingLR12 a")

    fakeLinkIDs = []
    i = 0
    for el in head_elem:
        if el.attrib.has_key('href'):
            i = i+1

            head_link = el.attrib['href']
            head_root = lxml.html.parse(base_url+slug+"/"+head_link).getroot()

            for bad in head_root.xpath("//a[text()='Nichtamtliches Inhaltsverzeichnis']"):
                bad.getparent().remove(bad)
            for bad in head_root.xpath("//div[contains(@class, 'jnheader')]"):
                bad.getparent().remove(bad)

            # Its only a link to the whole law text rather to one part of it
            if '#' in head_link:
                fakeLinkIDs.append(i)
                continue

            headHtml_elem = head_root.cssselect("#paddingLR12")

            directory = output_dir + '/' + slug.replace('-','_') + '/'
            if not os.path.exists(directory):
                os.makedirs(directory)

            with open(directory + str(i), 'w') as lawFile:
                lawFile.write(lxml.etree.tostring(headHtml_elem[0]))

            # Write "link" to real first chapters
            if len(fakeLinkIDs) != 0:
                for fake in fakeLinkIDs:
                    with open(directory + str(fake), 'w') as lawFile:
                        lawFile.write("%"+str(i)+"%")
                fakeLinkIDs = []

# Debug
#law_index_html = lxml.html.parse(base_url+"aabg/index.html").getroot()
#writeLawHead('aabg', law_index_html)
#exit()

# First, fetch links to all laws + short name and full name
laws = getAllLaws()

i = 1
lawIter = chain(laws);
with codecs.open(output_dir + "/laws", 'w', 'utf-8') as lawsFile:
    lawsFile.write("[\n")

    while True:
        try:
            name,title,slug = lawIter.next()
        except StopIteration:
            lawsFile.write(u'["%s", "%s", "%s"]\n]' % ( name,slug,title ))
            break

        print "Loading #%i: %s" % (i, name)

        lawsFile.write(u'["%s", "%s", "%s"],\n' % ( name,slug,title ))

        law_index_html = lxml.html.parse(base_url+slug+"/index.html").getroot()
        writeLawText(slug, law_index_html)
        writeLawHead(slug, law_index_html)

        i = i+1

    

    