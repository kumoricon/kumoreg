package org.kumoricon.model.role;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity
@Table(name = "rights")
public class Right implements Serializable {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Integer id;
    @NotNull
    @Column(unique=true)
    private String name;

    public String toString() {
        return String.format("%s", name);
    }

    public Right() {}

    public Right(String name) {
        this();
        this.name = name;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Right))
            return false;
        if (this.getId() == null) {
            return this == other;
        } else {
            Right o = (Right) other;
            return this.getId() == o.getId();
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
}
