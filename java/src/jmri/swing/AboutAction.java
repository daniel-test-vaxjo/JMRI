package jmri.swing;

import java.awt.event.ActionEvent;
import javax.swing.Icon;
import jmri.util.swing.JmriAbstractAction;
import jmri.util.swing.JmriPanel;
import jmri.util.swing.WindowInterface;

import org.apiguardian.api.API;
import static org.apiguardian.api.API.Status.*;

/**
 *
 * @author Randall Wood
 * @deprecated since 4.21.1; use {@link apps.swing.AboutAction} instead
 */
@Deprecated
@API(status = EXPERIMENTAL)
public class AboutAction extends JmriAbstractAction {

    public AboutAction(String s, WindowInterface wi) {
        super(s, wi);
    }

    public AboutAction(String s, Icon i, WindowInterface wi) {
        super(s, i, wi);
    }

    public AboutAction() {
        super("About");
    }

    @Override
    @SuppressWarnings("deprecation")
    public void actionPerformed(ActionEvent e) {
        new AboutDialog(null, true).setVisible(true);
    }

    // never invoked, because we overrode actionPerformed above
    @Override
    public JmriPanel makePanel() {
        throw new IllegalArgumentException("Should not be invoked");
    }
    //private static final Logger log = LoggerFactory.getLogger(AboutAction.class);
}
