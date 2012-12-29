@rem needs this lein.bat https://github.com/technomancy/leiningen/blob/master/bin/lein.bat
	@rem put it in PATH

@echo off
set JAVA_CMD="c:\program files\java\jdk1.6.0_31\bin\java.exe"
call lein uberjar

set deployDIR=deploy
rem %deployDIR% is a folder link to your running server's plugin folder
rem you make it by running this cmd: mklink /d deploy c:\craftbukkit\plugins\
if EXIST "%deployDIR%" ( 
	move target\cljminecraft-*standalone.jar %deployDIR%\cljminecraft.jar
	if ERRORLEVEL 1 echo FAILED, make sure bukkit isn't running (so the plugin .jar isn't in locked)
	)

if NOT "%1" == "nopause" @pause
