#!/bin/bash

cd /home/erobinson/diabetes/cloop/device/processes

# if already running skip
for pid in $(pidof -x run_device_processes.sh); do
  if [ $pid != $$ ]; then
    echo "[$(date)] : run_device_processes.sh : Process is already running with PID $pid" >> log/runner.log
    exit 1
  fi
done

echo "[$(date)] : run_device_processes.sh : Starting run" >> log/runner.log

sudo timeout 600 python sync_device_pump.py
sleep 2
sudo timeout 600 python sync_device_phone.py
sleep 20
sudo timeout 600 python confirm_injection_process.py
sleep 10
sudo timeout 600 python injection_process.py
sleep 30
sudo timeout 600 python sync_device_phone.py
sleep 2
sudo timeout 600 python shutdown_process.py

echo "[$(date)] : run_device_processes.sh : End of run" >> log/runner.log

