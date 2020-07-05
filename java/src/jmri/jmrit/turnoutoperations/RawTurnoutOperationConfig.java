package jmri.jmrit.turnoutoperations;

import jmri.TurnoutOperation;

import org.apiguardian.api.API;
import static org.apiguardian.api.API.Status.*;

/**
 * Configuration for RawTurnoutOperation class All the work is done by the
 * Common... class Based on NoFeedbackTurnountOperationConfig.java
 *
 * @author Paul Bender Copyright 2008
 *
 */
@API(status = MAINTAINED)
public class RawTurnoutOperationConfig extends CommonTurnoutOperationConfig {
    /**
     * Create the config JPanel, if there is one, to configure this operation
     * type.
     * @param op turnout operation.
     */
    public RawTurnoutOperationConfig(TurnoutOperation op) {
        super(op);
    }
}
