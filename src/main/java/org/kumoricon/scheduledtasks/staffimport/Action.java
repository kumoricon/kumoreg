package org.kumoricon.scheduledtasks.staffimport;

public class Action {
    public int actionsVersion;
    public String[] deleted;

    public Action() {}

    public String toString() {
        return String.format("[Action: version %s deletes %s]", actionsVersion, String.join(", ", deleted));
    }
}


