#!/usr/bin/env bash
# Install/configure KumoReg and dependencies on an Ubuntu 16.04 server
# This script assumes a fresh server and will install updates and dependencies as well as
# create the database and overwrite default configuration files

# If running in Virtualbox, you probably want to use a bridged network adapter

# Edit passwords in:
#     config/application.properties
#     config/application-training.properties
#     script/database.sql

SERVER_PACKAGES="openjdk-8-jdk-headless vim"
FONT_PACKAGES="fonts-dejavu* fonts-hack* fonts-takao-mincho fonts-takao unifont ttf-unifont fonts-noto ttf-wqy-zenhei ttf-wqy-microhei"
CUPS_PACKAGES="cups hplip cups-bsd"
DATABASE_PACKAGES="mariadb-server"
# Enable Universe repository for JDK 8
sudo add-apt-repository universe

# Update packages
apt-get -y update

# Disable automatic updates
rm /etc/apt/apt.conf.d/20auto-upgrades
echo 'APT::Periodic::Update-Package-Lists "0";' > /etc/apt/apt.conf.d/20auto-upgrades
echo 'APT::Periodic::Unattended-Upgrade "0";' >> /etc/apt/apt.conf.d/20auto-upgrades
systemctl stop apt-daily.timer
systemctl disable apt-daily.timer
systemctl disable apt-daily.service
systemctl stop apt-daily-upgrade.timer
systemctl disable apt-daily-upgrade.timer
systemctl disable apt-daily-upgrade.service


# Wait for package managers to finish
while fuser /var/lib/dpkg/lock >/dev/null 2>&1 ; do
    echo -en "\rWaiting for other software managers to finish..."
    sleep 0.5
    ((i=i+1))
done

# Install software
apt-get -y install ${SERVER_PACKAGES} ${FONT_PACKAGES} ${CUPS_PACKAGES} ${DATABASE_PACKAGES}


# Configure CUPS
paperconfig -p statement
systemctl stop cups
mv -n /etc/cups/cupsd.conf /etc/cups/cupsd.conf.orig
mv config/cupsd.conf /etc/cups/
chmod 644 /etc/cups/cupsd.conf
chown root:lp /etc/cups/cupsd.conf
systemctl enable cups
systemctl start cups
systemctl enable cups-browsed
systemctl start cups-browsed

# Configure MySQL/MariaDB
# Edit script/database.sql and change "password" to actual db password
echo "Creating databases"
mysql < script/database.sql

# Install kumoreg
useradd -m kumoreg -s /sbin/nologin
usermod -a -G lpadmin kumoreg
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

if [ -e /usr/local/kumoreg/badgeResources ] ; then
    echo "/usr/local/kumoreg/badgeResources exists - leaving in place"
else
    mkdir "/usr/local/kumoreg/badgeResources"
fi

if [ -e /usr/local/kumoreg/badgeResources/badgeImage ] ; then
    echo "/usr/local/kumoreg/badgeResources/badgeImage exists - leaving in place"
else
    mkdir "/usr/local/kumoreg/badgeResources/badgeImage"
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


