#!/bin/sh

export AGENT=$(pwd)/$(find target -name 'jetty-alpn-agent-*.jar' | head -1)
echo "Agent $AGENT"
export SBT_OPTS="$SBT_OPTS -javaagent:$AGENT"
sbt -Dhttps.port=8081 -Dplay.server.https.engineProvider=ssl.MySSLEngineProvider
