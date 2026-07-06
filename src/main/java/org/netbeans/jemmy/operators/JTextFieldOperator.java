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

import java.awt.Container;
import java.awt.event.ActionListener;
import javax.swing.BoundedRangeModel;
import javax.swing.JTextField;
import org.jspecify.annotations.Nullable;
import org.netbeans.jemmy.ComponentChooser;

/**
 * Timeouts used:
 * <ul>
 * <li>JTextComponentOperator.PushKeyTimeout - time between key pressing and releasing during text typing</li>
 * <li>JTextComponentOperator.BetweenKeysTimeout - time to sleep between two chars typing</li>
 * <li>JTextComponentOperator.ChangeCaretPositionTimeout - maximum time to chenge caret position</li>
 * <li>JTextComponentOperator.TypeTextTimeout - maximum time to type text</li>
 * <li>ComponentOperator.WaitComponentTimeout - time to wait component displayed</li>
 * <li>ComponentOperator.WaitFocusTimeout - time to wait component focus</li>
 * <li>JScrollBarOperator.OneScrollClickTimeout - time for one scroll click</li>
 * <li>JScrollBarOperator.WholeScrollTimeout - time for the whole scrolling</li>
 * </ul>
 *
 * @see org.netbeans.jemmy.Timeouts
 *
 */
public class JTextFieldOperator extends JTextComponentOperator {

    public JTextFieldOperator(JTextField b) {
        super(b);
    }

    public JTextFieldOperator(ContainerOperator<?> cont, ComponentChooser chooser, int index) {
        this((JTextField) cont.waitSubComponent(new JTextFieldFinder(chooser), index));
        copyEnvironment(cont);
    }

    public JTextFieldOperator(ContainerOperator<?> cont, ComponentChooser chooser) {
        this(cont, chooser, 0);
    }

    /**
     * Waits component in container first. Uses cont's timeout and
     * output for waiting and to init operator.
     *
     * @see ComponentOperator#isCaptionEqual(String, String, boolean, boolean)
     */
    public JTextFieldOperator(ContainerOperator<?> cont, String text, int index) {
        this((JTextField) waitComponent(
                cont,
                new JTextFieldFinder(new JTextComponentOperator.JTextComponentByTextFinder(text, cont.getComparator())),
                index));
        copyEnvironment(cont);
    }

    /**
     * Waits component in container first. Uses cont's timeout and
     * output for waiting and to init operator.
     *
     * @see ComponentOperator#isCaptionEqual(String, String, boolean, boolean)
     */
    public JTextFieldOperator(ContainerOperator<?> cont, String text) {
        this(cont, text, 0);
    }

    /**
     * Waits component in container first. Uses cont's timeout and
     * output for waiting and to init operator.
     */
    public JTextFieldOperator(ContainerOperator<?> cont, int index) {
        this((JTextField) waitComponent(cont, new JTextFieldFinder(), index));
        copyEnvironment(cont);
    }

    /**
     * Waits component in container first. Uses cont's timeout and
     * output for waiting and to init operator.
     */
    public JTextFieldOperator(ContainerOperator<?> cont) {
        this(cont, 0);
    }

    /**
     * Searches JTextField in container.
     *
     * @return JTextField instance or null if component was not found.
     */
    public static @Nullable JTextField findJTextField(Container cont, ComponentChooser chooser, int index) {
        return (JTextField) findJTextComponent(cont, new JTextFieldFinder(chooser), index);
    }

    /**
     * Searches JTextField in container.
     *
     * @return JTextField instance or null if component was not found.
     */
    public static @Nullable JTextField findJTextField(Container cont, ComponentChooser chooser) {
        return findJTextField(cont, chooser, 0);
    }

    /**
     * Searches JTextField by text.
     *
     * @return JTextField instance or null if component was not found.
     * @see ComponentOperator#isCaptionEqual(String, String, boolean, boolean)
     */
    public static @Nullable JTextField findJTextField(Container cont, String text, boolean ce, boolean ccs, int index) {
        return (findJTextField(
                cont,
                new JTextFieldFinder(new JTextComponentOperator.JTextComponentByTextFinder(
                        text, new DefaultStringComparator(ce, ccs))),
                index));
    }

    /**
     * Searches JTextField by text.
     *
     * @return JTextField instance or null if component was not found.
     * @see ComponentOperator#isCaptionEqual(String, String, boolean, boolean)
     */
    public static @Nullable JTextField findJTextField(Container cont, String text, boolean ce, boolean ccs) {
        return findJTextField(cont, text, ce, ccs, 0);
    }

    /**
     * Waits JTextField in container.
     *
     * @return JTextField instance.
     */
    public static JTextField waitJTextField(Container cont, ComponentChooser chooser, int index) {
        return (JTextField) waitJTextComponent(cont, new JTextFieldFinder(chooser), index);
    }

