#!/usr/bin/env bash

mvn clean package && java -jar target/hermes-crawler.jar --dir . --remote hdfs://apollon1:9000 --output /crawl/