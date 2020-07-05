package jmri.jmrit.display.layoutEditor;

import javax.swing.AbstractAction;
import jmri.jmrit.display.EditorActionFactory;
import org.apiguardian.api.API;
import static org.apiguardian.api.API.Status.*;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Randall Wood Copyright 2020
 */
@ServiceProvider(service = EditorActionFactory.class)
@API(status = MAINTAINED)
public final class LayoutEditorActionFactory implements EditorActionFactory {

    @Override
    public AbstractAction createAction() {
        return new LayoutEditorAction(getTitle());
    }

    @Override
    public String getTitle() {
        return Bundle.getMessage("LayoutEditor");
    }
    
}
