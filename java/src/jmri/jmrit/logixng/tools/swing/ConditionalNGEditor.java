package jmri.jmrit.logixng.tools.swing;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.swing.*;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.*;

import jmri.jmrit.logixng.FemaleSocket;
import jmri.InstanceManager;
import jmri.Manager;
import jmri.NamedBean;
import jmri.UserPreferencesManager;
import jmri.jmrit.logixng.*;
import jmri.jmrit.logixng.swing.SwingConfiguratorInterface;
import jmri.jmrit.logixng.swing.SwingTools;

/**
 * Editor of ConditionalNG
 * 
 * @author Daniel Bergqvist 2018
 */
public class ConditionalNGEditor extends TreeViewer {

    protected final ConditionalNG _conditionalNG;
    
    ClipboardEditor _clipboardEditor = null;
    
    // Add ConditionalNG Variables
    private JDialog selectItemTypeDialog = null;
    private JDialog addItemDialog = null;
    private JDialog editConditionalNGDialog = null;
    private final JTextField _systemName = new JTextField(20);
    private final JTextField _addUserName = new JTextField(20);
    
    private final Comparator<SwingConfiguratorInterface> _swingConfiguratorComboBoxComparator
            = (SwingConfiguratorInterface o1, SwingConfiguratorInterface o2) -> o1.toString().compareTo(o2.toString());
    
    private final SortedComboBoxModel<SwingConfiguratorInterface> _swingConfiguratorComboBoxModel
            = new SortedComboBoxModel<>(_swingConfiguratorComboBoxComparator);
    
    private final JComboBox<Category> _categoryComboBox = new JComboBox<>();
    private final JComboBox<SwingConfiguratorInterface> _swingConfiguratorComboBox = new JComboBox<>(_swingConfiguratorComboBoxModel);
    private final JCheckBox _autoSystemName = new JCheckBox(Bundle.getMessage("LabelAutoSysName"));   // NOI18N
    private final JLabel _sysNameLabel = new JLabel(Bundle.getMessage("SystemName") + ":");  // NOI18N
    private final JLabel _userNameLabel = new JLabel(Bundle.getMessage("UserName") + ":");   // NOI18N
    private final String systemNameAuto = this.getClass().getName() + ".AutoSystemName";     // NOI18N
    private final JLabel _categoryLabel = new JLabel(Bundle.getMessage("Category") + ":");   // NOI18N
    private final JLabel _typeLabel = new JLabel(Bundle.getMessage("Type") + ":");   // NOI18N
//    private Class maleSocketClass = null;
    private JButton create;
    private JButton edit;
    boolean _showReminder = false;
    
//    private Map<Category, List<Class<? extends Base>>> connectableClasses;
    
    private SwingConfiguratorInterface addSwingConfiguratorInterface;
    private SwingConfiguratorInterface editSwingConfiguratorInterface;
    
    // Edit ConditionalNG Variables
    private boolean _inEditMode = false;
    
    
    /**
     * Maintain a list of listeners -- normally only one.
     */
    private final List<LogixNGEventListener> listenerList = new ArrayList<>();
    
    /**
     * This contains a list of commands to be processed by the listener
     * recipient.
     */
    final HashMap<String, String> logixNGData = new HashMap<>();
    
    /**
     * Construct a ConditionalEditor.
     * <p>
     * This is used by JmriUserPreferencesManager since it tries to create an
     * instance of this class.
     */
    public ConditionalNGEditor() {
        super(null);
        this._conditionalNG = null;
    }
    
    /**
     * Construct a ConditionalEditor.
     *
     * @param conditionalNG the ConditionalNG to be edited
     */
    public ConditionalNGEditor(@Nonnull ConditionalNG conditionalNG) {
        super(conditionalNG.getFemaleSocket());
        
        this._conditionalNG = conditionalNG;
        
        if (_conditionalNG.getUserName() == null) {
            setTitle(Bundle.getMessage("TitleEditConditionalNG", _conditionalNG.getSystemName()));
        } else {
            setTitle(Bundle.getMessage("TitleEditConditionalNG2", _conditionalNG.getSystemName(), _conditionalNG.getUserName()));
        }
    }
    
