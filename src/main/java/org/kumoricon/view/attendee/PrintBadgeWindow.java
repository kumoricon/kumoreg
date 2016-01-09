package org.kumoricon.view.attendee;

import com.vaadin.server.FontAwesome;
import com.vaadin.ui.*;
import org.kumoricon.presenter.attendee.PreRegCheckInPresenter;

public class PrintBadgeWindow extends Window {
    Label question = new Label("Did the badge print successfully?");
    Button printedSuccessfully = new Button("Yes");
    Button reprint = new Button("Reprint");
    private PreRegCheckInPresenter handler;

    public PrintBadgeWindow(PreRegCheckInPresenter preRegCheckInPresenter) {
        super("Print Badge");
        this.handler = preRegCheckInPresenter;
        setIcon(FontAwesome.PRINT);
        center();

        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setMargin(true);
        verticalLayout.setSpacing(true);
        verticalLayout.addComponent(question);

        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setSpacing(true);
        horizontalLayout.addComponent(printedSuccessfully);
        printedSuccessfully.focus();
        horizontalLayout.addComponent(reprint);

        printedSuccessfully.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                handler.badgePrintSuccess();
            }
        });
        reprint.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                handler.badgePrintFailed();
            }
        });
        verticalLayout.addComponent(horizontalLayout);
        setContent(verticalLayout);
    }


    public PreRegCheckInPresenter getHandler() { return handler; }
    public void setHandler(PreRegCheckInPresenter handler) { this.handler = handler; }
}
