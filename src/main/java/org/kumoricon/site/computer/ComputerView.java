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

@ViewScope
@SpringView(name = ComputerView.VIEW_NAME)
public class ComputerView extends BaseView implements View {
    public static final String VIEW_NAME = "computers";
    public static final String REQUIRED_RIGHT = "manage_devices";

    @Autowired
    private ComputerPresenter handler;

    private Grid printerMapList = new Grid();
    private Grid installedPrinterList = new Grid();
    private Panel leftPanel = new Panel();
    private Panel rightPanel = new Panel(); //com.vaadin.ui.
    private Label leftLabel;
    private Label leftNoteLabel;
    private Label rightLabel;
    private Label rightNoteLabel;
    private BeanItemContainer<Computer> computerList = new BeanItemContainer<Computer>(Computer.class);
    private BeanItemContainer<Printer> printerList = new BeanItemContainer<Printer>(Printer.class);
    private Button btnAddNewMapping = new Button("Add");
    private Button btnDeleteMapping = new Button("Delete");
    private Button btnAddInstalledPrinter = new Button("Install");
    private Button btnDeleteInstalledPrinter = new Button("Uninstall");

    @PostConstruct
    public void init() {

        //Create an overall page layout with two columns
        HorizontalLayout pageLayout = new HorizontalLayout();
        pageLayout.setSizeFull();
        pageLayout.setSpacing(true);

        // Left column (everything related to mapping computer IP addresses to installed printers)
        VerticalLayout leftLayout = new VerticalLayout();
        leftLayout.setSizeUndefined();
        leftLayout.setMargin(true);
        leftLayout.setSpacing(true);
        leftLabel = new Label ("<span style='font-size:24px'>Computer - Printer Mappings</span>",ContentMode.HTML);
        leftNoteLabel = new Label(String.format("<span style='font-size:12px'>This computer's IP address is: </span>" + getCurrentClientIPAddress()),ContentMode.HTML);
        leftLayout.addComponent(leftLabel);
        leftLayout.addComponent(leftNoteLabel);
        printerMapList.setEditorEnabled(true);
        printerMapList.setSelectionMode(Grid.SelectionMode.SINGLE);
        printerMapList.setColumnReorderingAllowed(true);
        printerMapList.setContainerDataSource(computerList);
        printerMapList.removeColumn("id");
        printerMapList.removeColumn("uuid");
        printerMapList.getColumn("Printer Name").setText("Printer IP");

        printerMapList.getEditorFieldGroup().addCommitHandler(new FieldGroup.CommitHandler() {
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

        leftLayout.addComponent(printerMapList);

        HorizontalLayout mappingButtons = new HorizontalLayout();
        mappingButtons.setSpacing(true);
        mappingButtons.addComponent(btnAddNewMapping);
        mappingButtons.addComponent(btnDeleteMapping);

        btnAddNewMapping.addClickListener((Button.ClickListener) clickEvent -> {
            handler.addNewComputer(this);
        });

        btnDeleteMapping.addClickListener((Button.ClickListener) clickEvent -> {
            BeanItem<Computer> item = computerList.getItem(printerMapList.getSelectedRow());
            if (item != null) {
                handler.deleteComputer(this, item.getBean());
            }
        });
        leftLayout.addComponent(mappingButtons);

        leftPanel.setContent(leftLayout);

        // Right column (everything related to printers intalled on the server)
        VerticalLayout rightLayout = new VerticalLayout();
        rightLayout.setSizeUndefined();
        rightLayout.setMargin(true);
        rightLayout.setSpacing(true);
        rightLabel = new Label("<span style='font-size:24px'>Installed Printers</span>",ContentMode.HTML);
        rightNoteLabel = new Label("<span style='font-size:12px'>Printers must be installed on the server before they can be mapped</span>",ContentMode.HTML);
        rightLayout.addComponent(rightLabel);
        rightLayout.addComponent(rightNoteLabel);
        rightLayout.addComponent(installedPrinterList);
        installedPrinterList.setEditorEnabled(false);
        installedPrinterList.setSelectionMode(Grid.SelectionMode.SINGLE);
        installedPrinterList.setColumnReorderingAllowed(true);
        installedPrinterList.setContainerDataSource(printerList);
        installedPrinterList.removeColumn("id");
        installedPrinterList.removeColumn("uuid");

        HorizontalLayout installedPrinterButtons = new HorizontalLayout();
        installedPrinterButtons.setSpacing(true);
        installedPrinterButtons.addComponent(btnAddInstalledPrinter);
        installedPrinterButtons.addComponent(btnDeleteInstalledPrinter);

        btnAddInstalledPrinter.addClickListener((Button.ClickListener) clickEvent -> {
            handler.addNewInstalledPrinter(this);
        });

        btnDeleteInstalledPrinter.addClickListener((Button.ClickListener) clickEvent -> {
            BeanItem<Printer> item = printerList.getItem(installedPrinterList.getSelectedRow());
            if (item != null) {
                handler.deleteInstalledPrinter(this, item.getBean());
            }
        });
        rightLayout.addComponent(installedPrinterButtons);

        rightPanel.setContent(rightLayout);

        // Display the page
        pageLayout.addComponent(leftPanel);
        pageLayout.addComponent(rightPanel);
        addComponent(pageLayout);

        // Populate data
        handler.showComputerList(this);
        handler.showInstalledPrinterList(this);
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

    public void clearSelection() { printerMapList.select(null); }

    public String getRequiredRight() { return REQUIRED_RIGHT; }

    public void addComputer(Computer computer) {
        computerList.addItem(computer);
    }
}