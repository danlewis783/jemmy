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
import java.awt.TextArea;
import java.util.Hashtable;
import org.netbeans.jemmy.ComponentChooser;
import org.netbeans.jemmy.Outputable;
import org.netbeans.jemmy.TestOut;
import org.netbeans.jemmy.Timeoutable;
import org.netbeans.jemmy.Timeouts;

/**
 * This operator type covers java.awt.textArea component.
 *
 * @see org.netbeans.jemmy.Timeouts
 *
 */
public class TextAreaOperator extends TextComponentOperator implements Timeoutable, Outputable {

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
     * @param b The {@code java.awt.TextArea} managed by this instance.
     */
    public TextAreaOperator(TextArea b) {
        super(b);
    }

    public TextAreaOperator(ContainerOperator<?> cont, ComponentChooser chooser, int index) {
        this((TextArea) cont.waitSubComponent(new TextAreaFinder(chooser), index));
        copyEnvironment(cont);
    }

    public TextAreaOperator(ContainerOperator<?> cont, ComponentChooser chooser) {
        this(cont, chooser, 0);
    }

    /**
     * Waits for a component in a container to show. The component
     * is identified as the {@code index+1}'th
     * {@code java.awt.TextArea} that shows, lies below the container in
     * the display containment hierarchy, and that has the desired text. Uses
     * cont's timeout and output for waiting and to init this operator.
     *
     * @param cont The operator for a container containing the sought for
     * textArea.
     * @param index Ordinal component index. The first component has
     * {@code index} 0.
     * @see ComponentOperator#isCaptionEqual(String, String, boolean, boolean)
     */
    public TextAreaOperator(ContainerOperator<?> cont, String text, int index) {
        this((TextArea) waitComponent(cont, new TextAreaByTextFinder(text, cont.getComparator()), index));
        copyEnvironment(cont);
    }

    /**
     * Waits for a component in a container to show. The component
     * is identified as the first {@code java.awt.TextArea} that shows,
     * lies below the container in the display containment hierarchy, and that
     * has the desired text. Uses cont's timeout and output for waiting and to
     * init this operator.
     *
     * @param cont The operator for a container containing the sought for
     * textArea.
     * @see ComponentOperator#isCaptionEqual(String, String, boolean, boolean)
     */
    public TextAreaOperator(ContainerOperator<?> cont, String text) {
        this(cont, text, 0);
    }

    /**
     * Waits component in container first. Uses cont's timeout and
     * output for waiting and to init operator.
     *
     * @param cont The operator for a container containing the sought for
     * textArea.
     * @see ComponentOperator#isCaptionEqual(String, String, boolean, boolean)
     */
    public TextAreaOperator(ContainerOperator<?> cont, int index) {
        this((TextArea) waitComponent(cont, new TextAreaFinder(), index));
        copyEnvironment(cont);
    }

    /**
     * Waits component in container first. Uses cont's timeout and
     * output for waiting and to init operator.
     *
     * @param cont The operator for a container containing the sought for
     * textArea.
     * @see ComponentOperator#isCaptionEqual(String, String, boolean, boolean)
     */
    public TextAreaOperator(ContainerOperator<?> cont) {
        this(cont, 0);
    }

    /**
     * Searches TextArea in a container.
     *
     * @param cont Container in which to search for the component. The container
     * lies above the component in the display containment hierarchy. The
     * containment need not be direct.
     * @param index Ordinal component index. The first {@code index} is 0.
     * @return TextArea instance or null if component was not found.
     */
    public static TextArea findTextArea(Container cont, ComponentChooser chooser, int index) {
        return (TextArea) findComponent(cont, new TextAreaFinder(chooser), index);
    }

    /**
     * Searches for the first TextArea in a container.
     *
     * @param cont Container in which to search for the component. The container
     * lies above the component in the display containment hierarchy. The
     * containment need not be direct.
     * @return TextArea instance or null if component was not found.
     */
    public static TextArea findTextArea(Container cont, ComponentChooser chooser) {
        return findTextArea(cont, chooser, 0);
    }

    /**
     * Searches TextArea by text.
     *
     * @return TextArea instance or null if component was not found.
     * @see ComponentOperator#isCaptionEqual(String, String, boolean, boolean)
     */
    public static TextArea findTextArea(Container cont, String text, boolean ce, boolean ccs, int index) {
        return findTextArea(cont, new TextAreaByTextFinder(text, new DefaultStringComparator(ce, ccs)), index);
    }

    /**
     * Searches TextArea by text.
     *
     * @return TextArea instance or null if component was not found.
     * @see ComponentOperator#isCaptionEqual(String, String, boolean, boolean)
     */
    public static TextArea findTextArea(Container cont, String text, boolean ce, boolean ccs) {
        return findTextArea(cont, text, ce, ccs, 0);
    }

