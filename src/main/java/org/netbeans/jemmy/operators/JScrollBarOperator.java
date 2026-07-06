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
import java.awt.event.AdjustmentListener;
import java.util.Hashtable;
import java.util.Objects;
import javax.swing.BoundedRangeModel;
import javax.swing.JButton;
import javax.swing.JScrollBar;
import javax.swing.plaf.ScrollBarUI;
import org.jspecify.annotations.Nullable;
import org.netbeans.jemmy.Action;
import org.netbeans.jemmy.ComponentChooser;
import org.netbeans.jemmy.ComponentSearcher;
import org.netbeans.jemmy.Outputable;
import org.netbeans.jemmy.TestOut;
import org.netbeans.jemmy.Timeoutable;
import org.netbeans.jemmy.Timeouts;
import org.netbeans.jemmy.Waitable;
import org.netbeans.jemmy.drivers.DriverManager;
import org.netbeans.jemmy.drivers.ScrollDriver;
import org.netbeans.jemmy.drivers.scrolling.ScrollAdjuster;
import org.netbeans.jemmy.util.EmptyVisualizer;

/**
 * Operator is supposed to be used to operate with an instance of javax.swing.JScrollBar class.
 * <p>
 * Timeouts used:
 * <ul>
 * <li>JScrollBarOperator.OneScrollClickTimeout - time for one scroll click</li>
 * <li>JScrollBarOperator.WholeScrollTimeout - time for the whole scrolling</li>
 * <li>JScrollBarOperator.BeforeDropTimeout - to sleep before drop JScrollBarOperator.DragAndDropScrollingDelta - to
 * sleep before drag steps ComponentOperator.WaitComponentTimeout - time to wait component displayed</li>
 * </ul>
 *
 * @see org.netbeans.jemmy.Timeouts
 *
 */
public class JScrollBarOperator extends JComponentOperator implements Timeoutable, Outputable {

    /**
     * Identifier for a "minimum" property.
     *
     * @see #getDump
     */
    public static final String MINIMUM_DPROP = "Minimum";

    /**
     * Identifier for a "maximum" property.
     *
     * @see #getDump
     */
    public static final String MAXIMUM_DPROP = "Maximum";

    /**
     * Identifier for a "value" property.
     *
     * @see #getDump
     */
    public static final String VALUE_DPROP = "Value";

    /**
     * Identifier for a "orientation" property.
     *
     * @see #getDump
     */
    public static final String ORIENTATION_DPROP = "Orientation";

    /**
     * Identifier for a "HORIZONTAL" value of "orientation" property.
     *
     * @see #getDump
     */
    public static final String HORIZONTAL_ORIENTATION_DPROP_VALUE = "HORIZONTAL";

    /**
     * Identifier for a "VERTICAL" value of "orientation" property.
     *
     * @see #getDump
     */
    public static final String VERTICAL_ORIENTATION_DPROP_VALUE = "VERTICAL";

    private static final long ONE_SCROLL_CLICK_TIMEOUT = 0;
    private static final long WHOLE_SCROLL_TIMEOUT = 60000;
    private static final long BEFORE_DROP_TIMEOUT = 0;
    private static final long DRAG_AND_DROP_SCROLLING_DELTA = 0;

    private @SuppressWarnings("NullAway.Init") Timeouts timeouts;
    private @SuppressWarnings("NullAway.Init") TestOut output;
    private @SuppressWarnings("NullAway.Init") @Nullable JButtonOperator minButtOperator;
    private @SuppressWarnings("NullAway.Init") @Nullable JButtonOperator maxButtOperator;

    private ScrollDriver driver;

    public JScrollBarOperator(JScrollBar b) {
        super(b);
        driver = DriverManager.getScrollDriver(getClass());
    }

    public JScrollBarOperator(ContainerOperator<?> cont, ComponentChooser chooser, int index) {
        this((JScrollBar) cont.waitSubComponent(new JScrollBarFinder(chooser), index));
        copyEnvironment(cont);
    }

    public JScrollBarOperator(ContainerOperator<?> cont, ComponentChooser chooser) {
        this(cont, chooser, 0);
    }

