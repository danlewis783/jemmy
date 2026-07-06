/*
 * Copyright (c) 1997, 2020, Oracle and/or its affiliates. All rights reserved.
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

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Hashtable;
import javax.swing.JScrollPane;
import javax.swing.event.CaretListener;
import javax.swing.plaf.TextUI;
import javax.swing.text.BadLocationException;
import javax.swing.text.Caret;
import javax.swing.text.Document;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;
import javax.swing.text.Keymap;
import org.jspecify.annotations.Nullable;
import org.netbeans.jemmy.Action;
import org.netbeans.jemmy.ComponentChooser;
import org.netbeans.jemmy.ComponentSearcher;
import org.netbeans.jemmy.JemmyException;
import org.netbeans.jemmy.JemmyInputException;
import org.netbeans.jemmy.Outputable;
import org.netbeans.jemmy.TestOut;
import org.netbeans.jemmy.Timeoutable;
import org.netbeans.jemmy.Timeouts;
import org.netbeans.jemmy.drivers.DriverManager;
import org.netbeans.jemmy.drivers.TextDriver;
import org.netbeans.jemmy.util.EmptyVisualizer;

/**
 * Class provides basic functions to operate with JTextComponent (selection, typing, deleting)
 * <p>
 * Timeouts used:
 * <ul>
 * <li>JTextComponentOperator.PushKeyTimeout - time between key pressing and releasing during text typing</li>
 * <li>JTextComponentOperator.BetweenKeysTimeout - time to sleep between two chars typing</li>
 * <li>JTextComponentOperator.ChangeCaretPositionTimeout - maximum time to chenge caret position</li>
 * <li>JTextComponentOperator.TypeTextTimeout - maximum time to type text</li>
 * <li>ComponentOperator.WaitComponentTimeout - time to wait component displayed</li>
 * <li>ComponentOperator.WaitFocusTimeout - time to wait component focus</li>
 * <li>ComponentOperator.WaitStateTimeout - time to wait for text</li>
 * <li>JScrollBarOperator.OneScrollClickTimeout - time for one scroll click</li>
 * <li>JScrollBarOperator.WholeScrollTimeout - time for the whole scrolling</li>
 * </ul>
 *
 * @see org.netbeans.jemmy.Timeouts
 *
 */
public class JTextComponentOperator extends JComponentOperator implements Timeoutable, Outputable {

    /**
     * Identifier for a "text" property.
     *
     * @see #getDump
     */
    public static final String TEXT_DPROP = "Text";

    /**
     * Identifier for a "selected text" property.
     *
     * @see #getDump
     */
    public static final String SELECTED_TEXT_DPROP = "Selected text";

    /**
     * Identifier for a "editable" property.
     *
     * @see #getDump
     */
    public static final String IS_EDITABLE_DPROP = "Editable";

    private static final long PUSH_KEY_TIMEOUT = 0;
    private static final long BETWEEN_KEYS_TIMEOUT = 0;
    private static final long CHANGE_CARET_POSITION_TIMEOUT = 60000;
    private static final long TYPE_TEXT_TIMEOUT = 60000;

    private @SuppressWarnings("NullAway.Init") Timeouts timeouts;
    private @SuppressWarnings("NullAway.Init") TestOut output;

    /**
     * Notifies what modifiers are pressed.
     *
     * @deprecated All text operations are performed by TextDriver regitered for
     * this operator type.
     */
    @Deprecated
    protected int modifiersPressed = 0;

    private TextDriver driver;

    public JTextComponentOperator(JTextComponent b) {
        super(b);
        driver = DriverManager.getTextDriver(getClass());
    }

    public JTextComponentOperator(ContainerOperator<?> cont, ComponentChooser chooser, int index) {
        this((JTextComponent) cont.waitSubComponent(new JTextComponentFinder(chooser), index));
        copyEnvironment(cont);
    }

    public JTextComponentOperator(ContainerOperator<?> cont, ComponentChooser chooser) {
        this(cont, chooser, 0);
    }

    /**
     * Waits component in container first. Uses cont's timeout and
     * output for waiting and to init operator.
     *
     * @see ComponentOperator#isCaptionEqual(String, String, boolean, boolean)
     */
    public JTextComponentOperator(ContainerOperator<?> cont, String text, int index) {
        this((JTextComponent) waitComponent(cont, new JTextComponentByTextFinder(text, cont.getComparator()), index));
        copyEnvironment(cont);
    }

