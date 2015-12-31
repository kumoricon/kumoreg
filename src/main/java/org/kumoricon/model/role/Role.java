package org.kumoricon.model.role;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Role {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Integer id;
    @NotNull
    @Column(unique=true)
    private String name;
    @ManyToMany
    private Set<Right> rights;

    private static String[] availableRights = {"Pre Reg Check In", "At Con Check In", "Attendee Search",
            "Attendee Modify", "Set Manual Price", "Manage Pass Types", "Edit Notes", "Manage Staff", "Override",
            "Reprint Badge"};

    public Role() {
        this.rights = new HashSet<Right>();
    }


    public Role(String name) {
        this();
        this.name = name;
    }

    public Integer getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public void addRight(Right right) { rights.add(right); }
    public void addRights(Set<Right> rights) { this.rights.addAll(rights); }
    public void removeRight(String right) { rights.remove(right); }
    public Set<Right> getRights() { return new HashSet<Right>(rights); }
    public boolean hasRight(String name) { return rights.contains(name); }

    public String toString() {
        return name;
    }

    public String[] getAvailableRights() {
        return availableRights;
    }

    public void clearRights() {
        this.rights.clear();
    }
}
