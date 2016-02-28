Security Rights
===============

Rights are individual strings that are checked to enable/disable features for a certain user. Names are not case 
sensitive, and should have underscores between words.

Available rights are:

| Name                        | Description                                                       |
|-----------------------------|-------------------------------------------------------------------|
| at_con_registration         | Can add new attendees via At-Con Registration                     |
| pre_reg_check_in            | Can check in preregistered attendees                              |
| attendee_search             | Can search for and view attendees                                 |
| attendee_edit               | Can edit attendees from search results                            |
| attendee_edit_notes         | Can edit notes field on attendees, but no other fields            |
| attendee_override_price     | Can manually set price for attendee                               |
| print_badge                 | Can print badge on attendee checkin                               |
| reprint_badge               | Can reprint attendee badges after attendee is checked in          |
| reprint_badge_with_override | Can reprint badge if a user with reprint_badge right approves it  |
| badge_type_press            | Can select the "Press" badge type                                 |
|                             |                                                                   |
| view_attendance_report      | Can view attendance report (counts only)                          |
| view_revenue_report         | Can view revenue report                                           |
| view_staff_report           | Can view staff report (lists name/phone numbers)                  |
| view_role_report            | Can view roles and rights assigned to each                        |
|                             |                                                                   |
| manage_staff                | Can add/edit users and reset passwords                            |
| manage_pass_types           | Can add/edit badge types                                          |
| manage_roles                | Can add/edit security roles                                       |
| manage_devices              | Can add/edit devices (computer/printer mappings)                  |
| import_pre_reg_data         | Import pre-registered attendees and orders                        |
| load_base_data              | Loads default data (users, roles, rights)                         |
| super_admin                 | Override - can do everything                                      |

Rights can be added/removed from a given Role in the regular user interface.


Adding new rights
-----------------
Currently there's no interface for adding rights in the UI (since they're only useful from code).
If you add one, make sure to add it to LoadTestDataPresenter (loads default roles/rights/badges/etc) along
with setting it in the appropriate Role. 

The name should be self explanatory.
