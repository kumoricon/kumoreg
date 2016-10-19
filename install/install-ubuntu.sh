#!/usr/bin/env bash
# Install/configure KumoReg and dependencies on an Ubuntu 16.04 server
# This script assumes a fresh server and will install updates and dependencies as well as
# create the database and overwrite default configuration files

# If running in Virtualbox, you probably want to use a bridged network adapter

# Edit passwords in:
#     config/application.properties
#     config/application-training.properties
#     script/database.sql

# Update packages
apt-get -y update
apt-get -y upgrade

# Install server software
apt-get -y install openjdk-8-jdk vim

# Install cups 
apt-get -y install cups hplip
systemctl stop cups
mv -n /etc/cups/cupsd.conf /etc/cups/cupsd.conf.orig
mv config/cupsd.conf /etc/cups/
chmod 644 /etc/cups/cupsd.conf
chown root:lp /etc/cups/cupsd.conf
systemctl enable cups
systemctl start cups

# Install mariadb
apt-get -y install mariadb-server

# Edit script/database.sql and change "password" to actual db password
echo "Creating databases"
mysql < script/database.sql

# Install kumoreg
useradd kumoreg -s /sbin/nologin
mkdir /usr/local/kumoreg/
mv KumoReg-*.jar /usr/local/kumoreg/

if [ -h /usr/local/kumoreg/kumoreg.jar ] ; then
    rm /usr/local/kumoreg/kumoreg.jar
fi
ln -s /usr/local/kumoreg/KumoReg*.jar /usr/local/kumoreg/kumoreg.jar

mv script/addprinter.sh /usr/local/kumoreg/
if [ -h /usr/local/bin/addprinter ] ; then
    rm /usr/local/bin/addprinter
fi
ln -s /usr/local/kumoreg/addprinter.sh /usr/local/bin/addprinter

if [ -e /usr/local/kumoreg/application.properties ] ; then
    echo "/usr/local/kumoreg/application.properties found - leaving existing file in place"
else
    mv config/application.properties /usr/local/kumoreg/
fi

if [ -e /usr/local/kumoreg/application-training.properties ]
then
    echo "/usr/local/kumoreg/application-training.properties found - leaving existing file in place"
else
    mv config/application-training.properties /usr/local/kumoreg/
fi


chown kumoreg:kumoreg /usr/local/kumoreg/KumoReg-*.jar
chmod 755 /usr/local/kumoreg/addprinter.sh
chmod 500 /usr/local/kumoreg/KumoReg-*.jar
chmod 444 /usr/local/kumoreg/*.properties

echo "Adding unit files to systemd config"
mv config/kumoreg.service /etc/systemd/system/
mv config/kumoregtraining.service /etc/systemd/system/

systemctl enable kumoreg
systemctl enable kumoregtraining
systemctl start kumoreg
systemctl start kumoregtraining


