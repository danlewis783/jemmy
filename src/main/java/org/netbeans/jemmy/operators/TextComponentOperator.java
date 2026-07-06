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

import java.awt.Component;
import java.awt.Container;
import java.awt.TextComponent;
import java.awt.event.TextListener;
import java.util.Hashtable;
import org.jspecify.annotations.Nullable;
import org.netbeans.jemmy.Action;
import org.netbeans.jemmy.ComponentChooser;
import org.netbeans.jemmy.Outputable;
import org.netbeans.jemmy.TestOut;
import org.netbeans.jemmy.Timeoutable;
import org.netbeans.jemmy.Timeouts;
import org.netbeans.jemmy.drivers.DriverManager;
import org.netbeans.jemmy.drivers.TextDriver;

/**
 * This operator type covers java.awt.TextArea component.
 *
 * @see org.netbeans.jemmy.Timeouts
 *
 */
public class TextComponentOperator extends ComponentOperator implements Timeoutable, Outputable {

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

    private static final long PUSH_KEY_TIMEOUT = 0;
    private static final long BETWEEN_KEYS_TIMEOUT = 0;
    private static final long CHANGE_CARET_POSITION_TIMEOUT = 60000;
    private static final long TYPE_TEXT_TIMEOUT = 60000;

    private @SuppressWarnings("NullAway.Init") Timeouts timeouts;
    private @SuppressWarnings("NullAway.Init") TestOut output;

    private TextDriver driver;

    /**
     * @param b The {@code java.awt.TextComponent} managed by this
     * instance.
     */
    public TextComponentOperator(TextComponent b) {
        super(b);
        driver = DriverManager.getTextDriver(getClass());
    }

    public TextComponentOperator(ContainerOperator<?> cont, ComponentChooser chooser, int index) {
        this((TextComponent) cont.waitSubComponent(new TextComponentFinder(chooser), index));
        copyEnvironment(cont);
    }

    public TextComponentOperator(ContainerOperator<?> cont, ComponentChooser chooser) {
        this(cont, chooser, 0);
    }

    /**
     * Waits for a component in a container to show. The component
     * is identified as the {@code index+1}'th
     * {@code java.awt.TextComponent} that shows, lies below the container
     * in the display containment hierarchy, and that has the desired text. Uses
     * cont's timeout and output for waiting and to init this operator.
     *
     * @param cont The operator for a container containing the sought for
     * textComponent.
     * @param index Ordinal component index. The first component has
     * {@code index} 0.
     * @see ComponentOperator#isCaptionEqual(String, String, boolean, boolean)
     */
    public TextComponentOperator(ContainerOperator<?> cont, String text, int index) {
        this((TextComponent) waitComponent(cont, new TextComponentByTextFinder(text, cont.getComparator()), index));
        copyEnvironment(cont);
    }

    /**
     * Waits for a component in a container to show. The component
     * is identified as the first {@code java.awt.TextComponent} that
     * shows, lies below the container in the display containment hierarchy, and
     * that has the desired text. Uses cont's timeout and output for waiting and
     * to init this operator.
     *
     * @param cont The operator for a container containing the sought for
     * textComponent.
     * @see ComponentOperator#isCaptionEqual(String, String, boolean, boolean)
     */
    public TextComponentOperator(ContainerOperator<?> cont, String text) {
        this(cont, text, 0);
    }

    /**
     * Waits component in container first. Uses cont's timeout and
     * output for waiting and to init operator.
     *
     * @param cont The operator for a container containing the sought for
     * textComponent.
     * @see ComponentOperator#isCaptionEqual(String, String, boolean, boolean)
     */
    public TextComponentOperator(ContainerOperator<?> cont, int index) {
        this((TextComponent) waitComponent(cont, new TextComponentFinder(), index));
        copyEnvironment(cont);
    }

    /**
     * Waits component in container first. Uses cont's timeout and
     * output for waiting and to init operator.
     *
     * @param cont The operator for a container containing the sought for
     * textComponent.
     * @see ComponentOperator#isCaptionEqual(String, String, boolean, boolean)
     */
    public TextComponentOperator(ContainerOperator<?> cont) {
        this(cont, 0);
    }