    /**
     * Waits component in container first. Uses cont's timeout and
     * output for waiting and to init operator.
     */
    public JScrollBarOperator(ContainerOperator<?> cont, int index) {
        this((JScrollBar) waitComponent(cont, new JScrollBarFinder(), index));
        copyEnvironment(cont);
    }

    /**
     * Waits component in container first. Uses cont's timeout and
     * output for waiting and to init operator.
     */
    public JScrollBarOperator(ContainerOperator<?> cont) {
        this(cont, 0);
    }

    /**
     * Searches JScrollBar in container.
     *
     * @return JScrollBar instance or null if component was not found.
     */
    public static @Nullable JScrollBar findJScrollBar(Container cont, ComponentChooser chooser, int index) {
        return (JScrollBar) findComponent(cont, new JScrollBarFinder(chooser), index);
    }

    /**
     * Searches 0'th JScrollBar in container.
     *
     * @return JScrollBar instance or null if component was not found.
     */
    public static @Nullable JScrollBar findJScrollBar(Container cont, ComponentChooser chooser) {
        return findJScrollBar(cont, chooser, 0);
    }

    /**
     * Searches JScrollBar in container.
     *
     * @return JScrollBar instance or null if component was not found.
     */
    public static @Nullable JScrollBar findJScrollBar(Container cont, int index) {
        return findJScrollBar(
                cont, ComponentSearcher.getTrueChooser(Integer.toString(index) + "'th JScrollBar instance"), index);
    }

    /**
     * Searches 0'th JScrollBar in container.
     *
     * @return JScrollBar instance or null if component was not found.
     */
    public static @Nullable JScrollBar findJScrollBar(Container cont) {
        return findJScrollBar(cont, 0);
    }

    /**
     * Waits JScrollBar in container.
     *
     * @return JScrollBar instance or null if component was not displayed.
     */
    public static JScrollBar waitJScrollBar(Container cont, ComponentChooser chooser, int index) {
        return (JScrollBar) waitComponent(cont, new JScrollBarFinder(chooser), index);
    }

    /**
     * Waits 0'th JScrollBar in container.
     *
     * @return JScrollBar instance or null if component was not displayed.
     */
    public static JScrollBar waitJScrollBar(Container cont, ComponentChooser chooser) {
        return waitJScrollBar(cont, chooser, 0);
    }

    /**
     * Waits JScrollBar in container.
     *
     * @return JScrollBar instance or null if component was not displayed.
     */
    public static JScrollBar waitJScrollBar(Container cont, int index) {
        return waitJScrollBar(
                cont, ComponentSearcher.getTrueChooser(Integer.toString(index) + "'th JScrollBar instance"), index);
    }

    /**
     * Waits 0'th JScrollBar in container.
     *
     * @return JScrollBar instance or null if component was not displayed.
     */
    public static JScrollBar waitJScrollBar(Container cont) {
        return waitJScrollBar(cont, 0);
    }

