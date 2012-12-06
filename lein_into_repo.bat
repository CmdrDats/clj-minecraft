@rem needs this lein.bat https://github.com/technomancy/leiningen/blob/master/bin/lein.bat
	@rem put it in PATH

@echo off

rem every time you make a change in this project you should run this .bat file to put the updated .jar into the local repository

set JAVA_CMD="c:\program files\java\jdk1.6.0_31\bin\java.exe"
call lein install
if NOT "%1" == "nopause" @pause