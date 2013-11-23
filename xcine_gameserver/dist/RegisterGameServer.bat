@echo off
title xcine gameserver registration console
@java -Djava.util.logging.config.file=config/console.cfg -cp ./libs/*; net.xcine.gsregistering.GameServerRegister
@pause