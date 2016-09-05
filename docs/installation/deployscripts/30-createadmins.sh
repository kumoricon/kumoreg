#!/usr/bin/env bash

# Add some admin users on the server. Make them members of wheel (so they can
# sudo) and sys (so they can manage printers). Groups may need to be changed
# if you're installing on a non-Centos Linux distribution.

echo "Creating admin user accounts"

ADMINS=(
'micah'
'kent'
)

for u in ${ADMINS[@]}; do
    echo "    Adding user ${u} in groups: wheel, sys"
    sudo adduser ${u} -G wheel,sys
done


