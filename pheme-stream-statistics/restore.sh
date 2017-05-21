#!/bin/bash

url="http://gatezkt1:8080/pheme-stream-statistics/"
files=`ls *json`

for file in $files
do

        topic=`echo $file | sed -e 's/capture-\(.*\)\.json/\1/' | tr '-' '/'`
        curl -i -d @$file -XPOST $url$topic

done


