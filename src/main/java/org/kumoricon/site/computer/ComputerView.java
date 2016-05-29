package org.kumoricon.site.computer;

import com.vaadin.data.Item;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.*;
import org.kumoricon.model.computer.Computer;
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

    private Grid data = new Grid();
    private Label yourAddress;
    private BeanItemContainer<Computer> computerList = new BeanItemContainer<Computer>(Computer.class);
    private Button btnAddNew = new Button("Add");
    private Button btnDelete = new Button("Delete");

    @PostConstruct
    public void init() {
        VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);
        layout.setSpacing(true);
        yourAddress = new Label("Your IP Address is: " + getCurrentClientIPAddress());
        layout.addComponent(yourAddress);
        data.setCaption("Computer - Printer Mappings");
        data.setEditorEnabled(true);
        data.setSelectionMode(Grid.SelectionMode.SINGLE);
        data.setColumnReorderingAllowed(true);
        data.setContainerDataSource(computerList);
        data.removeColumn("id");

        data.getEditorFieldGroup().addCommitHandler(new FieldGroup.CommitHandler() {
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

        layout.addComponent(data);

        HorizontalLayout buttons = new HorizontalLayout();
        buttons.setSpacing(true);
        buttons.addComponent(btnAddNew);
        buttons.addComponent(btnDelete);

        btnAddNew.addClickListener((Button.ClickListener) clickEvent -> {
            handler.addNewComputer(this);
        });

        btnDelete.addClickListener((Button.ClickListener) clickEvent -> {
            BeanItem<Computer> item = computerList.getItem(data.getSelectedRow());
            if (item != null) {
                handler.deleteComputer(this, item.getBean());
            }
        });

        layout.addComponent(buttons);

        addComponent(layout);
        handler.showComputerList(this);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {
        super.enter(viewChangeEvent);
    }

    public void setHandler(ComputerPresenter presenter) {
        this.handler = presenter;
    }

    public void afterSuccessfulFetch(List<Computer> computers) {
        computerList.removeAllItems();
        computerList.addAll(computers);
    }

    public void clearSelection() { data.select(null); }

    public String getRequiredRight() { return REQUIRED_RIGHT; }

    public void addComputer(Computer computer) {
        computerList.addItem(computer);
    }
}