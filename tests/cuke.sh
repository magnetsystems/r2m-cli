#!/bin/sh
#
# This is a help script to run cucumber scenarios given a set of tags.
# ex: Running the controllers.feature (annotated with @controllers)
# sh cuke.sh @controllers
# 
# You can also combine tags to filter out scenarios:
#
# ex: only starting the server created in the publicServices.feature. Run the scenarios annotated with @publicServices AND @start
# sh cuke.sh @publicServices @start
#
# ex: only running the publicServices test part: Run the scenarios annotated with both @publicServices AND @test
# sh cuke.sh @publicServices @test
#
# ex: only running the publicServices cleanup part: Run the scenarios annotated with both @publicServices AND @cleanup
# sh cuke.sh @publicServices @cleanup

# ex: Running the publicServices scenario scenarion annotated with @start, and then the one annotated with @test :
# This is equivalent to running scenarios annotated with both @publicService AND (@start OR @test)
# sh cuke.sh @publicServices @start,@test
#
# to see the html report:
# sh cuke.sh report
#


TAGS=""
while test $# -gt 0
do
  if [ "report" == $1 ];
  then
    open target/cucumber-report/index.html
    exit
  fi
  TAGS="$TAGS --tags $1" 
  shift
done
echo "Running mvn test -Dtest=*Cukes* -Dcucumber.options=\"${TAGS} --tags ~@wip \"" 
# use mvndebug -DforkMode=never if you want to remote debug on port 8000
#mvnDebug -DforkMode=never test -Dtest=*Cukes* -Dcucumber.options="--tags ~@wip $TAGS"
mvn test -Dtest=*Cukes* -Dcucumber.options="--tags ~@wip $TAGS"


