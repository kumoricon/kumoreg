package org.kumoricon.site.computer.window;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.ShortcutAction;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.*;
import org.kumoricon.model.printer.Printer;
import java.util.ArrayList;
import java.util.List;

public class AddPrinterWindow extends Window {

    public static final String REQUIRED_RIGHT = "manage_devices";
    private TextField txtHostname = new TextField("Hostname: ");
    private ComboBox comboboxModel;
    private Button btnCancel = new Button("Cancel");
    private Button btnInstall = new Button("Install");
    private Label labelBlankLine = new Label(" ");
    private Boolean cancelPressed = false;
    private Printer printer = new Printer();

    public PrinterWindowCallback installSuccessHandler;
    public PrinterWindowCallback installFailureHandler;

    public Boolean getCancelPressed() {
        return this.cancelPressed;
    }
    public void setCancelPressed(Boolean value) {
        this.cancelPressed = value;
    }

    public String getTxtHostname() {
        return txtHostname.getValue();
    }

    public Printer getInstalledPrinter() {
        return this.printer;
    }

    public AddPrinterWindow() {
        super(" Add Printer");

        VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);

        //Create a combo box with an item for each printer model
        List<Printer> modelList = new ArrayList<Printer>();
        /* TODO pull model information from a database table */
        /* TEMPORARY */ modelList.add(new Printer("", "8610"));
        /* TEMPORARY */ modelList.add(new Printer("", "251"));
        /* TEMPORARY */ modelList.add(new Printer("", "0000"));
        BeanItemContainer<Printer> objects = new BeanItemContainer(Printer.class, modelList);
        this.comboboxModel = new ComboBox("Model", objects);
        this.comboboxModel.setTextInputAllowed(false);
        this.comboboxModel.setItemCaptionPropertyId("model");
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

        btnCancel.addClickListener((Button.ClickListener) clickEvent -> {
            this.setCancelPressed(true);
            this.close();
        });

        btnInstall.addClickListener((Button.ClickListener) clickEvent -> {
            String model = ((Printer) comboboxModel.getValue()).getModel();
            this.printer.setName(this.txtHostname.getValue());
            this.printer.setModel(model);

            // Validate input
            if (this.printer.getModel() == "") {
                /* TODO Improvwe validation */
                /* TODO disaply error message */
                return;
            }
            else if (this.printer.getName() == "") {
                /* TODO Improvwe validation */
                /* TODO disaply error message */
                return;
            }

            // Proceeed with installation
            String status = printer.install();
            if (status.startsWith("Error") == false) {
                if (installSuccessHandler != null) {
                    installSuccessHandler.run();
                }
            }
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
    }

}
