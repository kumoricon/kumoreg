Change Log
----------------
- Version 0.9.7 (Pending)
    - Removed Check In by Badge Report, added Search by Badge Type screen. (Attendees can be 
      edited from the Search by Badge Type screen with the proper permission)
    - Long names (and badge names) will be resized to fit on the badge (to a certain point)

- Version 0.9.6 (9/13/2016)
    - Changed error message to disappear automatically after a few seconds, instead of requiring a click
    - Allow MMDDYYYY in birthdate field (as well as MM/DD/YYY and MM-DD-YYYY). It will be converted to MM/DD/YYYY 
      automatically
    - When birthdate field is cleared, also remove paid amount and the age in years from the form
    - Added editable badge prefix to users, rather than basing it on the first letter of their names 
    - Rearranged fields on User edit window to be more convenient; username and badge prefix will be auto generated
    - Added configuration option to display "training mode" banner behind left menu
    - Log IP address in place of username for users that are not logged in when they view a page
    - Added a prompt when logging out if user needs to close their till (has orders worth more than $0 
      in current session)
    - Added confirmation window when canceling an order
    - When adding Computer/Printer mapping in admin panel, default computer's IP address to current client's address, 
      and printer's IP address to the current network.
    - Director role can use the staff badge type
    - Fixed text wrapping on Attendee Report
    - Added example automated deployment scripts, see [Installation](docs/installation.md)
    - Stopped logging all reformatted phone numbers (still available with debug level)
    - Minor bugfixes on till report
    - Always allow Notes/History table to scroll when viewing attendee
    - Model cleanup - made equals()/hashcode() consistent

- Version 0.9.5 (8/20/2016)
    - Attendee importer will work when there are only 18 fields instead of 19 (notes field is missing/blank)
    - Moved the Lite badge color stripe up a point. (Alignment done; matches current badge stock)

- Version 0.9.4 (8/17/2016)
    - Fixed duplicate badge number creation
    - Format phone numbers as (xxx) xxx-xxxx instead of xxx-xxx-xxxx
    - Added parental consent form received checkbox to At-Con registration screen
    - Use computer specific offsets when displaying badge PDFs in browser
    - Added base data for one-day events
    - Removed unnecessary error logging from BadgePresenter
    - Added revenue to Attendance report with right view_attendance_report_revenue
    - Added roles for VIP and other special badge (press, etc) check in
    - Disabled animations to improve browser performance
    - Fixed bug that would keep application from starting with OpenJDK
    - Added Staff badge type to Lite data
    - For orders with a total of $0, payment type no longer required; it will be set to cash automatically
    - Disable pre reg check in button until information has been verified and consent form has been received for minors
    - For tables that are clickable, change pointer to hand instead of arrow
    
- Version 0.9.3 (8/6/2016)
    - Select the number of badges to generate when printing test badges
    - Added configuration option for requiring attendee phone or email
    - Added custom (somewhat more colorful) theme
    - Added Lite/Full badge formats and configuration option to choose between them
    - On PreReg screen, changed label "Last Name or Order ID" to "Last Name" for clarity
    - Attempted fix for scroll bars sometimes not appearing in Attendee edit window
    - Added printer offset fields to Print Test Badge screen for users with manage_devices right
    - Renamed Right attendee_edit_notes to attendee_add_note to more accurately reflect what it does
    - Changed password reset message to indicate what's happening


- Version 0.9.2 (7/21/2016)
    - Added back/forward button support to Check In by Badge report
    - Print report to the user's printer when they close out their till
    - Added configurable offset to Computers, which will move printed output for that computer by points (1/72 inch); works 
      for both Badges and the Close Out Till report
    - Back/forward will work on PreReg screen


- Version 0.9.1 (6/13/2016)
    - Added scroll bars to modal attendee windows
    - Accept "-" or "/" in birth dates
    - Extended Panelist report to work for any badge type and renamed it to "Check In by Badge"
    - Format names and telephone numbers on blur
    - Updated documentation


- Version 0.9.0 (5/30/2016)
    - Initial release for testing