    @Override
    public void initComponents() {
        super.initComponents();
        
        // The menu is created in parent class TreeViewer
        JMenuBar menuBar = getJMenuBar();
        
        JMenu toolsMenu = new JMenu(Bundle.getMessage("MenuTools"));
        JMenuItem openClipboardItem = new JMenuItem(Bundle.getMessage("MenuOpenClipboard"));
        openClipboardItem.addActionListener((ActionEvent e) -> {
            openClipboard();
        });
        toolsMenu.add(openClipboardItem);
        menuBar.add(toolsMenu);
        
        
        PopupMenu popup = new PopupMenu(tree, femaleSocketTreeModel);
        popup.init();
        
        // The JTree can get big, so allow it to scroll
        JScrollPane scrollpane = new JScrollPane(tree);

        // create panel
        JPanel pPanel = new JPanel();
        pPanel.setLayout(new BoxLayout(pPanel, BoxLayout.Y_AXIS));
        
        // Display it all in a window and make the window appear
        pPanel.add(scrollpane, "Center");

        // add panels
        getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        getContentPane().add(pPanel);
        
//        initMinimumSize(new Dimension(panelWidth700, panelHeight500));
    }

    /** {@inheritDoc} */
    @Override
    public void windowClosed(WindowEvent e) {
        logixNGData.clear();
        logixNGData.put("Finish", _conditionalNG.getSystemName());  // NOI18N
        fireLogixNGEvent();
    }
    
    public void openClipboard() {
        if (_clipboardEditor == null) {
            _clipboardEditor = new ClipboardEditor();
            _clipboardEditor.initComponents();
            _clipboardEditor.setVisible(true);

            _clipboardEditor.addLogixNGEventListener(() -> {
                _clipboardEditor.clipboardData.forEach((key, value) -> {
                    if (key.equals("Finish")) {                  // NOI18N
                        _clipboardEditor = null;
                    }
                });
            });
        } else {
            _clipboardEditor.setVisible(true);
        }
    }

    public void addLogixNGEventListener(LogixNGEventListener listener) {
        listenerList.add(listener);
    }
    
    /**
     * Notify the listeners to check for new data.
     */
    void fireLogixNGEvent() {
        for (LogixNGEventListener l : listenerList) {
            l.logixNGEventOccurred();
        }
    }
    
    
    public interface LogixNGEventListener extends EventListener {
        
        public void logixNGEventOccurred();
    }
    
    
    /**
     * Check if another ConditionalNG editing session is currently open or no system
     * name is provided.
     *
     * @param sName system name of ConditionalNG to be copied
     * @return true if a new session may be started
     */
    boolean checkFlags(String sName) {
        if (_inEditMode) {
            // Already editing a ConditionalNG, ask for completion of that edit
            JOptionPane.showMessageDialog(null,
//                    Bundle.getMessage("ConditionalNGError32", _curConditionalNG.getSystemName()),
                    Bundle.getMessage("ConditionalNGError32", "aaa"),
                    Bundle.getMessage("ErrorTitle"),
                    JOptionPane.ERROR_MESSAGE);
            toFront();
//            if (_treeEdit != null) {
//                _treeEdit.toFront();
////                _treeEdit.bringToFront();
//            }
            return false;
        }
/*
        if (_inCopyMode) {
            // Already editing a ConditionalNG, ask for completion of that edit
            JOptionPane.showMessageDialog(null,
                    Bundle.getMessage("ConditionalNGError31", _logixNGSysName),
                    Bundle.getMessage("ErrorTitle"), // NOI18N
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
*/
/*
        if (sName != null) {
            // check if a LogixNG with this name exists
            LogixNG x = _logixNGManager.getBySystemName(sName);
            if (x == null) {
                // LogixNG does not exist, so cannot be edited
                log.error("No LogixNG with system name: " + sName);
                JOptionPane.showMessageDialog(null,
                        Bundle.getMessage("LogixNGError5"),
                        Bundle.getMessage("ErrorTitle"), // NOI18N
                        JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }
*/
        return true;
    }

