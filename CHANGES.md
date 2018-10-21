Change Log
----------------
- Version 4.0.1 (Pending)

- Version 4.0.0 (10/21/2018)
    - Fixed bug where you couldn't add attendees to an at con order with a zero-cost attendee
    - Fixed bug where completing a zero-cost order wouldn't navigate to print badge screen
    - Fixed bug where birthdays on 12/31 would show up in the wrong year on check in screen
    - When checking "Parent is Emergency Contact", copy data from which ever field has data to
      which ever one is empty. (If both have data, copy emergency contact info to parent contact
      fields)
    - Timezone fixes on session and payment records (WARNING: not an actual schema change, but
      this will likely break compatibility with older databases)
    - Removed padding on Attendee detail form so it will fit better on 1366x768 screens (actual browser size: 1366x662)
    - Added printer override for reports. Reports can be sent to a designated printer instead of the
      one assigned to a computer. This is most useful for till reports.
    - Fixed bug where Till Session Admin page wouldn't show sessions if there was a session with no end time
    - Track override user in notes when reprinting badge

- Version 3.0.7 (7/26/2018)
    - Added Edit button on check in views
    - Automatically print badge on pre reg check in
    - Cut down on logging when entering views
    - Generate order ID if it is missing during attendee import (added for staff, but occurs for all imports)
    - Added workaround for staff imported with missing information - checkin won't be allowed until emergency contact 
      and birthdate is set. In imported data, 1/1/1900 is a "magic number" that represents a missing birthdate.

- Version 3.0.6 (7/25/2018)
    - Auto-print badges when entering check in and order print views
    - Auto-detect network printers
    - Added Exhibitor w/Wifi, Artist w/Wifi, and extra badge types
    - Added button on Print Test Badge view to test print troublesome fan
      names (names that have caused issues in the past) for easy font testing

- Version 3.0.5 (7/13/2018)
    - Added right check to manual price override
    - Added example test data to docs/testdata directory
    - Added reprint button to Order print view
    - Updated lite badge price, day
    - Kumo Lite 2018 badge format added
    - During at con reg, if only one badge type is available auto select it
    - Added ability to parse MMDDYY dates. 00-18 will be 20xx, 19-99 will be
      19xx. (Shifts automatically each year)

