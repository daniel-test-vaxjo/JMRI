package jmri.jmris.json;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import org.apiguardian.api.API;
import static org.apiguardian.api.API.Status.*;

/**
 * @author Randall Wood Copyright (C) 2012, 2015
 * @deprecated since 4.19.4; use
 *             {@link jmri.server.json.JsonServerPreferencesPanel} instead
 */
@Deprecated
@SuppressFBWarnings(value = "NM_SAME_SIMPLE_NAME_AS_SUPERCLASS", justification = "Deprecated for replacement.")
@API(status = EXPERIMENTAL)
public class JsonServerPreferencesPanel extends jmri.server.json.JsonServerPreferencesPanel {
}
