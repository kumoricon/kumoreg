Data Import Format for Attendees Who Preregister on Website
===========================================================

 Tab delimited file with Unix line endings (LF). The first row is a header and is
 currently ignored. (IE, it makes the file easier to read, but changing the field order
 would break the import)
 An example file is [here](PreRegDataImportExample.tsv).
 
 
 Contains the following fields:

| Field | Name              | Empty/Null OK? | Notes                                                                   |
| ----: | ----------------- | :------------: | ----------------------------------------------------------------------- |
|   1   | First Name        | No             | 60 characters max                                                       |
|   2   | Last Name         | No             | 60 characters max                                                       |
|   3   | Badge Name        | Yes            | 60 characters max [[1]](#1)                                             |
|   4   | Badge Number      | Yes            | 10 characters max [[2]](#2)                                             |
|   5   | Zip Code          | Yes            | 10 characters max                                                       |
|   6   | Country           | Yes            | 250 characters max                                                      |
|   7   | Phone             | Yes            | 60 characters max                                                       |
|   8   | Email Address     | Yes            | 250 characters max                                                      |
|   9   | Birthdate         | No             | YYYY-MM-DD format (ex: 1990-12-30)                                      |
|  10   | Emergency Contact | No             | Emergency Contact Name, 250 characters max                              |
|  11   | Emergency Phone   | No             | Emergency Contact Phone, 250 characters max                             |
|  12   | EC Same as Parent | Yes            | "Y" if emergency contact is parent, "N" otherwise (Defaults N if empty) |
|  13   | Parent Name       | Yes            | 250 characters max (required for minors)                                |
|  14   | Parent Phone      | Yes            | 60 characters max (required for minors)                                 |
|  15   | Paid              | No             | "Y" if paid, "N" otherwise. [[3]](#3)                                   |
|  16   | Amount            | No             | Amount Paid, numbers/decimal only. (ex: 50.00 )                         |
|  17   | Pass Type         | No             | 50 characters max, "Weekend", "VIP", etc [[4]](#4)                      |
|  18   | Order ID          | No             | 32 alphanumeric characters (lowercase a-z 0-9) [[5]](#5)                |
|  19   | Notes             | Yes            | Notes/comments (coupon codes, etc)                                      | 
 
 
Notes:
------
Zip Code, Country, Phone, and E-mail address should be passed if possible, but some attendees (children under 6,
for example) are likely to not have one or more so empty/null values are okay.

<a name="1"></a>1: Currently we're storing 60 characters for the badge name. I believe the actual limit
is 15 characters.

<a name="2"></a>2: If empty/null, a badge number will be generated during import.

<a name="3"></a>3: Paid = "N" indicates that money needs to be taken before a badge is printed and the attendee
is allowed in to con. Attendees who have free badges (children under 6) should be "Y". All attendees in a given 
order must be paid or all attendees in an order must be not paid.

<a name="4"></a>4: Valid badge types: 

- Weekend
- VIP
- Artist
- Exhibitor
- Guest
- Emerging Press
- Standard Press
- Industry
- Panelist

Day badges (Friday, Saturday, Sunday) can't be purchased in advance.

<a name="5"></a>5: Used to associate attendees who register at the same time/pay together.