- Version 3.0.4 (6/18/2018)
    - Show parental consent form checkbox in detail form
    - Fix enable/disable save button on checkin pages
    - Added New Registration button to homepage
    - Renamed "At-Con check in" to "New Registration"
    - Replaced birthdate field to fix bug in DateField control
    - Fixed order notes getting lost on navigation
    - Changed some fields to only validate after blur (won't remove spaces as you type)
    - Set timezone explicitly

- Version 3.0.3 (5/31/2018)
    - Staff check in fixes
    - Add dummy e-mail address when importing staff
    - Don't show "*" on phone and email fields when they are not required per configuration
    - Tweaked tabIndex on payment views
    - Updated layout on report views

- Version 3.0.2 (5/1/2018)
    - Added Check In links to search by name/badge pages
    - Added search to home page

- Version 3.0.1 (3/10/2018)
    - Moved most windows to their own views
    - Require both phone number and email address when registering at con
    - Don't show Fan Name field when registering at con
    - Hide legal first name and legal last name by default, added checkbox to display
      those fields
    - Migrated Vaadin 7 controls to Vaadin 8

- Version 3.0.0 (12/29/2017)
    - Layout change for speed (Base on CSSLayout)
    - Upgrade to Vaadin 8.1.0
    - Upgrade to Spring Boot 1.5.2 (database schema changed)
    - Trainingmode defaults to false
    - Minor UI corrections
    - Fixed bug where adding printers through UI would fail after repeated use
    - UI migration to individual views still needed

- Version 2.0.8 (10/28/2017)
    - Day badges use age range color on bottom of badge. Fixed color of font
      to be white or black on that as appropriate
    - Only validate attendee detail form in handler, not in the form itself
      (fixes bug where a missing field would only show the "parental consent
      form not received" message)
    - After showing search results, select all text in the search box and focus on that

- Version 2.0.7 (10/27/2017)
    - Fixed file handle leak caused by PDFBox

- Version 2.0.5 (10/25/2017)
    - Clear badge preprinted flag when a staffer is imported
    - Remove staff that were deleted from online system during data import

- Version 2.0.4 (10/24/2017)
    - Import attendees in JSON format
    - Added Small Press badge type
    - Required fields for attendees are marked with an asterisk (phone OR email is required, not set as required)
    - Set security requirements on all badge types and updated roles appropriately
    - Added Pre-print badge button to Attendee window
    - Badge color updates

- Version 2.0.3 (10/20/2017)
    - Fixed bug that would cause server to crash after running out of files
    - Added button to print example of all badge types
    - Added notes to staff import

- Version 2.0.2 (10/10/2017)
    - Split out badge type color bar and age range color bar
    - Added badge type to Print Test Badge screen
    - Added .json file import for staff information
    - Skip printing badges for attendees that are flagged as having pre-printed badges (staff)
    - Select all attendees by default in attendee print window. (No change in behavior - but users would
      select attendees one by one thinking that they had to before choosing "printed successfully")
    - Added .json file import for staff information
    - Select all attendees by default in attendee print window. (No change in behavior - but users would
      select attendees one by one thinking that they had to before choosing "printed successfully")
    - Staff badges added
    - Fixed bug where attendee checkin time would be updated after they had already checked in
    - Added 2017 badge format
    - Added pre-printing feature to generate badges before con

- Version 2.0.1 (7/4/2017)
    - Fixed bug where adding a minor without a parental consent form would display red "!" instead
      of a useful error message
    - Fixed bug where deleting an attendee from an unpaid order would not completely delete them
    - Disable Take Payment button when order total is $0
    - Disable Order complete button when there are no attendees in the order
    
- Version 2.0.0 (5/31/2017)
    - Bugfix: Till report won't scroll when longer than one screen high
    - Database schema/model changes. Not compatible with previous databases
    - Maintain sort order in search results after editing an attendee
    - Search by first name, last name, and badge name instead of just last name.
    - Added attendee export to tab-separated file
    - Added till export to tab-separated file
    - Fixed Reprint Badge button on Test Badge window
    - Added friendly error message when trying to print and no printer can be found
    - Don't change paid amount automatically when editing an attendee that has already checked in
    - Removed Pre Reg check in screen, added check in button on Search windows
    - When adding attendee at con, carry over emergency contact info from the previous attendee in that order
    - Added "Add Note" button for at-con attendees
    - Added Order List view
    - Added multiple payments per order
    - Added legal name fields
    - Added "Comped Badge" checkbox to attendee detail form, field to Attendee record
    - Added Badge Type field (attendee, staff, other)
    - Changed Badge Admin screen to use table
    - Changed User Admin screen to use table
    - Moved till sessions to their own table, rebuilt till report
    - Added till session management/reporting screen (can close tills for other users with proper right)
    - Only create example users when trainingMode is enabled in configuration file
    - Changed "badge name" to "fan name"
    - Added check ins by user in last 15 minutes report
    - Support printing different layouts for different badge types

- Version 1.0.2 (10/27/2016)
    - Bugfix: refresh search view after "save and reprint badge"

- Version 1.0.1 (10/26/2016)
    - Allow editing on prereg check in window if user has attenee_edit right
    
- Version 1.0.0 (10/22/2016)
    - Till report only displays closed sessions, displays same start and end time for all
      payment types in session
    - Added blacklist warning messages (at-con check in only)
    - When checking in a pre registered attendee, if they have not paid redirect to the at-con order flow 
      with their order open.
    - When importing attendees, verify that every attendee in an order has either paid or not paid

- Version 0.9.7 (10/21/2016)
    - Removed Check In by Badge Report, added Search by Badge Type screen. (Attendees can be 
      edited from the Search by Badge Type screen with the proper permission)
    - Long names (and badge names) will be resized to fit on the badge (to a certain point)
    - Added automated install script for Ubuntu 16.04, updated installation instructions
    - Final stripe colors for specialty badges set in base data
    - Added basic till report

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
