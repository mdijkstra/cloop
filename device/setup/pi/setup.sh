# set the DIABETES home for all users in the /etc/profile
sudo cp profile /etc/profile

if diff keyboard /etc/default/keyboard >/dev/null; then
	echo "############ Keyboard file is already set"
else
	echo "############ Setting Keyboard file and restarting..."
	sudo cp keyboard /etc/default/keyboard
	sudo reboot
fi

if diff hosts.allow /etc/hosts.allow >/dev/null; then
	echo "########### SSH hosts already allowed"
else
	echo "########### Setting up SSH hosts"
	sudo cp hosts.allow /etc/hosts.allow
fi

sudo apt-get update
sudo apt-get -y upgrade

sudo apt-get -y autoremove
sudo apt-get -y install vim bluetooth bluez-utils blueman wicd-curses bluetooth python-bluez python-gobject python-dbus screen

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

cd /home/pi/diabetes
git clone https://github.com/bewest/decoding-carelink
cd /home/pi/diabetes/cloop/device/setup/pi

echo ""
echo "############## RUN ##########"
echo "run: sudo apt-get install mysql-server python-mysqldb"
echo "    requires password input pass=raspberry"
echo ""
echo "run: sudo wicd-curses"
echo "    in order to setup wifi"



