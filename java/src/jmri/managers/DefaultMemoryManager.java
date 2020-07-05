package jmri.managers;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import jmri.Memory;
import jmri.implementation.DefaultMemory;
import jmri.jmrix.internal.InternalSystemConnectionMemo;

import org.apiguardian.api.API;
import static org.apiguardian.api.API.Status.*;

/**
 * Provide the concrete implementation for the Internal Memory Manager.
 *
 * @author Bob Jacobsen Copyright (C) 2004
 */
@API(status = EXPERIMENTAL)
public class DefaultMemoryManager extends AbstractMemoryManager {

    public DefaultMemoryManager(InternalSystemConnectionMemo memo) {
        super(memo);
    }

    @Override
    @Nonnull
    protected Memory createNewMemory(@Nonnull String systemName, @CheckForNull String userName) {
        // makeSystemName validates that systemName is correct
        return new DefaultMemory(makeSystemName(systemName), userName);
    }

}
