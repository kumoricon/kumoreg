#!/usr/bin/env bash
# Shortcut script for adding specific model printers from the command line
# Tested on Ubuntu 16.04

USAGE="

Automates adding the printer at a given IP address to CUPS with the proper
page size. The printer's name will be its IP address.

Usage: addprinter.sh <ip address> <model>
Note:  <hostname> may now be used instead of <ip address>

Examples:
addprinter.sh 192.168.1.23 8610
addprinter.sh 192.168.1.23 251

Note that the printer models must be configured inside this script.
Must be a member of the sys group to administer printers on CentOS
"

# Validate arguments
if [[ ($# -ne 2) ]] ; then
    echo "${USAGE}"
    exit 1;
fi

# Delete printer if it exists; useful for testing
#lpadmin -x ${1} 2>/dev/null

if [ ${2} = "8610" ]; then
    echo "Adding HP Inkjet Pro 8610 on ${1}"
    lpadmin -p ${1} -v socket://${1} -m drv:///hpcups.drv/hp-officejet_pro_8610.ppd -o media=na_invoice_5.5x8.5in -o printer-error-policy=abort-job  -E
    lpoptions -p ${1} -o PageSize=Custom.8.5x5.5in -o media=Custom.8.5x5.5in


elif [ ${2} = "251" ]; then
    echo "Adding HP Laserjet Pro 200 M251NW on ${1}"
    lpadmin -p ${1} -v socket://${1} -m postscript-hp:/ppd/hplip/HP/hp-laserjet_200_color_m251-ps.ppd -o PageSize=Custom.8.5x5.5i -o media=Custom.8.5x5.5in -o printer-error-policy=abort-job -E
    lpoptions -p ${1} -o PageSize=Custom.8.5x5.5in -o media=Custom.8.5x5.5in

elif [ ${2} = "0000" ]; then
    echo "Adding Brother HL-2540 on ${1}"
    lpadmin -p ${1} -v socket://${1} -m drv:///usr/share/ppd/hl2280dw.ppd -P /usr/share/ppd/hl2280dw.ppd -o PageSize=Custom.8.5x5.5i -o media=Custom.8.5x5.5in -o printer-error-policy=abort-job -E
    lpoptions -p ${1} -o PageSize=Custom.8.5x5.5in -o media=Custom.8.5x5.5in

else
    echo "Error: printer model not found. Must be one of:
    8610            HP Officejet Pro 8610
     251            HP Laserjet Pro 200 M251NW"
    exit 1;
fi