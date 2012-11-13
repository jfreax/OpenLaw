#!/bin/bash

export gesetzePath="/home/jens/VCS/gesetze/"

echo "["

while read s
do 
  echo -n "[\""
  grep "^jurabk:" $s | sed 's/jurabk: //g' | tr -d \\n
  echo -n "\", \""
  grep "^origslug:" $s | sed 's/origslug: //g' | tr -d \\n
  echo -n "\", \""
  cat $s | head -2 | tail -1 | sed 's/Title: //g' | sed 's/"/\\"/g' | tr -d \\n
  echo "\"],"
done < <(find $gesetzePath -iname index.md)

echo "]"
