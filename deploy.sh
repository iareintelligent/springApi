#!/bin/sh
 mvn clean package -DskipTests && scp -i "jcalc.pem" target/jcalc-0.0.1.jar  ubuntu@ec2-*-*-*-*.us-west-2.compute.amazonaws.com:~/
