package org.kumoricon.site.utility.preprint;

import com.vaadin.navigator.View;
import com.vaadin.server.StreamResource;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.*;
import org.kumoricon.BaseGridView;
import org.kumoricon.model.badge.Badge;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@SpringView(name = PreprintBadgeView.VIEW_NAME)
public class PreprintBadgeView extends BaseGridView implements View {
    public static final String VIEW_NAME = "preprintBadge";
    public static final String REQUIRED_RIGHT = "pre_print_badges";

    private final PreprintBadgePresenter handler;

    private NativeSelect<Badge> badgeType;
    private TextField xOffset;
    private TextField yOffset;
    private DateField ageAsOfDate;
    private BrowserFrame pdf;

    @Autowired
    public PreprintBadgeView(PreprintBadgePresenter handler) {this.handler = handler;}

    @PostConstruct
    public void init() {
        setColumns(3);
        setRows(6);
        setColumnExpandRatio(0, 5);
        setColumnExpandRatio(1, 5);
        pdf = new BrowserFrame();
        pdf.setWidth("500px");
        pdf.setHeight("500px");
        addComponent(pdf, 0, 0, 1, 5);

        badgeType = new NativeSelect<>("Badge Type");
        badgeType.setEmptySelectionAllowed(false);
        List<Badge> availableBadges = handler.getBadges();
        badgeType.setItems(availableBadges);
        badgeType.setValue(availableBadges.get(0));
        addComponent(badgeType, 2, 1);

        xOffset = new TextField("Horizontal Offset (points)");
        yOffset = new TextField("Vertical Offset (points)");

        xOffset.setDescription("Points (1/72 inch). Negative values move left, positive values move right");
        yOffset.setDescription("Points (1/72 inch). Negative values move down, positive values move up");
        xOffset.setValue("0");
        yOffset.setValue("0");
        addComponent(xOffset, 2, 2);
        addComponent(yOffset, 2, 3);

        xOffset.setVisible(currentUserHasRight("manage_devices"));
        yOffset.setVisible(currentUserHasRight("manage_devices"));

        ageAsOfDate = new DateField("Calculate age as of");
        ageAsOfDate.setDateFormat("MM/dd/yyyy");
        ageAsOfDate.setValue(LocalDate.now(ZoneId.of("America/Los_Angeles")));
        addComponent(ageAsOfDate, 2, 4);

        Label notes = new Label("Warning: all attendees in the system with the selected badge type will " +
                " be flagged as having a badge to pick up");
        addComponent(notes, 2, 0);

        Button display = new Button("Pre-Print Badges");
        addComponent(display, 2, 5);
        display.focus();
        display.addClickListener((Button.ClickListener) clickEvent ->
                handler.printBadges(this, badgeType.getValue(), getXOffset(), getYOffset(), getDateForAgeCalculation()));

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
        if (ageAsOfDate.getValue() == null) { return LocalDate.now(ZoneId.of("America/Los_Angeles")); }
        return ageAsOfDate.getValue();
    }

    public String getRequiredRight() { return REQUIRED_RIGHT; }

    public void showPDF(StreamResource.StreamSource source) {
        String filename = "badge" + System.currentTimeMillis() + ".pdf";
        StreamResource resource = new StreamResource(source, filename);
        removeComponent(pdf);
        pdf = new BrowserFrame("", resource);
        pdf.setWidth("500px");
        pdf.setHeight("500px");
        addComponent(pdf, 0, 0, 1, 5);
        resource.setMIMEType("application/pdf");
        resource.getStream().setParameter("Content-Disposition", "attachment; filename=" + filename);
    }

}