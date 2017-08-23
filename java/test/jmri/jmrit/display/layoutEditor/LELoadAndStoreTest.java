package jmri.jmrit.display.layoutEditor;

import static jmri.configurexml.LoadAndStoreTestBase.getFiles;

import java.io.File;
import jmri.configurexml.LoadAndStoreTestBase;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

/**
 * Test that configuration files can be read and then stored again consistently.
 * When done across various versions of schema, this checks ability to read
 * older files in newer versions; completeness of reading code; etc.
 * <p>
 * Functional checks, that e.g. check the details of a specific type are being
 * read properly, should go into another type-specific test class.
 * <p>
 * The functionality comes from the common base class, this is just here to
 * insert the test suite into the JUnit hierarchy at the right place.
 *
 * @author Bob Jacobsen Copyright 2009, 2014
 * @since 2.5.5 (renamed & reworked in 3.9 series)
 */
@RunWith(Parameterized.class)
public class LELoadAndStoreTest extends LoadAndStoreTestBase {

    @Parameterized.Parameters(name = "{0} (pass={1} (saveType={2}))")
    public static Iterable<Object[]> data() {
        return getFiles(new File("java/test/jmri/jmrit/display/layoutEditor"), false, true);
    }

    public LELoadAndStoreTest(File inFile, boolean inPass) {
        super(inFile, inPass, SaveType.User);
    }
}
