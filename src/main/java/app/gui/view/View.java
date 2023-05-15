package app.gui.view;

import app.gui.controller.Controller;
import app.gui.model.Model;
import app.gui.utils.Approach;
import app.gui.utils.Event;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Objects;

import javax.swing.*;

public class View extends JFrame implements ActionListener, ModelObserver {
    private final Controller controller;
    private final DefaultListModel<String> distributionListModel = new DefaultListModel<>();
    private final DefaultListModel<String> topFilesListModel = new DefaultListModel<>();
    private final String[] approaches = new String[]{
            Approach.TASK.getMessage(),
            Approach.VIRTUAL_THREAD.getMessage(),
            Approach.EVENTS.getMessage(),
            Approach.ASYNC_EVENTS.getMessage(),
            Approach.REACTIVE.getMessage()
    };

    private final JFileChooser chooser = new JFileChooser();
    private final JComboBox<String> approachCombo = new JComboBox<>(approaches);
    //                                                    "/Users/mattia/Desktop/Università/Triennale"
    private final JTextField directoryTxt = new JTextField("/Users/mattia/Desktop/Università/Triennale", 20);
    private final JTextField intervalsTxt = new JTextField("5", 5);
    private final JTextField maxLinesTxt = new JTextField("300", 5);


    public View(Controller controller) {
        super("Source Analyzer");
        this.controller = controller;
        setupGUI();
    }

    public void actionPerformed(ActionEvent ev) {
        try {
            switch (Event.valueOf(ev.getActionCommand())) {
                case START -> {
                    if (!directoryTxt.getText().equals("")
                            && !Objects.equals(intervalsTxt.getText(), "")
                            && !Objects.equals(maxLinesTxt.getText(), "")
                            && Integer.parseInt(intervalsTxt.getText()) > 0
                            && Integer.parseInt(maxLinesTxt.getText()) > 0) {
                        controller.setParameters(Approach.getByMessage((String) approachCombo.getSelectedItem()),
                                directoryTxt.getText(), Integer.parseInt(intervalsTxt.getText()), Integer.parseInt(maxLinesTxt.getText()));
                    }
                }
                case RESET -> resetParameters();
            }
            controller.processEvent(Event.valueOf(ev.getActionCommand()));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void modelUpdated(Model model) {
        try {
            SwingUtilities.invokeLater(() -> {
                this.distributionListModel.clear();
                this.topFilesListModel.clear();
                model.getDistributions().forEach((k, v) -> {
                    this.distributionListModel.addElement(k + " " + v);
                });
                model.getTopFiles().entrySet()
                        .stream()
                        .sorted((a, b) -> b.getValue() - a.getValue())
                        .forEach(entry -> this.topFilesListModel.addElement(entry.getKey() + " " + entry.getValue()));
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    private void setupGUI() {
        setSize(800, 600);
        setResizable(false);

        //------------- START PARAMETERS PANEL -------------

        JPanel parametersPanel = new JPanel();

        //dir panel e interval panel inclusi in inline panel (horizontal)
        JPanel approachPanel = new JPanel();
        JLabel approachLabel = new JLabel("Seleziona approccio:");
        approachPanel.add(approachLabel);
        approachCombo.setSelectedIndex(0);
        approachPanel.add(approachCombo);

        JPanel dirPanel = new JPanel();
        JLabel dirLabel = new JLabel("Dir:");
        dirPanel.add(dirLabel);
        dirPanel.add(directoryTxt);

        JButton openFileChooserBtn = new JButton("Scegli directory");
        openFileChooserBtn.setActionCommand(Event.OPEN_FILE_DIALOG.getCommand());
        openFileChooserBtn.addActionListener(e -> {
            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                directoryTxt.setText(chooser.getSelectedFile().toString());
            } else {
                directoryTxt.setText("No Selection");
            }

        });
        dirPanel.add(openFileChooserBtn);

        chooser.setDialogTitle("Choose the directory to scan");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);

        JPanel intPanel = new JPanel();
        JLabel intLabel = new JLabel("Int:");
        intPanel.add(intLabel);
        intPanel.add(intervalsTxt);

        JPanel maxLinesPanel = new JPanel();
        JLabel maxLinesLabel = new JLabel("Max Lines:");
        maxLinesPanel.add(maxLinesLabel);
        maxLinesPanel.add(maxLinesTxt);

        JPanel inlinePanel = new JPanel();
        inlinePanel.add(intPanel);
        inlinePanel.add(maxLinesPanel);
        parametersPanel.setLayout(new BoxLayout(parametersPanel, BoxLayout.X_AXIS));

        parametersPanel.add(approachPanel);
        parametersPanel.add(dirPanel);
        parametersPanel.add(inlinePanel);
        parametersPanel.setLayout(new BoxLayout(parametersPanel, BoxLayout.Y_AXIS));

        //------------- END PARAMETERS PANEL -------------

        //------------- START LISTS PANEL -------------

        JPanel dataPanel = new JPanel();

        JList<String> distributionList = new JList<>(distributionListModel);
        distributionList.setFixedCellWidth(400);
        distributionList.setFixedCellHeight(35);

        JList<String> topFilesList = new JList<>(topFilesListModel);
        topFilesList.setFixedCellWidth(400);
        topFilesList.setFixedCellHeight(35);
        topFilesList.setAlignmentX(CENTER_ALIGNMENT);
        distributionList.setAlignmentX(CENTER_ALIGNMENT);
        topFilesList.setAlignmentY(CENTER_ALIGNMENT);
        distributionList.setAlignmentY(CENTER_ALIGNMENT);

        dataPanel.setLayout(new BoxLayout(dataPanel, BoxLayout.X_AXIS));

        dataPanel.add(distributionList);
        dataPanel.add(topFilesList);

        //------------- END LISTS PANEL -------------


        //------------- START ACTION PANEL -------------
        //action panel -> BorderLayout.SOUTH
        JPanel actionPanel = new JPanel();

        JButton startBtn = new JButton("Start");
        startBtn.setActionCommand(Event.START.getCommand());
        startBtn.addActionListener(this);
        JButton stopBtn = new JButton("Stop");
        stopBtn.setActionCommand(Event.STOP.getCommand());
        stopBtn.addActionListener(this);
        JButton resetBtn = new JButton("Reset");
        resetBtn.setActionCommand(Event.RESET.getCommand());
        resetBtn.addActionListener(this);

        actionPanel.add(resetBtn);
        actionPanel.add(startBtn);
        actionPanel.add(stopBtn);

        //------------- END ACTION PANEL -------------

        setLayout(new BorderLayout());

        add(parametersPanel, BorderLayout.NORTH);

        add(dataPanel, BorderLayout.CENTER);

        add(actionPanel, BorderLayout.SOUTH);

        this.setLocationRelativeTo(null);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent ev) {
                System.exit(-1);
            }
        });

    }

    private void resetParameters() {
        distributionListModel.clear();
        topFilesListModel.clear();
    }
}
