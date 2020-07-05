package jmri.jmrix.bachrus;

import java.util.ResourceBundle;
import javax.swing.JMenu;

import org.apiguardian.api.API;
import static org.apiguardian.api.API.Status.*;

/**
 * Create a "Systems" menu containing the bachrus-specific tools.
 *
 * @author Andrew Crosland Copyright 2010
 */
@API(status = EXPERIMENTAL)
public class SpeedoMenu extends JMenu {

    public SpeedoMenu(String name, SpeedoSystemConnectionMemo memo) {
        this(memo);
        setText(name);
    }

    public SpeedoMenu(SpeedoSystemConnectionMemo memo) {

        super();
        ResourceBundle rb = ResourceBundle.getBundle("jmri.jmrix.JmrixSystemsBundle");

        if (memo != null) {
            setText(memo.getUserName());
        } else {
            setText("Speedo");
        }

        if (memo != null) {
            add(new jmri.jmrix.bachrus.SpeedoConsoleAction(rb.getString("MenuItemSpeedo"), memo));
        }
    }

}
