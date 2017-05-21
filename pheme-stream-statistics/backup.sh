#!/bin/bash

topics=(pheme_capture pheme_en_events pheme_en pheme_en_concepts pheme_en_graphdb pheme_en_preprocessed pheme_en_processed pheme_en_entities)
host="http://gatezkt1:8080/pheme-stream-statistics/"

for topic in "${topics[@]}"
do
        wget -O - $host'minutes/'$topic > capture-minutes-$topic.json
        wget -O - $host'hours/'$topic > capture-hours-$topic.json
done

