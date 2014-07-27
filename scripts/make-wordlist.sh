#!/bin/bash

temp=temp.txt
omim=omim.txt
htmlpath="./resources/OMIM/*.html"
outpath="./output/omim"

## list all omim files
## ensure file names contain no whitespace
## by replacing ' ' with '_'
rename 's/ /_/g' $htmlpath
ls $htmlpath > $omim

## ensure omim directory exists
## and that old omim files are deleted
if [[ ! -d $outpath ]]
then
    mkdir -p $outpath
else
    rm -f $outpath/*
fi

# for each disease
while read file
do
    ## extract omim id from file name
    entry=`echo $file | grep -o -E '[0-9]+' | head -1 | sed -e 's/^0\+//'`
    ## construct filename
    outfile="$outpath/omim$entry.txt"

    ## convert html 2 txt
    html2text $file > $temp

    ## convert upper 2 lower
    cat $temp | tr '[:upper:]' '[:lower:]' > $outfile

    ## ' and - should be deleted (replaced with '')
    cat $outfile | tr -d "'-" > $temp

    ## replaces whitespace with newline
    cat $temp | tr -s [:space:][:punct:] \\n | sort -u > $outfile

    ## replaces punct with newline
    ## http://www.regular-expressions.info/posixbrackets.html
    cat $outfile | tr -s [:punct:] \\n | sort -u > $temp

    ## empty outfile
    rm $outfile

    ## ensure each term is:
    ## 1. only made up of lower case letters and numbers
    ## 2. has length gt 1
    ## 3. contains atleast one letter
    ## else discard
    while read word
    do
    	if echo $word | grep -q "^[a-z0-9]\+$" && [[ ${#word} -gt 1 ]] && echo $word | grep -q "[a-z]"
    	then
    	    echo $word >> $outfile
    	fi
    done < $temp
done < $omim

rm $temp
rm $omim
