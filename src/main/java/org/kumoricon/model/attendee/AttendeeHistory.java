package org.kumoricon.model.attendee;

import org.kumoricon.model.user.User;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "attendeehistory")
public class AttendeeHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
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

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Date getTimestamp() { return timestamp; }
    public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Attendee getAttendee() { return attendee; }
    public void setAttendee(Attendee attendee) { this.attendee = attendee; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String toString() {
        if (message != null && message.length() > 100) {
            return String.format("[History %s: %s %s...]", id, timestamp, message.substring(0, 100));
        } else {
            return String.format("[History %s: %s %s]", id, timestamp, message);
        }
    }

}
