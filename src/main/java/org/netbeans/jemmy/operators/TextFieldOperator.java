/*
 * Copyright (c) 1997, 2016, Oracle and/or its affiliates. All rights reserved.
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

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.TextField;
import java.awt.event.ActionListener;
import java.util.Hashtable;
import org.netbeans.jemmy.ComponentChooser;
import org.netbeans.jemmy.Outputable;
import org.netbeans.jemmy.TestOut;
import org.netbeans.jemmy.Timeoutable;
import org.netbeans.jemmy.Timeouts;

/**
 * This operator type covers java.awt.TextField component.
 *
 * @see org.netbeans.jemmy.Timeouts
 *
 */
public class TextFieldOperator extends TextComponentOperator implements Timeoutable, Outputable {

    /**
     * Identifier for a "text" property.
     *
     * @see #getDump
     */
    public static final String TEXT_DPROP = "Text";

    private static final long PUSH_KEY_TIMEOUT = 0;
    private static final long BETWEEN_KEYS_TIMEOUT = 0;
    private static final long CHANGE_CARET_POSITION_TIMEOUT = 60000;
    private static final long TYPE_TEXT_TIMEOUT = 60000;

    private Timeouts timeouts;
    private TestOut output;

    /**
     * @param b The {@code java.awt.TextField} managed by this instance.
     */
    public TextFieldOperator(TextField b) {
        super(b);
    }

    public TextFieldOperator(ContainerOperator<?> cont, ComponentChooser chooser, int index) {
        this((TextField) cont.waitSubComponent(new TextFieldFinder(chooser), index));
        copyEnvironment(cont);
    }

    public TextFieldOperator(ContainerOperator<?> cont, ComponentChooser chooser) {
        this(cont, chooser, 0);
    }

    /**
     * Waits for a component in a container to show. The component
     * is identified as the {@code index+1}'th
     * {@code java.awt.TextField} that shows, lies below the container in
     * the display containment hierarchy, and that has the desired text. Uses
     * cont's timeout and output for waiting and to init this operator.
     *
     * @param cont The operator for a container containing the sought for
     * textField.
     * @param index Ordinal component index. The first component has
     * {@code index} 0.
     * @see ComponentOperator#isCaptionEqual(String, String, boolean, boolean)
     */
    public TextFieldOperator(ContainerOperator<?> cont, String text, int index) {
        this((TextField) waitComponent(cont, new TextFieldByTextFinder(text, cont.getComparator()), index));
        copyEnvironment(cont);
    }

    /**
     * Waits for a component in a container to show. The component
     * is identified as the first {@code java.awt.TextField} that shows,
     * lies below the container in the display containment hierarchy, and that
     * has the desired text. Uses cont's timeout and output for waiting and to
     * init this operator.
     *
     * @param cont The operator for a container containing the sought for
     * textField.
     * @see ComponentOperator#isCaptionEqual(String, String, boolean, boolean)
     */
    public TextFieldOperator(ContainerOperator<?> cont, String text) {
        this(cont, text, 0);
    }

    /**
     * Waits component in container first. Uses cont's timeout and
     * output for waiting and to init operator.
     *
     * @param cont The operator for a container containing the sought for
     * textField.
     * @see ComponentOperator#isCaptionEqual(String, String, boolean, boolean)
     */
    public TextFieldOperator(ContainerOperator<?> cont, int index) {
        this((TextField) waitComponent(cont, new TextFieldFinder(), index));
        copyEnvironment(cont);
    }

    /**
     * Waits component in container first. Uses cont's timeout and
     * output for waiting and to init operator.
     *
     * @param cont The operator for a container containing the sought for
     * textField.
     * @see ComponentOperator#isCaptionEqual(String, String, boolean, boolean)
     */
    public TextFieldOperator(ContainerOperator<?> cont) {
        this(cont, 0);
    }

    /**
     * Searches TextField in a container.
     *
     * @param cont Container in which to search for the component. The container
     * lies above the component in the display containment hierarchy. The
     * containment need not be direct.
     * @param index Ordinal component index. The first {@code index} is 0.
     * @return TextField instance or null if component was not found.
     */
    public static TextField findTextField(Container cont, ComponentChooser chooser, int index) {
        return (TextField) findComponent(cont, new TextFieldFinder(chooser), index);
    }

    /**
     * Searches for the first TextField in a container.
     *
     * @param cont Container in which to search for the component. The container
     * lies above the component in the display containment hierarchy. The
     * containment need not be direct.
     * @return TextField instance or null if component was not found.
     */
    public static TextField findTextField(Container cont, ComponentChooser chooser) {
        return findTextField(cont, chooser, 0);
    }

    /**
     * Searches TextField by text.
     *
     * @return TextField instance or null if component was not found.
     * @see ComponentOperator#isCaptionEqual(String, String, boolean, boolean)
     */
    public static TextField findTextField(Container cont, String text, boolean ce, boolean ccs, int index) {
        return findTextField(cont, new TextFieldByTextFinder(text, new DefaultStringComparator(ce, ccs)), index);
    }

    /**
     * Searches TextField by text.
     *
     * @return TextField instance or null if component was not found.
     * @see ComponentOperator#isCaptionEqual(String, String, boolean, boolean)
     */
    public static TextField findTextField(Container cont, String text, boolean ce, boolean ccs) {
        return findTextField(cont, text, ce, ccs, 0);
    }

