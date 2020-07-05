package jmri.jmrix.oaktree;

import java.util.Locale;
import javax.annotation.Nonnull;
import jmri.Light;
import jmri.managers.AbstractLightManager;
import org.apiguardian.api.API;
import static org.apiguardian.api.API.Status.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implement LightManager for Oak Tree serial systems.
 * <p>
 * System names are "OLnnn", where O is the user configurable system prefix,
 * nnn is the bit number without padding.
 * <p>
 * Based in part on SerialTurnoutManager.java
 *
 * @author Dave Duchamp Copyright (C) 2004
 * @author Bob Jacobsen Copyright (C) 2006
 */
@API(status = EXPERIMENTAL)
public class SerialLightManager extends AbstractLightManager {

    public SerialLightManager(OakTreeSystemConnectionMemo memo) {
        super(memo);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public OakTreeSystemConnectionMemo getMemo() {
        return (OakTreeSystemConnectionMemo) memo;
    }

    /**
     * Create a new Light based on the system name.
     * Assumes calling method has checked that a Light with this system name
     * does not already exist.
     *
     * @return null if the system name is not in a valid format or if the
     * system name does not correspond to a configured OakTree digital output bit
     */
    @Override
    public Light createNewLight(@Nonnull String systemName, String userName) {
        Light lgt = null;
        // Validate the systemName
        if (SerialAddress.validSystemNameFormat(systemName, 'L', getSystemPrefix()) == NameValidity.VALID) {
            lgt = new SerialLight(systemName, userName, getMemo());
            if (!SerialAddress.validSystemNameConfig(systemName, 'L', getMemo())) {
                log.warn("Light system Name does not refer to configured hardware: {}", systemName);
            }
        } else {
            log.error("Invalid Light system Name format: {}", systemName);
        }
        return lgt;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public String validateSystemNameFormat(@Nonnull String systemName, @Nonnull Locale locale) {
        return SerialAddress.validateSystemNameFormat(systemName, getSystemNamePrefix(), locale);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NameValidity validSystemNameFormat(@Nonnull String systemName) {
        return (SerialAddress.validSystemNameFormat(systemName, typeLetter(), getSystemPrefix()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean validSystemNameConfig(@Nonnull String systemName) {
        return (SerialAddress.validSystemNameConfig(systemName, typeLetter(), getMemo()));
    }

    /**
     * Convert system name to its alternate format.
     *
     * @return a normalized system name if system name is valid and has a valid
     * alternate representation, else return ""
     */
    @Override
    @Nonnull
    public String convertSystemNameToAlternate(@Nonnull String systemName) {
        return (SerialAddress.convertSystemNameToAlternate(systemName, getSystemPrefix()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getEntryToolTip() {
        return Bundle.getMessage("AddOutputEntryToolTip");
    }

    private final static Logger log = LoggerFactory.getLogger(SerialLightManager.class);

}
