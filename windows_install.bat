@echo off
setlocal
setlocal ENABLEDELAYEDEXPANSION

if exist ".\StarMade.jar" if not exist ".\StarMade" (
	set sm_root=.
)
if exist ".\StarMade" if exist ".\StarMade\StarMade.jar" (
	set sm_root=.\StarMade
)
if exist "..\StarMade.jar" (
	set sm_root=..
)
if exist "..\..\StarMade.jar" (
	set sm_root=..\..
)
if not defined sm_root (
	echo.
	echo Couldn't find StarMade.jar, please move my whole folder into your game files
	echo.
	goto :eof
)
set smj=%sm_root%\StarMade.jar
echo Found StarMade.jar at %smj%
set config=%sm_root%\data\config
set bcxml=%config%\BlockConfig.xml
set ecxml=%config%\EffectConfig.xml
set bbcxml=%config%\blockBehaviorConfig.xml

if exist "%smj%.original" (
	if not "%1%" == "reinstall" (
		echo.
		echo Fix appears to already be installed. You can either restore from backups and
		echo reinstall now, or you can delete StarMade.jar.original to install over top of
		echo your current StarMade.jar and config files.
		echo.
		set /p line="Restore and reinstall now? (y/n): "
		if "!line!" == "y" (
			call .\windows_uninstall.bat
			echo Reinstalling...
		) else (
			echo.
			echo ^<No files written^>
			goto :eof
		)
	) else (
		call .\windows_uninstall.bat
		echo Reinstalling...
	)
) else (
	echo Backing up existing files...
	echo Backing up StarMade.jar -^> StarMade.jar.original
	copy /y %smj% "%smj%.original" > nul
	echo Backing up BlockConfig.xml -^> BlockConfig.xml.original
	copy /y %bcxml% "%bcxml%.original" > nul
	echo Backing up EffectConfig.xml -^> EffectConfig.xml.original
	copy /y %ecxml% "%ecxml%.original" > nul
	echo Backing up blockBehaviorConfig.xml -^> blockBehaviorConfig.xml.original
	copy /y %bbcxml% "%bbcxml%.original" > nul
)

echo.
echo Installing class files in StarMade.jar...
"%sm_root%\..\dep\java\jre1.7.0_80\bin\java" -jar .\redist\smmi.jar "%smj%.original" %smj% .\bin
echo Installing config...
echo -^> BlockConfig.xml
copy ".\config\BlockConfig.xml" %bcxml% > nul
echo -^> EffectConfig.xml
copy ".\config\EffectConfig.xml" %ecxml% > nul
echo -^> blockBehaviorConfig.xml
copy ".\config\blockBehaviorConfig.xml" %bbcxml% > nul
echo Done!
echo.
type changelog.txt
pause
