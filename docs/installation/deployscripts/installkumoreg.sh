#!/usr/bin/env bash

# Install KumoReg and required dependences on the target server.
#
#
# Before running this script: 
#    - Make sure the configuration options below are correct
#    - Turn off the sudo password for members of the wheel group to 
#      make your life easier.
#
#      Run:
#              sudo visudo
#
#      And change the lines:
#              ## Same thing without a password
#              # %wheel        ALL=(ALL)       NOPASSWD: ALL
#      To:
#              ## Same thing without a password
#              %wheel        ALL=(ALL)       NOPASSWD: ALL
#



##############################################################################
## Configuration
##############################################################################

# Your home directory, no trailing slash
HOMEDIR="${HOME}"

# Location of the KumoReg source files; package should be built before running
# this script. Don't include the trailing slash.
SOURCEDIR="${HOMEDIR}/IdeaProjects/kumoreg"

# KumoReg jar file name
JARFILE="KumoReg-0.9.5.jar"

# Database root user password. Note, this is the password that will be set, 
# this script assumes that the database's root password is blank (which it 
# is by default)
DBROOTPASSWORD="MyRootPassword"

# Database Application Password (used by kumoreg and kumoregtraining)
DBAPPPASSWORD="MyAppPassword"

#############################################################################
## End Configuration
#############################################################################

USAGE="

Automates installing KumoReg on a Centos 7 server from a bare install via ssh,
including setting up users and databases. See configuration options in this 
script.

Usage:  installkumoreg.sh user@server.address

Examples:
	installkumoreg.sh jason@192.168.1.59

"

# Validate arguments
if [[ ($# -ne 1) || ${1} == "-h" || ${1} == "--help" || ${1} == "-?" ]] ; then
    echo "${USAGE}"
    exit 1
fi

# Validate configuration options. Files here will be copied to the target server
REQUIREDFILES=(
"${SOURCEDIR}/docs/installation/production/application.properties"
"${SOURCEDIR}/docs/installation/training/application-training.properties"
"${SOURCEDIR}/docs/installation/production/kumoreg.service"
"${SOURCEDIR}/docs/installation/training/kumoregtraining.service"
"${SOURCEDIR}/docs/installation/addprinter.sh"
"${SOURCEDIR}/target/${JARFILE}" )

for f in ${REQUIREDFILES[@]}; do
    if [[ ! -r "${f}" ]] ; then
        echo "Error: ${f} not found. Make sure SOURCEDIR is set correctly in "
        echo "installkumoreg.sh"
        exit 1
    fi
done


# Add ssh key to remote authorized_users file if it exists
if [[ -r "${HOMEDIR}/.ssh/id_rsa.pub" ]]; then
  echo "Copying id_rsa.pub to remote authorized_keys file. You will have to 
        enter your password on the remote server twice."
  ssh "${1}" 'mkdir .ssh 2>/dev/null; chmod 700 .ssh'
  scp "${HOMEDIR}/.ssh/id_rsa.pub" "${1}:.ssh/authorized_keys"
fi

echo "Copying KumoReg files to remote server"
for f in ${REQUIREDFILES[@]}; do
    scp "${f}" "${1}:"
done

echo "Executing setup scripts"
SCRIPTS=(
'10-update.sh'
'20-installsoftware.sh'
'21-installcups.sh'
'22-installmariadb.sh'
'23-installkumoreg.sh'
'30-createadmins.sh'
)

for f in ${SCRIPTS[@]}; do
    echo "Copying ${f} to remote server"
    scp "${f}" "${1}:"
done

# Run scripts individually since some of them take different arguments
ssh -t -t "${1}" "bash 10-update.sh"
ssh -t -t "${1}" "bash 20-installsoftware.sh"
ssh -t -t "${1}" "bash 21-installcups.sh"
ssh -t -t "${1}" "bash 22-installmariadb.sh ${DBROOTPASSWORD} ${DBAPPPASSWORD}"
ssh -t -t "${1}" "bash 23-installkumoreg.sh ${DBAPPPASSWORD}"
ssh -t -t "${1}" "bash 30-createadmins.sh"


for f in ${SCRIPTS[@]}; do
    echo "Deleting ${f} on remote server"
    ssh "${1}" "rm ${f}"
done

