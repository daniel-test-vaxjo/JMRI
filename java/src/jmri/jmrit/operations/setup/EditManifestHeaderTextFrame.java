package jmri.jmrit.operations.setup;

import jmri.jmrit.operations.OperationsFrame;

import org.apiguardian.api.API;
import static org.apiguardian.api.API.Status.*;

/**
 * Frame for user edit of manifest header text strings
 *
 * @author Dan Boudreau Copyright (C) 2014
 * 
 */
@API(status = MAINTAINED)
public class EditManifestHeaderTextFrame extends OperationsFrame {

    public EditManifestHeaderTextFrame() {
        super(Bundle.getMessage("TitleManifestHeaderText"), new EditManifestHeaderTextPanel());
    }

    @Override
    public void initComponents() {
        super.initComponents();

        // build menu
        addHelpMenu("package.jmri.jmrit.operations.Operations_ManifestPrintOptionsTools", true); // NOI18N

        initMinimumSize();
    }
}
