package org.kumoricon.site.computer;

import org.kumoricon.model.computer.Computer;
import org.kumoricon.model.computer.ComputerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.List;


@Controller
public class ComputerPresenter {
    @Autowired
    private ComputerRepository computerRepository;

    private static final Logger log = LoggerFactory.getLogger(ComputerPresenter.class);

    public ComputerPresenter() {
    }

    public void addNewComputer(ComputerView view) {
        log.info("{} added new computer", view.getCurrentUsername());
        Computer computer = new Computer();
        saveComputer(view, computer);
    }


    public void saveComputer(ComputerView view, Computer computer) {
        log.info("{} saved computer {}", view.getCurrentUsername(), computer);
        computerRepository.save(computer);
        view.notify("Saved");
        view.navigateTo(ComputerView.VIEW_NAME);
        showComputerList(view);
    }

    public void showComputerList(ComputerView view) {
        log.info("{} viewed computer list", view.getCurrentUsername());
        List<Computer> computers = computerRepository.findAll();
        view.afterSuccessfulFetch(computers);
    }

    public void deleteComputer(ComputerView view, Computer computer) {
        log.info("{} deleted computer {}", view.getCurrentUsername(), computer);
        computerRepository.delete(computer);
        view.notify("Deleted " + computer.getIpAddress());
        view.afterSuccessfulFetch(computerRepository.findAll());
    }
}
