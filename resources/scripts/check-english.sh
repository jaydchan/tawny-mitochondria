#!/bin/bash

temp=temp.txt
outfile=../cenglish.txt
infile=../input/english.txt

## if temp exists rm
if [[ -e $temp ]]
then
    rm $temp
fi

cat $infile | tr '[:upper:]' '[:lower:]' > $outfile

# should exempt dash (-)
cat $outfile | tr -s [:space:][:punct:] \\n | sort -u> $temp

echo '' > $outfile

while read word
do
    if echo $word | grep -q "[a-z]"
    then
	echo $word >> $outfile
    fi
done <$temp

rm $temp
