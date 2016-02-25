package org.kumoricon.presenter;

import org.kumoricon.model.badge.Badge;
import org.kumoricon.model.badge.BadgeRepository;
import org.kumoricon.view.HomeView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@Scope("request")
public class HomePresenter {

    @Autowired
    private BadgeRepository badgeRepository;


    private HomeView view;

    public void showBadges() {
        List<Badge> badges = badgeRepository.findByVisibleTrue();
        if (badges.size() > 0) {
            view.showBadges(badges);
        }
    }

    public HomeView getView() { return view; }

    public void setView(HomeView view) { this.view = view; }
}
