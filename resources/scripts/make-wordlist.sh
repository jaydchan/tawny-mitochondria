#!/bin/bash

temp=temp.txt
outfile=../omim.txt

## if temp exists rm
if [[ -e $temp ]]
then
    rm $temp
fi

## if outfile exists rm
if [[ -e $outfile ]]
then
    rm $outfile
fi

html2text ../OMIM/*.html >> $temp

cat $temp | tr '[:upper:]' '[:lower:]' > $outfile

# should exempt dash (-)
cat $outfile | tr -s [:space:][:punct:] \\n | sort -u> $temp

while read word
do
    if echo $word | grep -q "[a-z]" && [[ ${#word} -gt 1 ]]
    then
	echo $word >> $outfile
    fi
done <$temp

rm $temp
