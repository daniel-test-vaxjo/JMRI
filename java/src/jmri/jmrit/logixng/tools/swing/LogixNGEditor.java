package jmri.jmrit.logixng.tools.swing;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import jmri.InstanceManager;
import jmri.jmrit.beantable.BeanTableDataModel;
import jmri.jmrit.beantable.BeanTableFrame;
import jmri.jmrit.logixng.LogixNG;
import jmri.jmrit.logixng.LogixNG_Manager;
import jmri.jmrit.logixng.ConditionalNG;
import jmri.jmrit.logixng.implementation.DefaultConditionalNG;
import jmri.util.JmriJFrame;
import jmri.util.swing.*;
import jmri.util.table.ButtonEditor;
import jmri.util.table.ButtonRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Editor for LogixNG
 *
 * @author Dave Duchamp Copyright (C) 2007  (ConditionalListEdit)
 * @author Pete Cressman Copyright (C) 2009, 2010, 2011  (ConditionalListEdit)
 * @author Matthew Harris copyright (c) 2009  (ConditionalListEdit)
 * @author Dave Sand copyright (c) 2017  (ConditionalListEdit)
 * @author Daniel Bergqvist (c) 2019
 */
public final class LogixNGEditor {
    
    BeanTableFrame<LogixNG> beanTableFrame;
    BeanTableDataModel<LogixNG> beanTableDataModel;
    
    LogixNG_Manager _logixNG_Manager = null;
    LogixNG _curLogixNG = null;
    
    ConditionalNGEditor _treeEdit = null;
    
    int _numConditionalNGs = 0;
    boolean _inEditMode = false;
    
    boolean _showReminder = false;
    boolean _suppressReminder = false;
    boolean _suppressIndirectRef = false;
    
    JmriBeanComboBox _comboNameBox = null;
    
    
    /**
     * Create a new ConditionalNG List View editor.
     *
     * @param f the bean table frame
     * @param m the bean table model
     * @param sName name of the LogixNG being edited
     */
    public LogixNGEditor(BeanTableFrame<LogixNG> f, BeanTableDataModel<LogixNG> m, String sName) {
        this.beanTableFrame = f;
        this.beanTableDataModel = m;
        _logixNG_Manager = InstanceManager.getDefault(jmri.jmrit.logixng.LogixNG_Manager.class);
        _curLogixNG = _logixNG_Manager.getBySystemName(sName);
        makeEditLogixNGWindow();
    }

    // ------------ LogixNG Variables ------------
    JmriJFrame _editLogixNGFrame = null;
    JTextField editUserName = new JTextField(20);
    JLabel status = new JLabel(" ");

    // ------------ ConditionalNG Variables ------------
    ConditionalNGTableModel conditionalTableModel = null;
    ConditionalNG _curConditionalNG = null;
    int _conditionalRowNumber = 0;
    boolean _inReorderMode = false;
    boolean _inActReorder = false;
    boolean _inVarReorder = false;
    int _nextInOrder = 0;

    // ------------ Select LogixNG/ConditionalNG Variables ------------
    JPanel _selectLogixNGPanel = null;
    JPanel _selectConditionalNGPanel = null;
    JComboBox<String> _selectLogixNGBox = new JComboBox<>();
    JComboBox<String> _selectConditionalNGBox = new JComboBox<>();
    TreeMap<String, String> _selectLogixNGMap = new TreeMap<>();
    ArrayList<String> _selectConditionalNGList = new ArrayList<>();

    // ------------ Edit ConditionalNG Variables ------------
    boolean _inEditConditionalNGMode = false;
    JmriJFrame _editConditionalNGFrame = null;
    JTextField _conditionalUserName = new JTextField(22);
    JRadioButton _triggerOnChangeButton;

    // ------------ Methods for Edit LogixNG Pane ------------

