Staff Import
============

The server will read .json files from a configured directory
and update the attendee database. This is used to import updates
to staff information.

By default, the directories are:

/tmp/kumoreg/inbox (prod)
/tmp/training/inbox (training mode)

Files that are processed successfully will be moved to:

/tmp/kumoreg/finished (prod)
/tmp/training/finished (training mode)

And files that are not loaded successfully will be moved to:

/tmp/kumoreg/deadletterqueue (prod)
/tmp/training/deadletterqueue (training mode)

