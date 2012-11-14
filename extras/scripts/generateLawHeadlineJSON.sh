#!/bin/bash

gesetzePath="/home/jens/VCS/gesetze/"
outputPath=$1

while read s
do 
  name=$(grep "^origslug:" $s | sed 's/origslug: //g' | sed 's/-/_/g' | tr -d \\n)
  filename="${outputPath}/${name}/heads"
  
  echo -n "" > "$filename"
  
  grep "^#" $s | sed 's/"/\\"/g' | while read line
  do
    if [ $(echo $line | grep -c "^# " ) -gt 0 ]; then
      echo $line | sed 's/^# /1:/g' >> "$filename"
    elif [ $(echo $line | grep -c "^## " ) -gt 0 ]; then
      echo $line | sed 's/^## /2:/g' >> "$filename"
    elif [ $(echo $line | grep -c "^### " ) -gt 0 ]; then
      echo $line | sed 's/^### /3:/g' >> "$filename"
    elif [ $(echo $line | grep -c "^#### " ) -gt 0 ]; then
      echo $line | sed 's/^#### /4:/g' >> "$filename"
    elif [ $(echo $line | grep -c "^##### " ) -gt 0 ]; then
      echo $line | sed 's/^##### /5:/g' >> "$filename"
    elif [ $(echo $line | grep -c "^###### " ) -gt 0 ]; then
      echo $line | sed 's/^###### /6:/g' >> "$filename"
    fi
  done

done < <(find $gesetzePath -iname index.md)


