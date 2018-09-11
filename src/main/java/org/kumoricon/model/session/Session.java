package org.kumoricon.model.session;

import org.kumoricon.model.Record;
import org.kumoricon.model.user.User;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.time.Instant;

@Entity
@Table(name = "sessions")
public class Session extends Record {
    private Instant start;
    private Instant end;
    @NotNull
    @ManyToOne
    private User user;
    private boolean open;

    public Session() {}

    /**
     * Generate an open session for the given user starting now
     * @param user User
     */
    public Session(User user) {
        setUser(user);
        setStart(Instant.now());
        setOpen(true);
    }

    public Instant getStart() { return start; }
    public void setStart(Instant start) {
        if (this.start == null) {
            this.start = start;
        }
    }

    public Instant getEnd() { return end; }
    public void setEnd(Instant end) {
        if (this.end == null) {
            this.end = end;
        }
    }

    public User getUser() { return user; }
    public void setUser(User user) {
        if (this.user == null) {
            this.user = user;
        }
    }

    public boolean isOpen() { return open; }
    public void setOpen(boolean open) { this.open = open; }

    @Override
    public String toString() {
        return String.format("[Session: %s]", id);
    }
}
