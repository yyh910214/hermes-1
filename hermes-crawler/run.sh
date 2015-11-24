#!/usr/bin/env bash

java -jar target/hermes-crawler.jar --dir . --remote hdfs://apollon1:9000 --output /crawl/