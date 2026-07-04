package org.netbeans.jemmy;

import java.io.File;
import java.io.FileNotFoundException;

import javax.swing.UIManager;

import org.netbeans.jemmy.util.Dumper;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;

/**
 * Dumps the whole UI hierarchy to an XML file when a test fails, replacing
 * the TestNG {@code @AfterMethod(ITestResult)} idiom used before the JUnit 5
 * migration.
 */
public class DumpOnFailure implements TestWatcher {

    @Override
    public void testFailed(ExtensionContext context, Throwable cause) {
        try {
            Dumper.dumpAll(new File(UIManager.getLookAndFeel().getClass().getSimpleName()
                    + "_" + context.getRequiredTestMethod().getName() + "-dump.xml").getAbsolutePath());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
