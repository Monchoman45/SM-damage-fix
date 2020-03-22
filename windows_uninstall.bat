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

if not exist "%smj%.original" (
	echo.
	echo "Patch not installed, cannot uninstall"
	echo.
	goto :eof
)

echo Restoring backups and reverting to vanilla...
echo Restoring StarMade.jar.original -^> StarMade.jar
copy /y "%smj%.original" %smj% > nul
set config="%sm_root%\data\config"
echo Restoring BlockConfig.xml.original -^> BlockConfig.xml
copy /y "%config%\BlockConfig.xml.original" "%config%\BlockConfig.xml" > nul
echo Restoring EffectConfig.xml.original -^> EffectConfig.xml"
copy /y "%config%\EffectConfig.xml.original" "%config%\EffectConfig.xml" > nul
echo Restoring blockBehaviorConfig.xml.original -^> blockBehaviorConfig.xml
copy /y "%config%\blockBehaviorConfig.xml.original" "%config%\blockBehaviorConfig.xml" > nul
pause
