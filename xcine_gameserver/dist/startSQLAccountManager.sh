#!/bin/sh
java -Djava.util.logging.config.file=config/console.cfg -cp ./libs/*:L2jxCine-core.jar:mysql-connector-java-5.1.14-bin.jar net.xcine.accountmanager.SQLAccountManager
