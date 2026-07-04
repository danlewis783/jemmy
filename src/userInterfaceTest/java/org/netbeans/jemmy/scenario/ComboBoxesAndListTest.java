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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

// testEditableSelection, testEditable and testNonEditable used to declare
// dependsOnMethods = "testEditableLookup" under TestNG; ordering keeps that
// guarantee.
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ComboBoxesAndListTest {

    private static JFrame win;
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
        assertThat(fo2.getSource()).isSameAs(fo.getSource());
        assertThat(fo3.getSource()).isSameAs(fo.getSource());

        Window window = new ComponentOperator(win).getWindow();
        assertThat(window).isSameAs(win);
    }
    @Test
    @Order(2)
    public void testEditableLookup() {
        JComboBoxOperator operator_1 = new JComboBoxOperator(JComboBoxOperator.
                findJComboBox(win,
                        "editable_one",
                        true, true,
                        0));
        JComboBoxOperator operator_10 = new JComboBoxOperator(fo);
        JComboBoxOperator operator_11 = new JComboBoxOperator(fo, "editable_one");
        JComboBoxOperator operator_12 = new JComboBoxOperator(fo, new NameComponentChooser("editable"));
        assertThat(operator_10.getSource()).isSameAs(operator_1.getSource());
        assertThat(operator_11.getSource()).isSameAs(operator_1.getSource());
        assertThat(operator_12.getSource()).isSameAs(operator_1.getSource());
        assertThat(operator_1.getItemCount()).as("item count").isEqualTo(4);
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
        assertThat(lo1.getSource()).isSameAs(lo0.getSource());
        assertThat(lo2.getSource()).isSameAs(lo0.getSource());
        assertThat(lo3.getSource()).isSameAs(lo0.getSource());
    }

    @Test
    @Order(4)
    public void testEditableSelection() {
        scroller.scrollToTop();

        JComboBoxOperator operator_1 = new JComboBoxOperator(fo);

        operator_1.selectItem("editable_two", true, true);
        operator_1.waitItemSelected("editable_two");

        JComboBoxOperator.waitJComboBox(win, "editable_two", true, true, -1);

        assertThat(operator_1.getSelectedIndex()).as("getSelectedIndex").isEqualTo(1);
        assertThat(operator_1.getSelectedItem()).as("getSelectedItem").isEqualTo("editable_two");
        assertThat(operator_1.getItemAt(1)).as("getItemAt(1)").isEqualTo("editable_two");
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
        assertThat(operator_01.getSource()).isSameAs(operator_00.getSource());

        fo.getTimeouts().setTimeout("ComponentOperator.WaitComponentTimeout", 1000);
        assertThatThrownBy(() -> new JComboBoxOperator(fo, new NameComponentChooser("non_edit")))
                .as("Found by subname!")
                .isInstanceOf(Exception.class);
    }

    @AfterAll
    public static void tearDown() {
        win.setVisible(false);
        win.dispose();
    }
}