    /**
     * Waits component in container first. Uses cont's timeout and
     * output for waiting and to init operator.
     *
     * @see ComponentOperator#isCaptionEqual(String, String, boolean, boolean)
     */
    public JTextComponentOperator(ContainerOperator<?> cont, String text) {
        this(cont, text, 0);
    }

    /**
     * Waits component in container first. Uses cont's timeout and
     * output for waiting and to init operator.
     */
    public JTextComponentOperator(ContainerOperator<?> cont, int index) {
        this((JTextComponent) waitComponent(cont, new JTextComponentFinder(), index));
        copyEnvironment(cont);
    }

    /**
     * Waits component in container first. Uses cont's timeout and
     * output for waiting and to init operator.
     */
    public JTextComponentOperator(ContainerOperator<?> cont) {
        this(cont, 0);
    }

    static {
        Timeouts.initDefault("JTextComponentOperator.PushKeyTimeout", PUSH_KEY_TIMEOUT);
        Timeouts.initDefault("JTextComponentOperator.BetweenKeysTimeout", BETWEEN_KEYS_TIMEOUT);
        Timeouts.initDefault("JTextComponentOperator.ChangeCaretPositionTimeout", CHANGE_CARET_POSITION_TIMEOUT);
        Timeouts.initDefault("JTextComponentOperator.TypeTextTimeout", TYPE_TEXT_TIMEOUT);
    }

    /**
     * Searches JTextComponent in container.
     *
     * @return JTextComponent instance or null if component was not found.
     */
    public static @Nullable JTextComponent findJTextComponent(Container cont, ComponentChooser chooser, int index) {
        return (JTextComponent) findComponent(cont, new JTextComponentFinder(chooser), index);
    }

    /**
     * Searches JTextComponent in container.
     *
     * @return JTextComponent instance or null if component was not found.
     */
    public static @Nullable JTextComponent findJTextComponent(Container cont, ComponentChooser chooser) {
        return findJTextComponent(cont, chooser, 0);
    }

    /**
     * Searches JTextComponent by text.
     *
     * @return JTextComponent instance or null if component was not found.
     * @see ComponentOperator#isCaptionEqual(String, String, boolean, boolean)
     */
    public static @Nullable JTextComponent findJTextComponent(
            Container cont, String text, boolean ce, boolean ccs, int index) {
        return findJTextComponent(
                cont, new JTextComponentByTextFinder(text, new DefaultStringComparator(ce, ccs)), index);
    }

    /**
     * Searches JTextComponent by text.
     *
     * @return JTextComponent instance or null if component was not found.
     * @see ComponentOperator#isCaptionEqual(String, String, boolean, boolean)
     */
    public static @Nullable JTextComponent findJTextComponent(Container cont, String text, boolean ce, boolean ccs) {
        return findJTextComponent(cont, text, ce, ccs, 0);
    }

    /**
     * Waits JTextComponent in container.
     *
     * @return JTextComponent instance.
     */
    public static JTextComponent waitJTextComponent(
            final Container cont, final ComponentChooser chooser, final int index) {
        return (JTextComponent) waitComponent(cont, new JTextComponentFinder(chooser), index);
    }

    /**
     * Waits JTextComponent in container.
     *
     * @return JTextComponent instance.
     */
    public static JTextComponent waitJTextComponent(Container cont, ComponentChooser chooser) {
        return waitJTextComponent(cont, chooser, 0);
    }

    /**
     * Waits JTextComponent by text.
     *
     * @return JTextComponent instance.
     * @see ComponentOperator#isCaptionEqual(String, String, boolean, boolean)
     */
    public static JTextComponent waitJTextComponent(Container cont, String text, boolean ce, boolean ccs, int index) {
        return waitJTextComponent(
                cont, new JTextComponentByTextFinder(text, new DefaultStringComparator(ce, ccs)), index);
    }

    /**
     * Waits JTextComponent by text.
     *
     * @return JTextComponent instance.
     * @see ComponentOperator#isCaptionEqual(String, String, boolean, boolean)
     */
    public static JTextComponent waitJTextComponent(Container cont, String text, boolean ce, boolean ccs) {
        return waitJTextComponent(cont, text, ce, ccs, 0);
    }

