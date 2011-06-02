package jmri.jmrit.beantable;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import java.util.ResourceBundle;

    /**
     * Table Action for dealing with all the tables in a single view
     * with a list option to the left hand side.
     * <P>
     * @author	Bob Jacobsen   Copyright (C) 2003
     * @author	Kevin Dickerson   Copyright (C) 2009
     * @version	$Revision: 1.6 $
     */

public class ListedTableAction extends AbstractAction {

    String gotoListItem = null;
    String title = rbean.getString("TitleListedTable");
    public static final ResourceBundle rbean = ResourceBundle.getBundle("jmri.jmrit.beantable.BeanTableBundle");
    /**
     * Create an action with a specific title.
     * <P>
     * Note that the argument is the Action title, not the title of the
     * resulting frame.  Perhaps this should be changed?
     * @param s
     */

   public ListedTableAction(String s, String selection) {
        super(s);
        title=s;
        gotoListItem = selection;
    }

    public ListedTableAction(String s, String selection, int x, int y, int divider) {
        super(s);
        title=s;
        gotoListItem = selection;
        frameOffSetx = x;
        frameOffSety = y;
        dividerLocation = divider;
    }
    
    public ListedTableAction(String s, int x, int y, int divider) {
        super(s);
        title=s;
        frameOffSetx = x;
        frameOffSety = y;
        dividerLocation = divider;
    }

   public ListedTableAction(String s) {
        super(s);
        title=s;
    }
    
    public ListedTableAction() { this(rbean.getString("TitleListedTable"));}
    
    ListedTableFrame f;
    int frameOffSetx=0;
    int frameOffSety=0;
    int dividerLocation=0;

    public void actionPerformed() {
        // create the JTable model, with changes for specific NamedBean
        // create the frame
        f = new ListedTableFrame(title){
        };
        addToFrame(f);
        
        f.gotoListItem(gotoListItem);
        f.pack();
        
        f.setDividerLocation(dividerLocation);
        f.setVisible(true);
    }
    
    public void actionPerformed(ActionEvent e) {
        actionPerformed();
    }

    public void addToFrame(ListedTableFrame f) {
    }
    
    String helpTarget() {
        return "package.jmri.jmrit.beantable.ListedTableAction";
    }
    
}