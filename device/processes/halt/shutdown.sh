cd /home/pi/diabetes/cloop/device/processes/halt
if diff shutdown.base shutdown.comp >/dev/null; then
	date >> shutdown.log
	echo "Shutdown files match. Going to shutdown..." >> shutdown.log
	cp shutdown.reset shutdown.comp
#	sudo reboot
else
	date >> shutdown.log
	echo "Shutdown files are different. Continue :)" >> shutdown.log
fi