    /**
     * Waits TextArea in container.
     *
     * @return TextArea instance.
     */
    public static TextArea waitTextArea(Container cont, ComponentChooser chooser, int index) {
        return (TextArea) waitComponent(cont, new TextAreaFinder(chooser), index);
    }

    /**
     * Waits 0'th TextArea in container.
     *
     * @return TextArea instance.
     */
    public static TextArea waitTextArea(Container cont, ComponentChooser chooser) {
        return waitTextArea(cont, chooser, 0);
    }

    /**
     * Waits TextArea by text.
     *
     * @return TextArea instance.
     * @see ComponentOperator#isCaptionEqual(String, String, boolean, boolean)
     */
    public static TextArea waitTextArea(Container cont, String text, boolean ce, boolean ccs, int index) {
        return waitTextArea(cont, new TextAreaByTextFinder(text, new DefaultStringComparator(ce, ccs)), index);
    }

    /**
     * Waits TextArea by text.
     *
     * @return TextArea instance.
     * @see ComponentOperator#isCaptionEqual(String, String, boolean, boolean)
     */
    public static TextArea waitTextArea(Container cont, String text, boolean ce, boolean ccs) {
        return waitTextArea(cont, text, ce, ccs, 0);
    }

    static {
        Timeouts.initDefault("TextAreaOperator.PushKeyTimeout", PUSH_KEY_TIMEOUT);
        Timeouts.initDefault("TextAreaOperator.BetweenKeysTimeout", BETWEEN_KEYS_TIMEOUT);
        Timeouts.initDefault("TextAreaOperator.ChangeCaretPositionTimeout", CHANGE_CARET_POSITION_TIMEOUT);
        Timeouts.initDefault("TextAreaOperator.TypeTextTimeout", TYPE_TEXT_TIMEOUT);
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
        result.put(TEXT_DPROP, ((TextArea) getSource()).getText());
        return result;
    }

    ////////////////////////////////////////////////////////
    // Mapping                                             //
    public int getColumns() {
        return (runMapping(new MapIntegerAction("getColumns") {
            @Override
            public int map() {
                return ((TextArea) getSource()).getColumns();
            }
        }));
    }

    public Dimension getMinimumSize(final int i, final int i1) {
        return (runMapping(new MapAction<Dimension>("getMinimumSize") {
            @Override
            public Dimension map() {
                return ((TextArea) getSource()).getMinimumSize(i, i1);
            }
        }));
    }

    public Dimension getPreferredSize(final int i, final int i1) {
        return (runMapping(new MapAction<Dimension>("getPreferredSize") {
            @Override
            public Dimension map() {
                return ((TextArea) getSource()).getPreferredSize(i, i1);
            }
        }));
    }

    public int getRows() {
        return (runMapping(new MapIntegerAction("getRows") {
            @Override
            public int map() {
                return ((TextArea) getSource()).getRows();
            }
        }));
    }

    public int getScrollbarVisibility() {
        return (runMapping(new MapIntegerAction("getScrollbarVisibility") {
            @Override
            public int map() {
                return ((TextArea) getSource()).getScrollbarVisibility();
            }
        }));
    }

    public void replaceRange(final String string, final int i, final int i1) {
        runMapping(new MapVoidAction("replaceRange") {
            @Override
            public void map() {
                ((TextArea) getSource()).replaceRange(string, i, i1);
            }
        });
    }

    public void setColumns(final int i) {
        runMapping(new MapVoidAction("setColumns") {
            @Override
            public void map() {
                ((TextArea) getSource()).setColumns(i);
            }
        });
    }

    public void setRows(final int i) {
        runMapping(new MapVoidAction("setRows") {
            @Override
            public void map() {
                ((TextArea) getSource()).setRows(i);
            }
        });
    }

    // End of mapping                                      //
    ////////////////////////////////////////////////////////
    /**
     * Allows to find component by text.
     */
    public static class TextAreaByTextFinder implements ComponentChooser {

        String label;
        StringComparator comparator;

        public TextAreaByTextFinder(String lb, StringComparator comparator) {
            label = lb;
            this.comparator = comparator;
        }

        public TextAreaByTextFinder(String lb) {
            this(lb, Operator.getDefaultStringComparator());
        }

        @Override
        public boolean checkComponent(Component comp) {
            if (comp instanceof TextArea) {
                if (((TextArea) comp).getText() != null) {
                    return (comparator.equals(((TextArea) comp).getText(), label));
                }
            }
            return false;
        }

        @Override
        public String getDescription() {
            return "TextArea with text \"" + label + "\"";
        }
    }

    /**
     * Checks component type.
     */
    public static class TextAreaFinder extends Finder {

        public TextAreaFinder(ComponentChooser sf) {
            super(TextArea.class, sf);
        }

        public TextAreaFinder() {
            super(TextArea.class);
        }
    }
}
