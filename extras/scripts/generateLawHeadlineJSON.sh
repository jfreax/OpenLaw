#!/bin/bash

gesetzePath="/home/jens/VCS/gesetze/"
outputPath="/home/jens/Dropbox/Programmierung/Java/Android/Gesetze/extras/server data/law/"


while read s
do 
  name=$(grep "^origslug:" $s | sed 's/origslug: //g' | tr -d \\n)
  
  echo -n "" > "$outputPath/$name"
  
  grep "^#" $s | sed 's/"/\\"/g' | while read line
  do
    if [ $(echo $line | grep -c "^# " ) -gt 0 ]; then
      echo $line | sed 's/^# /1:/g' >> "$outputPath/$name"
    elif [ $(echo $line | grep -c "^## " ) -gt 0 ]; then
      echo $line | sed 's/^## /2:/g' >> "$outputPath/$name"
    elif [ $(echo $line | grep -c "^### " ) -gt 0 ]; then
      echo $line | sed 's/^### /3:/g' >> "$outputPath/$name"
    elif [ $(echo $line | grep -c "^#### " ) -gt 0 ]; then
      echo $line | sed 's/^#### /4:/g' >> "$outputPath/$name"
    elif [ $(echo $line | grep -c "^##### " ) -gt 0 ]; then
      echo $line | sed 's/^##### /5:/g' >> "$outputPath/$name"
    elif [ $(echo $line | grep -c "^###### " ) -gt 0 ]; then
      echo $line | sed 's/^###### /6:/g' >> "$outputPath/$name"
    fi
  done

#   name=$(grep "^slug:" $s | sed 's/slug: //g' | tr -d \\n)
#   echo "[" > "$outputPath/$name"
# 
#   # Headline 1
#   first=1
#   echo -n "{1: {" >> "$outputPath/$name"
#   grep "^# " $s | sed 's/^# //g' | sed 's/"/\\"/g' | while read headline
#   do
#     if [ $first -eq 0 ]; then
#       echo -n "," >> "$outputPath/$name"
#     else
#       first=0
#     fi
#     echo -n "\"$headline\"" >> "$outputPath/$name"
#   done
#   echo "}], " >> "$outputPath/$name"
# 
#   # Headline 2
#   first=1
#   echo -n "{2: {" >> "$outputPath/$name"
#   grep "^## " $s | sed 's/^## //g' | sed 's/"/\\"/g' | while read headline
#   do
#     if [ $first -eq 0 ]; then
#       echo -n "," >> "$outputPath/$name"
#     else
#       first=0
#     fi
#     echo -n "\"$headline\"" >> "$outputPath/$name"
#   done
#   echo "}]" >> "$outputPath/$name"
# 
#   # Headline 3
#   first=1
#   echo -n "{3: {" >> "$outputPath/$name"
#   grep "^### " $s | sed 's/^### //g' | sed 's/"/\\"/g' | while read headline
#   do
#     if [ $first -eq 0 ]; then
#       echo -n "," >> "$outputPath/$name"
#     else
#       first=0
#     fi
#     echo -n "\"$headline\"" >> "$outputPath/$name"
#   done
#   echo "}]" >> "$outputPath/$name"
#   
#   echo "]" >> "$outputPath/$name"
#   
done < <(find $gesetzePath -iname index.md)


