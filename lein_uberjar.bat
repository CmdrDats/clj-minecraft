@rem needs this lein.bat https://github.com/technomancy/leiningen/blob/master/bin/lein.bat
	@rem put it in PATH or near this .bat file

@echo off
set JAVA_CMD="c:\program files\java\jdk1.6.0_31\bin\java.exe"
call lein uberjar
@pause