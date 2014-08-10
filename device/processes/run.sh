cd /home/pi/diabetes/cloop/device/processes

sudo python sync_device_pump.py
sleep 2
sudo python sync_device_phone.py
sleep 2
sudo python injection_process.py
sleep 2
sudo python sync_device_phone.py
sleep 2
