package app.view;

import app.model.Model;

public interface ModelObserver {

    void modelUpdated(Model model);
}
