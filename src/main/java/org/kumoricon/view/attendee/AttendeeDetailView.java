package org.kumoricon.view.attendee;

import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.*;
import org.kumoricon.model.attendee.Attendee;
import org.kumoricon.presenter.attendee.AttendeeDetailPresenter;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

@ViewScope
@SpringView(name = AttendeeDetailView.VIEW_NAME)
public class AttendeeDetailView extends VerticalLayout implements View {
    public static final String VIEW_NAME = "attendee";

    private AttendeeDetailForm detailForm = new AttendeeDetailForm();
    private Button btnSave;
    private Button btnCancel;
    private Button btnSaveAndReprint;

    @Autowired
    private AttendeeDetailPresenter handler;

    public AttendeeDetailView() {

    }

    @PostConstruct
    public void init() {
        handler.setView(this);
        addComponent(detailForm);
        addComponent(buildSaveCancel());

    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {
        String parameters = viewChangeEvent.getParameters();
        if (parameters != null && !parameters.equals("")) {
            handler.showAttendee((Integer.parseInt(viewChangeEvent.getParameters())));
        }
    }


    private HorizontalLayout buildSaveCancel() {
        HorizontalLayout h = new HorizontalLayout();
        h.setMargin(true);
        h.setSpacing(true);
        btnSave = new Button("Save");
        btnCancel = new Button("Cancel");
        btnSaveAndReprint = new Button("Save and Reprint Badge");

        btnSave.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                try {
                    detailForm.commit();
                    handler.saveAttendee();
                } catch (FieldGroup.CommitException e) {
                    Notification.show(e.getMessage());
                }
            }
        });
        btnCancel.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                handler.cancelAttendee();
            }
        });
        btnSaveAndReprint.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                handler.saveAttendeeAndRepreintBadge();
            }
        });
        h.addComponent(btnSave);
        h.addComponent(btnSaveAndReprint);
        h.addComponent(btnCancel);
        return h;
    }

    public void setHandler(AttendeeDetailPresenter handler) {
        this.handler = handler;
    }

    public Layout getDetailForm() { return detailForm; }
    public Attendee getAttendee() { return detailForm.getAttendee(); }


}
