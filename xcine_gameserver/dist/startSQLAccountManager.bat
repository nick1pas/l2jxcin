@echo off
title xcine account manager console
@java -Djava.util.logging.config.file=config/console.cfg -cp ./libs/*; net.xcine.accountmanager.SQLAccountManager
@pause
