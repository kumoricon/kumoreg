package org.kumoricon.site;

import org.kumoricon.model.badge.Badge;
import org.kumoricon.model.badge.BadgeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.List;

@Controller
@Scope("request")
public class HomePresenter {

    @Autowired
    private BadgeRepository badgeRepository;

    private static final Logger log = LoggerFactory.getLogger(HomePresenter.class);

    public void showBadges(HomeView view) {
        log.info("{} viewed home screen", view.getCurrentUsername());
        List<Badge> badges = badgeRepository.findByVisibleTrue();
        List<Badge> badgesUserCanView = new ArrayList<>();
        for (Badge badge : badges) {
            if (badge.getRequiredRight() == null || view.currentUserHasRight(badge.getRequiredRight())) {
                badgesUserCanView.add(badge);
            }
        }

        if (badgesUserCanView.size() > 0) {
            view.showBadges(badgesUserCanView);
        }
    }
}
