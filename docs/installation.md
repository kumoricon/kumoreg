# Installation

(Assumes init.d based server)

1. **Installation** 

    1. Create directory ```sudo mkdir /usr/local/kumoreg/```
    2. Build .jar file and copy it to /usr/local/kumoreg/
    3. Create symlink with ```sudo ln -s /usr/local/kumoreg/KumoReg-x.x.x.jar /usr/local/kumoreg/KumoReg.jar```
    4. Create symlink in /etc/init.d ```sudo ln -s /usr/local/kumoreg/KumoReg.jar /etc/init.d/kumoreg```

2. **Securing the application**

    5. Create a user to run the application ```adduser kumoreg -r -s /usr/sbin/nologin```
    6. Set KumoReg-x.x.x.jar to be owned by the new user ```sudo chown kumoreg:kumoreg /usr/local/kumoreg/KumoReg-*.jar```
    7. Make the .jar file read+execute only ```sudo chmod 500 /usr/local/kumoreg/KumoReg-*.jar```

3. **Configuration**

    8. Place [application.properties](installation/application.properties) file in /usr/local/kumoreg/ 
    9. Configure database settings in application.properties
    10. If printing via printers installed on the server, set kumoreg.printing.enablePrintingFromServer=true in 
       application.properties   
    11. Make application.properties read only ```sudo chmod 444 /usr/local/kumoreg/application.properties```

4. **Initial Setup**

    12. Start service: ```sudo service kumoreg start```
    13. Browse to http://servername:8080/ 
    14. Log in as admin / password
    15. Load base data from Utilities > Load Base Data, or import backed up data
    16. If necessary:
        - Configure Roles under Administration > Roles
        - Configure Users under Administration > Users
        - Configure Badge Types under Administration > Badge Types
        - Map printers and computers under Administration > Computers
