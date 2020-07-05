package jmri.jmrit.operations.trains.excel;

import org.apiguardian.api.API;
import static org.apiguardian.api.API.Status.*;

/**
 * A convenient place to access operations xml element and attribute names.
 *
 * @author Daniel Boudreau Copyright (C) 2013
 * 
 *
 */
@API(status = MAINTAINED)
public class Xml {

    private Xml(){
       // class of constants
    }

    // Common to operation xml files
    static final String NAME = "name"; // NOI18N

    // ManifestCreator.java
    static final String MANIFEST_CREATOR = "manifestCreator"; // NOI18N
    static final String RUN_FILE = "runFile"; // NOI18N
    static final String DIRECTORY = "directory"; // NOI18N
    static final String COMMON_FILE = "commonFile"; // NOI18N

    // SwitchListCreator
    static final String SWITCHLIST_CREATOR = "switchlistCreator"; // NOI18N
}
