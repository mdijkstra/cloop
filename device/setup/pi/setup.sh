if diff keyboard /etc/default/keyboard >/dev/null; then
	echo "Keyboard file is already set"
else
	echo "Setting Keyboard file and restarting..."
	sudo cp keyboard /etc/default/keyboard
	sudo reboot
fi

sudo apt-get update
sudo apt-get -y upgrade

sudo apt-get autoremove
sudo apt-get install vim bluetooth bluez-utils blueman

sudo cp timezone /etc/timezone


echo "run: sudo apt-get install mysql-server python-mysqldb"
echo "    requires password input pass=raspberry"




