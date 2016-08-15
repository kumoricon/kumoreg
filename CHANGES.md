Change Log
----------------
- Version 0.9.4 (Pending)
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
    - Accept "-" or "/" in birthdates
    - Extended Panelist report to work for any badge type and renamed it to "Check In by Badge"
    - Format names and telephone numbers on blur
    - Updated documentation


- Version 0.9.0 (5/30/2016)
    - Initial release for testing
