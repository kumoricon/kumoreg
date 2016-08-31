#!/usr/bin/env bash


USAGE="

Automates adding the printer at a given IP address to CUPS with the proper
page size. The printer's name will be its IP address.

Usage: addprinter.sh <ip address> <model>

Examples:
addprinter.sh 192.168.1.23 8610
addprinter.sh 192.168.1.23 251

Note that the printer models must be configured inside this script.
Must be a member of the lp group
"

# Validate arguments
if [[ ($# -ne 2) ]] ; then
    echo "${USAGE}"
    exit 1;
fi

if [[ "${1}" =~ ^[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}$ ]]
then
    ipAddress=${1}
else
    echo "Error: First argument must be a valid IPv4 address"
    exit 1;
fi


if [ ${2} = "8610" ]; then
    echo "Adding HP Inkjet Pro 8610 on ${1}"
    lpadmin -p ${1} -v socket://${1} -m drv:///hp/hpcups.drv/hp-officejet_pro_8600.ppd -o media-default=na_invoice_5.5x8.5in -E


elif [ ${2} = "251" ]; then
    echo "Adding HP Laserjet Pro 200 M251NW on ${1}"
    lpadmin -p ${1} -v socket://${1} -m lsb/usr/HP/hp-laserjet_200_color_m251-ps.ppd.gz -o media-default=na_invoice_5.5x8.5in -E


else
    echo "Error: printer model not found. Must be one of:
    8610            HP Officejet Pro 8610
     251            HP Laserjet Pro 200 M251NW"
    exit 1;
fi