    /**
     * Respond to the Add menu choice in the popup menu.
     *
     * @param femaleSocket the female socket
     * @param path the path to the item the user has clicked on
     */
    protected void addPressed(FemaleSocket femaleSocket, TreePath path) {
        
        Map<Category, List<Class<? extends Base>>> connectableClasses =
                femaleSocket.getConnectableClasses();
        
        _categoryComboBox.removeAllItems();
        List<Category> list = new ArrayList<>(connectableClasses.keySet());
        Collections.sort(list);
        for (Category item : list) {
            _categoryComboBox.addItem(item);
        }
        
        for (ItemListener l : _categoryComboBox.getItemListeners()) {
            _categoryComboBox.removeItemListener(l);
        }
        
        _categoryComboBox.addItemListener((ItemEvent e) -> {
            Category category = _categoryComboBox.getItemAt(_categoryComboBox.getSelectedIndex());
            _swingConfiguratorComboBox.removeAllItems();
            List<Class<? extends Base>> classes = connectableClasses.get(category);
            if (classes != null) {
                for (Class<? extends Base> clazz : classes) {
                    SwingConfiguratorInterface sci = SwingTools.getSwingConfiguratorForClass(clazz);
                    if (sci != null) {
                        _swingConfiguratorComboBox.addItem(sci);
                    } else {
                        log.error("Class {} has no swing configurator interface", clazz.getName());
                    }
                }
            }
        });
        
        // Ensure the type combo box gets updated
        _categoryComboBox.setSelectedIndex(-1);
        _categoryComboBox.setSelectedIndex(0);
        
        
        selectItemTypeDialog  = new JDialog(
                this,
                Bundle.getMessage(
                        "AddMaleSocketDialogTitle",
                        femaleSocket.getLongDescription()),
                true);
//        selectItemTypeFrame.addHelpMenu(
//                "package.jmri.jmrit.logixng.tools.swing.ConditionalNGAddEdit", true);     // NOI18N
        selectItemTypeDialog.setLocation(50, 30);
        Container contentPanel = selectItemTypeDialog.getContentPane();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

        JPanel p;
        p = new JPanel();
//        p.setLayout(new FlowLayout());
        p.setLayout(new java.awt.GridBagLayout());
        java.awt.GridBagConstraints c = new java.awt.GridBagConstraints();
        c.gridwidth = 1;
        c.gridheight = 1;
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = java.awt.GridBagConstraints.EAST;
        p.add(_categoryLabel, c);
        c.gridy = 1;
        p.add(_typeLabel, c);
        c.gridx = 1;
        c.gridy = 0;
        c.anchor = java.awt.GridBagConstraints.WEST;
        c.weightx = 1.0;
        c.fill = java.awt.GridBagConstraints.HORIZONTAL;  // text field will expand
        p.add(_categoryComboBox, c);
        c.gridy = 1;
        p.add(_swingConfiguratorComboBox, c);
        
        _categoryComboBox.setToolTipText(Bundle.getMessage("CategoryNamesHint"));    // NOI18N
        _swingConfiguratorComboBox.setToolTipText(Bundle.getMessage("TypeNamesHint"));   // NOI18N
        contentPanel.add(p);
        // set up message
        JPanel panel3 = new JPanel();
        panel3.setLayout(new BoxLayout(panel3, BoxLayout.Y_AXIS));
        
//        maleSocketClass = connectableClasses.get(Category.ITEM).get(0);
        
//        swingConfiguratorInterface = SwingTools.getSwingConfiguratorForClass(maleSocketClass);
        contentPanel.add(panel3);

        // set up create and cancel buttons
        JPanel panel5 = new JPanel();
        panel5.setLayout(new FlowLayout());
        // Cancel
        JButton cancel = new JButton(Bundle.getMessage("ButtonCancel"));    // NOI18N
        panel5.add(cancel);
        cancel.addActionListener((ActionEvent e) -> {
            cancelAddPressed(null);
        });
//        cancel.setToolTipText(Bundle.getMessage("CancelLogixButtonHint"));      // NOI18N
        cancel.setToolTipText("CancelLogixButtonHint");      // NOI18N

        selectItemTypeDialog.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                cancelAddPressed(null);
            }
        });

        create = new JButton(Bundle.getMessage("ButtonCreate"));  // NOI18N
        panel5.add(create);
        create.addActionListener((ActionEvent e) -> {
            cancelAddPressed(null);
            
            SwingConfiguratorInterface swingConfiguratorInterface =
                    _swingConfiguratorComboBox.getItemAt(_swingConfiguratorComboBox.getSelectedIndex());
//            System.err.format("swingConfiguratorInterface: %s%n", swingConfiguratorInterface.getClass().getName());
            createAddFrame(femaleSocket, path, swingConfiguratorInterface);
        });
        
        contentPanel.add(panel5);

        _autoSystemName.addItemListener((ItemEvent e) -> {
            autoSystemName();
        });
