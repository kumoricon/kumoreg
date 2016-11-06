package org.kumoricon.model.blacklist;

import org.kumoricon.model.Record;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "blacklist")
public class BlacklistName extends Record {
    @NotNull
    private String firstName;
    @NotNull
    private String lastName;

    public BlacklistName() {}

    public BlacklistName(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String toString() {
        if (id != null) {
            return String.format("[Blacklist %s: %s %s]", id, firstName, lastName);
        } else {
            return String.format("[Blacklist: %s %s]", firstName, lastName);
        }
    }
}
