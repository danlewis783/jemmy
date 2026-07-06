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
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Objects;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.plaf.ScrollPaneUI;
import org.jspecify.annotations.Nullable;
import org.netbeans.jemmy.ComponentChooser;
import org.netbeans.jemmy.ComponentSearcher;
import org.netbeans.jemmy.Outputable;
import org.netbeans.jemmy.TestOut;
import org.netbeans.jemmy.Timeoutable;
import org.netbeans.jemmy.Timeouts;
import org.netbeans.jemmy.drivers.scrolling.ScrollAdjuster;
import org.netbeans.jemmy.util.EmptyVisualizer;

/**
 * Timeouts used:
 * <ul>
 * <li>JScrollBarOperator.OneScrollClickTimeout - time for one scroll click</li>
 * <li>JScrollBarOperator.WholeScrollTimeout - time for the whole scrolling</li>
 * <li>ComponentOperator.WaitComponentTimeout - time to wait component displayed</li>
 * </ul>
 *
 * @see org.netbeans.jemmy.Timeouts
 *
 */
public class JScrollPaneOperator extends JComponentOperator implements Timeoutable, Outputable {

    private static int X_POINT_RECT_SIZE = 6;
    private static int Y_POINT_RECT_SIZE = 4;

    private @SuppressWarnings("NullAway.Init") Timeouts timeouts;
    private @SuppressWarnings("NullAway.Init") TestOut output;
    private @Nullable JScrollBarOperator hScrollBarOper = null;
    private @Nullable JScrollBarOperator vScrollBarOper = null;

    public JScrollPaneOperator(JScrollPane b) {
        super(b);
    }

    public JScrollPaneOperator(ContainerOperator<?> cont, ComponentChooser chooser, int index) {
        this((JScrollPane) cont.waitSubComponent(new JScrollPaneFinder(chooser), index));
        copyEnvironment(cont);
    }

    public JScrollPaneOperator(ContainerOperator<?> cont, ComponentChooser chooser) {
        this(cont, chooser, 0);
    }

    /**
     * Waits component in container first. Uses cont's timeout and
     * output for waiting and to init operator.
     */
    public JScrollPaneOperator(ContainerOperator<?> cont, int index) {
        this((JScrollPane) waitComponent(cont, new JScrollPaneFinder(), index));
        copyEnvironment(cont);
    }

    /**
     * Waits component in container first. Uses cont's timeout and
     * output for waiting and to init operator.
     */
    public JScrollPaneOperator(ContainerOperator<?> cont) {
        this(cont, 0);
    }

    /**
     * Searches JScrollPane in container.
     *
     * @return JScrollPane instance or null if component was not found.
     */
    public static @Nullable JScrollPane findJScrollPane(Container cont, ComponentChooser chooser, int index) {
        return (JScrollPane) findComponent(cont, new JScrollPaneFinder(chooser), index);
    }

    /**
     * Searches 0'th JScrollPane in container.
     *
     * @return JScrollPane instance or null if component was not found.
     */
    public static @Nullable JScrollPane findJScrollPane(Container cont, ComponentChooser chooser) {
        return findJScrollPane(cont, chooser, 0);
    }

    /**
     * Searches JScrollPane in container.
     *
     * @return JScrollPane instance or null if component was not found.
     */
    public static @Nullable JScrollPane findJScrollPane(Container cont, int index) {
        return findJScrollPane(
                cont, ComponentSearcher.getTrueChooser(Integer.toString(index) + "'th JScrollPane instance"), index);
    }

    /**
     * Searches 0'th JScrollPane in container.
     *
     * @return JScrollPane instance or null if component was not found.
     */
    public static @Nullable JScrollPane findJScrollPane(Container cont) {
        return findJScrollPane(cont, 0);
    }

    /**
     * Searches JScrollPane object which component lies on.
     *
     * @return JScrollPane instance or null if component was not found.
     */
    public static @Nullable JScrollPane findJScrollPaneUnder(Component comp, ComponentChooser chooser) {
        return (JScrollPane) findContainerUnder(comp, new JScrollPaneFinder(chooser));
    }

    /**
     * Searches JScrollPane object which component lies on.
     *
     * @return JScrollPane instance or null if component was not found.
     */
    public static @Nullable JScrollPane findJScrollPaneUnder(Component comp) {
        return findJScrollPaneUnder(comp, new JScrollPaneFinder());
    }

    /**
     * Waits JScrollPane in container.
     *
     * @return JScrollPane instance or null if component was not displayed.
     */
    public static JScrollPane waitJScrollPane(Container cont, ComponentChooser chooser, int index) {
        return (JScrollPane) waitComponent(cont, new JScrollPaneFinder(chooser), index);
    }

