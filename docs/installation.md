# Installation

To install a single instance of KumoReg, skip to Manual Installation below.

## Automatic installation via script
Scripts to install a production and training instance of KumoReg and all its requirements 
on a fresh Ubuntu 18.04 server. X-Y-Z is the version number.

**This will install Java, CUPS, MariaDB and overwrite the default configurations.**

1) Copy kumoreg-X-Y-Z-bundle.tar.gz to the server
2) On the server, run:

```
tar -xzvf kumoreg-X-Y-Z-bundle.tar.gz
cd kumoreg-X-Y-Z
```

3) Set the database password in:
    - ```config/application.properties```
    - ```config/application-training.properties```
    - ```and script/database.sql```

4) Run ```sudo ./install-ubuntu.sh```

Kumoreg will be installed in /usr/local/kumoreg

| Environment | Port | Service Name / DB user |  Database Name   | Configuration file                                 |
|-------------|------|------------------------|------------------|----------------------------------------------------|
| Production  | 8080 | kumoreg                | kumoreg          | /usr/local/kumoreg/application.properties          |
| Training    | 8081 | kumoregtraining        | kumoreg_training | /usr/local/kumoreg/application-training.properties |
| CUPS        |  631 |                        |                  |                                                    |

Services will be configured to start on boot via SystemD, which also handles logging.


5. Go to **KumoReg configuration**, below.
    
    
## Manual installation

(Assumes init.d based server)

1. **Installation** 

    1. Create directory ```sudo mkdir /usr/local/kumoreg/```
    2. Build .jar file and copy it to /usr/local/kumoreg/
    3. Create symlink with ```sudo ln -s /usr/local/kumoreg/KumoReg-x.x.x.jar /usr/local/kumoreg/kumoreg.jar```
    4. Create symlink in /etc/init.d ```sudo ln -s /usr/local/kumoreg/kumoreg.jar /etc/init.d/kumoreg```

2. **Securing the application**

    5. Create a user to run the application ```adduser kumoreg -r -s /usr/sbin/nologin```
    6. Set KumoReg-x.x.x.jar to be owned by the new user ```sudo chown kumoreg:kumoreg /usr/local/kumoreg/KumoReg-*.jar```
    7. Make the .jar file read+execute only ```sudo chmod 500 /usr/local/kumoreg/KumoReg-*.jar```

3. **Configuration**

    8. Place [application.properties](installation/production/application.properties) file in /usr/local/kumoreg/ 
    9. Configure database settings in application.properties
    10. If printing via printers installed on the server, set kumoreg.printing.enablePrintingFromServer=true in 
       application.properties   
    11. Configure paths for staff data import in application.properties
    12. Make application.properties read only: ```sudo chmod 444 /usr/local/kumoreg/application.properties```
    13. Start service: ```sudo service kumoreg start```


## KumoReg configuration

1. Browse to http://servername:8080/ 
2. Log in as admin / password
3. Load base data from Utilities > Load Base Data
4. If necessary:
    - Set up printers in CUPS at https://servername:631 or user the ```addprinter``` script
    - Configure Roles under Administration > Roles
    - Configure Users under Administration > Users
    - Configure Badge Types under Administration > Badge Types
    - Map printers and computers under Administration > Computers
5. Copy any badge resources (background PDFs and fonts) to /usr/local/kumoreg/badgeResource
6. Set up staff integration script to write to inbox path specified above