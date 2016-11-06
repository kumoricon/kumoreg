package org.kumoricon.model;

import javax.persistence.*;
import java.util.UUID;


/**
 * Base class for database records. Provides a field for id numbers (database row),
 * version (opportunistic locking), and UUID (object identity)
 * See http://www.onjava.com/pub/a/onjava/2006/09/13/dont-let-hibernate-steal-your-identity.html
 * For more on identity
 */
@MappedSuperclass
public abstract class Record {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    protected Integer id;

    /**
     * UUID is used for object identity, but relations are still handled using the "id" property
     * for JOIN speed and readability.
     */
    @Column(name = "uuid", nullable = false, updatable = false, columnDefinition = "char(36)")
    private String uuid = UUID.randomUUID().toString();

    // Todo: enable version field for optimistic locking. Not enabled yet because the rest of
    // the app doesn't handle that yet.
//    @Version
//    @Column(name = "version")
//    protected Integer version = 0;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getUuid() { return uuid; }

//    public Integer getVersion() { return version; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Record)) return false;

        Record record = (Record) o;
        return uuid.equals(record.uuid);
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }

    @Override
    public String toString() {
        return String.format("[%s: %s]", getClass().getSimpleName(), id);
    }
}
