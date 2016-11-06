package org.kumoricon.model.role;

import org.kumoricon.model.Record;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "rights")
public class Right extends Record implements Comparable {
    @NotNull
    @Column(unique=true)
    private String name;
    private String description;

    public Right() {}

    public Right(String name) {
        this();
        this.name = name;
    }

    public Right(String name, String description) {
        this(name);
        this.description = description;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if ( !(other instanceof Right) ) return false;

        final Right right = (Right) other;

        return right.getName().equals(getName());
    }

    @Override
    public int hashCode() {
        return getName().hashCode();
    }

    public Integer getId() { return id; }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() { return name; }
    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    @Override
    public String toString() {
        if (id != null) {
            return String.format("[Right %s: %s]", id, name);
        } else {
            return String.format("[Right: %s]", name);
        }
    }

    @Override
    public int compareTo(Object o) {
        if (o != null && o instanceof Right) {
            Right other = (Right)o;
            return name.compareTo(other.getName());
        }
        return 0;
    }
}
