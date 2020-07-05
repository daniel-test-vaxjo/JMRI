package jmri.jmrix.loconet.uhlenbrock;

import jmri.jmrix.loconet.LnProgrammerManager;
import jmri.jmrix.loconet.LocoNetSystemConnectionMemo;

import org.apiguardian.api.API;
import static org.apiguardian.api.API.Status.*;

/**
 * Extend LnProgrammerManager to disable on-the-track programming, which is not
 * supported by IB-COM or Intellibox II
 *
 * @see jmri.managers.DefaultProgrammerManager
 * @author Lisby Copyright (C) 2014
 * 
 */
@API(status = EXPERIMENTAL)
public class UhlenbrockProgrammerManager extends LnProgrammerManager {

    public UhlenbrockProgrammerManager(LocoNetSystemConnectionMemo memo) {
        super(memo);
    }

    @Override
    public boolean isAddressedModePossible() {
        return true;
    }
}
