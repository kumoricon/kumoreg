#Logging

The application server's log file is written to /var/log/kumoreg.log by default.


##Examples


```
2016-04-24 11:41:51.681  INFO 5906 --- [nio-8080-exec-8] o.k.p.attendee.AttendeeSearchPresenter   : [User 1: admin] searched for "smith" and got 44 results
2016-04-24 11:41:56.802  INFO 5906 --- [nio-8080-exec-9] o.k.p.attendee.AttendeeSearchPresenter   : [User 1: admin] displayed Attendee 686
```

## Technical details
Models should have a toString() method that returns the id and identifying information (name) to facilitate logging.

The format is:
  `[Type id: Name]`

For example:
  `[User 5: bsmith]`
  `[User 6: jdoe]`


