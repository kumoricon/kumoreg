package org.kumoricon.site.computer.window;

import com.vaadin.event.ShortcutAction;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Window;
import com.vaadin.v7.shared.ui.label.ContentMode;
import com.vaadin.v7.ui.*;

public class ViewInstructionsWindow extends Window {

    private Label labelInstructionText = new Label();
    private Button btnClose = new Button("Close");

    public ViewInstructionsWindow(String instructions) {
        super(" Instructions");

        setIcon(FontAwesome.BOOK);
        setModal(true);
        setClosable(true);
        center();
        setWidth("70%");

        FormLayout verticalLayout = new FormLayout();
        verticalLayout.setMargin(true);
        verticalLayout.setSpacing(true);
        verticalLayout.addComponent(labelInstructionText);

        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setSpacing(true);
        horizontalLayout.addComponent(btnClose);

        btnClose.addClickListener((Button.ClickListener) clickEvent -> close());

        verticalLayout.addComponent(horizontalLayout);
        setContent(verticalLayout);

        labelInstructionText.setContentMode(ContentMode.HTML);
        labelInstructionText.setSizeFull();
        labelInstructionText.setValue(instructions);

        btnClose.setClickShortcut(ShortcutAction.KeyCode.ENTER);
        btnClose.addClickListener((Button.ClickListener) clickEvent -> {
            this.close();
        });
    }
}