#!/bin/bash

if [[ -e "./StarMade.jar" && ! -e "./StarMade" ]]; then
	sm_root="."
elif [[ -e "./StarMade" && -e "./StarMade/StarMade.jar" ]]; then
	sm_root="./StarMade"
elif [ -e "../StarMade.jar" ]; then
	sm_root=".."
elif [ -e "../../StarMade.jar" ]; then
	sm_root="../.."
else
	echo
	echo "Couldn't find StarMade.jar, please move my whole folder into your game files"
	echo
	exit 1
fi
smj="$sm_root/StarMade.jar"
echo "Found StarMade.jar at $smj"

if [ ! -e "$smj.original" ]; then
	echo
	echo "Patch not installed, cannot uninstall"
	echo
	exit 1
fi

echo "Restoring backups and reverting to vanilla..."
echo "Restoring StarMade.jar.original -> StarMade.jar"
cp "$smj.original" $smj
config="$sm_root/data/config"
echo "Restoring BlockConfig.xml.original -> BlockConfig.xml"
cp "$config/BlockConfig.xml.original" "$config/BlockConfig.xml"
echo "Restoring EffectConfig.xml.original -> EffectConfig.xml"
cp "$config/EffectConfig.xml.original" "$config/EffectConfig.xml"
echo "Restoring blockBehaviorConfig.xml.original -> blockBehaviorConfig.xml"
cp "$config/blockBehaviorConfig.xml.original" "$config/blockBehaviorConfig.xml"
