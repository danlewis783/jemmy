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
import javax.swing.Icon;
import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.Style;
import javax.swing.text.StyledDocument;
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
public class JTextPaneOperator extends JEditorPaneOperator {

    public JTextPaneOperator(JTextPane b) {
        super(b);
    }

    public JTextPaneOperator(ContainerOperator<?> cont, ComponentChooser chooser, int index) {
        this((JTextPane) cont.waitSubComponent(new JTextPaneFinder(chooser), index));
        copyEnvironment(cont);
    }

    public JTextPaneOperator(ContainerOperator<?> cont, ComponentChooser chooser) {
        this(cont, chooser, 0);
    }

    /**
     * Waits component in container first. Uses cont's timeout and
     * output for waiting and to init operator.
     *
     * @see ComponentOperator#isCaptionEqual(String, String, boolean, boolean)
     */
    public JTextPaneOperator(ContainerOperator<?> cont, String text, int index) {
        this((JTextPane) waitComponent(
                cont,
                new JTextPaneFinder(new JTextComponentOperator.JTextComponentByTextFinder(text, cont.getComparator())),
                index));
        copyEnvironment(cont);
    }

    /**
     * Waits component in container first. Uses cont's timeout and
     * output for waiting and to init operator.
     *
     * @see ComponentOperator#isCaptionEqual(String, String, boolean, boolean)
     */
    public JTextPaneOperator(ContainerOperator<?> cont, String text) {
        this(cont, text, 0);
    }

    /**
     * Waits component in container first. Uses cont's timeout and
     * output for waiting and to init operator.
     */
    public JTextPaneOperator(ContainerOperator<?> cont, int index) {
        this((JTextPane) waitComponent(cont, new JTextPaneFinder(), index));
        copyEnvironment(cont);
    }

    /**
     * Waits component in container first. Uses cont's timeout and
     * output for waiting and to init operator.
     */
    public JTextPaneOperator(ContainerOperator<?> cont) {
        this(cont, 0);
    }

    /**
     * Searches JTextPane in container.
     *
     * @return JTextPane instance or null if component was not found.
     */
    public static JTextPane findJTextPane(Container cont, ComponentChooser chooser, int index) {
        return (JTextPane) findJTextComponent(cont, new JTextPaneFinder(chooser), index);
    }

    /**
     * Searches JTextPane in container.
     *
     * @return JTextPane instance or null if component was not found.
     */
    public static JTextPane findJTextPane(Container cont, ComponentChooser chooser) {
        return findJTextPane(cont, chooser, 0);
    }

    /**
     * Searches JTextPane by text.
     *
     * @return JTextPane instance or null if component was not found.
     * @see ComponentOperator#isCaptionEqual(String, String, boolean, boolean)
     */
    public static JTextPane findJTextPane(Container cont, String text, boolean ce, boolean ccs, int index) {
        return (findJTextPane(
                cont,
                new JTextPaneFinder(new JTextComponentOperator.JTextComponentByTextFinder(
                        text, new DefaultStringComparator(ce, ccs))),
                index));
    }

    /**
     * Searches JTextPane by text.
     *
     * @return JTextPane instance or null if component was not found.
     * @see ComponentOperator#isCaptionEqual(String, String, boolean, boolean)
     */
    public static JTextPane findJTextPane(Container cont, String text, boolean ce, boolean ccs) {
        return findJTextPane(cont, text, ce, ccs, 0);
    }

    /**
     * Waits JTextPane in container.
     *
     * @return JTextPane instance.
     */
    public static JTextPane waitJTextPane(Container cont, ComponentChooser chooser, int index) {
        return (JTextPane) waitJTextComponent(cont, new JTextPaneFinder(chooser), index);
    }

    /**
     * Waits JTextPane in container.
     *
     * @return JTextPane instance.
     */
    public static JTextPane waitJTextPane(Container cont, ComponentChooser chooser) {
        return waitJTextPane(cont, chooser, 0);
    }

    /**
     * Waits JTextPane by text.
     *
     * @return JTextPane instance.
     * @see ComponentOperator#isCaptionEqual(String, String, boolean, boolean)
     */
    public static JTextPane waitJTextPane(Container cont, String text, boolean ce, boolean ccs, int index) {
        return (waitJTextPane(
                cont,
                new JTextPaneFinder(new JTextComponentOperator.JTextComponentByTextFinder(
                        text, new DefaultStringComparator(ce, ccs))),
                index));
    }

