# Architecture

## Overall System

- Server running Linux
    - Web application server (Tomcat)
    - Database (MySQL)
    - Printers installed locally on server (Printing via Java built in features, tested with CUPS)
- Workstations may be thin clients or regular computers
    - Configuration based on IP address - IP address must remain the same
- Printers must be networked 
    - Configuration based on IP address - IP address must remain the same



## Printing

Badges are generated as PDFs on the fly and printed to printers installed locally on the server. In the past, 
printer settings/drivers has been problematic.

As a backup, the generated PDF may be displayed in the browser and printed locally via the workstation's printer
drivers. This requires the user to do more clicks (it doesn't print automatically when the PDF is displayed)