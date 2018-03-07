package org.kumoricon.site.utility.testbadge;

import com.vaadin.navigator.View;
import com.vaadin.server.StreamResource;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.*;
import org.kumoricon.BaseGridView;
import org.kumoricon.model.badge.Badge;
import org.kumoricon.service.print.formatter.BadgePrintFormatter;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.List;

@SpringView(name = TestBadgeView.VIEW_NAME)
public class TestBadgeView extends BaseGridView implements View {
    public static final String VIEW_NAME = "testBadge";
    public static final String REQUIRED_RIGHT = null;

    private final TestBadgePresenter handler;

    private NativeSelect<Badge> badgeType;
    private TextField xOffset;
    private TextField yOffset;
    private BrowserFrame pdf;

    @Autowired
    public TestBadgeView(TestBadgePresenter handler) {this.handler = handler;}

    @PostConstruct
    public void init() {
        setColumns(2);
        setRows(7);
        setColumnExpandRatio(0, 10);

        pdf = new BrowserFrame();
        pdf.setWidth("500px");
        pdf.setHeight("500px");
        addComponent(pdf, 0, 0, 0, 5);


        NativeSelect<Integer> numberOfBadges = new NativeSelect<>("Number of Badges");
        numberOfBadges.setItems(1, 2, 3);
        numberOfBadges.setValue(1);
        numberOfBadges.setEmptySelectionAllowed(false);
        addComponent(numberOfBadges, 1, 0);

        badgeType = new NativeSelect<>("Badge Type");
        badgeType.setEmptySelectionAllowed(false);
        List<Badge> availableBadges = handler.getBadges();
        badgeType.setItems(availableBadges);
        badgeType.setValue(availableBadges.get(0));
        addComponent(badgeType, 1, 1);

        xOffset = new TextField("Horizontal Offset (points)");
        yOffset = new TextField("Vertical Offset (points)");
        xOffset.setDescription("Points (1/72 inch). Negative values move left, positive values move right");
        yOffset.setDescription("Points (1/72 inch). Negative values move down, positive values move up");
        xOffset.setValue("0");
        yOffset.setValue("0");
        addComponent(xOffset, 1, 2);
        addComponent(yOffset, 1, 3);

        xOffset.setVisible(currentUserHasRight("manage_devices"));
        yOffset.setVisible(currentUserHasRight("manage_devices"));

        Button display = new Button("Print Test Badges");
        addComponent(display,1 ,4);
        display.focus();
        display.addClickListener((Button.ClickListener) clickEvent ->
                handler.printBadges(this, numberOfBadges.getValue(), badgeType.getValue(), getXOffset(), getYOffset()));

        Button printAllBadges = new Button("Print all badge types");
        printAllBadges.addClickListener(clickEvent -> handler.printBadges(this, getXOffset(), getYOffset()));
        addComponent(printAllBadges, 1, 5);

        Label notes = new Label("Note: Changed offsets will not be saved. Set them in Administration > Computers.");
        addComponent(notes, 1, 6);
        notes.setVisible(currentUserHasRight("manage_devices"));

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

    public void showPDF(BadgePrintFormatter source) {
        String filename = "testbadge" + System.currentTimeMillis() + ".pdf";
        StreamResource resource = new StreamResource(source, filename);
        removeComponent(pdf);
        pdf = new BrowserFrame("", resource);
        pdf.setWidth("500px");
        pdf.setHeight("500px");
        addComponent(pdf, 0, 0, 0, 5);
        resource.setMIMEType("application/pdf");
        resource.getStream().setParameter("Content-Disposition", "attachment; filename=" + filename);

    }
}