package org.kumoricon.presenter.computer;

import org.kumoricon.model.computer.Computer;
import org.kumoricon.model.computer.ComputerRepository;
import org.kumoricon.view.computer.ComputerView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.List;


@Controller
public class ComputerPresenter {
    @Autowired
    private ComputerRepository computerRepository;

    public ComputerPresenter() {
    }


    public void addNewComputer(ComputerView view) {
        Computer computer = new Computer();
        saveComputer(view, computer);
    }


    public void saveComputer(ComputerView view, Computer computer) {
        computerRepository.save(computer);
        view.notify("Saved");
        view.navigateTo(ComputerView.VIEW_NAME);
        showComputerList(view);
    }

    public void showComputerList(ComputerView view) {
        List<Computer> computers = computerRepository.findAll();
        view.afterSuccessfulFetch(computers);
    }

    public void deleteComputer(ComputerView view, Computer computer) {
        computerRepository.delete(computer);
        view.notify("Deleted " + computer.getIpAddress());
        view.afterSuccessfulFetch(computerRepository.findAll());
    }
}
