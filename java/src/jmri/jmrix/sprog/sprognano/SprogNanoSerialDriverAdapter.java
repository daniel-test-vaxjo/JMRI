package jmri.jmrix.sprog.sprognano;

import jmri.jmrix.sprog.SprogConstants.SprogMode;

import org.apiguardian.api.API;
import static org.apiguardian.api.API.Status.*;

/**
 * Implements SerialPortAdapter for the Sprog system.
 * <p>
 * This connects an SSPROG DCC SPROG Nano command station via a USB virtual 
 * serial com port.
 * <p>
 * The current implementation only handles the 9,600 baud rate, and does not use
 * any other options at configuration time.
 *
 * @author Andrew Crosland Copyright (C) 2016
 */
@API(status = EXPERIMENTAL)
public class SprogNanoSerialDriverAdapter
        extends jmri.jmrix.sprog.serialdriver.SerialDriverAdapter {

    public SprogNanoSerialDriverAdapter() {
        super(SprogMode.OPS);
        options.put("TrackPowerState", new Option(Bundle.getMessage("OptionTrackPowerLabel"),
                new String[]{Bundle.getMessage("PowerStateOff"), Bundle.getMessage("PowerStateOn")},
                true)); // first element (TrackPowerState) NOI18N
        //Set the username to match name; once refactored to handle multiple connections or user setable names/prefixes then this can be removed
        this.getSystemConnectionMemo().setUserName(Bundle.getMessage("SprogNanoCSTitle"));
    }
    
    // private final static Logger log = LoggerFactory.getLogger(SprogNanoSerialDriverAdapter.class);

}
