#!/bin/bash
cd -- "$(find $HOME -type d -name wildfly-10.1.0.Final 2> /dev/null)"
cd bin/
if [ $# -eq 0 ]
  then
   ./jboss-cli.sh --connect command=:shutdown
   exit 1     
fi
./jboss-cli.sh --connect --controller=$1 command=:shutdown 
