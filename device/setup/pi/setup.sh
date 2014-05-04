# set the DIABETES home for all users in the /etc/profile
sudo cp profile /etc/profile

if diff keyboard /etc/default/keyboard >/dev/null; then
	echo "############ Keyboard file is already set"
else
	echo "############ Setting Keyboard file and restarting..."
	sudo cp keyboard /etc/default/keyboard
	sudo reboot
fi

sudo apt-get update
sudo apt-get -y upgrade

sudo apt-get -y autoremove
sudo apt-get -y install vim bluetooth bluez-utils blueman wicd-curses wtop bluetooth python-bluez python-gobject python-dbus

sudo easy_install pudb

if diff rc.local /etc/rc.local >/dev/null; then
	echo "############ rc.local already set"
else
	echo "############ setting rc.local... will require restart to take effect"
	sudo cp rc.local /etc/rc.local
fi

if diff keyboard /etc/default/keyboard >/dev/null; then
	echo "############ Timezone already set"
else
	echo "############ setting timezone..."
	sudo cp timezone /etc/timezone
fi

echo ""
echo "############## RUN ##########"
echo "run: sudo apt-get install mysql-server python-mysqldb"
echo "    requires password input pass=raspberry"
echo ""
echo "run: sudo wicd-curses"
echo "    in order to setup wifi"



