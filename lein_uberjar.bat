@rem needs this lein.bat https://github.com/technomancy/leiningen/blob/master/bin/lein.bat
	@rem put it in PATH or near this .bat file

@echo off
set JAVA_CMD="c:\program files\java\jdk1.6.0_31\bin\java.exe"
call lein uberjar

rem built.jar is a folder link to your running server's plugin folder
rem you make it by running this cmd: mklink /d built.jar c:\craftbukkit\plugins\
if EXIST "built.jar" ( move target\clj-minecraft-*standalone*.jar built.jar )

if NOT "%1" == "nopause" @pause
