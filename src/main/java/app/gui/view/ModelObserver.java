package app.gui.view;

import app.gui.model.Model;

public interface ModelObserver {

    void modelUpdated(Model model);
}
