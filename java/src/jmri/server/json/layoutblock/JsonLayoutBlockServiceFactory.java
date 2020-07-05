package jmri.server.json.layoutblock;

import org.apiguardian.api.API;
import static org.apiguardian.api.API.Status.*;
import static jmri.server.json.layoutblock.JsonLayoutBlock.LAYOUTBLOCK;
import static jmri.server.json.layoutblock.JsonLayoutBlock.LAYOUTBLOCKS;

import com.fasterxml.jackson.databind.ObjectMapper;
import jmri.server.json.JsonConnection;
import jmri.spi.JsonServiceFactory;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author mstevetodd Copyright (C) 2018
 * @author Randall Wood Copyright 2018
 */
@ServiceProvider(service = JsonServiceFactory.class)
@API(status = EXPERIMENTAL)
public class JsonLayoutBlockServiceFactory implements JsonServiceFactory<JsonLayoutBlockHttpService, JsonLayoutBlockSocketService> {


    @Override
    public String[] getTypes(String version) {
        return new String[]{LAYOUTBLOCK, LAYOUTBLOCKS};
    }

    @Override
    public JsonLayoutBlockSocketService getSocketService(JsonConnection connection, String version) {
        return new JsonLayoutBlockSocketService(connection);
    }

    @Override
    public JsonLayoutBlockHttpService getHttpService(ObjectMapper mapper, String version) {
        return new JsonLayoutBlockHttpService(mapper);
    }

}
