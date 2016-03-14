package org.kumoricon.presenter.badge;

import org.kumoricon.model.badge.Badge;
import org.kumoricon.model.badge.BadgeFactory;
import org.kumoricon.model.badge.BadgeRepository;
import org.kumoricon.view.badge.BadgeView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.List;


@Controller
public class BadgePresenter {
    @Autowired
    private BadgeRepository badgeRepository;

    public BadgePresenter() {
    }

    public void badgeSelected(BadgeView view, Badge badge) {
        if (badge != null) {
            view.navigateTo(BadgeView.VIEW_NAME + "/" + badge.getId().toString());
            view.showBadge(badge);
        }
    }

    public void badgeSelected(BadgeView view, Integer id) {
        if (id != null) {
            org.kumoricon.model.badge.Badge badge = badgeRepository.findOne(id);
            badgeSelected(view, badge);
        }
    }

    public void addNewBadge(BadgeView view) {
        view.clearBadgeForm();
        view.showBadgeForm();
        view.navigateTo(BadgeView.VIEW_NAME);
        Badge newBadge = BadgeFactory.emptyBadgeFactory();
        view.showBadge(newBadge);
    }

    public void cancelBadge(BadgeView view) {
        view.navigateTo(BadgeView.VIEW_NAME);
        view.clearBadgeForm();
        view.hideBadgeForm();
        view.clearSelection();
    }

    public void saveBadge(BadgeView view) {
        Badge badge = view.getBadge();

        badgeRepository.save(badge);
        view.navigateTo(BadgeView.VIEW_NAME);
        showBadgeList(view);
    }

    public void showBadgeList(BadgeView view) {
        List<Badge> badges = badgeRepository.findAll();
        view.afterSuccessfulFetch(badges);
    }

    public void navigateToRole(BadgeView view, String parameters) {
        if (parameters != null) {
            Integer id = Integer.parseInt(parameters);
            Badge badge = badgeRepository.findOne(id);
            view.selectBadge(badge);
        }
    }
}