    /**
     * Waits JTextPane by text.
     *
     * @return JTextPane instance.
     * @see ComponentOperator#isCaptionEqual(String, String, boolean, boolean)
     */
    public static JTextPane waitJTextPane(Container cont, String text, boolean ce, boolean ccs) {
        return waitJTextPane(cont, text, ce, ccs, 0);
    }

    ////////////////////////////////////////////////////////
    // Mapping                                             //
    public Style addStyle(final String string, final Style style) {
        return (runMapping(new MapAction<Style>("addStyle") {
            @Override
            public Style map() {
                return ((JTextPane) getSource()).addStyle(string, style);
            }
        }));
    }

    public AttributeSet getCharacterAttributes() {
        return (runMapping(new MapAction<AttributeSet>("getCharacterAttributes") {
            @Override
            public AttributeSet map() {
                return ((JTextPane) getSource()).getCharacterAttributes();
            }
        }));
    }

    public MutableAttributeSet getInputAttributes() {
        return (runMapping(new MapAction<MutableAttributeSet>("getInputAttributes") {
            @Override
            public MutableAttributeSet map() {
                return ((JTextPane) getSource()).getInputAttributes();
            }
        }));
    }

    public Style getLogicalStyle() {
        return (runMapping(new MapAction<Style>("getLogicalStyle") {
            @Override
            public Style map() {
                return ((JTextPane) getSource()).getLogicalStyle();
            }
        }));
    }

    public AttributeSet getParagraphAttributes() {
        return (runMapping(new MapAction<AttributeSet>("getParagraphAttributes") {
            @Override
            public AttributeSet map() {
                return ((JTextPane) getSource()).getParagraphAttributes();
            }
        }));
    }

    public Style getStyle(final String string) {
        return (runMapping(new MapAction<Style>("getStyle") {
            @Override
            public Style map() {
                return ((JTextPane) getSource()).getStyle(string);
            }
        }));
    }

    public StyledDocument getStyledDocument() {
        return (runMapping(new MapAction<StyledDocument>("getStyledDocument") {
            @Override
            public StyledDocument map() {
                return ((JTextPane) getSource()).getStyledDocument();
            }
        }));
    }

    public void insertComponent(final Component component) {
        runMapping(new MapVoidAction("insertComponent") {
            @Override
            public void map() {
                ((JTextPane) getSource()).insertComponent(component);
            }
        });
    }

    public void insertIcon(final Icon icon) {
        runMapping(new MapVoidAction("insertIcon") {
            @Override
            public void map() {
                ((JTextPane) getSource()).insertIcon(icon);
            }
        });
    }

    public void removeStyle(final String string) {
        runMapping(new MapVoidAction("removeStyle") {
            @Override
            public void map() {
                ((JTextPane) getSource()).removeStyle(string);
            }
        });
    }

    public void setCharacterAttributes(final AttributeSet attributeSet, final boolean b) {
        runMapping(new MapVoidAction("setCharacterAttributes") {
            @Override
            public void map() {
                ((JTextPane) getSource()).setCharacterAttributes(attributeSet, b);
            }
        });
    }

    public void setLogicalStyle(final Style style) {
        runMapping(new MapVoidAction("setLogicalStyle") {
            @Override
            public void map() {
                ((JTextPane) getSource()).setLogicalStyle(style);
            }
        });
    }

    public void setParagraphAttributes(final AttributeSet attributeSet, final boolean b) {
        runMapping(new MapVoidAction("setParagraphAttributes") {
            @Override
            public void map() {
                ((JTextPane) getSource()).setParagraphAttributes(attributeSet, b);
            }
        });
    }

    public void setStyledDocument(final StyledDocument styledDocument) {
        runMapping(new MapVoidAction("setStyledDocument") {
            @Override
            public void map() {
                ((JTextPane) getSource()).setStyledDocument(styledDocument);
            }
        });
    }

    // End of mapping                                      //
    ////////////////////////////////////////////////////////
    /**
     * Checks component type.
     */
    public static class JTextPaneFinder extends Finder {

        /**
         * Constructs JTextPaneFinder.
         */
        public JTextPaneFinder(ComponentChooser sf) {
            super(JTextPane.class, sf);
        }

        /**
         * Constructs JTextPaneFinder.
         */
        public JTextPaneFinder() {
            super(JTextPane.class);
        }
    }
}
