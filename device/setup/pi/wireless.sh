#!/bin/bash
sudo ifconfig wlan0 up
sudo iwconfig wlan0 essid Bandit key 7148137630
sudo dhclient wlan0

