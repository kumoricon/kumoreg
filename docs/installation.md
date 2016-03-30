# Installation

**DRAFT**

1. Install Tomcat (and MySQL if you don't already have a database server)
2. Place kumoreg.jar in webapps directory
3. Configure all network printers in CUPS.
4. Update database information in application.properties file
5. If printing via printers installed on the server, set kumoreg.printing.enablePrintingFromServer=true in 
   application.properties
6. Start Tomcat
7. Browse to http://servername:8080/ 
8. Log in as admin / password
9. Load base data from Utilities > Load Base Data, or import backed up data
10. If necessary:
    - Configure Roles under Administration > Roles
    - Configure Users under Administration > Users
    - Configure Badge Types under Administration > Badge Types
    - Map printers and computers under Administration > Computers
