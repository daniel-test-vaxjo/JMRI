package jmri.server.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.NullNode;

import jmri.InstanceManager;
import jmri.Turnout;
import jmri.TurnoutManager;
import jmri.server.json.turnout.JsonTurnout;
import jmri.server.json.turnout.JsonTurnoutHttpService;
import jmri.util.JUnitUtil;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Assert;
import org.junit.jupiter.api.*;

/**
 * Test handling of null, or non-existent Named Beans. Testing of existent, or
 * non-null Named Beans is covered elsewhere.
 *
 * @author Randall Wood Copyright 2018
 */
public class JsonNamedBeanHttpServiceTest extends JsonNamedBeanHttpServiceTestBase<Turnout, JsonNamedBeanHttpService<Turnout>> {

    public JsonNamedBeanHttpServiceTest() {
    }

    @BeforeEach
    @Override
    public void setUp() throws Exception {
        super.setUp();
        service = new JsonTurnoutHttpService(mapper);
        JUnitUtil.initInternalTurnoutManager();
    }

    @AfterEach
    @Override
    public void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test of doGet method, of class JsonNamedBeanHttpService with
     * invalid NamedBean types. Uses a JsonTurnoutHttpService since the
     * JsonNamedBeanHttpService is abstract. This only tests the error
     * cases since subclasses test successful cases.
     *
     * @throws java.lang.Exception on unexpected exceptions
     */
    @Test
    @Override
    public void testDoGet() throws Exception {
        String name = "non-existant";
        String type = "non-existant";
        try {
            service.doGet(type, name, service.getObjectMapper().createObjectNode(), new JsonRequest(locale, JSON.V5, JSON.GET, 42));
            Assert.fail("Expected JsonException not thrown.");
        } catch (JsonException ex) {
            this.validate(ex.getJsonMessage());
            Assert.assertEquals("Error code is HTTP \"internal error\"", 500, ex.getCode());
            Assert.assertEquals("Error message is HTTP \"not found\"", "There was an error; see the JMRI application logs for details.", ex.getLocalizedMessage());
            Assert.assertEquals("Message Id", 42, ex.getId());
        }
    }

    /**
     * Test of getNamedBean method, of class JsonNamedBeanHttpService with
     * invalid NamedBean types. Uses a JsonTurnoutHttpService since the
     * JsonNamedBeanHttpService is abstract.
     *
     * @throws java.lang.Exception on unexpected exceptions
     */
    @Test
    @Override
    public void testGetNamedBean() throws Exception {
        String name = "non-existant";
        String type = "non-existant";
        try {
            service.getNamedBean(bean, name, type, new JsonRequest(locale, JSON.V5, JSON.GET, 0));
            Assert.fail("Expected JsonException not thrown.");
        } catch (JsonException ex) {
            this.validate(ex.getJsonMessage());
            Assert.assertEquals("Error code is HTTP \"not found\"", 404, ex.getCode());
            Assert.assertEquals("Error message is HTTP \"not found\"", "Object type non-existant named \"non-existant\" not found.", ex.getLocalizedMessage());
            Assert.assertEquals("Message Id", 0, ex.getId());
        }
    }

    /**
     * Test of getNamedBean method, of class JsonNamedBeanHttpService with a
     * turnout with some property values. Uses a JsonTurnoutHttpService since
     * the JsonNamedBeanHttpService is abstract.
     *
     * @throws java.lang.Exception on unexpected exceptions
     */
    @Test
    public void testGetNamedBeanWithProperties() throws Exception {
        String name = "IT1";
        // retain turnout as NamedBean to ensure only "generic" NamedBean
        // methods are used
        bean = InstanceManager.getDefault(TurnoutManager.class).provide(name);
        bean.setUserName("Turnout 1");
        bean.setComment("Turnout Comment");
        bean.setProperty("foo", "bar");
        bean.setProperty("bar", null);
        JsonNode root = service.getNamedBean(bean, name, JsonTurnout.TURNOUT, new JsonRequest(locale, JSON.V5, JSON.GET, 42));
        JsonNode data = root.path(JSON.DATA);
        Assert.assertEquals("Correct system name", bean.getSystemName(), data.path(JSON.NAME).asText());
        Assert.assertEquals("Correct user name", bean.getUserName(), data.path(JSON.USERNAME).asText());
        Assert.assertEquals("Correct comment", bean.getComment(), data.path(JSON.COMMENT).asText());
        Assert.assertTrue("Has properties", data.path(JSON.PROPERTIES).isArray());
        Assert.assertEquals("Has 2 properties", 2, data.path(JSON.PROPERTIES).size());
        Assert.assertEquals("Message ID", 42, root.path(JSON.ID).asInt());
        data.path(JSON.PROPERTIES).fields().forEachRemaining((property) ->{
            switch (property.getKey()) {
                case "foo":
                    Assert.assertEquals("Foo value", "bar", property.getValue().asText());
                    break;
                case "bar":
                    Assert.assertTrue("Bar is null", property.getValue().isNull());
                    break;
                default:
                    Assert.fail("Unexpected property present.");
            }
        });
    }

    /**
     * Test of postNamedBean method, of class JsonNamedBeanHttpService with
     * invalid NamedBean types. Uses a JsonTurnoutHttpService since the
     * JsonNamedBeanHttpService is abstract.
     *
     * @throws java.lang.Exception on unexpected exceptions
     */
    @Test
    @Override
    public void testPostNamedBean() throws Exception {
        String name = "non-existant";
        String type = "non-existant";
        try {
            service.postNamedBean(bean, this.mapper.createObjectNode(), name, type, new JsonRequest(locale, JSON.V5, JSON.POST, 42));
            Assert.fail("Expected JsonException not thrown.");
        } catch (JsonException ex) {
            this.validate(ex.getJsonMessage());
            Assert.assertEquals("Error code is HTTP \"not found\"", 404, ex.getCode());
            Assert.assertEquals("Error message is HTTP \"not found\"", "Object type non-existant named \"non-existant\" not found.", ex.getLocalizedMessage());
            Assert.assertEquals("Message Id", 42, ex.getId());
        }
    }
    
    @Test
    @Override
    public void testDoDelete() {
        try {
            service.doDelete(service.getType(), "non-existant", NullNode.getInstance(), new JsonRequest(locale, JSON.V5, JSON.DELETE, 42));
            fail("Expected exception not thrown.");
        } catch (JsonException ex) {
            assertEquals("Code is HTTP NOT FOUND", 404, ex.getCode());
            assertEquals("Error message is HTTP \"not found\"", "Object type turnout named \"non-existant\" not found.", ex.getLocalizedMessage());
            assertEquals("ID is 42", 42, ex.getId());
        }
    }
}
