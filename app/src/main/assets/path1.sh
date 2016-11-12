#!/bin/sh
FILE=$1

if [ ! -f "$FILE" ]
then
    echo "Doesnotexist"
else
    echo "Exists"
fi
