package org.kumoricon.model.attendee;

import org.kumoricon.model.Record;
import org.kumoricon.model.user.User;

import javax.persistence.*;
import java.util.Date;

/**
 * Represents a timestamped message or event that is associated with an attendee. May be
 * generated (for example, when they check in) or entered manually by a user.
 */
@Entity
@Table(name = "attendeehistory")
public class AttendeeHistory extends Record {
    @Temporal(TemporalType.TIMESTAMP)
    private Date timestamp;
    @ManyToOne
    private User user;
    @ManyToOne
    private Attendee attendee;
    private String message;

    public AttendeeHistory() {}

    public AttendeeHistory(User user, Attendee attendee, String message) {
        this.user = user;
        this.message = message;
        this.attendee = attendee;
        this.timestamp = new Date();
    }

    public Date getTimestamp() {
        if (timestamp == null) return null;
        return new Date(timestamp.getTime());
    }
    public void setTimestamp(Date timestamp) {
        if (timestamp == null) this.timestamp = null;
        else this.timestamp = new Date(timestamp.getTime());
    }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Attendee getAttendee() { return attendee; }
    public void setAttendee(Attendee attendee) { this.attendee = attendee; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String toString() {
        String truncatedMessage = "";
        if (message != null) {
            truncatedMessage = message.length() > 100 ? message.substring(0, 100) + "..." : message;
        }
        if (id != null) {
            return String.format("[History %s: %s %s]", id, timestamp, truncatedMessage);
        } else {
            return String.format("[History: %s %s]", timestamp, truncatedMessage);
        }
    }
}
