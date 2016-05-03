package org.kumoricon.site.report;

import org.kumoricon.model.user.User;

public interface ReportView {
    void afterSuccessfulFetch(String data);
    User getCurrentUser();
}
