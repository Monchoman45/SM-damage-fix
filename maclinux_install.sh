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
config="$sm_root/data/config"
bcxml="$config/BlockConfig.xml"
ecxml="$config/EffectConfig.xml"
bbcxml="$config/blockBehaviorConfig.xml"

if [ -e "$smj.original" ]; then
	if [[ $1 != "reinstall" ]]; then
		echo
		echo "Fix appears to already be installed. You can either restore from backups and"
		echo "reinstall now, or you can delete StarMade.jar.original to install over top of"
		echo "your current StarMade.jar and config files."
		echo
		echo -n "Restore and reinstall now? (y/n): "
		while read line; do
			if [[ $line != "y" && $line != "Y" && $line != "n" && $line != "N" ]]; then
				echo -n "Restore and reinstall now? (y/n): "
				continue
			else
				break
			fi
		done <&0
	fi
	if [[ $1 == "reinstall" || $line == "y" || $line == "Y" ]]; then
		./maclinux_uninstall.sh
		echo "Reinstalling..."
	else
		echo 
		echo "<No files written>"
		exit 0
	fi
else
	echo "Backing up existing files..."
	echo "Backing up StarMade.jar -> StarMade.jar.original"
	cp $smj "$smj.original"
	echo "Backing up BlockConfig.xml -> BlockConfig.xml.original"
	cp $bcxml "$bcxml.original"
	echo "Backing up EffectConfig.xml -> EffectConfig.xml.original"
	cp $ecxml "$ecxml.original"
	echo "Backing up blockBehaviorConfig.xml -> blockBehaviorConfig.xml.original"
	cp $bbcxml "$bbcxml.original"
fi

echo
echo "Installing class files in StarMade.jar..."
#oldPATH=$PATH
#export PATH="$PATH:`realpath ./redist/linux`"
#echo $PATH
"$sm_root/../dep/java/jre1.7.0_80/bin/java" -jar ./redist/smmi.jar "$smj.original" $smj ./bin
#export PATH=$oldPATH
echo "Installing config..."
echo "-> BlockConfig.xml"
cp "./config/BlockConfig.xml" $bcxml
echo "-> EffectConfig.xml"
cp "./config/EffectConfig.xml" $ecxml
echo "-> blockBehaviorConfig.xml"
cp "./config/blockBehaviorConfig.xml" $bbcxml
echo "Done!"
echo
python3 effect_tables.py multiply
echo
cat changelog.txt
