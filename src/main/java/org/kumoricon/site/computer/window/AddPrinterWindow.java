package org.kumoricon.site.computer.window;

import com.vaadin.ui.*;
import com.vaadin.event.ShortcutAction;
import com.vaadin.server.FontAwesome;
import org.kumoricon.model.printer.Printer;
import java.util.Arrays;
import java.util.List;

public class AddPrinterWindow extends Window {

    public static final String REQUIRED_RIGHT = "manage_devices";
    private TextField txtHostname = new TextField("Hostname: ");
    private ComboBox<String> comboboxModel;
    private Button btnCancel = new Button("Cancel");
    private Button btnInstall = new Button("Install");
    private Label labelBlankLine = new Label(" ");
    private Printer printer = new Printer();

    public PrinterWindowCallback installSuccessHandler;
    public PrinterWindowCallback installFailureHandler;

    public Printer getInstalledPrinter() {
        return this.printer;
    }

    public AddPrinterWindow() {
        super(" Add Printer");

        VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);

        //Create a combo box with an item for each printer model
        /* TODO pull model information from a database table */
        List<String> modelList = Arrays.asList("8610", "251", "0000");
        this.comboboxModel = new ComboBox<>("Model");
        comboboxModel.setItems(modelList);
        layout.addComponent(txtHostname);
        layout.addComponent(comboboxModel);
        setIcon(FontAwesome.PRINT);
        setModal(true);
        setClosable(true);
        center();
        setWidth(400, Unit.PIXELS);

        HorizontalLayout buttons = new HorizontalLayout();
        buttons.setSpacing(true);
        buttons.addComponent(btnInstall);
        buttons.addComponent(btnCancel);

        layout.addComponent(labelBlankLine);
        layout.addComponent(buttons);
        setContent(layout);

        btnCancel.addClickListener((Button.ClickListener) clickEvent -> this.close());

        btnInstall.addClickListener((Button.ClickListener) clickEvent -> {
            String model = (comboboxModel.getValue());
            this.printer.setName(this.txtHostname.getValue());
            this.printer.setModel(model);

            // Validate input
            if (this.printer.getModel().equals("")) {
                /* TODO Improve validation */
                /* TODO display error message */
                return;
            }
            else if (this.printer.getName().equals("")) {
                /* TODO Improve validation */
                /* TODO display error message */
                return;
            }

            // Proceed with installation
            String status = printer.install();

            // Install succeeded so run the success handler if one has been set
            if (!status.startsWith("Error")) {
                if (installSuccessHandler != null) {
                    installSuccessHandler.run();
                }
            }

            // Install failed so run the failure handler if one has been set
            else {
                if (installFailureHandler != null) {
                    installFailureHandler.run();
                }
            }

            this.close();
        });
        btnInstall.setClickShortcut(ShortcutAction.KeyCode.ENTER);
        txtHostname.focus();
    }

    public void clearInputFields() {
        this.txtHostname.setValue("");
        this.comboboxModel.setValue(null);
        this.printer = new Printer();
        txtHostname.focus();
    }

}
