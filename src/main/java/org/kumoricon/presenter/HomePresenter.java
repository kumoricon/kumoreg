package org.kumoricon.presenter;

import org.kumoricon.model.badge.Badge;
import org.kumoricon.model.badge.BadgeRepository;
import org.kumoricon.view.HomeView;
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

    public void showBadges(HomeView view) {
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