    /**
     * Searches TextComponent in a container.
     *
     * @param cont Container in which to search for the component. The container
     * lies above the component in the display containment hierarchy. The
     * containment need not be direct.
     * @param index Ordinal component index. The first {@code index} is 0.
     * @return TextComponent instance or null if component was not found.
     */
    public static @Nullable TextComponent findTextComponent(Container cont, ComponentChooser chooser, int index) {
        return (TextComponent) findComponent(cont, new TextComponentFinder(chooser), index);
    }

    /**
     * Searches for the first TextComponent in a container.
     *
     * @param cont Container in which to search for the component. The container
     * lies above the component in the display containment hierarchy. The
     * containment need not be direct.
     * @return TextComponent instance or null if component was not found.
     */
    public static @Nullable TextComponent findTextComponent(Container cont, ComponentChooser chooser) {
        return findTextComponent(cont, chooser, 0);
    }

    /**
     * Searches TextComponent by text.
     *
     * @return TextComponent instance or null if component was not found.
     * @see ComponentOperator#isCaptionEqual(String, String, boolean, boolean)
     */
    public static @Nullable TextComponent findTextComponent(
            Container cont, String text, boolean ce, boolean ccs, int index) {
        return findTextComponent(
                cont, new TextComponentByTextFinder(text, new DefaultStringComparator(ce, ccs)), index);
    }

    /**
     * Searches TextComponent by text.
     *
     * @return TextComponent instance or null if component was not found.
     * @see ComponentOperator#isCaptionEqual(String, String, boolean, boolean)
     */
    public static @Nullable TextComponent findTextComponent(Container cont, String text, boolean ce, boolean ccs) {
        return findTextComponent(cont, text, ce, ccs, 0);
    }

    /**
     * Waits TextComponent in container.
     *
     * @return TextComponent instance.
     */
    public static TextComponent waitTextComponent(Container cont, ComponentChooser chooser, int index) {
        return (TextComponent) waitComponent(cont, new TextComponentFinder(chooser), index);
    }

    /**
     * Waits 0'th TextComponent in container.
     *
     * @return TextComponent instance.
     */
    public static TextComponent waitTextComponent(Container cont, ComponentChooser chooser) {
        return waitTextComponent(cont, chooser, 0);
    }

    /**
     * Waits TextComponent by text.
     *
     * @return TextComponent instance.
     * @see ComponentOperator#isCaptionEqual(String, String, boolean, boolean)
     */
    public static TextComponent waitTextComponent(Container cont, String text, boolean ce, boolean ccs, int index) {
        return waitTextComponent(
                cont, new TextComponentByTextFinder(text, new DefaultStringComparator(ce, ccs)), index);
    }

    /**
     * Waits TextComponent by text.
     *
     * @return TextComponent instance.
     * @see ComponentOperator#isCaptionEqual(String, String, boolean, boolean)
     */
    public static TextComponent waitTextComponent(Container cont, String text, boolean ce, boolean ccs) {
        return waitTextComponent(cont, text, ce, ccs, 0);
    }

