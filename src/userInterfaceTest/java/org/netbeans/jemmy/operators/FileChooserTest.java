/*
 * Copyright (c) 2017, 2018, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation. Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */
package org.netbeans.jemmy.operators;

import static org.assertj.core.api.Assertions.assertThat;

import java.awt.Component;
import java.awt.Container;
import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.UIManager;
import javax.swing.filechooser.FileSystemView;

import org.netbeans.jemmy.ComponentChooser;
import org.netbeans.jemmy.DumpOnFailure;
import org.netbeans.jemmy.LookAndFeelProvider;
import org.netbeans.jemmy.util.LookAndFeel;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

@ExtendWith(DumpOnFailure.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class FileChooserTest {

    private JFrameOperator frame;
    private JFileChooserOperator fileChooser;
    private File dir, file;

    @BeforeAll
    public void setup() throws IOException {
        dir = File.createTempFile("testDir", "");
        dir.delete(); dir.mkdirs();
        File.createTempFile("aestFile", ".txt", dir).deleteOnExit();
        file = File.createTempFile("testFile", ".txt", dir);
        file.deleteOnExit();
        File.createTempFile("zestFile", ".txt", dir).deleteOnExit();
    }

    // was a TestNG @BeforeMethod receiving the data-provider arguments;
    // JUnit has no such injection, so each test calls this helper itself
    private void showChooser(String lookAndFeel) throws Exception {
        UIManager.setLookAndFeel(lookAndFeel);
        FileChooserApp.show(dir);
        frame = new JFrameOperator("Sample File Chooser");
        fileChooser = new JFileChooserOperator(
                JFileChooserOperator.findJFileChooser((Container) frame.getSource()));
    }

    @AfterEach
    public void tearDown() {
        frame.setVisible(false);
        frame.dispose();
    }

    @ParameterizedTest
    @MethodSource("org.netbeans.jemmy.LookAndFeelProvider#availableLookAndFeels")
    public void testSelection(String lookAndFeel) throws Exception {
        showChooser(lookAndFeel);
        fileChooser.selectFile(file.getName());
        fileChooser.waitState(new ComponentChooser() {
            @Override
            public boolean checkComponent(Component comp) {
                return ((JFileChooser)comp).getSelectedFile() != null &&
                        fileChooser.getSelectedFile().getName().equals(file.getName());
            }

            @Override
            public String getDescription() {
                return "test file is selected";
            }
        });
    }

    @ParameterizedTest
    @MethodSource("org.netbeans.jemmy.LookAndFeelProvider#availableLookAndFeels")
    public void testCount(String lookAndFeel) throws Exception {
        showChooser(lookAndFeel);
        assertThat(fileChooser.getFileCount()).isGreaterThanOrEqualTo(3);
    }

    @ParameterizedTest
    @MethodSource("org.netbeans.jemmy.LookAndFeelProvider#availableLookAndFeels")
    public void testGoHome(String lookAndFeel) throws Exception {
        showChooser(lookAndFeel);
        // In Aqua, GTK and Motif L&Fs, JFileChooser does not have
        // "Go Home" button.
        if (!LookAndFeel.isAqua() && !LookAndFeel.isMotif() && !LookAndFeel.isGTK()) {
            File previousDirectory = fileChooser.getCurrentDirectory();
            fileChooser.goHome();
            fileChooser.waitState(chooser -> fileChooser.getCurrentDirectory().getPath().equals(
                    FileSystemView.getFileSystemView().getHomeDirectory().getPath()));
            fileChooser.setCurrentDirectory(previousDirectory);
            fileChooser.rescanCurrentDirectory();
        }
    }
}
