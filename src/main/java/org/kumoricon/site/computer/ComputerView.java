package org.kumoricon.site.computer;

import com.vaadin.data.Item;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.*;
import org.kumoricon.model.computer.Computer;
import org.kumoricon.model.printer.Printer;
import org.kumoricon.site.BaseView;
import org.springframework.beans.factory.annotation.Autowired;
import javax.annotation.PostConstruct;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@ViewScope
@SpringView(name = ComputerView.VIEW_NAME)
public class ComputerView extends BaseView implements View {
    public static final String VIEW_NAME = "computers";
    public static final String REQUIRED_RIGHT = "manage_devices";

    @Autowired
    private ComputerPresenter handler;
    private static final Logger log = LoggerFactory.getLogger(ComputerView.class);

    private Grid gridComputers = new Grid();
    private Grid gridPrinters = new Grid();
    private Panel panelLeft = new Panel();
    private Panel panelRight = new Panel();
    private Label lblLeftTitle;
    private Label lblLeftSubtitle;
    private Label lblRightTitle;
    private Label lblRightSubtitle;
    private BeanItemContainer<Computer> computerList = new BeanItemContainer<Computer>(Computer.class);
    private BeanItemContainer<Printer> printerList = new BeanItemContainer<Printer>(Printer.class);
    private Button btnAddComputer = new Button("Add");
    private Button btnDeleteComputer = new Button("Delete");
    private Button btnAddPrinter = new Button("Install...");
    private Button btnDeletePrinter = new Button("Uninstall");
    private Button btnViewInstructions = new Button("Instructions...");
    private Button btnRefreshPrinterGrid = new Button("Refresh");
    private Button btnEditPrinterModels = new Button("Models...");

    @PostConstruct
    public void init() {

        //Create an overall page layout with two columns
        HorizontalLayout pageLayout = new HorizontalLayout();
        pageLayout.setSizeFull();
        pageLayout.setSpacing(true);

        // Left column (computers)
        VerticalLayout layoutLeft = new VerticalLayout();
        layoutLeft.setSizeUndefined();
        layoutLeft.setMargin(true);
        layoutLeft.setSpacing(true);
        lblLeftTitle = new Label ("<span style='font-size:24px'>Computers</span>",ContentMode.HTML);
        lblLeftSubtitle = new Label("<span style='font-size:12px'>This computer's IP address is: </span>" + getCurrentClientIPAddress(), ContentMode.HTML);
        layoutLeft.addComponent(lblLeftTitle);
        layoutLeft.addComponent(lblLeftSubtitle);
        gridComputers.setEditorEnabled(true);
        gridComputers.setSelectionMode(Grid.SelectionMode.SINGLE);
        gridComputers.setColumnReorderingAllowed(true);
        gridComputers.setContainerDataSource(computerList);
        gridComputers.removeColumn("id");
        gridComputers.removeColumn("uuid");

        gridComputers.getEditorFieldGroup().addCommitHandler(new FieldGroup.CommitHandler() {
            @Override
            public void preCommit(FieldGroup.CommitEvent commitEvent) throws FieldGroup.CommitException {

            }

            @Override
            public void postCommit(FieldGroup.CommitEvent commitEvent) throws FieldGroup.CommitException {
                Item editedItem = commitEvent.getFieldBinder().getItemDataSource();
                BeanItem<Computer> item = (BeanItem<Computer>) editedItem;
                handler.saveComputer(ComputerView.this, item.getBean());
            }
        });

        layoutLeft.addComponent(gridComputers);

        HorizontalLayout computerButtons = new HorizontalLayout();
        computerButtons.setSpacing(true);
        computerButtons.addComponent(btnAddComputer);
        computerButtons.addComponent(btnDeleteComputer);
        computerButtons.addComponent(btnViewInstructions);

        btnViewInstructions.addClickListener((Button.ClickListener) clickEvent -> {
            handler.showInstructions(this);
        });

        btnAddComputer.addClickListener((Button.ClickListener) clickEvent -> {
            handler.addNewComputer(this);
        });

        btnDeleteComputer.addClickListener((Button.ClickListener) clickEvent -> {
            BeanItem<Computer> item = computerList.getItem(gridComputers.getSelectedRow());
            if (item != null) {
                handler.deleteComputer(this, item.getBean());
            }
        });
        layoutLeft.addComponent(computerButtons);

        panelLeft.setContent(layoutLeft);

        // Right column (printers)
        VerticalLayout layoutRight = new VerticalLayout();
        layoutRight.setSizeUndefined();
        layoutRight.setMargin(true);
        layoutRight.setSpacing(true);
        lblRightTitle = new Label("<span style='font-size:24px'>Printers</span>",ContentMode.HTML);
        lblRightSubtitle = new Label("<span style='font-size:12px'>Only printers listed below can be used to print badges</span>",ContentMode.HTML);
        layoutRight.addComponent(lblRightTitle);
        layoutRight.addComponent(lblRightSubtitle);
        layoutRight.addComponent(gridPrinters);
        gridPrinters.setEditorEnabled(false);
        gridPrinters.setSelectionMode(Grid.SelectionMode.SINGLE);
        gridPrinters.setColumnReorderingAllowed(true);
        gridPrinters.setContainerDataSource(printerList);
        gridPrinters.removeColumn("id");
        gridPrinters.removeColumn("uuid");
        gridPrinters.setColumnOrder("name", "model");

        HorizontalLayout printerButtons = new HorizontalLayout();
        printerButtons.setSpacing(true);
        printerButtons.addComponent(btnAddPrinter);
        printerButtons.addComponent(btnDeletePrinter);
        printerButtons.addComponent(btnRefreshPrinterGrid);
        printerButtons.addComponent(btnEditPrinterModels);
        /* TEMPORARY */ btnEditPrinterModels.setEnabled(false); /* TODO implement printer model feature */

        btnAddPrinter.addClickListener((Button.ClickListener) clickEvent -> {
            handler.addPrinter(this);
        });

        btnDeletePrinter.addClickListener((Button.ClickListener) clickEvent -> {
            BeanItem<Printer> item = printerList.getItem(gridPrinters.getSelectedRow());
            if (item != null) {
                handler.deletePrinter(this, item.getBean());
            }
        });

        btnAddPrinter.addClickListener((Button.ClickListener) clickEvent -> {
            handler.refreshPrinterList(this);
        });

        layoutRight.addComponent(printerButtons);
        panelRight.setContent(layoutRight);

        // Display the page
        pageLayout.addComponent(panelLeft);
        pageLayout.addComponent(panelRight);
        addComponent(pageLayout);

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
        computerList.removeAllItems();
        computerList.addAll(computers);
    }

    public void afterSuccessfulPrinterFetch(List<Printer> printers) {
        printerList.removeAllItems();
        printerList.addAll(printers);
    }

    public void clearSelection() { gridComputers.select(null); }

    public String getRequiredRight() { return REQUIRED_RIGHT; }

    public void addComputer(Computer computer) {
        computerList.addItem(computer);
    }
}