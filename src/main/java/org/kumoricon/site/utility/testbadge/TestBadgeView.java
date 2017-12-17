package org.kumoricon.site.utility.testbadge;

import com.vaadin.navigator.View;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.TextField;
import org.kumoricon.model.attendee.Attendee;
import org.kumoricon.model.badge.Badge;
import org.kumoricon.site.BaseView;
import org.kumoricon.site.attendee.AttendeePrintView;
import org.kumoricon.site.attendee.FieldFactory;
import org.kumoricon.site.attendee.window.PrintBadgeWindow;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.List;

@SpringView(name = TestBadgeView.VIEW_NAME)
public class TestBadgeView extends BaseView implements View, AttendeePrintView {
    public static final String VIEW_NAME = "testBadge";
    public static final String REQUIRED_RIGHT = null;

    private final TestBadgePresenter handler;

    private NativeSelect badgeType;
    private TextField xOffset;
    private TextField yOffset;

    @Autowired
    public TestBadgeView(TestBadgePresenter handler) {this.handler = handler;}

    @PostConstruct
    public void init() {
        NativeSelect numberOfBadges = FieldFactory.createNativeSelect("Badges to generate:", 1);
        numberOfBadges.addItems(1, 2, 3);
        numberOfBadges.setValue(1);
        numberOfBadges.setNullSelectionAllowed(false);
        addComponent(numberOfBadges);

        badgeType = FieldFactory.createNativeSelect("Badge Type", 2);
        badgeType.setMultiSelect(false);
        badgeType.setNullSelectionAllowed(false);
        List<Badge> availableBadges = handler.getBadges();
        badgeType.addItems(availableBadges);
        badgeType.setValue(availableBadges.get(0));
        addComponent(badgeType);

        xOffset = FieldFactory.createNegativeNumberField("Horizontal Offset (points)", 3);
        yOffset = FieldFactory.createNegativeNumberField("Vertical Offset (points)", 4);
        xOffset.setNullSettingAllowed(false);
        yOffset.setNullSettingAllowed(false);
        xOffset.setDescription("Points (1/72 inch). Negative values move left, positive values move right");
        yOffset.setDescription("Points (1/72 inch). Negative values move down, positive values move up");
        xOffset.setValue("0");
        yOffset.setValue("0");
        addComponent(xOffset);
        addComponent(yOffset);

        xOffset.setVisible(currentUserHasRight("manage_devices"));
        yOffset.setVisible(currentUserHasRight("manage_devices"));

        Button display = new Button("Print Test Badges");
        display.setTabIndex(4);
        addComponent(display);
        display.focus();
        display.addClickListener((Button.ClickListener) clickEvent ->
                handler.showAttendeeBadgeWindow(this, (Integer) numberOfBadges.getValue(), (Badge) badgeType.getValue(), getXOffset(), getYOffset()));

        Label notes = new Label("Note: Changed offsets will not be saved. Set them in Administration > Computers.");
        addComponent(notes);
        notes.setVisible(currentUserHasRight("manage_devices"));

        Button printAllBadges = new Button("Print all badge types");
        printAllBadges.addClickListener(clickEvent -> handler.showAttendeeBadgeWindow(this, getXOffset(), getYOffset()));
        addComponent(printAllBadges);
        handler.showCurrentOffsets(this, getCurrentClientIPAddress());
    }

    public void setXOffset(Integer offset) {
        xOffset.setValue(offset==null ? "0" : offset.toString());
    }
    public void setYOffset(Integer offset) {
        yOffset.setValue(offset==null ? "0" : offset.toString());
    }

    /**
     * Returns horizontal offset value from form, or 0 if an invalid value was entered
     * @return Offset in points
     */
    public Integer getXOffset() {
        try {
            return Integer.valueOf(xOffset.getValue());
        } catch (NumberFormatException e) {
            setXOffset(0);
            return 0;
        }
    }

    /**
     * Returns vertical offset value from form, or 0 if an invalid value was entered
     * @return Offset in points
     */
    public Integer getYOffset() {
        try {
            return Integer.parseInt(yOffset.getValue());
        } catch (NumberFormatException e) {
            setYOffset(0);
            return 0;
        }
    }

    public String getRequiredRight() { return REQUIRED_RIGHT; }

    @Override
    public void showPrintBadgeWindow(List<Attendee> attendeeList) {
        PrintBadgeWindow window = new PrintBadgeWindow(this, handler, attendeeList);
        showWindow(window);
    }
}