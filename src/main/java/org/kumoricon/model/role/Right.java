package org.kumoricon.model.role;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity
@Table(name = "rights")
public class Right implements Serializable, Comparable {
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
        if (!(other instanceof Right))
            return false;
        if (this.getId() == null) {
            return this == other;
        } else {
            Right o = (Right) other;
            return this.getId().equals(o.getId());
        }
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