    /**
     * Waits TextField in container.
     *
     * @return TextField instance.
     */
    public static TextField waitTextField(Container cont, ComponentChooser chooser, int index) {
        return (TextField) waitComponent(cont, new TextFieldFinder(chooser), index);
    }

    /**
     * Waits 0'th TextField in container.
     *
     * @return TextField instance.
     */
    public static TextField waitTextField(Container cont, ComponentChooser chooser) {
        return waitTextField(cont, chooser, 0);
    }

    /**
     * Waits TextField by text.
     *
     * @return TextField instance.
     * @see ComponentOperator#isCaptionEqual(String, String, boolean, boolean)
     */
    public static TextField waitTextField(Container cont, String text, boolean ce, boolean ccs, int index) {
        return waitTextField(cont, new TextFieldByTextFinder(text, new DefaultStringComparator(ce, ccs)), index);
    }

    /**
     * Waits TextField by text.
     *
     * @return TextField instance.
     * @see ComponentOperator#isCaptionEqual(String, String, boolean, boolean)
     */
    public static TextField waitTextField(Container cont, String text, boolean ce, boolean ccs) {
        return waitTextField(cont, text, ce, ccs, 0);
    }

    static {
        Timeouts.initDefault("TextFieldOperator.PushKeyTimeout", PUSH_KEY_TIMEOUT);
        Timeouts.initDefault("TextFieldOperator.BetweenKeysTimeout", BETWEEN_KEYS_TIMEOUT);
        Timeouts.initDefault("TextFieldOperator.ChangeCaretPositionTimeout", CHANGE_CARET_POSITION_TIMEOUT);
        Timeouts.initDefault("TextFieldOperator.TypeTextTimeout", TYPE_TEXT_TIMEOUT);
    }

    @Override
    public void setTimeouts(Timeouts timeouts) {
        super.setTimeouts(timeouts);
        this.timeouts = timeouts;
    }

    @Override
    public Timeouts getTimeouts() {
        return timeouts;
    }

    @Override
    public void setOutput(TestOut out) {
        output = out;
        super.setOutput(output.createErrorOutput());
    }

    @Override
    public TestOut getOutput() {
        return output;
    }

    @Override
    public Hashtable<String, Object> getDump() {
        Hashtable<String, Object> result = super.getDump();
        result.put(TEXT_DPROP, ((TextField) getSource()).getText());
        return result;
    }

    ////////////////////////////////////////////////////////
    // Mapping                                             //
    public void addActionListener(final ActionListener actionListener) {
        runMapping(new MapVoidAction("addActionListener") {
            @Override
            public void map() {
                ((TextField) getSource()).addActionListener(actionListener);
            }
        });
    }

    public boolean echoCharIsSet() {
        return (runMapping(new MapBooleanAction("echoCharIsSet") {
            @Override
            public boolean map() {
                return ((TextField) getSource()).echoCharIsSet();
            }
        }));
    }

    public int getColumns() {
        return (runMapping(new MapIntegerAction("getColumns") {
            @Override
            public int map() {
                return ((TextField) getSource()).getColumns();
            }
        }));
    }

    public char getEchoChar() {
        return (runMapping(new MapCharacterAction("getEchoChar") {
            @Override
            public char map() {
                return ((TextField) getSource()).getEchoChar();
            }
        }));
    }

    public Dimension getMinimumSize(final int i) {
        return (runMapping(new MapAction<Dimension>("getMinimumSize") {
            @Override
            public Dimension map() {
                return ((TextField) getSource()).getMinimumSize(i);
            }
        }));
    }

    public Dimension getPreferredSize(final int i) {
        return (runMapping(new MapAction<Dimension>("getPreferredSize") {
            @Override
            public Dimension map() {
                return ((TextField) getSource()).getPreferredSize(i);
            }
        }));
    }

    public void removeActionListener(final ActionListener actionListener) {
        runMapping(new MapVoidAction("removeActionListener") {
            @Override
            public void map() {
                ((TextField) getSource()).removeActionListener(actionListener);
            }
        });
    }

    public void setColumns(final int i) {
        runMapping(new MapVoidAction("setColumns") {
            @Override
            public void map() {
                ((TextField) getSource()).setColumns(i);
            }
        });
    }

    // End of mapping                                      //
    ////////////////////////////////////////////////////////
    /**
     * Allows to find component by text.
     */
    public static class TextFieldByTextFinder implements ComponentChooser {

        String label;
        StringComparator comparator;

        public TextFieldByTextFinder(String lb, StringComparator comparator) {
            label = lb;
            this.comparator = comparator;
        }

        public TextFieldByTextFinder(String lb) {
            this(lb, Operator.getDefaultStringComparator());
        }

        @Override
        public boolean checkComponent(Component comp) {
            if (comp instanceof TextField) {
                if (((TextField) comp).getText() != null) {
                    return (comparator.equals(((TextField) comp).getText(), label));
                }
            }
            return false;
        }

        @Override
        public String getDescription() {
            return "TextField with text \"" + label + "\"";
        }

        @Override
        public String toString() {
            return "TextFieldByTextFinder{" + "label=" + label + ", comparator=" + comparator + '}';
        }
    }

    /**
     * Checks component type.
     */
    public static class TextFieldFinder extends Finder {

        public TextFieldFinder(ComponentChooser sf) {
            super(TextField.class, sf);
        }

        public TextFieldFinder() {
            super(TextField.class);
        }
    }
}
