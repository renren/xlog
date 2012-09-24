#!/bin/bash

version="1.0-SNAPSHOT"
klist  > /dev/null 2>&1;
if [ $? -eq 0 ]; then
    user=`klist | sed -n -e "s/Default principal: \(.*\)\@XIAONEI.OPI.COM/\1/p"`
else
    user=`whoami`
fi
LANG=C
date=`date "+%Y-%m-%d %H:%M:%S"`

git log > /dev/null 2>&1;
GIT_RET=$?
svn info > /dev/null 2>&1;
SVN_RET=$?
if  test $GIT_RET -eq 0 ; then
  url=`git remote -v | awk '{print $2}' | head -n1`
  revision=`git log -1 --pretty=format:"%H"`

elif test $SVN_RET -eq 0; then
  url=`svn info|sed -n -e 's/URL: \(.*\)/\1/p'`
  revision=`svn info | sed -n -e 's/Last Changed Rev: \(.*\)/\1/p'`

else
  revision=????
fi

echo "@com.renren.dp.VersionAnnotation(
  version=\"$version\",
  user=\"$user\",
  date=\"$date\",
  url=\"$url\",
  revision=\"$revision\")
"