    /**
     * Waits 0'th JScrollPane in container.
     *
     * @return JScrollPane instance or null if component was not displayed.
     */
    public static JScrollPane waitJScrollPane(Container cont, ComponentChooser chooser) {
        return waitJScrollPane(cont, chooser, 0);
    }

    /**
     * Waits JScrollPane in container.
     *
     * @return JScrollPane instance or null if component was not displayed.
     */
    public static JScrollPane waitJScrollPane(Container cont, int index) {
        return waitJScrollPane(
                cont, ComponentSearcher.getTrueChooser(Integer.toString(index) + "'th JScrollPane instance"), index);
    }

    /**
     * Waits 0'th JScrollPane in container.
     *
     * @return JScrollPane instance or null if component was not displayed.
     */
    public static JScrollPane waitJScrollPane(Container cont) {
        return waitJScrollPane(cont, 0);
    }

    /**
     * Sets values for both JScrollBars.
     */
    public void setValues(int hValue, int vValue) {
        initOperators();
        if (hScrollBarOper != null) {
            hScrollBarOper.setValue(hValue);
        }

        if (vScrollBarOper != null) {
            vScrollBarOper.setValue(vValue);
        }
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

    /**
     * Scrolls horizontal scroll bar.
     */
    public void scrollToHorizontalValue(int value) {
        output.printTrace(
                "Scroll JScrollPane to " + Integer.toString(value) + " horizontal value \n" + toStringSource());
        output.printGolden("Scroll JScrollPane to " + Integer.toString(value) + " horizontal value");
        initOperators();
        makeComponentVisible();
        if (hScrollBarOper != null && hScrollBarOper.getSource().isVisible()) {
            hScrollBarOper.scrollToValue(value);
        }
    }

    /**
     * Scrolls horizontal scroll bar.
     */
    public void scrollToHorizontalValue(double proportionalValue) {
        output.printTrace("Scroll JScrollPane to " + Double.toString(proportionalValue)
                + " proportional horizontal value \n" + toStringSource());
        output.printGolden(
                "Scroll JScrollPane to " + Double.toString(proportionalValue) + " proportional horizontal value");
        initOperators();
        makeComponentVisible();
        if (hScrollBarOper != null && hScrollBarOper.getSource().isVisible()) {
            hScrollBarOper.scrollToValue(proportionalValue);
        }
    }

    /**
     * Scrolls vertical scroll bar.
     */
    public void scrollToVerticalValue(int value) {
        output.printTrace("Scroll JScrollPane to " + Integer.toString(value) + " vertical value \n" + toStringSource());
        output.printGolden("Scroll JScrollPane to " + Integer.toString(value) + " vertical value");
        initOperators();
        makeComponentVisible();
        if (vScrollBarOper != null && vScrollBarOper.getSource().isVisible()) {
            vScrollBarOper.scrollToValue(value);
        }
    }

    /**
     * Scrolls vertical scroll bar.
     */
    public void scrollToVerticalValue(double proportionalValue) {
        output.printTrace("Scroll JScrollPane to " + Double.toString(proportionalValue)
                + " proportional vertical value \n" + toStringSource());
        output.printGolden(
                "Scroll JScrollPane to " + Double.toString(proportionalValue) + " proportional vertical value");
        initOperators();
        makeComponentVisible();
        if (vScrollBarOper != null && vScrollBarOper.getSource().isVisible()) {
            vScrollBarOper.scrollToValue(proportionalValue);
        }
    }

    /**
     * Scrolls both scroll bars.
     */
    public void scrollToValues(int valueX, int valueY) {
        scrollToVerticalValue(valueX);
        scrollToHorizontalValue(valueX);
    }

    /**
     * Scrolls both scroll bars.
     */
    public void scrollToValues(double proportionalValueX, double proportionalValueY) {
        scrollToVerticalValue(proportionalValueX);
        scrollToHorizontalValue(proportionalValueY);
    }

    public void scrollToTop() {
        output.printTrace("Scroll JScrollPane to top\n" + toStringSource());
        output.printGolden("Scroll JScrollPane to top");
        initOperators();
        makeComponentVisible();
        if (vScrollBarOper != null && vScrollBarOper.getSource().isVisible()) {
            vScrollBarOper.scrollToMinimum();
        }
    }

    public void scrollToBottom() {
        output.printTrace("Scroll JScrollPane to bottom\n" + toStringSource());
        output.printGolden("Scroll JScrollPane to bottom");
        initOperators();
        makeComponentVisible();
        if (vScrollBarOper != null && vScrollBarOper.getSource().isVisible()) {
            vScrollBarOper.scrollToMaximum();
        }
    }

    public void scrollToLeft() {
        output.printTrace("Scroll JScrollPane to left\n" + toStringSource());
        output.printGolden("Scroll JScrollPane to left");
        initOperators();
        makeComponentVisible();
        if (hScrollBarOper != null && hScrollBarOper.getSource().isVisible()) {
            hScrollBarOper.scrollToMinimum();
        }
    }

    public void scrollToRight() {
        output.printTrace("Scroll JScrollPane to right\n" + toStringSource());
        output.printGolden("Scroll JScrollPane to right");
        initOperators();
        makeComponentVisible();
        if (hScrollBarOper != null && hScrollBarOper.getSource().isVisible()) {
            hScrollBarOper.scrollToMaximum();
        }
    }

    public void scrollToComponentRectangle(Component comp, int x, int y, int width, int height) {
        initOperators();
        makeComponentVisible();
        if (hScrollBarOper != null && hScrollBarOper.getSource().isVisible()) {
            hScrollBarOper.scrollTo(new ComponentRectChecker(comp, x, y, width, height, JScrollBar.HORIZONTAL));
        }
        if (vScrollBarOper != null && vScrollBarOper.getSource().isVisible()) {
            vScrollBarOper.scrollTo(new ComponentRectChecker(comp, x, y, width, height, JScrollBar.VERTICAL));
        }
    }

    public void scrollToComponentPoint(Component comp, int x, int y) {
        scrollToComponentRectangle(
                comp, x - X_POINT_RECT_SIZE, y - Y_POINT_RECT_SIZE, 2 * X_POINT_RECT_SIZE, 2 * Y_POINT_RECT_SIZE);
    }

    /**
     * Scrolls pane to component on this pane. Component should lay on the
     * JScrollPane view.
     */
    public void scrollToComponent(final Component comp) {
        String componentToString = runMapping(new Operator.MapAction<String>("comp.toString()") {
            @Override
            public String map() {
                return comp.toString();
            }
        });
        output.printTrace("Scroll JScrollPane " + toStringSource() + "\nto component " + componentToString);
        output.printGolden("Scroll JScrollPane to " + comp.getClass().getName() + " component.");
        scrollToComponentRectangle(comp, 0, 0, comp.getWidth(), comp.getHeight());
    }

    /**
     * Returns operator used for horizontal scrollbar.
     *
     * @return an operator for the horizontal scrollbar.
     */
    public JScrollBarOperator getHScrollBarOperator() {
        initOperators();
        return Objects.requireNonNull(hScrollBarOper, "scroll pane has no horizontal scroll bar");
    }

    /**
     * Returns operator used for vertical scrollbar.
     *
     * @return an operator for the vertical scrollbar.
     */
    public JScrollBarOperator getVScrollBarOperator() {
        initOperators();
        return Objects.requireNonNull(vScrollBarOper, "scroll pane has no vertical scroll bar");
    }

    /**
     * Checks if component's rectangle is inside view port (no scrolling
     * necessary).
     *
     * @return true if pointed subcomponent rectangle is inside the scrolling
     * area.
     */
    public boolean checkInside(Component comp, int x, int y, int width, int height) {
        Component view = getViewport().getView();
        Point toPoint = SwingUtilities.convertPoint(comp, x, y, getViewport().getView());
        initOperators();
        if (hScrollBarOper != null && hScrollBarOper.getSource().isVisible()) {
            if (toPoint.x < hScrollBarOper.getValue()) {
                return false;
            }
            if (comp.getWidth() > view.getWidth()) {
                return toPoint.x > 0;
            } else {
                return (toPoint.x + comp.getWidth() > hScrollBarOper.getValue() + view.getWidth());
            }
        }
        if (vScrollBarOper != null && vScrollBarOper.getSource().isVisible()) {
            if (toPoint.y < vScrollBarOper.getValue()) {
                return false;
            }
            if (comp.getHeight() > view.getHeight()) {
                return toPoint.y > 0;
            } else {
                return (toPoint.y + comp.getHeight() > vScrollBarOper.getValue() + view.getHeight());
            }
        }
        return true;
    }

    /**
     * Checks if component is inside view port (no scrolling necessary).
     *
     * @return true if pointed subcomponent is inside the scrolling area.
     */
    public boolean checkInside(Component comp) {
        return checkInside(comp, 0, 0, comp.getWidth(), comp.getHeight());
    }

    ////////////////////////////////////////////////////////
    // Mapping                                             //
    public JScrollBar createHorizontalScrollBar() {
        return (runMapping(new MapAction<JScrollBar>("createHorizontalScrollBar") {
            @Override
            public JScrollBar map() {
                return ((JScrollPane) getSource()).createHorizontalScrollBar();
            }
        }));
    }

    public JScrollBar createVerticalScrollBar() {
        return (runMapping(new MapAction<JScrollBar>("createVerticalScrollBar") {
            @Override
            public JScrollBar map() {
                return ((JScrollPane) getSource()).createVerticalScrollBar();
            }
        }));
    }

    public JViewport getColumnHeader() {
        return (runMapping(new MapAction<JViewport>("getColumnHeader") {
            @Override
            public JViewport map() {
                return ((JScrollPane) getSource()).getColumnHeader();
            }
        }));
    }

    public Component getCorner(final String string) {
        return (runMapping(new MapAction<Component>("getCorner") {
            @Override
            public Component map() {
                return ((JScrollPane) getSource()).getCorner(string);
            }
        }));
    }

    public JScrollBar getHorizontalScrollBar() {
        return (runMapping(new MapAction<JScrollBar>("getHorizontalScrollBar") {
            @Override
            public JScrollBar map() {
                return ((JScrollPane) getSource()).getHorizontalScrollBar();
            }
        }));
    }

    public int getHorizontalScrollBarPolicy() {
        return (runMapping(new MapIntegerAction("getHorizontalScrollBarPolicy") {
            @Override
            public int map() {
                return ((JScrollPane) getSource()).getHorizontalScrollBarPolicy();
            }
        }));
    }

    public JViewport getRowHeader() {
        return (runMapping(new MapAction<JViewport>("getRowHeader") {
            @Override
            public JViewport map() {
                return ((JScrollPane) getSource()).getRowHeader();
            }
        }));
    }

    public ScrollPaneUI getUI() {
        return (runMapping(new MapAction<ScrollPaneUI>("getUI") {
            @Override
            public ScrollPaneUI map() {
                return ((JScrollPane) getSource()).getUI();
            }
        }));
    }

    public JScrollBar getVerticalScrollBar() {
        return (runMapping(new MapAction<JScrollBar>("getVerticalScrollBar") {
            @Override
            public JScrollBar map() {
                return ((JScrollPane) getSource()).getVerticalScrollBar();
            }
        }));
    }

    public int getVerticalScrollBarPolicy() {
        return (runMapping(new MapIntegerAction("getVerticalScrollBarPolicy") {
            @Override
            public int map() {
                return ((JScrollPane) getSource()).getVerticalScrollBarPolicy();
            }
        }));
    }

    public JViewport getViewport() {
        return (runMapping(new MapAction<JViewport>("getViewport") {
            @Override
            public JViewport map() {
                return ((JScrollPane) getSource()).getViewport();
            }
        }));
    }

    public Border getViewportBorder() {
        return (runMapping(new MapAction<Border>("getViewportBorder") {
            @Override
            public Border map() {
                return ((JScrollPane) getSource()).getViewportBorder();
            }
        }));
    }

    public Rectangle getViewportBorderBounds() {
        return (runMapping(new MapAction<Rectangle>("getViewportBorderBounds") {
            @Override
            public Rectangle map() {
                return ((JScrollPane) getSource()).getViewportBorderBounds();
            }
        }));
    }

    public void setColumnHeader(final JViewport jViewport) {
        runMapping(new MapVoidAction("setColumnHeader") {
            @Override
            public void map() {
                ((JScrollPane) getSource()).setColumnHeader(jViewport);
            }
        });
    }

    public void setColumnHeaderView(final Component component) {
        runMapping(new MapVoidAction("setColumnHeaderView") {
            @Override
            public void map() {
                ((JScrollPane) getSource()).setColumnHeaderView(component);
            }
        });
    }

    public void setCorner(final String string, final Component component) {
        runMapping(new MapVoidAction("setCorner") {
            @Override
            public void map() {
                ((JScrollPane) getSource()).setCorner(string, component);
            }
        });
    }

    public void setHorizontalScrollBar(final JScrollBar jScrollBar) {
        runMapping(new MapVoidAction("setHorizontalScrollBar") {
            @Override
            public void map() {
                ((JScrollPane) getSource()).setHorizontalScrollBar(jScrollBar);
            }
        });
    }

    public void setHorizontalScrollBarPolicy(final int i) {
        runMapping(new MapVoidAction("setHorizontalScrollBarPolicy") {
            @Override
            public void map() {
                ((JScrollPane) getSource()).setHorizontalScrollBarPolicy(i);
            }
        });
    }

    public void setRowHeader(final JViewport jViewport) {
        runMapping(new MapVoidAction("setRowHeader") {
            @Override
            public void map() {
                ((JScrollPane) getSource()).setRowHeader(jViewport);
            }
        });
    }

    public void setRowHeaderView(final Component component) {
        runMapping(new MapVoidAction("setRowHeaderView") {
            @Override
            public void map() {
                ((JScrollPane) getSource()).setRowHeaderView(component);
            }
        });
    }

    public void setUI(final ScrollPaneUI scrollPaneUI) {
        runMapping(new MapVoidAction("setUI") {
            @Override
            public void map() {
                ((JScrollPane) getSource()).setUI(scrollPaneUI);
            }
        });
    }

    public void setVerticalScrollBar(final JScrollBar jScrollBar) {
        runMapping(new MapVoidAction("setVerticalScrollBar") {
            @Override
            public void map() {
                ((JScrollPane) getSource()).setVerticalScrollBar(jScrollBar);
            }
        });
    }

    public void setVerticalScrollBarPolicy(final int i) {
        runMapping(new MapVoidAction("setVerticalScrollBarPolicy") {
            @Override
            public void map() {
                ((JScrollPane) getSource()).setVerticalScrollBarPolicy(i);
            }
        });
    }

    public void setViewport(final JViewport jViewport) {
        runMapping(new MapVoidAction("setViewport") {
            @Override
            public void map() {
                ((JScrollPane) getSource()).setViewport(jViewport);
            }
        });
    }

    public void setViewportBorder(final Border border) {
        runMapping(new MapVoidAction("setViewportBorder") {
            @Override
            public void map() {
                ((JScrollPane) getSource()).setViewportBorder(border);
            }
        });
    }

    public void setViewportView(final Component component) {
        runMapping(new MapVoidAction("setViewportView") {
            @Override
            public void map() {
                ((JScrollPane) getSource()).setViewportView(component);
            }
        });
    }

    // End of mapping                                      //
    ////////////////////////////////////////////////////////
    private void initOperators() {
        if (hScrollBarOper == null
                && getHorizontalScrollBar() != null
                && getHorizontalScrollBar().isVisible()) {
            hScrollBarOper = new JScrollBarOperator(getHorizontalScrollBar());
            hScrollBarOper.copyEnvironment(this);
            hScrollBarOper.setVisualizer(new EmptyVisualizer());
        }
        if (vScrollBarOper == null
                && getVerticalScrollBar() != null
                && getVerticalScrollBar().isVisible()) {
            vScrollBarOper = new JScrollBarOperator(getVerticalScrollBar());
            vScrollBarOper.copyEnvironment(this);
            vScrollBarOper.setVisualizer(new EmptyVisualizer());
        }
    }

    private class ComponentRectChecker implements JScrollBarOperator.ScrollChecker {

        Component comp;
        int x;
        int y;
        int width;
        int height;
        int orientation;

        public ComponentRectChecker(Component comp, int x, int y, int width, int height, int orientation) {
            this.comp = comp;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.orientation = orientation;
        }

        @Override
        public int getScrollDirection(JScrollBarOperator oper) {
            Point toPoint =
                    SwingUtilities.convertPoint(comp, x, y, getViewport().getView());
            int to = (orientation == JScrollBar.HORIZONTAL) ? toPoint.x : toPoint.y;
            int ln = (orientation == JScrollBar.HORIZONTAL) ? width : height;
            int lv = (orientation == JScrollBar.HORIZONTAL)
                    ? getViewport().getWidth()
                    : getViewport().getHeight();
            int vl = oper.getValue();
            if (to < vl) {
                return ScrollAdjuster.DECREASE_SCROLL_DIRECTION;
            } else if ((to + ln - 1) > (vl + lv) && to > vl) {
                return ScrollAdjuster.INCREASE_SCROLL_DIRECTION;
            } else {
                return ScrollAdjuster.DO_NOT_TOUCH_SCROLL_DIRECTION;
            }
        }

        @Override
        public String getDescription() {
            return "";
        }

        @Override
        public String toString() {
            return "ComponentRectChecker{" + "comp=" + comp + ", x=" + x + ", y=" + y + ", width=" + width + ", height="
                    + height + ", orientation=" + orientation + '}';
        }
    }

    /**
     * Checks component type.
     */
    public static class JScrollPaneFinder extends Finder {

        /**
         * Constructs JScrollPaneFinder.
         */
        public JScrollPaneFinder(ComponentChooser sf) {
            super(JScrollPane.class, sf);
        }

        /**
         * Constructs JScrollPaneFinder.
         */
        public JScrollPaneFinder() {
            super(JScrollPane.class);
        }
    }
}