    /**
     * Waits JTextField in container.
     *
     * @return JTextField instance.
     */
    public static JTextField waitJTextField(Container cont, ComponentChooser chooser) {
        return waitJTextField(cont, chooser, 0);
    }

    /**
     * Waits JTextField by text.
     *
     * @return JTextField instance.
     * @see ComponentOperator#isCaptionEqual(String, String, boolean, boolean)
     */
    public static JTextField waitJTextField(Container cont, String text, boolean ce, boolean ccs, int index) {
        return (waitJTextField(
                cont,
                new JTextFieldFinder(new JTextComponentOperator.JTextComponentByTextFinder(
                        text, new DefaultStringComparator(ce, ccs))),
                index));
    }

    /**
     * Waits JTextField by text.
     *
     * @return JTextField instance.
     * @see ComponentOperator#isCaptionEqual(String, String, boolean, boolean)
     */
    public static JTextField waitJTextField(Container cont, String text, boolean ce, boolean ccs) {
        return waitJTextField(cont, text, ce, ccs, 0);
    }

    /**
     * Wait some text to be displayed starting from certain position.
     */
    @Override
    public void waitText(String text, int position) {
        super.waitText(removeNewLines(text), position);
    }

    /**
     * Wait some text to be displayed.
     */
    @Override
    public void waitText(String text) {
        super.waitText(removeNewLines(text));
    }

    ////////////////////////////////////////////////////////
    // Mapping                                             //
    public void addActionListener(final ActionListener actionListener) {
        runMapping(new MapVoidAction("addActionListener") {
            @Override
            public void map() {
                ((JTextField) getSource()).addActionListener(actionListener);
            }
        });
    }

    public int getColumns() {
        return (runMapping(new MapIntegerAction("getColumns") {
            @Override
            public int map() {
                return ((JTextField) getSource()).getColumns();
            }
        }));
    }

    public int getHorizontalAlignment() {
        return (runMapping(new MapIntegerAction("getHorizontalAlignment") {
            @Override
            public int map() {
                return ((JTextField) getSource()).getHorizontalAlignment();
            }
        }));
    }

    public BoundedRangeModel getHorizontalVisibility() {
        return (runMapping(new MapAction<BoundedRangeModel>("getHorizontalVisibility") {
            @Override
            public BoundedRangeModel map() {
                return ((JTextField) getSource()).getHorizontalVisibility();
            }
        }));
    }

    public int getScrollOffset() {
        return (runMapping(new MapIntegerAction("getScrollOffset") {
            @Override
            public int map() {
                return ((JTextField) getSource()).getScrollOffset();
            }
        }));
    }

    public void postActionEvent() {
        runMapping(new MapVoidAction("postActionEvent") {
            @Override
            public void map() {
                ((JTextField) getSource()).postActionEvent();
            }
        });
    }

    public void removeActionListener(final ActionListener actionListener) {
        runMapping(new MapVoidAction("removeActionListener") {
            @Override
            public void map() {
                ((JTextField) getSource()).removeActionListener(actionListener);
            }
        });
    }

    public void setActionCommand(final String string) {
        runMapping(new MapVoidAction("setActionCommand") {
            @Override
            public void map() {
                ((JTextField) getSource()).setActionCommand(string);
            }
        });
    }

    public void setColumns(final int i) {
        runMapping(new MapVoidAction("setColumns") {
            @Override
            public void map() {
                ((JTextField) getSource()).setColumns(i);
            }
        });
    }

    public void setHorizontalAlignment(final int i) {
        runMapping(new MapVoidAction("setHorizontalAlignment") {
            @Override
            public void map() {
                ((JTextField) getSource()).setHorizontalAlignment(i);
            }
        });
    }

    public void setScrollOffset(final int i) {
        runMapping(new MapVoidAction("setScrollOffset") {
            @Override
            public void map() {
                ((JTextField) getSource()).setScrollOffset(i);
            }
        });
    }

    // End of mapping                                      //
    ////////////////////////////////////////////////////////
    private String removeNewLines(String text) {
        StringBuffer buff = new StringBuffer(text);
        int i = 0;
        while (i < buff.length()) {
            if (buff.charAt(i) != '\n') {
                i++;
            } else {
                buff.deleteCharAt(i);
            }
        }
        return buff.toString();
    }

    /**
     * Checks component type.
     */
    public static class JTextFieldFinder extends Finder {

        /**
         * Constructs JTextFieldFinder.
         */
        public JTextFieldFinder(ComponentChooser sf) {
            super(JTextField.class, sf);
        }

        /**
         * Constructs JTextFieldFinder.
         */
        public JTextFieldFinder() {
            super(JTextField.class);
        }
    }
}