//        addLogixNGFrame.setLocationRelativeTo(component);
        selectItemTypeDialog.setLocationRelativeTo(null);
        selectItemTypeDialog.pack();
        selectItemTypeDialog.setVisible(true);
    }
    
    /**
     * Respond to the Add menu choice in the popup menu.
     *
     * @param femaleSocket the female socket
     * @param swingConfiguratorInterface the swing configurator used to configure the new class
     * @param path the path to the item the user has clicked on
     */
    protected void createAddFrame(FemaleSocket femaleSocket, TreePath path,
            SwingConfiguratorInterface swingConfiguratorInterface) {
        // possible change
        if (!checkFlags(null)) {
            return;
        }
        _showReminder = true;
        // make an Add Item Frame
        if (addItemDialog == null) {
            addSwingConfiguratorInterface = swingConfiguratorInterface;
            // Create ConditionalNG
            create = new JButton(Bundle.getMessage("ButtonCreate"));  // NOI18N
            create.addActionListener((ActionEvent e) -> {
                List<String> errorMessages = new ArrayList<>();
                
                boolean isValid = true;
                
                if (_systemName.getText().isEmpty() && _autoSystemName.isSelected()) {
                    _systemName.setText(addSwingConfiguratorInterface.getAutoSystemName());
                }
                
                if (addSwingConfiguratorInterface.getManager()
                        .validSystemNameFormat(_systemName.getText()) != Manager.NameValidity.VALID) {
                    isValid = false;
                    errorMessages.add(Bundle.getMessage("InvalidSystemName", _systemName.getText()));
                }
                
                isValid &= addSwingConfiguratorInterface.validate(errorMessages);
                
                if (isValid) {
                    MaleSocket socket;
                    if (_addUserName.getText().isEmpty()) {
                        socket = addSwingConfiguratorInterface.createNewObject(_systemName.getText(), null);
                    } else {
                        socket = addSwingConfiguratorInterface.createNewObject(_systemName.getText(), _addUserName.getText());
                    }
                    try {
                        femaleSocket.connect(socket);
                    } catch (SocketAlreadyConnectedException ex) {
                        throw new RuntimeException(ex);
                    }
                    addSwingConfiguratorInterface.dispose();
                    addItemDialog.dispose();
                    addItemDialog = null;
                    for (TreeModelListener l : femaleSocketTreeModel.listeners) {
                        TreeModelEvent tme = new TreeModelEvent(
                                femaleSocket,
                                path.getPath()
                        );
                        l.treeNodesChanged(tme);
                    }
                    tree.expandPath(path);
                    tree.updateUI();
                    InstanceManager.getOptionalDefault(UserPreferencesManager.class).ifPresent((prefMgr) -> {
                        prefMgr.setCheckboxPreferenceState(systemNameAuto, _autoSystemName.isSelected());
                    });
                } else {
                    StringBuilder errorMsg = new StringBuilder();
                    for (String s : errorMessages) {
                        if (errorMsg.length() > 0) errorMsg.append("<br>");
                        errorMsg.append(s);
                    }
                    JOptionPane.showMessageDialog(null,
                            Bundle.getMessage("ValidateErrorMessage", errorMsg),
                            Bundle.getMessage("ValidateErrorTitle"),
                            JOptionPane.ERROR_MESSAGE);
                }
            });
            create.setToolTipText(Bundle.getMessage("CreateButtonHint"));  // NOI18N
            
            makeAddEditFrame(true, "AddMessage", femaleSocket, addSwingConfiguratorInterface, create);  // NOI18N
        }
    }

    /**
     * Respond to the Edit menu choice in the popup menu.
     *
     * @param femaleSocket the female socket
     * @param path the path to the item the user has clicked on
     */
    protected void editPressed(FemaleSocket femaleSocket, TreePath path) {
        // possible change
        if (!checkFlags(null)) {
            return;
        }
        _showReminder = true;
        // make an Edit Frame
        if (editConditionalNGDialog == null) {
            editSwingConfiguratorInterface = SwingTools.getSwingConfiguratorForClass(femaleSocket.getConnectedSocket().getObject().getClass());
            // Edit ConditionalNG
            edit = new JButton(Bundle.getMessage("ButtonOK"));  // NOI18N
            edit.addActionListener((ActionEvent e) -> {
                List<String> errorMessages = new ArrayList<>();
                
                boolean isValid = true;
                
                if (editSwingConfiguratorInterface.getManager()
                        .validSystemNameFormat(_systemName.getText()) != Manager.NameValidity.VALID) {
                    isValid = false;
                    errorMessages.add(Bundle.getMessage("InvalidSystemName", _systemName.getText()));
                }
                
                isValid &= editSwingConfiguratorInterface.validate(errorMessages);
                
                if (isValid) {
                    Base object = femaleSocket.getConnectedSocket().getObject();
                    ((NamedBean)object).setUserName(_addUserName.getText());
                    editSwingConfiguratorInterface.updateObject(femaleSocket.getConnectedSocket().getObject());
                    editSwingConfiguratorInterface.dispose();
                    editConditionalNGDialog.dispose();
                    editConditionalNGDialog = null;
                    for (TreeModelListener l : femaleSocketTreeModel.listeners) {
                        TreeModelEvent tme = new TreeModelEvent(
                                femaleSocket,
                                path.getPath()
                        );
                        l.treeNodesChanged(tme);
                    }
                } else {
                    StringBuilder errorMsg = new StringBuilder();
                    for (String s : errorMessages) {
                        if (errorMsg.length() > 0) errorMsg.append("<br>");
                        errorMsg.append(s);
                    }
                    JOptionPane.showMessageDialog(null,
                            Bundle.getMessage("ValidateErrorMessage", errorMsg),
                            Bundle.getMessage("ValidateErrorTitle"),
                            JOptionPane.ERROR_MESSAGE);
                }
            });
            edit.setToolTipText(Bundle.getMessage("EditButtonHint"));  // NOI18N
            
            makeAddEditFrame(false, null, femaleSocket, editSwingConfiguratorInterface, edit);  // NOI18N
        }
    }

    /**
     * Create or edit action/expression dialog.
     *
     * @param addOrEdit true if add, false if edit
     * @param messageId part 1 of property key to fetch as user instruction on
     *                  pane, either 1 or 2 is added to form the whole key
     * @param femaleSocket the female socket to which we want to add something
     * @param swingConfiguratorInterface the swing interface to configure this item
     * @param button a button to add to the dialog
     */
    void makeAddEditFrame(boolean addOrEdit, String messageId,
            FemaleSocket femaleSocket,
            SwingConfiguratorInterface swingConfiguratorInterface,
            JButton button) {
        JDialog frame  = new JDialog(
                this,
                Bundle.getMessage(
                        addOrEdit ? "AddMaleSocketDialogTitle" : "EditMaleSocketDialogTitle",
                        femaleSocket.getLongDescription()),
                true);
//        frame.addHelpMenu(
//                "package.jmri.jmrit.logixng.tools.swing.ConditionalNGAddEdit", true);     // NOI18N
        Container contentPanel = frame.getContentPane();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

        JPanel p;
        p = new JPanel();
//        p.setLayout(new FlowLayout());
        p.setLayout(new java.awt.GridBagLayout());
        java.awt.GridBagConstraints c = new java.awt.GridBagConstraints();
        c.gridwidth = 1;
        c.gridheight = 1;
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = java.awt.GridBagConstraints.EAST;
        p.add(_sysNameLabel, c);
        c.gridy = 1;
        p.add(_userNameLabel, c);
        c.gridx = 1;
        c.gridy = 0;
        c.anchor = java.awt.GridBagConstraints.WEST;
        c.weightx = 1.0;
        c.fill = java.awt.GridBagConstraints.HORIZONTAL;  // text field will expand
        p.add(_systemName, c);
        c.gridy = 1;
        p.add(_addUserName, c);
        if (!femaleSocket.isConnected()) {
            c.gridx = 2;
            c.gridy = 1;
            c.anchor = java.awt.GridBagConstraints.WEST;
            c.weightx = 1.0;
            c.fill = java.awt.GridBagConstraints.HORIZONTAL;  // text field will expand
            c.gridy = 0;
            p.add(_autoSystemName, c);
        }
        
        if (femaleSocket.isConnected()) {
            _systemName.setText(femaleSocket.getConnectedSocket().getSystemName());
            _systemName.setEnabled(false);
            _addUserName.setText(femaleSocket.getConnectedSocket().getUserName());
        } else {
            _systemName.setText("");
            _systemName.setEnabled(true);
            _addUserName.setText("");
        }
        
        _addUserName.setToolTipText(Bundle.getMessage("UserNameHint"));
//        _addUserName.setToolTipText("LogixNGUserNameHint");    // NOI18N
        _systemName.setToolTipText(Bundle.getMessage("SystemNameHint",
                swingConfiguratorInterface.getExampleSystemName()));
//        _systemName.setToolTipText("LogixNGSystemNameHint");   // NOI18N
        contentPanel.add(p);
        // set up message
        JPanel panel3 = new JPanel();
        panel3.setLayout(new BoxLayout(panel3, BoxLayout.Y_AXIS));
        JPanel panel31 = new JPanel();
//        panel31.setLayout(new FlowLayout());
        JPanel panel32 = new JPanel();
        if (messageId != null) {
            JLabel message1 = new JLabel(Bundle.getMessage(messageId + "1"));  // NOI18N
            panel31.add(message1);
            JLabel message2 = new JLabel(Bundle.getMessage(messageId + "2"));  // NOI18N
            panel32.add(message2);
        }
        
        // set up create and cancel buttons
        JPanel panel5 = new JPanel();
        panel5.setLayout(new FlowLayout());
        
        // Get panel for the item
        JPanel panel33;
        if (femaleSocket.isConnected()) {
            panel33 = swingConfiguratorInterface.getConfigPanel(femaleSocket.getConnectedSocket().getObject(), panel5);
        } else {
            panel33 = swingConfiguratorInterface.getConfigPanel(panel5);
        }
        panel3.add(panel31);
        panel3.add(panel32);
        panel3.add(panel33);
        contentPanel.add(panel3);
        
        // Cancel
        JButton cancel = new JButton(Bundle.getMessage("ButtonCancel"));    // NOI18N
        panel5.add(cancel);
        cancel.addActionListener((ActionEvent e) -> {
            if (!femaleSocket.isConnected()) {
                cancelCreateItem(null);
            } else {
                cancelEditPressed(null);
            }
        });
//        cancel.setToolTipText(Bundle.getMessage("CancelLogixButtonHint"));      // NOI18N
        cancel.setToolTipText("CancelLogixButtonHint");      // NOI18N

        panel5.add(button);

        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                if (addOrEdit) {
                    cancelCreateItem(null);
                } else {
                    cancelEditPressed(null);
                }
            }
        });

        contentPanel.add(panel5);

        _autoSystemName.addItemListener((ItemEvent e) -> {
            autoSystemName();
        });
