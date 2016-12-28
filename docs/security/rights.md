Security Rights
===============

Rights are individual strings that are checked to enable/disable features for a certain user. Names are not case 
sensitive, and should have underscores between words.

Available rights are:

| Name                          | Description                                                     |
|-------------------------------|-----------------------------------------------------------------|
| at_con_registration           | Add new attendees via At-Con Registration and close till        |
| at_con_registration_blacklist | Allow at-con registration for names on the blacklist            |
| pre_reg_check_in              | Check in preregistered attendees                                |
| pre_reg_check_in_edit         | Edit preregistered attendees during check in                    |
| attendee_search               | Search for and view attendees                                   |
| attendee_edit                 | Edit attendees from search results                              |
| attendee_add_note             | Add note to attendee                                            |
| attendee_override_price       | Manually set price for attendee                                 |
| attendee_edit_with_override   | Edit attendee if user with attendee_edit right approves it <sup>[1](#footnote1)</sup>|
| print_badge                   | Print badge on attendee check in                                |
| reprint_badge                 | Reprint attendee badges after attendee is checked in            |
| reprint_badge_with_override   | Reprint badge if a user with reprint_badge right approves it    |
| badge_type_emerging_press     | Select/check in the \"Emerging Press\" badge type               |
| badge_type_standard_press     | Select/check in the \"Standard Press\" badge type               |
| badge_type_vip                | Select/check in the \"VIP\" badge type                          |
| badge_type_artist             | Select/check in the \"Artist\" badge type                       |
| badge_type_exhibitor          | Select/check in the \"Exhibitor\" badge type                    |
| badge_type_guest              | Select/check in the \"Guest\" badge type                        |
| badge_type_industry           | Select/check in the \"Industry\" badge type                     |
| badge_type_panelist           | Select/check in the \"Panelist\" badge type                     |
| badge_type_staff              | Select/check in the \"Staff\" badge type
|                               |                                                                 |
| view_attendance_report        | View attendance report (counts only)                            |
| view_attendance_report_revenue| View attendance report (with revenue totals, requires view_attendance_report)  |
| view_check_in_by_hour_report  | View attendee check ins per hour report                         |
| view_staff_report             | View staff report (lists name/phone numbers)                    |
| view_role_report              | View registration system role report                            |
| view_check_in_by_badge_report | View check ins by badge type report                             |
| view_till_report              | View till report                                                |
| view_export                   | Export information/reports                                      |
|                               |                                                                 |
| manage_staff                  | Add/edit users and reset passwords                              |
| manage_pass_types             | Add/edit badge types                                            |
| manage_roles                  | Add/edit security roles                                         |
| manage_devices                | Add/edit devices (computer/printer mappings). Test offsets on Print Test Badge screen |                                         
| manage_orders                 | View/edit orders                                                |
| import_pre_reg_data           | Import pre-registered attendees and orders                      |
| load_base_data                | Load default data (users, roles, rights)                        |


<a name="footnote1">1</a>: When an override is entered, the user gets all edit rights that the overriding user has in
a single attendee edit window. For example, attendee_override_price.

Rights can be added/removed from a given Role in the regular user interface.


Adding new rights
-----------------
Currently there's no interface for adding rights in the UI (since they're only useful from code).
If you add one, make sure to add it to LoadTestDataPresenter (loads default roles/rights/badges/etc) along
with setting it in the appropriate Role. 

The name should be self explanatory.
