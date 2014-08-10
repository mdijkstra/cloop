#!/bin/bash

# if already running skip
for pid in $(pidof -x run_device_processes.sh); do
  if [ $pid != $$ ]; then
    echo "[$(date)] : run_device_processes.sh : Process is already running with PID $pid"
    exit 1
  fi
done

cd /home/pi/diabetes/cloop/device/processes

sudo python sync_device_pump.py
sleep 2
sudo python sync_device_phone.py
sleep 2
sudo python injection_process.py
sleep 2
sudo python sync_device_phone.py
sleep 2
# run shutdown if requested
