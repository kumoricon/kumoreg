package org.kumoricon.model.role;

import org.kumoricon.model.Record;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "roles")
public class Role extends Record {
    @NotNull
    @Column(unique=true)
    private String name;
    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Right> rights;

    public Role() {
        this.rights = new HashSet<>();
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

    /**
     * Removes the right with the given name. Name is not case sensitive
     * @param name Name of right
     */
    public void removeRight(String name) {
        if (this.name == null) return;
        name = name.toLowerCase();
        Right target = null;
        for (Right r : rights) {
            if (r.getName().equals(name)) {
                target = r;
            }
        }
        if (target != null) {
            rights.remove(target);
        }
    }

    public Set<Right> getRights() { return new HashSet<>(rights); }

    /**
     * Returns true if this role has the given right, or if this role has the "super_admin" right.
     * @param name Name of Right
     * @return boolean
     */
    public boolean hasRight(String name) {
        if (name == null) { return false; }
        name = name.toLowerCase();
        for (Right r : rights) {    // Todo: better way to do this?
            if (r.getName().equals(name) || r.getName().equals("super_admin")) { return true; }
        }
        return false;
    }

    public String toString() {
        if (id != null) {
            return String.format("[Role %s: %s]", id, name);
        } else {
            return String.format("[Role: %s]", name);
        }
    }

    public void clearRights() {
        this.rights.clear();
    }
}
