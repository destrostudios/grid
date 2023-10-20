#!/bin/bash

VERSION=$1
SERVER=$2
CLIENT=$3

# Checkout
git clone https://github.com/destrostudios/grid.git
if [ -n "$VERSION" ]; then
  git checkout "$VERSION"
fi

# Build
mkdir workspace
echo -n "../assets/" > workspace/assets.ini
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64;mvn clean install

# Deploy (Client)
rm -rf "${CLIENT}"*
mv assets "${CLIENT}"
mv grid-client/target/libs "${CLIENT}"
mv grid-client/target/grid-client-0.0.1.jar "${CLIENT}Grid.jar"
echo -n "./assets/" > "${CLIENT}assets.ini"
curl https://destrostudios.com:8080/apps/6/updateFiles

# Deploy (Server)
mv grid-server/target/grid-server-0.0.1-jar-with-dependencies.jar "${SERVER}grid.jar"
mv ecosystem.config.js "${SERVER}"
cd "${SERVER}"
pm2 restart ecosystem.config.js