    static {
        Timeouts.initDefault("TextComponentOperator.PushKeyTimeout", PUSH_KEY_TIMEOUT);
        Timeouts.initDefault("TextComponentOperator.BetweenKeysTimeout", BETWEEN_KEYS_TIMEOUT);
        Timeouts.initDefault("TextComponentOperator.ChangeCaretPositionTimeout", CHANGE_CARET_POSITION_TIMEOUT);
        Timeouts.initDefault("TextComponentOperator.TypeTextTimeout", TYPE_TEXT_TIMEOUT);
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
    public void copyEnvironment(Operator anotherOperator) {
        super.copyEnvironment(anotherOperator);
        driver = (TextDriver)
                DriverManager.getDriver(DriverManager.TEXT_DRIVER_ID, getClass(), anotherOperator.getProperties());
    }

    public void changeCaretPosition(final int position) {
        makeComponentVisible();
        produceTimeRestricted(
                new Action<Void, Void>() {
                    @Override
                    public Void launch(Void obj) {
                        driver.changeCaretPosition(TextComponentOperator.this, position);
                        return null;
                    }

                    @Override
                    public String getDescription() {
                        return "Caret moving";
                    }

                    @Override
                    public String toString() {
                        return "TextComponentOperator.changeCaretPosition.Action{description = " + getDescription()
                                + '}';
                    }
                },
                "TextComponentOperator.ChangeCaretPositionTimeout");
    }

    /**
     * Selects a part of text.
     */
    public void selectText(final int startPosition, final int finalPosition) {
        makeComponentVisible();
        produceTimeRestricted(
                new Action<Void, Void>() {
                    @Override
                    public Void launch(Void obj) {
                        driver.selectText(TextComponentOperator.this, startPosition, finalPosition);
                        return null;
                    }

                    @Override
                    public String getDescription() {
                        return "Text selecting";
                    }

                    @Override
                    public String toString() {
                        return "TextComponentOperator.selectText.Action{description = " + getDescription() + '}';
                    }
                },
                "TextComponentOperator.TypeTextTimeout");
    }

    /**
     * Finds start text position.
     *
     * @param index Index of text instance (first instance has index 0)
     * @return Caret position correspondent to text start.
     */
    public int getPositionByText(String text, int index) {
        String allText = getText();
        int position = 0;
        int ind = 0;
        while ((position = allText.indexOf(text, position)) >= 0) {
            if (ind == index) {
                return position;
            } else {
                ind++;
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
    public int getPositionByText(String text) {
        return getPositionByText(text, 0);
    }

    public void clearText() {
        output.printLine("Clearing text in text component\n    : " + toStringSource());
        output.printGolden("Clearing text in text component");
        makeComponentVisible();
        produceTimeRestricted(
                new Action<Void, Void>() {
                    @Override
                    public Void launch(Void obj) {
                        driver.clearText(TextComponentOperator.this);
                        return null;
                    }

                    @Override
                    public String getDescription() {
                        return "Text clearing";
                    }

                    @Override
                    public String toString() {
                        return "TextComponentOperator.clearText.Action{description = " + getDescription() + '}';
                    }
                },
                "TextComponentOperator.TypeTextTimeout");
    }

    /**
     * Types text starting from known position.
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
                        driver.typeText(TextComponentOperator.this, text, caretPosition);
                        return null;
                    }

                    @Override
                    public String getDescription() {
                        return "Text typing";
                    }

                    @Override
                    public String toString() {
                        return "TextComponentOperator.typeText.Action{description = " + getDescription() + '}';
                    }
                },
                "TextComponentOperator.TypeTextTimeout");
    }

    /**
     * Types text starting from known position.
     */
    public void typeText(String text) {
        typeText(text, getCaretPosition());
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
                        driver.enterText(TextComponentOperator.this, text);
                        return null;
                    }

                    @Override
                    public String getDescription() {
                        return "Text entering";
                    }

                    @Override
                    public String toString() {
                        return "TextComponentOperator.enterText.Action{description = " + getDescription() + '}';
                    }
                },
                "TextComponentOperator.TypeTextTimeout");
    }

    @Override
    public Hashtable<String, Object> getDump() {
        Hashtable<String, Object> result = super.getDump();
        result.put(TEXT_DPROP, ((TextComponent) getSource()).getText());
        String selected = ((TextComponent) getSource()).getSelectedText();
        result.put(SELECTED_TEXT_DPROP, (selected != null) ? selected : "");
        return result;
    }

    ////////////////////////////////////////////////////////
    // Mapping                                             //
    public void addTextListener(final TextListener textListener) {
        runMapping(new MapVoidAction("addTextListener") {
            @Override
            public void map() {
                ((TextComponent) getSource()).addTextListener(textListener);
            }
        });
    }

    public int getCaretPosition() {
        return (runMapping(new MapIntegerAction("getCaretPosition") {
            @Override
            public int map() {
                return ((TextComponent) getSource()).getCaretPosition();
            }
        }));
    }

    public String getSelectedText() {
        return (runMapping(new MapAction<String>("getSelectedText") {
            @Override
            public String map() {
                return ((TextComponent) getSource()).getSelectedText();
            }
        }));
    }

    public int getSelectionEnd() {
        return (runMapping(new MapIntegerAction("getSelectionEnd") {
            @Override
            public int map() {
                return ((TextComponent) getSource()).getSelectionEnd();
            }
        }));
    }

    public int getSelectionStart() {
        return (runMapping(new MapIntegerAction("getSelectionStart") {
            @Override
            public int map() {
                return ((TextComponent) getSource()).getSelectionStart();
            }
        }));
    }

    public String getText() {
        return (runMapping(new MapAction<String>("getText") {
            @Override
            public String map() {
                return ((TextComponent) getSource()).getText();
            }
        }));
    }

    public boolean isEditable() {
        return (runMapping(new MapBooleanAction("isEditable") {
            @Override
            public boolean map() {
                return ((TextComponent) getSource()).isEditable();
            }
        }));
    }

    public void removeTextListener(final TextListener textListener) {
        runMapping(new MapVoidAction("removeTextListener") {
            @Override
            public void map() {
                ((TextComponent) getSource()).removeTextListener(textListener);
            }
        });
    }

    public void select(final int i, final int i1) {
        runMapping(new MapVoidAction("select") {
            @Override
            public void map() {
                ((TextComponent) getSource()).select(i, i1);
            }
        });
    }

    public void selectAll() {
        runMapping(new MapVoidAction("selectAll") {
            @Override
            public void map() {
                ((TextComponent) getSource()).selectAll();
            }
        });
    }

    public void setCaretPosition(final int i) {
        runMapping(new MapVoidAction("setCaretPosition") {
            @Override
            public void map() {
                ((TextComponent) getSource()).setCaretPosition(i);
            }
        });
    }

    public void setEditable(final boolean b) {
        runMapping(new MapVoidAction("setEditable") {
            @Override
            public void map() {
                ((TextComponent) getSource()).setEditable(b);
            }
        });
    }

    public void setSelectionEnd(final int i) {
        runMapping(new MapVoidAction("setSelectionEnd") {
            @Override
            public void map() {
                ((TextComponent) getSource()).setSelectionEnd(i);
            }
        });
    }

    public void setSelectionStart(final int i) {
        runMapping(new MapVoidAction("setSelectionStart") {
            @Override
            public void map() {
                ((TextComponent) getSource()).setSelectionStart(i);
            }
        });
    }

    public void setText(final String string) {
        runMapping(new MapVoidAction("setText") {
            @Override
            public void map() {
                ((TextComponent) getSource()).setText(string);
            }
        });
    }

    // End of mapping                                      //
    ////////////////////////////////////////////////////////
    /**
     * Return a TextDriver used by this component.
     *
     * @return a driver got by the operator during creation.
     */
    protected TextDriver getTextDriver() {
        return driver;
    }

    /**
     * Allows to find component by text.
     */
    public static class TextComponentByTextFinder implements ComponentChooser {

        String label;
        StringComparator comparator;

        public TextComponentByTextFinder(String lb, StringComparator comparator) {
            label = lb;
            this.comparator = comparator;
        }

        public TextComponentByTextFinder(String lb) {
            this(lb, Operator.getDefaultStringComparator());
        }

        @Override
        public boolean checkComponent(Component comp) {
            if (comp instanceof TextComponent) {
                if (((TextComponent) comp).getText() != null) {
                    return (comparator.equals(((TextComponent) comp).getText(), label));
                }
            }
            return false;
        }

        @Override
        public String getDescription() {
            return "TextComponent with text \"" + label + "\"";
        }

        @Override
        public String toString() {
            return "TextComponentByTextFinder{" + "label=" + label + ", comparator=" + comparator + '}';
        }
    }

    /**
     * Checks component type.
     */
    public static class TextComponentFinder extends Finder {

        public TextComponentFinder(ComponentChooser sf) {
            super(TextComponent.class, sf);
        }

        public TextComponentFinder() {
            super(TextComponent.class);
        }
    }
}