    /**
     * Create and/or initialize the Edit LogixNG pane.
     */
    void makeEditLogixNGWindow() {
        editUserName.setText(_curLogixNG.getUserName());
        // clear conditional table if needed
        if (conditionalTableModel != null) {
            conditionalTableModel.fireTableStructureChanged();
        }
        _inEditMode = true;
        if (_editLogixNGFrame == null) {
            if (_curLogixNG.getUserName() != null) {
                _editLogixNGFrame = new JmriJFrame(
                        Bundle.getMessage("TitleEditLogixNG2",
                                _curLogixNG.getSystemName(),   // NOI18N
                                _curLogixNG.getUserName()),    // NOI18N
                        false,
                        false);
            } else {
                _editLogixNGFrame = new JmriJFrame(
                        Bundle.getMessage("TitleEditLogixNG", _curLogixNG.getSystemName()),  // NOI18N
                        false,
                        false);
            }
            _editLogixNGFrame.addHelpMenu(
                    "package.jmri.jmrit.conditional.ConditionalNGListEditor", true);  // NOI18N
            _editLogixNGFrame.setLocation(100, 30);
            Container contentPane = _editLogixNGFrame.getContentPane();
            contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
            JPanel panel1 = new JPanel();
            panel1.setLayout(new FlowLayout());
            JLabel systemNameLabel = new JLabel(Bundle.getMessage("ColumnSystemName") + ":");  // NOI18N
            panel1.add(systemNameLabel);
            JLabel fixedSystemName = new JLabel(_curLogixNG.getSystemName());
            panel1.add(fixedSystemName);
            contentPane.add(panel1);
            JPanel panel2 = new JPanel();
            panel2.setLayout(new FlowLayout());
            JLabel userNameLabel = new JLabel(Bundle.getMessage("ColumnUserName") + ":");  // NOI18N
            panel2.add(userNameLabel);
            panel2.add(editUserName);
            editUserName.setToolTipText(Bundle.getMessage("LogixNGUserNameHint2"));  // NOI18N
            contentPane.add(panel2);
            // add table of ConditionalNGs
            JPanel pctSpace = new JPanel();
            pctSpace.setLayout(new FlowLayout());
            pctSpace.add(new JLabel("   "));
            contentPane.add(pctSpace);
            JPanel pTitle = new JPanel();
            pTitle.setLayout(new FlowLayout());
            pTitle.add(new JLabel(Bundle.getMessage("ConditionalNGTableTitle")));  // NOI18N
            contentPane.add(pTitle);
            // initialize table of conditionals
            conditionalTableModel = new ConditionalNGTableModel();
            JTable conditionalTable = new JTable(conditionalTableModel);
            conditionalTable.setRowSelectionAllowed(false);
            TableColumnModel conditionalColumnModel = conditionalTable
                    .getColumnModel();
            TableColumn sNameColumn = conditionalColumnModel
                    .getColumn(ConditionalNGTableModel.SNAME_COLUMN);
            sNameColumn.setResizable(true);
            sNameColumn.setMinWidth(100);
            sNameColumn.setPreferredWidth(130);
            TableColumn uNameColumn = conditionalColumnModel
                    .getColumn(ConditionalNGTableModel.UNAME_COLUMN);
            uNameColumn.setResizable(true);
            uNameColumn.setMinWidth(210);
            uNameColumn.setPreferredWidth(260);
            TableColumn buttonColumn = conditionalColumnModel
                    .getColumn(ConditionalNGTableModel.BUTTON_COLUMN);

            // install button renderer and editor
            ButtonRenderer buttonRenderer = new ButtonRenderer();
            conditionalTable.setDefaultRenderer(JButton.class, buttonRenderer);
            TableCellEditor buttonEditor = new ButtonEditor(new JButton());
            conditionalTable.setDefaultEditor(JButton.class, buttonEditor);
            JButton testButton = new JButton("XXXXXX");  // NOI18N
            conditionalTable.setRowHeight(testButton.getPreferredSize().height);
            buttonColumn.setMinWidth(testButton.getPreferredSize().width);
            buttonColumn.setMaxWidth(testButton.getPreferredSize().width);
            buttonColumn.setResizable(false);

            JScrollPane conditionalTableScrollPane = new JScrollPane(conditionalTable);
            Dimension dim = conditionalTable.getPreferredSize();
            dim.height = 450;
            conditionalTableScrollPane.getViewport().setPreferredSize(dim);
            contentPane.add(conditionalTableScrollPane);

            // add message area between table and buttons
            JPanel panel4 = new JPanel();
            panel4.setLayout(new BoxLayout(panel4, BoxLayout.Y_AXIS));
            JPanel panel41 = new JPanel();
            panel41.setLayout(new FlowLayout());
            panel41.add(status);
            panel4.add(panel41);
            JPanel panel42 = new JPanel();
            panel42.setLayout(new FlowLayout());
            // ConditionalNG panel buttons - New ConditionalNG
            JButton newConditionalNGButton = new JButton(Bundle.getMessage("NewConditionalNGButton"));  // NOI18N
            panel42.add(newConditionalNGButton);
            newConditionalNGButton.addActionListener((e) -> {
                newConditionalNGPressed(e);
            });
            newConditionalNGButton.setToolTipText(Bundle.getMessage("NewConditionalNGButtonHint"));  // NOI18N
            // ConditionalNG panel buttons - Reorder
            JButton reorderButton = new JButton(Bundle.getMessage("ReorderButton"));  // NOI18N
            panel42.add(reorderButton);
            reorderButton.addActionListener((e) -> {
                reorderPressed(e);
            });
            reorderButton.setToolTipText(Bundle.getMessage("ReorderButtonHint"));  // NOI18N
            // ConditionalNG panel buttons - Calculate
            JButton calculateButton = new JButton(Bundle.getMessage("CalculateButton"));  // NOI18N
            panel42.add(calculateButton);
            calculateButton.addActionListener((e) -> {
                calculatePressed(e);
            });
            calculateButton.setToolTipText(Bundle.getMessage("CalculateButtonHint"));  // NOI18N
            panel4.add(panel42);
            Border panel4Border = BorderFactory.createEtchedBorder();
            panel4.setBorder(panel4Border);
            contentPane.add(panel4);
            // add buttons at bottom of window
            JPanel panel5 = new JPanel();
            panel5.setLayout(new FlowLayout());
            // Bottom Buttons - Done LogixNG
            JButton done = new JButton(Bundle.getMessage("ButtonDone"));  // NOI18N
            panel5.add(done);
            done.addActionListener((e) -> {
                donePressed(e);
            });
            done.setToolTipText(Bundle.getMessage("DoneButtonHint"));  // NOI18N
            // Delete LogixNG
            JButton delete = new JButton(Bundle.getMessage("ButtonDelete"));  // NOI18N
            panel5.add(delete);
            delete.addActionListener((e) -> {
                deletePressed();
            });
            delete.setToolTipText(Bundle.getMessage("DeleteLogixNGButtonHint"));  // NOI18N
            contentPane.add(panel5);
        }

        _editLogixNGFrame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                if (_inEditMode) {
                    donePressed(null);
                } else {
                    finishDone();
                }
            }
        });
        _editLogixNGFrame.pack();
        _editLogixNGFrame.setVisible(true);
    }

    public void bringToFront() {
        if (_editLogixNGFrame != null) {
            _editLogixNGFrame.setVisible(true);
        }
    }

    /**
     * Display reminder to save.
     */
    void showSaveReminder() {
        if (_showReminder) {
            if (InstanceManager.getNullableDefault(jmri.UserPreferencesManager.class) != null) {
                InstanceManager.getDefault(jmri.UserPreferencesManager.class).
                        showInfoMessage(Bundle.getMessage("ReminderTitle"), // NOI18N
                                Bundle.getMessage("ReminderSaveString", // NOI18N
                                        Bundle.getMessage("MenuItemLogixNGTable")), // NOI18N
                                getClassName(),
                                "remindSaveLogixNG"); // NOI18N
            }
        }
    }

    /**
     * Respond to the Reorder Button in the Edit LogixNG pane.
     *
     * @param e The event heard
     */
    void reorderPressed(ActionEvent e) {
        if (checkEditConditionalNG()) {
            return;
        }
        // Check if reorder is reasonable
        _showReminder = true;
        _nextInOrder = 0;
        _inReorderMode = true;
        status.setText(Bundle.getMessage("ReorderMessage"));  // NOI18N
        conditionalTableModel.fireTableDataChanged();
    }

    /**
     * Respond to the First/Next (Delete) Button in the Edit LogixNG window.
     *
     * @param row index of the row to put as next in line (instead of the one
     *            that was supposed to be next)
     */
    void swapConditionalNG(int row) {
        _curLogixNG.swapConditionalNG(_nextInOrder, row);
        _nextInOrder++;
        if (_nextInOrder >= _numConditionalNGs) {
            _inReorderMode = false;
        }
        //status.setText("");
        conditionalTableModel.fireTableDataChanged();
    }

    /**
     * Responds to the Calculate Button in the Edit LogixNG window.
     *
     * @param e The event heard
     */
    void calculatePressed(ActionEvent e) {
        if (checkEditConditionalNG()) {
            return;
        }
        // are there ConditionalNGs to calculate?
        if (_numConditionalNGs > 0) {
            // There are conditionals to calculate
            for (int i = 0; i < _numConditionalNGs; i++) {
                ConditionalNG c = _curLogixNG.getConditionalNG(i);
                if (c == null) {
                    log.error("Invalid conditional system name when calculating"); // NOI18N
                } else {
                    c.execute();
                    // calculate without taking any action
//                    c.calculate(false, null);
                }
            }
            // force the table to update
            conditionalTableModel.fireTableDataChanged();
        }
    }

    /**
     * Respond to the Done button in the Edit LogixNG window.
     * <p>
     * Note: We also get here if the Edit LogixNG window is dismissed, or if the
     * Add button is pressed in the Logic Table with an active Edit LogixNG
     * window.
     *
     * @param e The event heard
     */
    void donePressed(ActionEvent e) {
        if (checkEditConditionalNG()) {
            return;
        }
        // Check if the User Name has been changed
        String uName = editUserName.getText().trim();
        if (!(uName.equals(_curLogixNG.getUserName()))) {
            // user name has changed - check if already in use
            if (uName.length() > 0) {
                LogixNG p = _logixNG_Manager.getByUserName(uName);
                if (p != null) {
                    // LogixNG with this user name already exists
                    log.error("Failure to update LogixNG with Duplicate User Name: " // NOI18N
                            + uName);
                    JOptionPane.showMessageDialog(_editLogixNGFrame,
                            Bundle.getMessage("Error6"),
                            Bundle.getMessage("ErrorTitle"), // NOI18N
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            // user name is unique, change it
            // user name is unique, change it
            logixData.clear();
            logixData.put("chgUname", uName);  // NOI18N
            fireLogixNGEvent();
        }
        // complete update and activate LogixNG
        finishDone();
    }

    void finishDone() {
        showSaveReminder();
        _inEditMode = false;
        if (_editLogixNGFrame != null) {
            _editLogixNGFrame.setVisible(false);
            _editLogixNGFrame.dispose();
            _editLogixNGFrame = null;
        }
        logixData.clear();
        logixData.put("Finish", _curLogixNG.getSystemName());   // NOI18N
        fireLogixNGEvent();
    }

    /**
     * Respond to the Delete button in the Edit LogixNG window.
     */
    void deletePressed() {
        if (checkEditConditionalNG()) {
            return;
        }
/*        
        if (!checkConditionalNGReferences(_curLogixNG.getSystemName())) {
            return;
        }
*/        
        _showReminder = true;
        logixData.clear();
        logixData.put("Delete", _curLogixNG.getSystemName());   // NOI18N
        fireLogixNGEvent();
        finishDone();
    }

    /**
     * Respond to the New ConditionalNG Button in Edit LogixNG Window.
     *
     * @param e The event heard
     */
    void newConditionalNGPressed(ActionEvent e) {
        if (checkEditConditionalNG()) {
            return;
        }
        // make system name for new conditional
        int num = _curLogixNG.getNumConditionalNGs() + 1;
        _curConditionalNG = null;
        String cName = null;
        while (_curConditionalNG == null) {
            cName = _curLogixNG.getSystemName() + ":" + Integer.toString(num);
            if (_curLogixNG.getConditionalNG(cName) == null) {
                _curConditionalNG = new DefaultConditionalNG(cName);
            }
            num++;
            if (num == 1000) {
                break;
            }
        }
        if (_curConditionalNG == null) {
            // should never get here unless there is an assignment conflict
            log.error("Failure to create ConditionalNG with System Name: " // NOI18N
                    + cName);
            return;
        }
        // add to LogixNG at the end of the calculate order
        _curLogixNG.addConditionalNG(_curConditionalNG);
        conditionalTableModel.fireTableRowsInserted(_numConditionalNGs, _numConditionalNGs);
        _conditionalRowNumber = _numConditionalNGs;
        _numConditionalNGs++;
        _showReminder = true;
        makeEditConditionalNGWindow();
    }

    // ============ Edit Conditional Window and Methods ============

    /**
     * Create and/or initialize the Edit Conditional window.
     * <p>
     * Note: you can get here via the New Conditional button
     * (newConditionalPressed) or via an Edit button in the Conditional table of
     * the Edit Logix window.
     */
    void makeEditConditionalNGWindow() {
        // deactivate this Logix
        _curLogixNG.deActivateLogixNG();
        _conditionalUserName.setText(_curConditionalNG.getUserName());
        
        // Create a new LogixNG edit view, add the listener.
//        if (_editMode == LogixNGTableAction.EditMode.TREEEDIT) {
            _treeEdit = new ConditionalNGEditor(_curConditionalNG);
            _treeEdit.initComponents();
            _treeEdit.setVisible(true);
            _inEditMode = true;
            
            _treeEdit.addLogixNGEventListener(new ConditionalNGEditor.LogixNGEventListener() {
                @Override
                public void logixNGEventOccurred() {
//                    String lgxName = sName;
                    String lgxName = _curLogixNG.getSystemName();
                    _treeEdit.logixNGData.forEach((key, value) -> {
                        if (key.equals("Finish")) {                  // NOI18N
                            _treeEdit = null;
                            _inEditMode = false;
                            _curLogixNG.setEnabled(true);
                            beanTableFrame.setVisible(true);
                        } else if (key.equals("Delete")) {           // NOI18N
                            deletePressed();
                        } else if (key.equals("chgUname")) {         // NOI18N
                            LogixNG x = _logixNG_Manager.getBySystemName(lgxName);
                            x.setUserName(value);
                            beanTableDataModel.fireTableDataChanged();
                        }
                    });
                }
            });
//        }
    }
    
    // ------------ Methods for Edit ConditionalNG Pane ------------

    /**
     * Respond to Edit Button in the ConditionalNG table of the Edit LogixNG Window.
     *
     * @param rx index (row number) of ConditionalNG to be edited
     */
    void editConditionalNGPressed(int rx) {
        if (_inEditConditionalNGMode) {
            // Already editing a ConditionalNG, ask for completion of that edit
            JOptionPane.showMessageDialog(_editConditionalNGFrame,
                    Bundle.getMessage("Error34", _curConditionalNG.getSystemName()),
                    Bundle.getMessage("ErrorTitle"), // NOI18N
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        // get ConditionalNG to edit
        _curConditionalNG = _curLogixNG.getConditionalNG(rx);
        if (_curConditionalNG == null) {
            log.error("Attempted edit of non-existant conditional.");  // NOI18N
            return;
        }
        _conditionalRowNumber = rx;
        // get action variables
        makeEditConditionalNGWindow();
    }

    /**
     * Check if edit of a conditional is in progress.
     *
     * @return true if this is the case, after showing dialog to user
     */
    boolean checkEditConditionalNG() {
        if (_inEditConditionalNGMode) {
            // Already editing a ConditionalNG, ask for completion of that edit
            JOptionPane.showMessageDialog(_editConditionalNGFrame,
                    Bundle.getMessage("Error35", _curConditionalNG.getSystemName()), // NOI18N
                    Bundle.getMessage("ErrorTitle"), // NOI18N
                    JOptionPane.ERROR_MESSAGE);
            return true;
        }
        return false;
    }

    boolean checkConditionalNGUserName(String uName, LogixNG logixNG) {
        if ((uName != null) && (!(uName.equals("")))) {
            for (int i=0; i < logixNG.getNumConditionalNGs(); i++) {
                ConditionalNG p = logixNG.getConditionalNG(i);
                if (uName.equals(p.getUserName())) {
                    // ConditionalNG with this user name already exists
                    log.error("Failure to update ConditionalNG with Duplicate User Name: " // NOI18N
                            + uName);
                    JOptionPane.showMessageDialog(_editConditionalNGFrame,
                            Bundle.getMessage("Error10"),    // NOI18N
                            Bundle.getMessage("ErrorTitle"), // NOI18N
                            JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            }
        } // else return false;
        return true;
    }

    /**
     * Check form of ConditionalNG systemName.
     *
     * @param sName system name of bean to be checked
     * @return false if sName is empty string or null
     */
    boolean checkConditionalNGSystemName(String sName) {
        if ((sName != null) && (!(sName.equals("")))) {
            ConditionalNG p = _curLogixNG.getConditionalNG(sName);
            if (p != null) {
                return false;
            }
        } else {
            return false;
        }
        return true;
    }

    // ------------ Table Models ------------

    /**
     * Table model for ConditionalNGs in the Edit LogixNG pane.
     */
    public final class ConditionalNGTableModel extends AbstractTableModel implements
            PropertyChangeListener {

        public static final int SNAME_COLUMN = 0;

        public static final int UNAME_COLUMN = 1;

        public static final int BUTTON_COLUMN = 2;

        public ConditionalNGTableModel() {
            super();
            updateConditionalNGListeners();
        }

        synchronized void updateConditionalNGListeners() {
            // first, remove listeners from the individual objects
            ConditionalNG c;
            _numConditionalNGs = _curLogixNG.getNumConditionalNGs();
            for (int i = 0; i < _numConditionalNGs; i++) {
                // if object has been deleted, it's not here; ignore it
                c = _curLogixNG.getConditionalNG(i);
                if (c != null) {
                    c.removePropertyChangeListener(this);
                }
            }
            // and add them back in
            for (int i = 0; i < _numConditionalNGs; i++) {
                c = _curLogixNG.getConditionalNG(i);
                if (c != null) {
                    c.addPropertyChangeListener(this);
                }
            }
        }

        @Override
        public void propertyChange(java.beans.PropertyChangeEvent e) {
            if (e.getPropertyName().equals("length")) {  // NOI18N
                // a new NamedBean is available in the manager
                updateConditionalNGListeners();
                fireTableDataChanged();
            } else if (matchPropertyName(e)) {
                // a value changed.
                fireTableDataChanged();
            }
        }

        /**
         * Check if this property event is announcing a change this table should
         * display.
         * <p>
         * Note that events will come both from the NamedBeans and from the
         * manager.
         *
         * @param e the event heard
         * @return true if a change in State or Appearance was heard
         */
        boolean matchPropertyName(java.beans.PropertyChangeEvent e) {
            return (e.getPropertyName().contains("State") ||      // NOI18N
                    e.getPropertyName().contains("Appearance"));  // NOI18N
        }

        @Override
        public Class<?> getColumnClass(int c) {
            if (c == BUTTON_COLUMN) {
                return JButton.class;
            }
            return String.class;
        }

        @Override
        public int getColumnCount() {
            return 4;
        }

        @Override
        public int getRowCount() {
            return (_numConditionalNGs);
        }

        @Override
        public boolean isCellEditable(int r, int c) {
            if (!_inReorderMode) {
                return ((c == UNAME_COLUMN) || (c == BUTTON_COLUMN));
            } else if (c == BUTTON_COLUMN) {
                if (r >= _nextInOrder) {
                    return (true);
                }
            }
            return (false);
        }

        @Override
        public String getColumnName(int col) {
            switch (col) {
                case SNAME_COLUMN:
                    return Bundle.getMessage("ColumnSystemName");  // NOI18N
                case UNAME_COLUMN:
                    return Bundle.getMessage("ColumnUserName");  // NOI18N
                case BUTTON_COLUMN:
                    return ""; // no label
                default:
                    return "";
            }
        }

        @edu.umd.cs.findbugs.annotations.SuppressFBWarnings(value = "DB_DUPLICATE_SWITCH_CLAUSES",
                justification = "better to keep cases in column order rather than to combine")
        public int getPreferredWidth(int col) {
            switch (col) {
                case SNAME_COLUMN:
                    return new JTextField(6).getPreferredSize().width;
                case UNAME_COLUMN:
                    return new JTextField(17).getPreferredSize().width;
                case BUTTON_COLUMN:
                    return new JTextField(6).getPreferredSize().width;
                default:
                    return new JTextField(5).getPreferredSize().width;
            }
        }

        @Override
        public Object getValueAt(int r, int col) {
            int rx = r;
            if ((rx > _numConditionalNGs) || (_curLogixNG == null)) {
                return null;
            }
            switch (col) {
                case BUTTON_COLUMN:
                    if (!_inReorderMode) {
                        return Bundle.getMessage("ButtonEdit");  // NOI18N
                    } else if (_nextInOrder == 0) {
                        return Bundle.getMessage("ButtonFirst");  // NOI18N
                    } else if (_nextInOrder <= r) {
                        return Bundle.getMessage("ButtonNext");  // NOI18N
                    } else {
                        return Integer.toString(rx + 1);
                    }
                case SNAME_COLUMN:
                    return _curLogixNG.getConditionalNG(rx);
                case UNAME_COLUMN: {
                    //log.debug("ConditionalNGTableModel: {}", _curLogixNG.getConditionalNGByNumberOrder(rx));  // NOI18N
                    ConditionalNG c = _curLogixNG.getConditionalNG(rx);
                    if (c != null) {
                        return c.getUserName();
                    }
                    return "";
                }
                default:
                    return Bundle.getMessage("BeanStateUnknown");  // NOI18N
            }
        }

        @Override
        public void setValueAt(Object value, int row, int col) {
            int rx = row;
            if ((rx > _numConditionalNGs) || (_curLogixNG == null)) {
                return;
            }
            if (col == BUTTON_COLUMN) {
                if (_inReorderMode) {
                    swapConditionalNG(row);
                } else {
                    // Use separate Runnable so window is created on top
                    class WindowMaker implements Runnable {

                        int row;

                        WindowMaker(int r) {
                            row = r;
                        }

                        @Override
                        public void run() {
                            editConditionalNGPressed(row);
                        }
                    }
                    WindowMaker t = new WindowMaker(rx);
                    javax.swing.SwingUtilities.invokeLater(t);
                }
            } else if (col == UNAME_COLUMN) {
                String uName = (String) value;
                ConditionalNG cn = _curLogixNG.getConditionalNGByUserName(uName);
                if (cn == null) {
                    ConditionalNG cdl = _curLogixNG.getConditionalNG(rx);
                    cdl.setUserName(uName.trim()); // N11N
                    fireTableRowsUpdated(rx, rx);
/*
                    // Update any conditional references
                    ArrayList<String> refList = InstanceManager.getDefault(jmri.ConditionalNGManager.class).getWhereUsed(sName);
                    if (refList != null) {
                        for (String ref : refList) {
                            ConditionalNG cRef = _conditionalManager.getBySystemName(ref);
                            List<ConditionalNGVariable> varList = cRef.getCopyOfStateVariables();
                            for (ConditionalNGVariable var : varList) {
                                // Find the affected conditional variable
                                if (var.getName().equals(sName)) {
                                    if (uName.length() > 0) {
                                        var.setGuiName(uName);
                                    } else {
                                        var.setGuiName(sName);
                                    }
                                }
                            }
                            cRef.setStateVariables(varList);
                        }
                    }
*/
                } else {
                    // Duplicate user name
                    if (cn != _curLogixNG.getConditionalNG(rx)) {
                        messageDuplicateConditionalNGUserName(cn.getSystemName());
                    }
                }
            }
        }
    }
    
    /**
     * Send a duplicate Conditional user name message for Edit Logix pane.
     *
     * @param svName proposed name that duplicates an existing name
     */
    void messageDuplicateConditionalNGUserName(String svName) {
        JOptionPane.showMessageDialog(null,
                Bundle.getMessage("Error30", svName),
                Bundle.getMessage("ErrorTitle"), // NOI18N
                JOptionPane.ERROR_MESSAGE);
    }
    
    protected String getClassName() {
        return LogixNGEditor.class.getName();
    }
    
    
    // ------------ LogixNG Notifications ------------
    // The ConditionalNG views support some direct changes to the parent logix.
    // This custom event is used to notify the parent LogixNG that changes are requested.
    // When the event occurs, the parent LogixNG can retrieve the necessary information
    // to carry out the actions.
    //
    // 1) Notify the calling LogixNG that the LogixNG user name has been changed.
    // 2) Notify the calling LogixNG that the conditional view is closing
    // 3) Notify the calling LogixNG that it is to be deleted
    /**
     * Create a custom listener event.
     */
    public interface LogixNGEventListener extends EventListener {

        void logixNGEventOccurred();
    }
    
    /**
     * Maintain a list of listeners -- normally only one.
     */
    List<LogixNGEventListener> listenerList = new ArrayList<>();
    
    /**
     * This contains a list of commands to be processed by the listener
     * recipient.
     */
    public HashMap<String, String> logixData = new HashMap<>();
    
    /**
     * Add a listener.
     *
     * @param listener The recipient
     */
    public void addLogixNGEventListener(LogixNGEventListener listener) {
        listenerList.add(listener);
    }
    
    /**
     * Remove a listener -- not used.
     *
     * @param listener The recipient
     */
    public void removeLogixNGEventListener(LogixNGEventListener listener) {
        listenerList.remove(listener);
    }
    
    /**
     * Notify the listeners to check for new data.
     */
    void fireLogixNGEvent() {
        for (LogixNGEventListener l : listenerList) {
            l.logixNGEventOccurred();
        }
    }
    
    
    private final static Logger log = LoggerFactory.getLogger(LogixNGEditor.class);

}
