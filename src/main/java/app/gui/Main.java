package app.gui;

import app.gui.controller.Controller;
import app.gui.model.Model;
import app.gui.view.View;

public class Main {

    static public void main(String[] args) {
        Model model = new Model();
        Controller controller = new Controller(model);
        View view = new View(controller);
        model.addObserver(view);
        view.setVisible(true);
    }

}