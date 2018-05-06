package org.kumoricon.site.computer;

import com.vaadin.data.Binder;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.*;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.ViewScope;
import org.kumoricon.BaseGridView;
import org.kumoricon.model.computer.Computer;
import org.kumoricon.model.printer.Printer;
import org.springframework.beans.factory.annotation.Autowired;
import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@ViewScope
@SpringView(name = ComputerView.VIEW_NAME)
public class ComputerView extends BaseGridView implements View {
    public static final String VIEW_NAME = "computers";
    public static final String REQUIRED_RIGHT = "manage_devices";

    @Autowired
    private ComputerPresenter handler;
    private static final Logger log = LoggerFactory.getLogger(ComputerView.class);

    private Grid<Computer> gridComputers = new Grid<>();
    private Grid<Printer> gridPrinters = new Grid<>();
    private Panel panelLeft = new Panel();
    private Panel panelRight = new Panel();
    private Label lblLeftTitle;
    private Label lblLeftSubtitle;
    private Label lblRightTitle;
    private Label lblRightSubtitle;
    private Button btnAddComputer = new Button("Add");
    private Button btnDeleteComputer = new Button("Delete");
    private Button btnAddPrinter = new Button("Install...");
    private Button btnDeletePrinter = new Button("Uninstall");
    private Button btnViewInstructions = new Button("Instructions...");
    private Button btnRefreshPrinterGrid = new Button("Refresh");
    private Button btnEditPrinterModels = new Button("Models...");

    @PostConstruct
    public void init() {
        setColumns(7);
        setRows(6);


        // Left column (computers)
        lblLeftTitle = new Label ("<span style='font-size:24px'>Computers</span>");
        lblLeftTitle.setContentMode(ContentMode.HTML);
        lblLeftSubtitle = new Label("<span style='font-size:12px'>This computer's IP address is: </span>" + getCurrentClientIPAddress(), ContentMode.HTML);
        addComponent(lblLeftTitle, 0, 0, 2, 0);
        addComponent(lblLeftSubtitle, 0, 1, 2, 1);

        addComponent(gridComputers,0,2, 2, 2);
        gridComputers.setSelectionMode(Grid.SelectionMode.SINGLE);
        gridComputers.setColumnReorderingAllowed(true);

        Binder<Computer> binder = gridComputers.getEditor().getBinder();

        TextField ipAddressField = new TextField();
        TextField xOffsetField = new TextField();
        TextField yOffsetField = new TextField();
        TextField printerNameField = new TextField();

        Binder.Binding<Computer, String> nameBinding = binder.bind(ipAddressField, Computer::getIpAddress, Computer::setIpAddress);
        Grid.Column<Computer, String> column = gridComputers.addColumn(Computer::getIpAddress);
        column.setEditorBinding(nameBinding);

        Binder.Binding<Computer, String> printerNameBinding = binder.bind(printerNameField, Computer::getPrinterName, Computer::setPrinterName);
        Grid.Column<Computer, String> printerNameColumn = gridComputers.addColumn(Computer::getPrinterName);
        printerNameColumn.setEditorBinding(printerNameBinding);
        gridComputers.getEditor().setEnabled(true);

        gridComputers.getEditor().addSaveListener(editorSaveEvent -> handler.saveComputer(this, editorSaveEvent.getBean()));

//        gridComputers.addColumn(Computer::getIpAddress).setCaption("IP Address").setEditorComponent(new TextField(), Computer::setIpAddress);
//        gridComputers.addColumn(Computer::getxOffset).setCaption("X Offset").setEditorComponent(new TextField(), Computer::setxOffset);
//        gridComputers.addColumn(Computer::getyOffset).setCaption("Y Offset").setEditorComponent(new TextField(), Computer::setyOffset);
//        gridComputers.addColumn(Computer::getPrinterName).setCaption("Printer").setEditorComponent(new TextField(), Computer::setPrinterName);


        addComponent(btnAddComputer, 0, 4);
        addComponent(btnDeleteComputer, 1, 4);
        addComponent(btnViewInstructions, 2, 4);

        btnViewInstructions.addClickListener((Button.ClickListener) clickEvent -> {
            handler.showInstructions(this);
        });

        btnAddComputer.addClickListener((Button.ClickListener) clickEvent -> {
            handler.addNewComputer(this);
        });

        btnDeleteComputer.addClickListener((Button.ClickListener) clickEvent -> {
            Set<Computer> selected = gridComputers.getSelectedItems();

            for (Computer c : selected) {
                handler.deleteComputer(this, c);
            }
        });



        // Right column (printers)

        lblRightTitle = new Label("<span style='font-size:24px'>Printers</span>",ContentMode.HTML);
        lblRightSubtitle = new Label("<span style='font-size:12px'>Only printers listed below can be used to print badges</span>",ContentMode.HTML);

        addComponent(lblRightTitle, 4, 0, 6, 0);
        addComponent(lblRightSubtitle, 4, 1, 6, 1);

        gridPrinters.setSelectionMode(Grid.SelectionMode.SINGLE);
        gridPrinters.setColumnReorderingAllowed(true);
        gridPrinters.getEditor().setEnabled(true);

        gridPrinters.addColumn(Printer::getName).setCaption("Name");
        gridPrinters.addColumn(Printer::getIpAddress).setCaption("IP Address");
        gridPrinters.addColumn(Printer::getModel).setCaption("Model");
        gridPrinters.addColumn(Printer::getStatus).setCaption("Status");

        addComponent(gridPrinters, 4, 2, 6, 2);

        addComponent(btnAddPrinter, 4, 4);
        addComponent(btnDeletePrinter, 5, 4);
        addComponent(btnRefreshPrinterGrid, 6, 4);
//        addComponent(btnEditPrinterModels, 8, 4);

        /* TEMPORARY */ btnEditPrinterModels.setEnabled(false); /* TODO implement printer model feature */

        btnAddPrinter.addClickListener((Button.ClickListener) clickEvent -> {
            handler.addPrinter(this);
        });

        btnDeletePrinter.addClickListener((Button.ClickListener) clickEvent -> {
            Set<Printer> selected = gridPrinters.getSelectedItems();
            for (Printer p : selected) {
                handler.deletePrinter(this, p);
            }
        });

        btnAddPrinter.addClickListener((Button.ClickListener) clickEvent -> {
            handler.refreshPrinterList(this);
        });


        // Populate data
        handler.showComputerList(this);
        handler.showPrinterList(this);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {
        super.enter(viewChangeEvent);
    }

    public void setHandler(ComputerPresenter presenter) {
        this.handler = presenter;
    }

    public void afterSuccessfulComputerFetch(List<Computer> computers) {
        gridComputers.setItems(computers);
    }

    public void afterSuccessfulPrinterFetch(List<Printer> printers) {
        gridPrinters.setItems(printers);
    }

    public String getRequiredRight() { return REQUIRED_RIGHT; }

}