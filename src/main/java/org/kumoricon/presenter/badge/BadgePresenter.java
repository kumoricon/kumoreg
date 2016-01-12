package org.kumoricon.presenter.badge;

import com.vaadin.navigator.Navigator;
import org.kumoricon.KumoRegUI;
import org.kumoricon.model.badge.Badge;
import org.kumoricon.model.badge.BadgeFactory;
import org.kumoricon.model.badge.BadgeRepository;
import org.kumoricon.view.badge.BadgeView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.util.List;


@Controller
@Scope("request")
public class BadgePresenter {
    @Autowired
    private BadgeRepository badgeRepository;

    private BadgeView view;

    public BadgePresenter() {
    }

    public void badgeSelected(Badge badge) {
        if (badge != null) {
            Navigator navigator = KumoRegUI.getCurrent().getNavigator();
            navigator.navigateTo(BadgeView.VIEW_NAME + "/" + badge.getId().toString());
            view.showBadge(badge);
        }
    }

    public void badgeSelected(Integer id) {
        if (id != null) {
            org.kumoricon.model.badge.Badge badge = badgeRepository.findOne(id);
            badgeSelected(badge);
        }
    }

    public void addNewBadge() {
        view.clearBadgeForm();
        view.showBadgeForm();
        KumoRegUI.getCurrent().getNavigator().navigateTo(BadgeView.VIEW_NAME);
        Badge newBadge = BadgeFactory.emptyBadgeFactory();
        view.showBadge(newBadge);
    }

    public void cancelBadge() {
        KumoRegUI.getCurrent().getNavigator().navigateTo(BadgeView.VIEW_NAME);
        view.clearBadgeForm();
        view.hideBadgeForm();
        view.clearSelection();
    }

    public void saveBadge() {
        Badge badge = view.getBadge();

        badgeRepository.save(badge);
        KumoRegUI.getCurrent().getNavigator().navigateTo(BadgeView.VIEW_NAME);
        showBadgeList();
    }

    public void showBadgeList() {
        List<Badge> badges = badgeRepository.findAll();
        view.afterSuccessfulFetch(badges);
    }

    public void navigateToRole(String parameters) {
        if (parameters != null) {
            Integer id = Integer.parseInt(parameters);
            Badge badge = badgeRepository.findOne(id);
            view.selectBadge(badge);
        }
    }


    public BadgeView getView() { return view; }
    public void setView(BadgeView roleView) { this.view = roleView; }

}
