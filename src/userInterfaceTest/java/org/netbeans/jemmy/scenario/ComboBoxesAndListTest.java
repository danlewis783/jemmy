/*
 * Copyright (c) 1997, 2017, Oracle and/or its affiliates. All rights reserved.
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
package org.netbeans.jemmy.scenario;

import org.netbeans.jemmy.ComponentSearcher;
import org.netbeans.jemmy.operators.*;
import org.netbeans.jemmy.util.NameComponentChooser;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.fail;

// testEditableSelection, testEditable and testNonEditable used to declare
// dependsOnMethods = "testEditableLookup" under TestNG; ordering keeps that
// guarantee.
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ComboBoxesAndListTest {

    private static JFrame win;
    private static Logger logger = Logger.getLogger(ComboBoxesAndListTest.class.getName());
    private static JFrameOperator fo;
    private static JScrollPaneOperator scroller;

    @BeforeAll
    public static void setup() throws InvocationTargetException, NoSuchMethodException, IOException {
        Util.testInfraSetup();
        new ComboBoxesAndListApp().display();
        win = JFrameOperator.waitJFrame("ComboBoxesAndListTest", true, true);
        fo = new JFrameOperator(win);
        scroller = new JScrollPaneOperator(JScrollPaneOperator.
                findJScrollPane(win,ComponentSearcher.getTrueChooser("Scroll pane")));
    }

    @Test
    @Order(1)
    public void testWindowLookup() {
        JFrameOperator fo2 = new JFrameOperator();
        FrameOperator fo3 = new FrameOperator();
        if (fo2.getSource() != fo.getSource() ||
                fo3.getSource() != fo.getSource()) {
            logger.severe("Wrong component found:");
            logger.severe(String.valueOf(fo.getSource()));
            logger.severe(String.valueOf(fo2.getSource()));
            logger.severe(String.valueOf(fo3.getSource()));
            fail();
        }

        Window window = new ComponentOperator(win).getWindow();
        assertSame(win, window);
    }
    @Test
    @Order(2)
    public void testEditableLookup() {
        logger.severe("test");
        JComboBoxOperator operator_1 = new JComboBoxOperator(JComboBoxOperator.
                findJComboBox(win,
                        "editable_one",
                        true, true,
                        0));
        JComboBoxOperator operator_10 = new JComboBoxOperator(fo);
        JComboBoxOperator operator_11 = new JComboBoxOperator(fo, "editable_one");
        JComboBoxOperator operator_12 = new JComboBoxOperator(fo, new NameComponentChooser("editable"));
        if(operator_10.getSource() != operator_1.getSource() ||
                operator_11.getSource() != operator_1.getSource() ||
                operator_12.getSource() != operator_1.getSource()) {
            logger.severe("Wrong component found:");
            logger.severe(String.valueOf(operator_1.getSource()));
            logger.severe(String.valueOf(operator_10.getSource()));
            logger.severe(String.valueOf(operator_11.getSource()));
            logger.severe(String.valueOf(operator_12.getSource()));
            fail();
        }
        assertEquals(4, operator_1.getItemCount(), "item count");
    }

    @Test
    @Order(3)
    public void testListLookup() {
        JComboBoxOperator operator_2 = new JComboBoxOperator(JComboBoxOperator.
                findJComboBox(win,
                        "non_editable_one",
                        true, true,
                        0));

        JListOperator lo0 = new JListOperator(fo);
        lo0.clickOnItem("two");
        JListOperator lo1 = new JListOperator(fo, "two", 1, 0);
        JListOperator lo2 = new JListOperator(fo, "two");
        JListOperator lo3 = new JListOperator(fo, new NameComponentChooser("list"));
        if(lo1.getSource() != lo0.getSource() ||
                lo2.getSource() != lo0.getSource() ||
                lo3.getSource() != lo0.getSource()) {
            logger.severe("Wrong component found:");
            logger.severe(String.valueOf(lo0.getSource()));
            logger.severe(String.valueOf(lo1.getSource()));
            logger.severe(String.valueOf(lo2.getSource()));
            logger.severe(String.valueOf(lo3.getSource()));
            fail();
        }
    }

    @Test
    @Order(4)
    public void testEditableSelection() {
        scroller.scrollToTop();

        JComboBoxOperator operator_1 = new JComboBoxOperator(fo);

        operator_1.selectItem("editable_two", true, true);
        operator_1.waitItemSelected("editable_two");

        JComboBoxOperator.waitJComboBox(win, "editable_two", true, true, -1);

        assertEquals(1, operator_1.getSelectedIndex(), "getSelectedIndex");
        assertEquals("editable_two", operator_1.getSelectedItem(), "getSelectedItem");
        assertEquals("editable_two", operator_1.getItemAt(1), "getItemAt(1)");
    }

    @Test
    @Order(5)
    public void testEditable() {
        scroller.scrollToTop();

        JComboBoxOperator operator_1 = new JComboBoxOperator(fo);

        operator_1.clearText();

        JTextFieldOperator.waitJTextField(win, "", true, true);

        operator_1.typeText("editable_old");
        JTextFieldOperator.waitJTextField(win, "editable_old", true, true);

        JTextFieldOperator tfo = new JTextFieldOperator(operator_1.findJTextField());
        tfo.selectText("old");
        tfo.typeText("new");

        JTextFieldOperator.waitJTextField(win, "editable_new", true, true);

        operator_1.enterText("editable_five");

        operator_1.selectItem("five", false, true);

        JComboBoxOperator.waitJComboBox(win, "editable_five", true, true, -1);
    }

    @Test
    @Order(6)
    public void testNonEditable() {
        scroller.scrollToBottom();

        JComboBoxOperator operator_2 = new JComboBoxOperator(fo, 1);
        operator_2.selectItem(2);
        JComboBoxOperator.waitJComboBox(win, "non_editable_three", true, true, -1);

        JComboBoxOperator operator_00 = new JComboBoxOperator(fo, new NameComponentChooser("non_editable"));
        JComboBoxOperator operator_01 = new JComboBoxOperator(fo, new NameComponentChooser("on_e", new Operator.DefaultStringComparator(false, false)));
        if(operator_00.getSource() != operator_01.getSource()) {
            logger.severe("Wrong component found:");
            logger.severe("Wrong");
            logger.severe(String.valueOf(operator_00.getSource()));
            logger.severe(String.valueOf(operator_01.getSource()));
            fail();
        }

        fo.getTimeouts().setTimeout("ComponentOperator.WaitComponentTimeout", 1000);
        try {
            new JComboBoxOperator(fo, new NameComponentChooser("non_edit"));
            logger.severe("Found by subname!");
            fail();
        } catch(Exception ee) {
        }
    }

    @AfterAll
    public static void tearDown() {
        win.setVisible(false);
        win.dispose();
    }
}
