#!/usr/bin/env bash

# Install and configure MySQL/MariaDB

if [[ ($# -ne 2) ]]; then
   echo " "
   echo "Error: ${0} must be called with two arguments:"
   echo "       database root password, database application connection password"
   echo "Example:"
   echo "    ${0} SuperSecretRootPassword ExtraSecretApplicationPassword"
   echo " "
   exit 1
fi

echo "Installing mariadb"
sudo yum install -y mariadb-server
sudo systemctl enable mariadb
sudo systemctl start mariadb.service

# Secure installation and create databases
cat >~/commands.sql << EndOfSQL
UPDATE mysql.user SET Password=PASSWORD('${1}') WHERE User='root';
DELETE FROM mysql.user WHERE User='';
DELETE FROM mysql.user WHERE User='root' AND Host NOT IN ('localhost', '127.0.0.1', '::1');
DROP DATABASE IF EXISTS test;
DELETE FROM mysql.db WHERE Db='test' OR Db='test\\_%';
FLUSH PRIVILEGES;

CREATE DATABASE kumoreg CHARACTER SET UTF8;
CREATE DATABASE kumoreg_training CHARACTER SET UTF8;
CREATE USER 'kumoreg'@'localhost' IDENTIFIED BY '${2}';
GRANT ALL PRIVILEGES ON kumoreg.* to 'kumoreg'@'localhost';
CREATE USER 'kumoregtraining'@'localhost' IDENTIFIED BY '${2}';
GRANT ALL PRIVILEGES ON kumoreg_training.* to 'kumoregtraining'@'localhost';
EndOfSQL

mysql -sfu root mysql < commands.sql
rm ~/commands.sql

