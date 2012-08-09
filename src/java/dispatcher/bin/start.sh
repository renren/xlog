#!/bin/bash

#BASEDIR=`dirname $0`
cd ..
JAVA_HOME=/home/xlog/java

CLASSPATH=.:$JAVA_HOME/lib/dt.jar:$JAVA_HOME/lib/tools.jar:$JAVA_HOME/jre/lib

LIBPATH="lib"

export LIBPATH

for f in `find $LIBPATH -name '*.jar'`
  do
    CLASSPATH=$CLASSPATH:$f
  done

# ******************************************************************
# ** Set java runtime options                                     **
# ** Change 256m to higher values in case you run out of memory.  **
# ******************************************************************

#export DEBUG="-Xdebug -Xrunjdwp:transport=dt_socket,address=3005,server=y,suspend=n"
OPT="-Xmx8g -Xms4g -Xmn2g -Xss128k -XX:+UseParallelGC -XX:ParallelGCThreads=20 -cp $CLASSPATH"

# ***************
# ** Run...    **
# ***************

java $OPT com.renren.dp.xlog.dispatcher.Bootstrap "${1+$@}"
