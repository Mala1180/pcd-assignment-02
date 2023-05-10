package app;

import app.controller.Controller;
import app.model.Model;
import app.view.View;

public class Main {

    static public void main(String[] args) {
        Model model = new Model();
        Controller controller = new Controller(model);
        View view = new View(controller);
        model.addObserver(view);
        view.setVisible(true);
    }

}