//        addLogixNGFrame.setLocationRelativeTo(component);
        frame.setLocationRelativeTo(null);
        frame.pack();
        
        if (addOrEdit) {
            addItemDialog = frame;
        } else {
            editConditionalNGDialog = frame;
        }
        
        _autoSystemName.setSelected(true);
        InstanceManager.getOptionalDefault(UserPreferencesManager.class).ifPresent((prefMgr) -> {
            _autoSystemName.setSelected(prefMgr.getCheckboxPreferenceState(systemNameAuto, true));
        });
        
        frame.setVisible(true);
    }
    
    /**
     * Enable/disable fields for data entry when user selects to have system
     * name automatically generated.
     */
    void autoSystemName() {
        if (_autoSystemName.isSelected()) {
            _systemName.setEnabled(false);
            _sysNameLabel.setEnabled(false);
        } else {
            _systemName.setEnabled(true);
            _sysNameLabel.setEnabled(true);
        }
    }

    /**
     * Respond to the Cancel button in Add ConditionalNG window.
     * <p>
     * Note: Also get there if the user closes the Add ConditionalNG window.
     *
     * @param e The event heard
     */
    void cancelAddPressed(ActionEvent e) {
        selectItemTypeDialog.setVisible(false);
        selectItemTypeDialog.dispose();
        selectItemTypeDialog = null;
//        _inCopyMode = false;
        this.setVisible(true);
    }
    /**
     * Respond to the Cancel button in Add ConditionalNG window.
     * <p>
     * Note: Also get there if the user closes the Add ConditionalNG window.
     *
     * @param e The event heard
     */
    void cancelCreateItem(ActionEvent e) {
        addItemDialog.setVisible(false);
        addSwingConfiguratorInterface.dispose();
        addItemDialog.dispose();
        addItemDialog = null;
//        _inCopyMode = false;
        this.setVisible(true);
    }


    /**
     * Respond to the Cancel button in Add ConditionalNG window.
     * <p>
     * Note: Also get there if the user closes the Add ConditionalNG window.
     *
     * @param e The event heard
     */
    void cancelEditPressed(ActionEvent e) {
        editConditionalNGDialog.setVisible(false);
        editSwingConfiguratorInterface.dispose();
        editConditionalNGDialog.dispose();
        editConditionalNGDialog = null;
//        _inCopyMode = false;
        this.setVisible(true);
    }
    
    
    
    private static final class SortedComboBoxModel<E> extends DefaultComboBoxModel<E> {

        private final Comparator<E> comparator;

        /*
         *  Create an empty model that will use the specified Comparator
         */
        public SortedComboBoxModel(@Nonnull Comparator<E> comparator) {
            super();
            this.comparator = comparator;
        }

        @Override
        public void addElement(E element) {
            insertElementAt(element, 0);
        }

        @SuppressWarnings("unchecked")
        @Override
        public void insertElementAt(E element, int index) {
            int size = getSize();

            //  Determine where to insert element to keep model in sorted order
            int i = 0;
            for (; i < size; i++) {
                E o = getElementAt(i);

                if (comparator.compare(o, element) > 0) {
                    break;
                }
            }

            super.insertElementAt(element, i);

            //  Select an element when it is added to the beginning of the model
            if (i == 0 && element != null) {
                setSelectedItem(element);
            }
        }
    }
    
    
    private final class PopupMenu extends JPopupMenu implements ActionListener {
        
        private static final String ACTION_COMMAND_ADD = "add";
        private static final String ACTION_COMMAND_REMOVE = "remove";
        private static final String ACTION_COMMAND_EDIT = "edit";
        private static final String ACTION_COMMAND_CUT = "cut";
        private static final String ACTION_COMMAND_COPY = "copy";
        private static final String ACTION_COMMAND_PASTE = "paste";
        private static final String ACTION_COMMAND_LOCK = "lock";
        private static final String ACTION_COMMAND_UNLOCK = "unlock";
        
        private final JTree _tree;
//        private final FemaleSocketTreeModel _model;
        private FemaleSocket _currentFemaleSocket;
        private TreePath _currentPath;
        
        private JMenuItem menuItemAdd;
        private JMenuItem menuItemRemove;
        private JMenuItem menuItemEdit;
        private JMenuItem menuItemCut;
        private JMenuItem menuItemCopy;
        private JMenuItem menuItemPaste;
        private JMenuItem menuItemLock;
        private JMenuItem menuItemUnlock;
        
        PopupMenu(JTree tree, FemaleSocketTreeModel model) {
            _tree = tree;
//            _model = model;
        }
        
        private void init() {
            menuItemAdd = new JMenuItem("Add");
            menuItemAdd.addActionListener(this);
            menuItemAdd.setActionCommand(ACTION_COMMAND_ADD);
            add(menuItemAdd);
            addSeparator();
            menuItemEdit = new JMenuItem("Edit");
            menuItemEdit.addActionListener(this);
            menuItemEdit.setActionCommand(ACTION_COMMAND_EDIT);
            add(menuItemEdit);
            menuItemRemove = new JMenuItem("Remove");
            menuItemRemove.addActionListener(this);
            menuItemRemove.setActionCommand(ACTION_COMMAND_REMOVE);
            add(menuItemRemove);
            addSeparator();
            menuItemCut = new JMenuItem("Cut");
            menuItemCut.addActionListener(this);
            menuItemCut.setActionCommand(ACTION_COMMAND_CUT);
            add(menuItemCut);
            menuItemCopy = new JMenuItem("Copy");
            menuItemCopy.addActionListener(this);
            menuItemCopy.setActionCommand(ACTION_COMMAND_COPY);
            add(menuItemCopy);
            menuItemPaste = new JMenuItem("Paste");
            menuItemPaste.addActionListener(this);
            menuItemPaste.setActionCommand(ACTION_COMMAND_PASTE);
            add(menuItemPaste);
            addSeparator();
            menuItemLock = new JMenuItem("Lock");
            menuItemLock.addActionListener(this);
            menuItemLock.setActionCommand(ACTION_COMMAND_LOCK);
            add(menuItemLock);
            menuItemUnlock = new JMenuItem("Unlock");
            menuItemUnlock.addActionListener(this);
            menuItemUnlock.setActionCommand(ACTION_COMMAND_UNLOCK);
            add(menuItemUnlock);
            setOpaque(true);
            setLightWeightPopupEnabled(true);
            
            final PopupMenu popupMenu = this;
            
            _tree.addMouseListener(
                    new MouseAdapter() {
                        
                        // On Windows, the popup is opened on mousePressed,
                        // on some other OS, the popup is opened on mouseReleased
                        
                        @Override
                        public void mousePressed(MouseEvent e) {
                            openPopupMenu(e);
                        }
                        
                        @Override
                        public void mouseReleased(MouseEvent e) {
                            openPopupMenu(e);
                        }
                        
                        private void openPopupMenu(MouseEvent e) {
                            if (e.isPopupTrigger() && !popupMenu.isVisible()) {
                                // Get the row the user has clicked on
                                TreePath path = _tree.getClosestPathForLocation(e.getX(), e.getY());
                                if (path != null) {
                                    // Check that the user has clicked on a row.
                                    Rectangle rect = _tree.getPathBounds(path);
                                    if ((e.getY() >= rect.y) && (e.getY() <= rect.y + rect.height)) {
                                        FemaleSocket femaleSocket = (FemaleSocket) path.getLastPathComponent();
                                        _tree.getLocationOnScreen();
                                        _tree.getX();
                                        showPopup(e.getX(), e.getY(), femaleSocket, path);
                                    }
                                }
                            }
                        }
                    }
            );
        }
        
        private void showPopup(int x, int y, FemaleSocket femaleSocket, TreePath path) {
            _currentFemaleSocket = femaleSocket;
            _currentPath = path;
            
            Clipboard clipboard = InstanceManager.getDefault(LogixNG_Manager.class).getClipboard();
            
            boolean isConnected = femaleSocket.isConnected();
            boolean canConnectFromClipboard =
                    !clipboard.isEmpty()
                    && femaleSocket.isCompatible(clipboard.getTopItem())
                    && !femaleSocket.isAncestor(clipboard.getTopItem());
            
            menuItemAdd.setEnabled(!isConnected);
            menuItemRemove.setEnabled(isConnected);
            menuItemEdit.setEnabled(isConnected);
            menuItemCut.setEnabled(isConnected);
            menuItemCopy.setEnabled(isConnected);
            menuItemPaste.setEnabled(!isConnected && canConnectFromClipboard);
            
            if (femaleSocket.isConnected()) {
                MaleSocket connectedSocket = femaleSocket.getConnectedSocket();
                menuItemLock.setEnabled(connectedSocket.getLock() == Base.Lock.NONE);
                menuItemUnlock.setEnabled(connectedSocket.getLock() == Base.Lock.USER_LOCK);
            } else {
                menuItemLock.setEnabled(false);
                menuItemUnlock.setEnabled(false);
            }
            show(_tree, x, y);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            switch (e.getActionCommand()) {
                case ACTION_COMMAND_ADD:
                    addPressed(_currentFemaleSocket, _currentPath);
                    break;
                    
                case ACTION_COMMAND_EDIT:
                    editPressed(_currentFemaleSocket, _currentPath);
                    break;
                    
                case ACTION_COMMAND_REMOVE:
                    break;
                    
                case ACTION_COMMAND_CUT:
                    if (_currentFemaleSocket.isConnected()) {
                        Clipboard clipboard =
                                InstanceManager.getDefault(LogixNG_Manager.class).getClipboard();
                        clipboard.add(_currentFemaleSocket.getConnectedSocket());
                        _currentFemaleSocket.disconnect();
                        updateTree();
                    } else {
                        log.error("_currentFemaleSocket is not connected");
                    }
                    break;
                    
                case ACTION_COMMAND_COPY:
                    break;
                    
                case ACTION_COMMAND_PASTE:
                    if (! _currentFemaleSocket.isConnected()) {
                        Clipboard clipboard =
                                InstanceManager.getDefault(LogixNG_Manager.class).getClipboard();
                        try {
                            _currentFemaleSocket.connect(clipboard.fetchTopItem());
                            updateTree();
                        } catch (SocketAlreadyConnectedException ex) {
                            log.error("item cannot be connected", ex);
                        }
                    } else {
                        log.error("_currentFemaleSocket is connected");
                    }
                    break;
                    
                case ACTION_COMMAND_LOCK:
                    break;
                    
                case ACTION_COMMAND_UNLOCK:
                    break;
                    
                default:
                    log.error("e.getActionCommand() returns unknown value {}", e.getActionCommand());
            }
        }
        
        private void updateTree() {
            for (TreeModelListener l : femaleSocketTreeModel.listeners) {
                TreeModelEvent tme = new TreeModelEvent(
                        _currentFemaleSocket,
                        _currentPath.getPath()
                );
                l.treeNodesChanged(tme);
            }
            tree.updateUI();
        }
    }
    
    
    private final static org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ConditionalNGEditor.class);

}
