package org.kumoricon.site.computer.window;

import com.vaadin.event.ShortcutAction;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.*;

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
        verticalLayout.addComponent(btnClose);
        verticalLayout.setComponentAlignment(btnClose, Alignment.MIDDLE_CENTER);

        btnClose.addClickListener((Button.ClickListener) clickEvent -> close());

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