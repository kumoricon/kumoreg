package org.kumoricon.model.role;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "rights")
public class Right implements Comparable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @NotNull
    @Column(unique=true)
    private String name;
    private String description;

    public String toString() {
        return name;
    }

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

        if ( !right.getName().equals( getName() ) ) return false;
        return true;
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
    public int compareTo(Object o) {
        if (o != null && o instanceof Right) {
            Right other = (Right)o;
            return name.compareTo(other.getName());
        }
        return 0;
    }
}
