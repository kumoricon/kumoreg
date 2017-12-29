package org.kumoricon.site.utility.preprint;

import com.vaadin.navigator.View;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Button;
import com.vaadin.v7.ui.*;
import org.kumoricon.model.attendee.Attendee;
import org.kumoricon.model.badge.Badge;
import org.kumoricon.site.BaseView;
import org.kumoricon.site.attendee.AttendeePrintView;
import org.kumoricon.site.attendee.FieldFactory;
import org.kumoricon.site.attendee.window.PrintBadgeWindow;
import org.kumoricon.site.fieldconverter.DateToLocalDateConverter;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@SpringView(name = PreprintBadgeView.VIEW_NAME)
public class PreprintBadgeView extends BaseView implements View, AttendeePrintView {
    public static final String VIEW_NAME = "preprintBadge";
    public static final String REQUIRED_RIGHT = "pre_print_badges";

    private final PreprintBadgePresenter handler;

    private NativeSelect badgeType;
    private TextField xOffset;
    private TextField yOffset;
    private DateField ageAsOfDate;

    @Autowired
    public PreprintBadgeView(PreprintBadgePresenter handler) {this.handler = handler;}

    @PostConstruct
    public void init() {
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

        ageAsOfDate = FieldFactory.createDateField("Calculate age as of", 5);
        ageAsOfDate.setConverter(new DateToLocalDateConverter());
        ageAsOfDate.setDateFormat("MM/dd/yyyy");
        ageAsOfDate.setValue(Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        addComponent(ageAsOfDate);

        Label notes = new Label("Warning: all attendees in the system with the selected badge type will " +
                " be flagged as having a badge to pick up");
        addComponent(notes);

        Button display = new Button("Pre-Print Badges");
        display.setTabIndex(4);
        addComponent(display);
        display.focus();
        display.addClickListener((Button.ClickListener) clickEvent ->
            handler.showAttendeeBadgeWindow(this,
                    (Badge) badgeType.getValue(),
                    getXOffset(),
                    getYOffset(),
                    getDateForAgeCalculation()));


        handler.showCurrentOffsets(this, getCurrentClientIPAddress());
    }

    void setXOffset(Integer offset) {
        xOffset.setValue(offset==null ? "0" : offset.toString());
    }
    void setYOffset(Integer offset) {
        yOffset.setValue(offset==null ? "0" : offset.toString());
    }

    /**
     * Returns horizontal offset value from form, or 0 if an invalid value was entered
     * @return Offset in points
     */
    Integer getXOffset() {
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

    public LocalDate getDateForAgeCalculation() {
        if (ageAsOfDate.getValue() == null) { return LocalDate.now(); }
        Date date = ageAsOfDate.getValue();
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public String getRequiredRight() { return REQUIRED_RIGHT; }

    @Override
    public void showPrintBadgeWindow(List<Attendee> attendeeList) {
        PrintBadgeWindow window = new PrintBadgeWindow(this, handler, attendeeList);
        showWindow(window);
    }
}