    @Override
    public void setTimeouts(Timeouts times) {
        timeouts = times;
        timeouts.setTimeout(
                "ComponentOperator.PushKeyTimeout", timeouts.getTimeout("JTextComponentOperator.PushKeyTimeout"));
        super.setTimeouts(timeouts);
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
    public void copyEnvironment(Operator anotherOperator) {
        super.copyEnvironment(anotherOperator);
        driver = (TextDriver)
                DriverManager.getDriver(DriverManager.TEXT_DRIVER_ID, getClass(), anotherOperator.getProperties());
    }

    /**
     * Finds start text position.
     *
     * @param index Index of text instance (first instance has index 0)
     * @return Caret position correspondent to text start.
     * @see JTextComponentOperator.TextChooser
     */
    public int getPositionByText(String text, TextChooser tChooser, int index) {
        output.printLine("Find " + tChooser.getDescription() + "\"" + text
                + "\" text in text component\n    : "
                + toStringSource());
        output.printGolden("Find " + tChooser.getDescription() + "\"" + text + "\" text in text component");
        String allText = getDisplayedText();
        Document doc = getDocument();
        int position = 0;
        int ind = 0;
        while ((position = allText.indexOf(text, position)) >= 0) {
            if (tChooser.checkPosition(doc, position)) {
                if (ind == index) {
                    return position;
                } else {
                    ind++;
                }
            }
            position = position + text.length();
        }
        return -1;
    }

    /**
     * Finds start text position.
     *
     * @return Caret position correspondent to text start.
     */
    public int getPositionByText(String text, TextChooser tChooser) {
        return getPositionByText(text, tChooser, 0);
    }

    /**
     * Finds start text position.
     *
     * @param index Index of text instance (first instance has index 0)
     * @return Caret position correspondent to text start.
     */
    public int getPositionByText(String text, int index) {
        return (getPositionByText(
                text,
                new TextChooser() {
                    @Override
                    public boolean checkPosition(Document doc, int offset) {
                        return true;
                    }

                    @Override
                    public String getDescription() {
                        return "any";
                    }

                    @Override
                    public String toString() {
                        return "JTextComponentOperator.getPositionByText.TextChooser{description = " + getDescription()
                                + '}';
                    }
                },
                index));
    }

    /**
     * Finds start text position.
     *
     * @return Caret position correspondent to text start.
     */
    public int getPositionByText(String text) {
        return getPositionByText(text, 0);
    }

    /**
     * Requests a focus, clears text, types new one and pushes Enter.
     */
    public void enterText(final String text) {
        makeComponentVisible();
        produceTimeRestricted(
                new Action<Void, Void>() {
                    @Override
                    public Void launch(Void obj) {
                        driver.enterText(JTextComponentOperator.this, text);
                        return null;
                    }

                    @Override
                    public String getDescription() {
                        return "Text entering";
                    }

                    @Override
                    public String toString() {
                        return "JTextComponentOperator.enterText.Action{description = " + getDescription() + '}';
                    }
                },
                "JTextComponentOperator.TypeTextTimeout");
    }

    /**
     * Changes caret position.
     *
     * @see #changeCaretPosition(String, int, boolean)
     */
    public void changeCaretPosition(final int position) {
        output.printLine("Change caret position to " + Integer.toString(position));
        makeComponentVisible();
        produceTimeRestricted(
                new Action<Void, Void>() {
                    @Override
                    public Void launch(Void obj) {
                        driver.changeCaretPosition(JTextComponentOperator.this, position);
                        return null;
                    }

                    @Override
                    public String getDescription() {
                        return "Caret moving";
                    }

                    @Override
                    public String toString() {
                        return "JTextComponentOperator.changeCaretPosition.Action{description = " + getDescription()
                                + '}';
                    }
                },
                "JTextComponentOperator.ChangeCaretPositionTimeout");
        if (getVerification()) {
            waitCaretPosition(position);
        }
    }

    /**
     * Puts caret before or after text.
     *
     * @param index Index of text instance (first instance has index 0)
     * @see #changeCaretPosition(int)
     * @see #getPositionByText(String, int)
     * @throws NoSuchTextException
     */
    public void changeCaretPosition(String text, int index, boolean before) {
        output.printLine("Put caret "
                + (before ? "before" : "after") + " "
                + Integer.toString(index) + "'th instance of \""
                + text + "\" text");
        makeComponentVisible();
        int offset = getPositionByText(text, index);
        if (offset == -1) {
            throw (new NoSuchTextException(text));
        }
        offset = before ? offset : offset + text.length();
        changeCaretPosition(offset);
    }

    /**
     * Puts caret before or after text.
     *
     * @see #changeCaretPosition(int)
     * @see #getPositionByText(String, int)
     * @throws NoSuchTextException
     */
    public void changeCaretPosition(String text, boolean before) {
        changeCaretPosition(text, 0, before);
    }

    /**
     * Types text starting from known position. If verification mode is on,
     * checks that right text has been typed and caret has been moved to right
     * position.
     *
     * @see #typeText(String)
     * @throws NoSuchTextException
     */
    public void typeText(final String text, final int caretPosition) {
        output.printLine("Typing text \"" + text + "\" from "
                + Integer.toString(caretPosition) + " position "
                + "in text component\n    : "
                + toStringSource());
        output.printGolden("Typing text \"" + text + "\" in text component");
        makeComponentVisible();
        produceTimeRestricted(
                new Action<Void, Void>() {
                    @Override
                    public Void launch(Void obj) {
                        driver.typeText(JTextComponentOperator.this, text, caretPosition);
                        return null;
                    }

                    @Override
                    public String getDescription() {
                        return "Text typing";
                    }

                    @Override
                    public String toString() {
                        return "JTextComponentOperator.typeText.Action{description = " + getDescription() + '}';
                    }
                },
                "JTextComponentOperator.TypeTextTimeout");
        if (getVerification()) {
            waitText(text, -1);
        }
    }

    /**
     * Types text starting from the current position.
     *
     * @see #typeText(String, int)
     */
    public void typeText(String text) {
        typeText(text, getCaretPosition());
    }

    /**
     * Selects a part of text.
     *
     * @see #selectText(String, int)
     * @see #selectText(String)
     */
    public void selectText(final int startPosition, final int finalPosition) {
        output.printLine("Select text from "
                + Integer.toString(startPosition) + " to "
                + Integer.toString(finalPosition)
                + " in text component\n    : "
                + toStringSource());
        makeComponentVisible();
        produceTimeRestricted(
                new Action<Void, Void>() {
                    @Override
                    public Void launch(Void obj) {
                        driver.selectText(JTextComponentOperator.this, startPosition, finalPosition);
                        return null;
                    }

                    @Override
                    public String getDescription() {
                        return "Text selecting";
                    }

                    @Override
                    public String toString() {
                        return "JTextComponentOperator.selectText.Action{description = " + getDescription() + '}';
                    }
                },
                "JTextComponentOperator.TypeTextTimeout");
    }

    /**
     * Selects a part of text.
     *
     * @param index Index of text instance (first instance has index 0)
     * @see #selectText(int, int)
     * @see #selectText(String)
     * @see #getPositionByText(String, int)
     * @throws NoSuchTextException
     */
    public void selectText(String text, int index) {
        output.printLine("Select "
                + Integer.toString(index) + "'th instance of \""
                + text + "\" text in component\n    : "
                + toStringSource());
        makeComponentVisible();
        int start = getPositionByText(text, index);
        if (start == -1) {
            throw (new NoSuchTextException(text));
        }
        selectText(start, start + text.length());
    }

    /**
     * Selects a part of text.
     *
     * @see #selectText(String, int)
     * @see #selectText(int, int)
     * @throws NoSuchTextException
     */
    public void selectText(String text) {
        selectText(text, 0);
    }

    public void clearText() {
        output.printLine("Clearing text in text component\n    : " + toStringSource());
        output.printGolden("Clearing text in text component");
        makeComponentVisible();
        produceTimeRestricted(
                new Action<Void, Void>() {
                    @Override
                    public Void launch(Void obj) {
                        driver.clearText(JTextComponentOperator.this);
                        return null;
                    }

                    @Override
                    public String getDescription() {
                        return "Text clearing";
                    }

                    @Override
                    public String toString() {
                        return "JTextComponentOperator.clearText.Action{description = " + getDescription() + '}';
                    }
                },
                "JTextComponentOperator.TypeTextTimeout");
    }

    /**
     * Scrolls to a text poistion.
     */
    public void scrollToPosition(int position) {
        output.printTrace(
                "Scroll JTextComponent to " + Integer.toString(position) + " position\n    : " + toStringSource());
        output.printGolden("Scroll JTextComponent to " + Integer.toString(position) + " position");
        makeComponentVisible();
        // try to find JScrollPane under.
        JScrollPane scroll = (JScrollPane) getContainer(
                new JScrollPaneOperator.JScrollPaneFinder(ComponentSearcher.getTrueChooser("JScrollPane")));
        if (scroll == null) {
            return;
        }
        JScrollPaneOperator scroller = new JScrollPaneOperator(scroll);
        scroller.copyEnvironment(this);
        scroller.setVisualizer(new EmptyVisualizer());
        Rectangle rect = modelToView(position);
        scroller.scrollToComponentRectangle(
                getSource(), (int) rect.getX(), (int) rect.getY(), (int) rect.getWidth(), (int) rect.getHeight());
    }

    /**
     * Returns text which is really displayed. Results returned by
     * {@code getText()} and {@code getDisplayedText()} are different
     * if text component is used to display
     * {@code javax.swing.text.StyledDocument}
     *
     * @return the text which is displayed.
     */
    public String getDisplayedText() {
        try {
            Document doc = getDocument();
            return doc.getText(0, doc.getLength());
        } catch (BadLocationException e) {
            throw (new JemmyException("Exception during text operation with\n    : " + toStringSource(), e));
        }
    }

    /**
     * Wait for text to be displayed starting from certain position.
     */
    public void waitText(final String text, final int position) {
        getOutput()
                .printLine("Wait \"" + text + "\" text starting from "
                        + Integer.toString(position) + " position in component \n    : "
                        + toStringSource());
        getOutput().printGolden("Wait \"" + text + "\" text starting from " + Integer.toString(position) + " position");
        waitState(new ComponentChooser() {
            @Override
            public boolean checkComponent(Component comp) {
                String alltext = getDisplayedText();
                if (position >= 0) {
                    if (position + text.length() <= alltext.length()) {
                        return (alltext.substring(position, position + text.length())
                                .equals(text));
                    } else {
                        return false;
                    }
                } else {
                    return alltext.indexOf(text) >= 0;
                }
            }

            @Override
            public String getDescription() {
                return ("Has \"" + text + "\" text starting from " + Integer.toString(position) + " position");
            }

            @Override
            public String toString() {
                return "JTextComponentOperator.waitText.ComponentChooser{description = " + getDescription() + '}';
            }
        });
    }

    /**
     * Waits for certain text.
     */
    public void waitText(String text) {
        getOutput().printLine("Wait \"" + text + "\" text in component \n    : " + toStringSource());
        getOutput().printGolden("Wait \"" + text + "\" text");
        waitState(new JTextComponentByTextFinder(text, getComparator()));
    }

    /**
     * Wait for caret to be moved to certain position.
     *
     * @param position a position which caret supposed to be moved to.
     */
    public void waitCaretPosition(final int position) {
        getOutput()
                .printLine("Wait caret to be at \"" + Integer.toString(position)
                        + " position in component \n    : "
                        + toStringSource());
        getOutput().printGolden("Wait caret to be at \"" + Integer.toString(position) + " position");
        waitState(new ComponentChooser() {
            @Override
            public boolean checkComponent(Component comp) {
                return getCaretPosition() == position;
            }

            @Override
            public String getDescription() {
                return "Has caret at " + Integer.toString(position) + " position";
            }

            @Override
            public String toString() {
                return "JTextComponentOperator.waitCaretPosition.ComponentChooser{description = " + getDescription()
                        + '}';
            }
        });
    }

    @Override
    public Hashtable<String, Object> getDump() {
        Hashtable<String, Object> result = super.getDump();
        result.put(TEXT_DPROP, ((JTextComponent) getSource()).getText());
        String selected = ((JTextComponent) getSource()).getSelectedText();
        result.put(SELECTED_TEXT_DPROP, (selected != null) ? selected : "");
        result.put(IS_EDITABLE_DPROP, ((JTextComponent) getSource()).isEditable() ? "true" : "false");
        return result;
    }

    ////////////////////////////////////////////////////////
    // Mapping                                             //
    public void addCaretListener(final CaretListener caretListener) {
        runMapping(new MapVoidAction("addCaretListener") {
            @Override
            public void map() {
                ((JTextComponent) getSource()).addCaretListener(caretListener);
            }
        });
    }

    public void copy() {
        runMapping(new MapVoidAction("copy") {
            @Override
            public void map() {
                ((JTextComponent) getSource()).copy();
            }
        });
    }

    public void cut() {
        runMapping(new MapVoidAction("cut") {
            @Override
            public void map() {
                ((JTextComponent) getSource()).cut();
            }
        });
    }

    public javax.swing.Action[] getActions() {
        return ((javax.swing.Action[]) runMapping(new MapAction<Object>("getActions") {
            @Override
            public Object map() {
                return ((JTextComponent) getSource()).getActions();
            }
        }));
    }

    public Caret getCaret() {
        return (runMapping(new MapAction<Caret>("getCaret") {
            @Override
            public Caret map() {
                return ((JTextComponent) getSource()).getCaret();
            }
        }));
    }

    public Color getCaretColor() {
        return (runMapping(new MapAction<Color>("getCaretColor") {
            @Override
            public Color map() {
                return ((JTextComponent) getSource()).getCaretColor();
            }
        }));
    }

    public int getCaretPosition() {
        return (runMapping(new MapIntegerAction("getCaretPosition") {
            @Override
            public int map() {
                return ((JTextComponent) getSource()).getCaretPosition();
            }
        }));
    }

    public Color getDisabledTextColor() {
        return (runMapping(new MapAction<Color>("getDisabledTextColor") {
            @Override
            public Color map() {
                return ((JTextComponent) getSource()).getDisabledTextColor();
            }
        }));
    }

    public Document getDocument() {
        return (runMapping(new MapAction<Document>("getDocument") {
            @Override
            public Document map() {
                return ((JTextComponent) getSource()).getDocument();
            }
        }));
    }

    public char getFocusAccelerator() {
        return (runMapping(new MapCharacterAction("getFocusAccelerator") {
            @Override
            public char map() {
                return ((JTextComponent) getSource()).getFocusAccelerator();
            }
        }));
    }

    public Highlighter getHighlighter() {
        return (runMapping(new MapAction<Highlighter>("getHighlighter") {
            @Override
            public Highlighter map() {
                return ((JTextComponent) getSource()).getHighlighter();
            }
        }));
    }

    public Keymap getKeymap() {
        return (runMapping(new MapAction<Keymap>("getKeymap") {
            @Override
            public Keymap map() {
                return ((JTextComponent) getSource()).getKeymap();
            }
        }));
    }

    public Insets getMargin() {
        return (runMapping(new MapAction<Insets>("getMargin") {
            @Override
            public Insets map() {
                return ((JTextComponent) getSource()).getMargin();
            }
        }));
    }

    public Dimension getPreferredScrollableViewportSize() {
        return (runMapping(new MapAction<Dimension>("getPreferredScrollableViewportSize") {
            @Override
            public Dimension map() {
                return ((JTextComponent) getSource()).getPreferredScrollableViewportSize();
            }
        }));
    }

    public int getScrollableBlockIncrement(final Rectangle rectangle, final int i, final int i1) {
        return (runMapping(new MapIntegerAction("getScrollableBlockIncrement") {
            @Override
            public int map() {
                return ((JTextComponent) getSource()).getScrollableBlockIncrement(rectangle, i, i1);
            }
        }));
    }

    public boolean getScrollableTracksViewportHeight() {
        return (runMapping(new MapBooleanAction("getScrollableTracksViewportHeight") {
            @Override
            public boolean map() {
                return ((JTextComponent) getSource()).getScrollableTracksViewportHeight();
            }
        }));
    }

    public boolean getScrollableTracksViewportWidth() {
        return (runMapping(new MapBooleanAction("getScrollableTracksViewportWidth") {
            @Override
            public boolean map() {
                return ((JTextComponent) getSource()).getScrollableTracksViewportWidth();
            }
        }));
    }

    public int getScrollableUnitIncrement(final Rectangle rectangle, final int i, final int i1) {
        return (runMapping(new MapIntegerAction("getScrollableUnitIncrement") {
            @Override
            public int map() {
                return ((JTextComponent) getSource()).getScrollableUnitIncrement(rectangle, i, i1);
            }
        }));
    }

    public String getSelectedText() {
        return (runMapping(new MapAction<String>("getSelectedText") {
            @Override
            public String map() {
                return ((JTextComponent) getSource()).getSelectedText();
            }
        }));
    }

    public Color getSelectedTextColor() {
        return (runMapping(new MapAction<Color>("getSelectedTextColor") {
            @Override
            public Color map() {
                return ((JTextComponent) getSource()).getSelectedTextColor();
            }
        }));
    }

    public Color getSelectionColor() {
        return (runMapping(new MapAction<Color>("getSelectionColor") {
            @Override
            public Color map() {
                return ((JTextComponent) getSource()).getSelectionColor();
            }
        }));
    }

    public int getSelectionEnd() {
        return (runMapping(new MapIntegerAction("getSelectionEnd") {
            @Override
            public int map() {
                return ((JTextComponent) getSource()).getSelectionEnd();
            }
        }));
    }

    public int getSelectionStart() {
        return (runMapping(new MapIntegerAction("getSelectionStart") {
            @Override
            public int map() {
                return ((JTextComponent) getSource()).getSelectionStart();
            }
        }));
    }

    public String getText() {
        return (runMapping(new MapAction<String>("getText") {
            @Override
            public String map() {
                return ((JTextComponent) getSource()).getText();
            }
        }));
    }

    public String getText(final int i, final int i1) {
        return (runMapping(new MapAction<String>("getText") {
            @Override
            public String map() throws BadLocationException {
                return ((JTextComponent) getSource()).getText(i, i1);
            }
        }));
    }

    public TextUI getUI() {
        return (runMapping(new MapAction<TextUI>("getUI") {
            @Override
            public TextUI map() {
                return ((JTextComponent) getSource()).getUI();
            }
        }));
    }

    public boolean isEditable() {
        return (runMapping(new MapBooleanAction("isEditable") {
            @Override
            public boolean map() {
                return ((JTextComponent) getSource()).isEditable();
            }
        }));
    }

    public Rectangle modelToView(final int i) {
        return (runMapping(new MapAction<Rectangle>("modelToView") {
            @Override
            public Rectangle map() throws BadLocationException {
                return ((JTextComponent) getSource()).modelToView(i);
            }
        }));
    }

    public void moveCaretPosition(final int i) {
        runMapping(new MapVoidAction("moveCaretPosition") {
            @Override
            public void map() {
                ((JTextComponent) getSource()).moveCaretPosition(i);
            }
        });
    }

    public void paste() {
        runMapping(new MapVoidAction("paste") {
            @Override
            public void map() {
                ((JTextComponent) getSource()).paste();
            }
        });
    }

    public void read(final Reader reader, final Object object) {
        runMapping(new MapVoidAction("read") {
            @Override
            public void map() throws IOException {
                ((JTextComponent) getSource()).read(reader, object);
            }
        });
    }

    public void removeCaretListener(final CaretListener caretListener) {
        runMapping(new MapVoidAction("removeCaretListener") {
            @Override
            public void map() {
                ((JTextComponent) getSource()).removeCaretListener(caretListener);
            }
        });
    }

    public void replaceSelection(final String string) {
        runMapping(new MapVoidAction("replaceSelection") {
            @Override
            public void map() {
                ((JTextComponent) getSource()).replaceSelection(string);
            }
        });
    }

    public void select(final int i, final int i1) {
        runMapping(new MapVoidAction("select") {
            @Override
            public void map() {
                ((JTextComponent) getSource()).select(i, i1);
            }
        });
    }

    public void selectAll() {
        runMapping(new MapVoidAction("selectAll") {
            @Override
            public void map() {
                ((JTextComponent) getSource()).selectAll();
            }
        });
    }

    public void setCaret(final Caret caret) {
        runMapping(new MapVoidAction("setCaret") {
            @Override
            public void map() {
                ((JTextComponent) getSource()).setCaret(caret);
            }
        });
    }

    public void setCaretColor(final Color color) {
        runMapping(new MapVoidAction("setCaretColor") {
            @Override
            public void map() {
                ((JTextComponent) getSource()).setCaretColor(color);
            }
        });
    }

    public void setCaretPosition(final int i) {
        runMapping(new MapVoidAction("setCaretPosition") {
            @Override
            public void map() {
                ((JTextComponent) getSource()).setCaretPosition(i);
            }
        });
    }

    public void setDisabledTextColor(final Color color) {
        runMapping(new MapVoidAction("setDisabledTextColor") {
            @Override
            public void map() {
                ((JTextComponent) getSource()).setDisabledTextColor(color);
            }
        });
    }

    public void setDocument(final Document document) {
        runMapping(new MapVoidAction("setDocument") {
            @Override
            public void map() {
                ((JTextComponent) getSource()).setDocument(document);
            }
        });
    }

    public void setEditable(final boolean b) {
        runMapping(new MapVoidAction("setEditable") {
            @Override
            public void map() {
                ((JTextComponent) getSource()).setEditable(b);
            }
        });
    }

    public void setFocusAccelerator(final char c) {
        runMapping(new MapVoidAction("setFocusAccelerator") {
            @Override
            public void map() {
                ((JTextComponent) getSource()).setFocusAccelerator(c);
            }
        });
    }

    public void setHighlighter(final Highlighter highlighter) {
        runMapping(new MapVoidAction("setHighlighter") {
            @Override
            public void map() {
                ((JTextComponent) getSource()).setHighlighter(highlighter);
            }
        });
    }

    public void setKeymap(final Keymap keymap) {
        runMapping(new MapVoidAction("setKeymap") {
            @Override
            public void map() {
                ((JTextComponent) getSource()).setKeymap(keymap);
            }
        });
    }

    public void setMargin(final Insets insets) {
        runMapping(new MapVoidAction("setMargin") {
            @Override
            public void map() {
                ((JTextComponent) getSource()).setMargin(insets);
            }
        });
    }

    public void setSelectedTextColor(final Color color) {
        runMapping(new MapVoidAction("setSelectedTextColor") {
            @Override
            public void map() {
                ((JTextComponent) getSource()).setSelectedTextColor(color);
            }
        });
    }

    public void setSelectionColor(final Color color) {
        runMapping(new MapVoidAction("setSelectionColor") {
            @Override
            public void map() {
                ((JTextComponent) getSource()).setSelectionColor(color);
            }
        });
    }

    public void setSelectionEnd(final int i) {
        runMapping(new MapVoidAction("setSelectionEnd") {
            @Override
            public void map() {
                ((JTextComponent) getSource()).setSelectionEnd(i);
            }
        });
    }

    public void setSelectionStart(final int i) {
        runMapping(new MapVoidAction("setSelectionStart") {
            @Override
            public void map() {
                ((JTextComponent) getSource()).setSelectionStart(i);
            }
        });
    }

    public void setText(final String string) {
        runMapping(new MapVoidAction("setText") {
            @Override
            public void map() {
                ((JTextComponent) getSource()).setText(string);
            }
        });
    }

    public void setUI(final TextUI textUI) {
        runMapping(new MapVoidAction("setUI") {
            @Override
            public void map() {
                ((JTextComponent) getSource()).setUI(textUI);
            }
        });
    }

    public int viewToModel(final Point point) {
        return (runMapping(new MapIntegerAction("viewToModel") {
            @Override
            public int map() {
                return ((JTextComponent) getSource()).viewToModel(point);
            }
        }));
    }

    public void write(final Writer writer) {
        runMapping(new MapVoidAction("write") {
            @Override
            public void map() throws IOException {
                ((JTextComponent) getSource()).write(writer);
            }
        });
    }

    // End of mapping                                      //
    ////////////////////////////////////////////////////////
    /**
     * Can be throught during a text operation if text has not been found in the
     * component.
     */
    public class NoSuchTextException extends JemmyInputException {

        private static final long serialVersionUID = 42L;

        public NoSuchTextException(String text) {
            super("No such text as \"" + text + "\"", getSource());
        }
    }

    /**
     * Interface defining additional text cearch criteria.
     *
     * @see #getPositionByText(java.lang.String,
     * JTextComponentOperator.TextChooser)
     */
    public interface TextChooser {

        /**
         * Checkes if position fits the criteria.
         *
         * @return true if the position fits the criteria.
         */
        public boolean checkPosition(Document document, int offset);

        /**
         * Returns a printable description of the criteria.
         *
         * @return a description of this chooser.
         */
        public String getDescription();
    }

    /**
     * Allows to find component by text.
     */
    public static class JTextComponentByTextFinder implements ComponentChooser {

        String label;
        StringComparator comparator;

        /**
         * Constructs JTextComponentByTextFinder.
         */
        public JTextComponentByTextFinder(String lb, StringComparator comparator) {
            label = lb;
            this.comparator = comparator;
        }

        /**
         * Constructs JTextComponentByTextFinder.
         */
        public JTextComponentByTextFinder(String lb) {
            this(lb, Operator.getDefaultStringComparator());
        }

        @Override
        public boolean checkComponent(Component comp) {
            if (comp instanceof JTextComponent) {
                if (((JTextComponent) comp).getText() != null) {
                    return (comparator.equals(((JTextComponent) comp).getText(), label));
                }
            }
            return false;
        }

        @Override
        public String getDescription() {
            return "JTextComponent with text \"" + label + "\"";
        }

        @Override
        public String toString() {
            return "JTextComponentByTextFinder{" + "label=" + label + ", comparator=" + comparator + '}';
        }
    }

    /**
     * Checks component type.
     */
    public static class JTextComponentFinder extends Finder {

        /**
         * Constructs JTextComponentFinder.
         */
        public JTextComponentFinder(ComponentChooser sf) {
            super(JTextComponent.class, sf);
        }

        /**
         * Constructs JTextComponentFinder.
         */
        public JTextComponentFinder() {
            super(JTextComponent.class);
        }
    }
}