    static {
        Timeouts.initDefault("JScrollBarOperator.OneScrollClickTimeout", ONE_SCROLL_CLICK_TIMEOUT);
        Timeouts.initDefault("JScrollBarOperator.WholeScrollTimeout", WHOLE_SCROLL_TIMEOUT);
        Timeouts.initDefault("JScrollBarOperator.BeforeDropTimeout", BEFORE_DROP_TIMEOUT);
        Timeouts.initDefault("JScrollBarOperator.DragAndDropScrollingDelta", DRAG_AND_DROP_SCROLLING_DELTA);
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
    public void setTimeouts(Timeouts timeouts) {
        this.timeouts = timeouts;
        super.setTimeouts(timeouts);
    }

    @Override
    public Timeouts getTimeouts() {
        return timeouts;
    }

    @Override
    public void copyEnvironment(Operator anotherOperator) {
        super.copyEnvironment(anotherOperator);
        driver = (ScrollDriver)
                DriverManager.getDriver(DriverManager.SCROLL_DRIVER_ID, getClass(), anotherOperator.getProperties());
    }

    /**
     * Does simple scroll click.
     *
     * @deprecated All scrolling is done through a ScrollDriver registered to
     * this operator type.
     */
    @Deprecated
    public void scroll(boolean increase) {
        scrollToValue(getValue() + (increase ? 1 : -1));
    }

    /**
     * Scrolls scrollbar to the position defined by w parameter. Uses
     * ScrollDriver registered to this operator type.
     *
     * @see #scrollTo(JScrollBarOperator.ScrollChecker)
     */
    public <P> void scrollTo(Waitable<?, P> w, P waiterParam, boolean increase) {
        scrollTo(new WaitableChecker<>(w, waiterParam, increase, this));
    }

    /**
     * Scrolls scrollbar to the position defined by a ScrollChecker
     * implementation.
     *
     * @see ScrollChecker
     */
    public void scrollTo(ScrollChecker checker) {
        scrollTo(new CheckerAdjustable(checker, this));
    }

    /**
     * Scrolls scrollbar to the position defined by a ScrollAdjuster
     * implementation.
     */
    public void scrollTo(final ScrollAdjuster adj) {
        initOperators();
        produceTimeRestricted(
                new Action<Void, Void>() {
                    @Override
                    public Void launch(Void obj) {
                        driver.scroll(JScrollBarOperator.this, adj);
                        return null;
                    }

                    @Override
                    public String getDescription() {
                        return "Scrolling";
                    }

                    @Override
                    public String toString() {
                        return "JScrollBarOperator.scrollTo.Action{description = " + getDescription() + '}';
                    }
                },
                "JScrollBarOperator.WholeScrollTimeout");
    }

    /**
     * Scrolls scroll bar to necessary value.
     */
    public void scrollToValue(int value) {
        output.printTrace("Scroll JScrollBar to " + Integer.toString(value) + " value\n" + toStringSource());
        output.printGolden("Scroll JScrollBar to " + Integer.toString(value) + " value");
        scrollTo(new ValueScrollAdjuster(value));
    }

    /**
     * Scrolls scroll bar to necessary proportional value.
     *
     * @param proportionalValue Proportional scroll to. Must be >= 0 and <= 1.
     */
    public void scrollToValue(double proportionalValue) {
        output.printTrace("Scroll JScrollBar to " + Double.toString(proportionalValue) + " proportional value\n"
                + toStringSource());
        output.printGolden("Scroll JScrollBar to " + Double.toString(proportionalValue) + " proportional value");
        scrollTo(new ValueScrollAdjuster(
                (int) (getMinimum() + (getMaximum() - getVisibleAmount() - getMinimum()) * proportionalValue)));
    }

    public void scrollToMinimum() {
        output.printTrace("Scroll JScrollBar to minimum value\n" + toStringSource());
        output.printGolden("Scroll JScrollBar to minimum value");
        initOperators();
        produceTimeRestricted(
                new Action<Void, Void>() {
                    @Override
                    public Void launch(Void obj) {
                        driver.scrollToMinimum(JScrollBarOperator.this, getOrientation());
                        return null;
                    }

                    @Override
                    public String getDescription() {
                        return "Scrolling";
                    }

                    @Override
                    public String toString() {
                        return "JScrollBarOperator.scrollToMinimum.Action{description = " + getDescription() + '}';
                    }
                },
                "JScrollBarOperator.WholeScrollTimeout");
    }

    public void scrollToMaximum() {
        output.printTrace("Scroll JScrollBar to maximum value\n" + toStringSource());
        output.printGolden("Scroll JScrollBar to maximum value");
        initOperators();
        produceTimeRestricted(
                new Action<Void, Void>() {
                    @Override
                    public Void launch(Void obj) {
                        driver.scrollToMaximum(JScrollBarOperator.this, getOrientation());
                        return null;
                    }

                    @Override
                    public String getDescription() {
                        return "Scrolling";
                    }

                    @Override
                    public String toString() {
                        return "JScrollBarOperator.scrollToMaximum.Action{description = " + getDescription() + '}';
                    }
                },
                "JScrollBarOperator.WholeScrollTimeout");
    }

    /**
     * Returns a button responsible for value decreasing.
     *
     * @return an operator for the decrease button.
     */
    public JButtonOperator getDecreaseButton() {
        initOperators();
        return Objects.requireNonNull(minButtOperator, "scroll bar has no decrease button");
    }

    /**
     * Returns a button responsible for value increasing.
     *
     * @return an operator for the increase button.
     */
    public JButtonOperator getIncreaseButton() {
        initOperators();
        return Objects.requireNonNull(maxButtOperator, "scroll bar has no increase button");
    }

    @Override
    public Hashtable<String, Object> getDump() {
        Hashtable<String, Object> result = super.getDump();
        result.put(MINIMUM_DPROP, Integer.toString(((JScrollBar) getSource()).getMinimum()));
        result.put(MAXIMUM_DPROP, Integer.toString(((JScrollBar) getSource()).getMaximum()));
        result.put(
                ORIENTATION_DPROP,
                (((JScrollBar) getSource()).getOrientation() == JScrollBar.HORIZONTAL)
                        ? HORIZONTAL_ORIENTATION_DPROP_VALUE
                        : VERTICAL_ORIENTATION_DPROP_VALUE);
        result.put(VALUE_DPROP, Integer.toString(((JScrollBar) getSource()).getValue()));
        return result;
    }

    ////////////////////////////////////////////////////////
    // Mapping                                             //
    public void addAdjustmentListener(final AdjustmentListener adjustmentListener) {
        runMapping(new MapVoidAction("addAdjustmentListener") {
            @Override
            public void map() {
                ((JScrollBar) getSource()).addAdjustmentListener(adjustmentListener);
            }
        });
    }

    public int getBlockIncrement() {
        return (runMapping(new MapIntegerAction("getBlockIncrement") {
            @Override
            public int map() {
                return ((JScrollBar) getSource()).getBlockIncrement();
            }
        }));
    }

    public int getBlockIncrement(final int i) {
        return (runMapping(new MapIntegerAction("getBlockIncrement") {
            @Override
            public int map() {
                return ((JScrollBar) getSource()).getBlockIncrement(i);
            }
        }));
    }

    public int getMaximum() {
        return (runMapping(new MapIntegerAction("getMaximum") {
            @Override
            public int map() {
                return ((JScrollBar) getSource()).getMaximum();
            }
        }));
    }

    public int getMinimum() {
        return (runMapping(new MapIntegerAction("getMinimum") {
            @Override
            public int map() {
                return ((JScrollBar) getSource()).getMinimum();
            }
        }));
    }

    public BoundedRangeModel getModel() {
        return (runMapping(new MapAction<BoundedRangeModel>("getModel") {
            @Override
            public BoundedRangeModel map() {
                return ((JScrollBar) getSource()).getModel();
            }
        }));
    }

    public int getOrientation() {
        return (runMapping(new MapIntegerAction("getOrientation") {
            @Override
            public int map() {
                return ((JScrollBar) getSource()).getOrientation();
            }
        }));
    }

    public ScrollBarUI getUI() {
        return (runMapping(new MapAction<ScrollBarUI>("getUI") {
            @Override
            public ScrollBarUI map() {
                return ((JScrollBar) getSource()).getUI();
            }
        }));
    }

    public int getUnitIncrement() {
        return (runMapping(new MapIntegerAction("getUnitIncrement") {
            @Override
            public int map() {
                return ((JScrollBar) getSource()).getUnitIncrement();
            }
        }));
    }

    public int getUnitIncrement(final int i) {
        return (runMapping(new MapIntegerAction("getUnitIncrement") {
            @Override
            public int map() {
                return ((JScrollBar) getSource()).getUnitIncrement(i);
            }
        }));
    }

    public int getValue() {
        return (runMapping(new MapIntegerAction("getValue") {
            @Override
            public int map() {
                return ((JScrollBar) getSource()).getValue();
            }
        }));
    }

    public boolean getValueIsAdjusting() {
        return (runMapping(new MapBooleanAction("getValueIsAdjusting") {
            @Override
            public boolean map() {
                return ((JScrollBar) getSource()).getValueIsAdjusting();
            }
        }));
    }

    public int getVisibleAmount() {
        return (runMapping(new MapIntegerAction("getVisibleAmount") {
            @Override
            public int map() {
                return ((JScrollBar) getSource()).getVisibleAmount();
            }
        }));
    }

    public void removeAdjustmentListener(final AdjustmentListener adjustmentListener) {
        runMapping(new MapVoidAction("removeAdjustmentListener") {
            @Override
            public void map() {
                ((JScrollBar) getSource()).removeAdjustmentListener(adjustmentListener);
            }
        });
    }

    public void setBlockIncrement(final int i) {
        runMapping(new MapVoidAction("setBlockIncrement") {
            @Override
            public void map() {
                ((JScrollBar) getSource()).setBlockIncrement(i);
            }
        });
    }

    public void setMaximum(final int i) {
        runMapping(new MapVoidAction("setMaximum") {
            @Override
            public void map() {
                ((JScrollBar) getSource()).setMaximum(i);
            }
        });
    }

    public void setMinimum(final int i) {
        runMapping(new MapVoidAction("setMinimum") {
            @Override
            public void map() {
                ((JScrollBar) getSource()).setMinimum(i);
            }
        });
    }

    public void setModel(final BoundedRangeModel boundedRangeModel) {
        runMapping(new MapVoidAction("setModel") {
            @Override
            public void map() {
                ((JScrollBar) getSource()).setModel(boundedRangeModel);
            }
        });
    }

    public void setOrientation(final int i) {
        runMapping(new MapVoidAction("setOrientation") {
            @Override
            public void map() {
                ((JScrollBar) getSource()).setOrientation(i);
            }
        });
    }

    public void setUnitIncrement(final int i) {
        runMapping(new MapVoidAction("setUnitIncrement") {
            @Override
            public void map() {
                ((JScrollBar) getSource()).setUnitIncrement(i);
            }
        });
    }

    public void setValue(final int i) {
        runMapping(new MapVoidAction("setValue") {
            @Override
            public void map() {
                ((JScrollBar) getSource()).setValue(i);
            }
        });
    }

    public void setValueIsAdjusting(final boolean b) {
        runMapping(new MapVoidAction("setValueIsAdjusting") {
            @Override
            public void map() {
                ((JScrollBar) getSource()).setValueIsAdjusting(b);
            }
        });
    }

    public void setValues(final int i, final int i1, final int i2, final int i3) {
        runMapping(new MapVoidAction("setValues") {
            @Override
            public void map() {
                ((JScrollBar) getSource()).setValues(i, i1, i2, i3);
            }
        });
    }

    public void setVisibleAmount(final int i) {
        runMapping(new MapVoidAction("setVisibleAmount") {
            @Override
            public void map() {
                ((JScrollBar) getSource()).setVisibleAmount(i);
            }
        });
    }

    // End of mapping                                      //
    ////////////////////////////////////////////////////////
    private void initOperators() {
        if (minButtOperator != null && maxButtOperator != null) {
            return;
        }
        ComponentChooser chooser = new ComponentChooser() {
            @Override
            public boolean checkComponent(Component comp) {
                return comp instanceof JButton;
            }

            @Override
            public String getDescription() {
                return "";
            }

            @Override
            public String toString() {
                return "JScrollBarOperator.initOperators.ComponentChooser{description = " + getDescription() + '}';
            }
        };
        ComponentSearcher searcher = new ComponentSearcher((Container) getSource());
        searcher.setOutput(output.createErrorOutput());
        JButton butt0 = (JButton) searcher.findComponent(chooser, 0);
        JButton butt1 = (JButton) searcher.findComponent(chooser, 1);

        if (butt0 == null || butt1 == null) {
            minButtOperator = null;
            maxButtOperator = null;
            return;
        }

        JButton minButt, maxButt;

        if (((JScrollBar) getSource()).getOrientation() == JScrollBar.HORIZONTAL) {
            if (butt0.getX() < butt1.getX()) {
                minButt = butt0;
                maxButt = butt1;
            } else {
                minButt = butt1;
                maxButt = butt0;
            }
        } else if (butt0.getY() < butt1.getY()) {
            minButt = butt0;
            maxButt = butt1;
        } else {
            minButt = butt1;
            maxButt = butt0;
        }
        minButtOperator = new JButtonOperator(minButt);
        maxButtOperator = new JButtonOperator(maxButt);

        minButtOperator.copyEnvironment(this);
        maxButtOperator.copyEnvironment(this);

        minButtOperator.setOutput(output.createErrorOutput());
        maxButtOperator.setOutput(output.createErrorOutput());

        Timeouts times = timeouts.cloneThis();
        times.setTimeout(
                "AbstractButtonOperator.PushButtonTimeout",
                times.getTimeout("JScrollBarOperator.OneScrollClickTimeout"));

        minButtOperator.setTimeouts(times);
        maxButtOperator.setTimeouts(times);

        minButtOperator.setVisualizer(new EmptyVisualizer());
        maxButtOperator.setVisualizer(new EmptyVisualizer());
    }

    /**
     * Interface can be used to define some kind of complicated scrolling rules.
     */
    public interface ScrollChecker {

        /**
         * Defines a scrolling direction.
         *
         * @see
         * org.netbeans.jemmy.drivers.scrolling.ScrollAdjuster#INCREASE_SCROLL_DIRECTION
         * @see
         * org.netbeans.jemmy.drivers.scrolling.ScrollAdjuster#DECREASE_SCROLL_DIRECTION
         * @see
         * org.netbeans.jemmy.drivers.scrolling.ScrollAdjuster#DO_NOT_TOUCH_SCROLL_DIRECTION
         * @return one of the following values:
         * ScrollAdjuster.INCREASE_SCROLL_DIRECTION
         * ScrollAdjuster.DECREASE_SCROLL_DIRECTION
         * ScrollAdjuster.DO_NOT_TOUCH_SCROLL_DIRECTION
         */
        public int getScrollDirection(JScrollBarOperator oper);

        /**
         * Scrolling rules decription.
         *
         * @return a description
         */
        public String getDescription();
    }

    private class ValueScrollAdjuster implements ScrollAdjuster {

        int value;

        public ValueScrollAdjuster(int value) {
            this.value = value;
        }

        @Override
        public int getScrollDirection() {
            if (getValue() == value) {
                return ScrollAdjuster.DO_NOT_TOUCH_SCROLL_DIRECTION;
            } else {
                return ((getValue() < value)
                        ? ScrollAdjuster.INCREASE_SCROLL_DIRECTION
                        : ScrollAdjuster.DECREASE_SCROLL_DIRECTION);
            }
        }

        @Override
        public int getScrollOrientation() {
            return getOrientation();
        }

        @Override
        public String getDescription() {
            return "Scroll to " + Integer.toString(value) + " value";
        }

        @Override
        public String toString() {
            return "ValueScrollAdjuster{" + "value=" + value + '}';
        }
    }

    private class WaitableChecker<P> implements ScrollAdjuster {

        Waitable<?, P> w;
        P waitParam;
        boolean increase;
        boolean reached = false;

        public WaitableChecker(Waitable<?, P> w, P waitParam, boolean increase, JScrollBarOperator oper) {
            this.w = w;
            this.waitParam = waitParam;
            this.increase = increase;
        }

        @Override
        public int getScrollDirection() {
            if (!reached && w.actionProduced(waitParam) != null) {
                reached = true;
            }
            if (reached) {
                return ScrollAdjuster.DO_NOT_TOUCH_SCROLL_DIRECTION;
            } else {
                return (increase ? ScrollAdjuster.INCREASE_SCROLL_DIRECTION : ScrollAdjuster.DECREASE_SCROLL_DIRECTION);
            }
        }

        @Override
        public int getScrollOrientation() {
            return getOrientation();
        }

        @Override
        public String getDescription() {
            return w.getDescription();
        }

        @Override
        public String toString() {
            return "WaitableChecker{" + "w=" + w + ", waitParam=" + waitParam + ", increase=" + increase + ", reached="
                    + reached + '}';
        }
    }

    private class CheckerAdjustable implements ScrollAdjuster {

        ScrollChecker checker;
        JScrollBarOperator oper;

        public CheckerAdjustable(ScrollChecker checker, JScrollBarOperator oper) {
            this.checker = checker;
            this.oper = oper;
        }

        @Override
        public int getScrollDirection() {
            return checker.getScrollDirection(oper);
        }

        @Override
        public int getScrollOrientation() {
            return getOrientation();
        }

        @Override
        public String getDescription() {
            return checker.getDescription();
        }

        @Override
        public String toString() {
            return "CheckerAdjustable{" + "checker=" + checker + ", oper=" + oper + '}';
        }
    }

    /**
     * Checks component type.
     */
    public static class JScrollBarFinder extends Finder {

        /**
         * Constructs JScrollBarFinder.
         */
        public JScrollBarFinder(ComponentChooser sf) {
            super(JScrollBar.class, sf);
        }

        /**
         * Constructs JScrollBarFinder.
         */
        public JScrollBarFinder() {
            super(JScrollBar.class);
        }
    }
}
