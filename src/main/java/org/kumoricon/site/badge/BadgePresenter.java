package org.kumoricon.site.badge;

import org.kumoricon.model.badge.Badge;
import org.kumoricon.model.badge.BadgeFactory;
import org.kumoricon.model.badge.BadgeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.List;


@Controller
public class BadgePresenter {
    private final BadgeRepository badgeRepository;

    private static final Logger log = LoggerFactory.getLogger(BadgePresenter.class);

    @Autowired
    public BadgePresenter(BadgeRepository badgeRepository) {
        this.badgeRepository = badgeRepository;
    }

    public void showBadgeList(BadgeListView view) {
        log.info("{} viewed badge list", view.getCurrentUsername());
        List<Badge> badges = badgeRepository.findAll();
        view.afterSuccessfulFetch(badges);
    }

    public void showBadge(BadgeEditView view, Integer badgeId) {
        if (badgeId != null) {
            Badge badge = badgeRepository.findOne(badgeId);
            if (badge == null) {
                log.error("{} tried to view badge id {} but it was not found in the database",
                        view.getCurrentUsername(), badgeId);
                view.notifyError(String.format("Badge %s not found", badgeId));
            } else {
                log.info("{} viewed badge {}", view.getCurrentUsername(), badgeId);
                view.afterSuccessfulFetch(badge);
            }
        } else {
            // Create new badge
            log.info("{} created new badge", view.getCurrentUsername());
            view.afterSuccessfulFetch(BadgeFactory.createEmptyBadge());
        }

    }

    public void saveBadge(BadgeEditView view, Badge badge) {
        try {
            badgeRepository.save(badge);
            log.info("{} saved badge {}", view.getCurrentUsername(), badge);
            view.navigateTo(BadgeListView.VIEW_NAME);
        } catch (Exception ex) {
            log.error("{} got an error saving badge {}: {}", view.getCurrentUsername(), badge, ex);
            view.notifyError(ex.getMessage());
        }
    }

    // Currently not used because badges may be set as not visible, but may not be deleted outright
//    public void deleteBadge(BadgeView view, Badge badge) {
//        if (badge.getId() != null) {
//            log.info("{} deleted badge {}", view.getCurrentUsername(), badge);
//            badgeRepository.delete(badge);
//            view.navigateTo(view.VIEW_NAME);
//            showBadgeList(view);
//            view.notify(badge.getName() + " deleted");
//        }
//        view.closeBadgeEditWindow();
//    }
}
