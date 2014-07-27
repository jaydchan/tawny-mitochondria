#!/bin/bash

temp=temp.txt
infile=./resources/input/english.txt
outfile=./output/cenglish.txt

cat $infile | tr '[:upper:]' '[:lower:]' > $outfile

# should exempt dash (-)
cat $outfile | tr -s [:space:][:punct:] \\n | sort -u> $temp

while read word
do
    if echo $word | grep -q "[a-z]"
    then
	echo $word >> $outfile
    fi
done <$temp

rm $temp
