#!/bin/bash

if [[ ($# -ne 1) ]]; then
   echo " "
   echo "Error: ${0} must be called with one argument:"
   echo "       database application connection password"
   echo "Example:"
   echo "    ${0} ExtraSecretApplicationPassword"
   echo " "
   exit 1
fi


echo "Creating kumoreg service account"
sudo adduser kumoreg -s /sbin/nologin

echo "Installing kumoreg files"
sudo mkdir /usr/local/kumoreg/
sudo mv KumoReg-*.jar /usr/local/kumoreg/
sudo ln -s /usr/local/kumoreg/KumoReg-*.jar /usr/local/kumoreg/kumoreg.jar

sudo mv addprinter.sh /usr/local/kumoreg/
sudo ln -s /usr/local/kumoreg/addprinter.sh /usr/local/bin/addprinter

echo "Configuring database password"
sudo sed -i "s/spring.datasource.password=password/spring.datasource.password=${1}/g" application.properties
sudo sed -i "s/spring.datasource.password=password/spring.datasource.password=${1}/g" application-training.properties
sudo mv application.properties /usr/local/kumoreg/
sudo mv application-training.properties /usr/local/kumoreg/

echo "Setting permissions"
sudo chown kumoreg:kumoreg /usr/local/kumoreg/KumoReg-*.jar
sudo chmod 755 /usr/local/kumoreg/addprinter.sh
sudo chmod 500 /usr/local/kumoreg/KumoReg-*.jar
sudo chmod 444 /usr/local/kumoreg/*.properties

echo "Adding unit files to systemd config"
sudo mv kumoreg.service /etc/systemd/system/
sudo mv kumoregtraining.service /etc/systemd/system/

echo "Opening firewall ports 8080 and 8081"
sudo firewall-cmd --add-port 8080/tcp
sudo firewall-cmd --permanent --add-port 8080/tcp
sudo firewall-cmd --add-port 8081/tcp
sudo firewall-cmd --permanent --add-port 8081/tcp

echo "Enabling kumoreg services to start at boot"
sudo systemctl enable kumoreg
sudo systemctl enable kumoregtraining

echo "Starting kumoreg services"
sudo systemctl start kumoreg
sudo systemctl start kumoregtraining


