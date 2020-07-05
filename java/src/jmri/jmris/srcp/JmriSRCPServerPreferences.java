package jmri.jmris.srcp;

import org.apiguardian.api.API;
import static org.apiguardian.api.API.Status.*;

@API(status = EXPERIMENTAL)
public class JmriSRCPServerPreferences extends jmri.jmris.AbstractServerPreferences {

    public static final int DEFAULT_PORT = 2056;
    static final String XML_PREFS_ELEMENT = "SRCPServerPreferences"; // NOI18N
    static final String PORT = "port"; // NOI18N

    public JmriSRCPServerPreferences(String fileName) {
        super(fileName);
    }

    public JmriSRCPServerPreferences() {
        super();
    }

    @Override
    public int getDefaultPort() {
        return Integer.parseInt(Bundle.getMessage("JMRISRCPServerPort"));
    }

}
