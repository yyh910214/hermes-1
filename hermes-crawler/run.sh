#!/usr/bin/env bash

mvn clean package

java -cp target/hermes-crawler.jar apolloners.hermes.ProductchartLauncher --dir . --remote hdfs://apollon1:9000 --output /crawl/
java -cp target/hermes-crawler.jar apolloners.hermes.CetizenLauncher --dir . --remote hdfs://apollon1:9000 --output /crawl/