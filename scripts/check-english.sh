#!/bin/bash

temp=temp.txt
infile=./resources/input/english.txt
outfile=./output/cenglish.txt

cat $infile | tr '[:upper:]' '[:lower:]' > $outfile

cat $outfile | tr -s [:space:][:punct:] \\n | sort -u> $temp

rm $temp
