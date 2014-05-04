line="* * * * * $DIABETES_HOME/cloop/device/processes/halt/shutdown.sh"
(crontab -u root -l; echo "$line" ) | crontab -u root -
