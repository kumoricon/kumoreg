#!/usr/bin/env bash

# This script is meant to be run as part of the guide to set up a development environment for kumoreg
# The guide is available at: https://______________________________

# Update the operating system
sudo apt --yes update && sudo apt --yes upgrade

# Take ownership of the kumoreg files which were downloaded previously
sudo chown -R $(whoami) ~/kumoreg

# Part of the configuration to share files between the host and guest operating systems
sudo apt-get --yes install virtualbox-guest-dkms
sudo usermod -aG vboxsf $(whoami)
sudo VBoxControl guestproperty set /VirtualBox/GuestAdd/SharedFolders/MountDir ~/Desktop

# Turn off operating system features which are not necessary in a development VM
gsettings set org.gnome.desktop.screensaver lock-delay 0
gsettings set org.gnome.desktop.screensaver lock-enabled false
gsettings set org.gnome.desktop.screensaver idle-activation-enabled false
gsettings set org.gnome.desktop.session idle-delay 0
dconf write /org/gnome/desktop/sound/event-sounds false

# Install Java 9 JDK
sudo apt-get --yes install openjdk-9-jdk vim

# Install MariaDB Server and create an empty database
sudo apt-get --yes install mariadb-server
sudo mysql < ~/kumoreg/install/script/database.sql

# Install and configure printer support
sudo apt-get --yes install cups hplip cups-bsd
cd ~/kumoreg/install/config
sudo cp cupsd.conf /etc/cups
sudo chmod 644 /etc/cups/cupsd.conf
chown root:lp /etc/cups/cupsd.conf
sudo systemctl enable cups
sudo systemctl start cups
sudo useradd kumoreg -s /sbin/nologin
sudo usermod -aG lpadmin kumoreg

# Install other software needed for development purposes
sudo apt-get --yes install maven
