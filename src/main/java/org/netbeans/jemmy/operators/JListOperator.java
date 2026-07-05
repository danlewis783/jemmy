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

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Hashtable;
import java.util.Vector;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionListener;
import javax.swing.plaf.ListUI;
import org.netbeans.jemmy.ComponentChooser;
import org.netbeans.jemmy.ComponentSearcher;
import org.netbeans.jemmy.JemmyInputException;
import org.netbeans.jemmy.Outputable;
import org.netbeans.jemmy.QueueTool;
import org.netbeans.jemmy.TestOut;
import org.netbeans.jemmy.TimeoutExpiredException;
import org.netbeans.jemmy.drivers.DriverManager;
import org.netbeans.jemmy.drivers.MultiSelListDriver;
import org.netbeans.jemmy.util.EmptyVisualizer;

/**
 * Timeouts used:
 * <ul>
 * <li>ComponentOperator.WaitComponentTimeout - time to wait component displayed</li>
 * <li>ComponentOperator.WaitStateTimeout - time to wait for item, and for item to be selected</li>
 * <li>JScrollBarOperator.OneScrollClickTimeout - time for one scroll click</li>
 * <li>JScrollBarOperator.WholeScrollTimeout - time for the whole scrolling</li>
 * </ul>
 *
 * @see org.netbeans.jemmy.Timeouts
 *
 */
public class JListOperator extends JComponentOperator implements Outputable {

    /**
     * Identifier for a "item" properties.
     *
     * @see #getDump
     */
    public static final String ITEM_PREFIX_DPROP = "Item";

    /**
     * Identifier for a "selected item" property.
     *
     * @see #getDump
     */
    public static final String SELECTED_ITEM_PREFIX_DPROP = "SelectedItem";

    private TestOut output;
    private MultiSelListDriver driver;

    public JListOperator(JList<?> b) {
        super(b);
        driver = DriverManager.getMultiSelListDriver(getClass());
    }

    public JListOperator(ContainerOperator<?> cont, ComponentChooser chooser, int index) {
        this((JList) cont.waitSubComponent(new JListFinder(chooser), index));
        copyEnvironment(cont);
    }

    public JListOperator(ContainerOperator<?> cont, ComponentChooser chooser) {
        this(cont, chooser, 0);
    }

    /**
     * Waits item text first. Uses cont's timeout and output for
     * waiting and to init operator.
     */
    public JListOperator(ContainerOperator<?> cont, String text, int itemIndex, int index) {
        this((JList) waitComponent(cont, new JListByItemFinder(text, itemIndex, cont.getComparator()), index));
        copyEnvironment(cont);
    }

    /**
     * Waits component by selected item text first. Uses cont's
     * timeout and output for waiting and to init operator.
     *
     * @see ComponentOperator#isCaptionEqual(String, String, boolean, boolean)
     */
    public JListOperator(ContainerOperator<?> cont, String text, int index) {
        this(cont, text, -1, index);
    }

    /**
     * Waits component in container first. Uses cont's timeout and
     * output for waiting and to init operator.
     *
     * @see ComponentOperator#isCaptionEqual(String, String, boolean, boolean)
     */
    public JListOperator(ContainerOperator<?> cont, String text) {
        this(cont, text, 0);
    }

    /**
     * Waits component in container first. Uses cont's timeout and
     * output for waiting and to init operator.
     */
    public JListOperator(ContainerOperator<?> cont, int index) {
        this((JList) waitComponent(cont, new JListFinder(), index));
        copyEnvironment(cont);
    }

    /**
     * Waits component in container first. Uses cont's timeout and
     * output for waiting and to init operator.
     */
    public JListOperator(ContainerOperator<?> cont) {
        this(cont, 0);
    }

    /**
     * Searches JList in container.
     *
     * @return JList instance or null if component was not found.
     */
    public static JList<?> findJList(Container cont, ComponentChooser chooser, int index) {
        return (JList) findComponent(cont, new JListFinder(chooser), index);
    }

    /**
     * Searches 0'th JList in container.
     *
     * @return JList instance or null if component was not found.
     */
    public static JList<?> findJList(Container cont, ComponentChooser chooser) {
        return findJList(cont, chooser, 0);
    }

    /**
     * Searches JList by item.
     *
     * @param itemIndex Index of item to compare text. If -1, selected item is
     * checked.
     * @return JList instance or null if component was not found.
     * @see ComponentOperator#isCaptionEqual(String, String, boolean, boolean)
     */
    public static JList<?> findJList(Container cont, String text, boolean ce, boolean ccs, int itemIndex, int index) {
        return findJList(cont, new JListByItemFinder(text, itemIndex, new DefaultStringComparator(ce, ccs)), index);
    }

    /**
     * Searches JList by item.
     *
     * @param itemIndex Index of item to compare text. If -1, selected item is
     * checked.
     * @return JList instance or null if component was not found.
     * @see ComponentOperator#isCaptionEqual(String, String, boolean, boolean)
     */
    public static JList<?> findJList(Container cont, String text, boolean ce, boolean ccs, int itemIndex) {
        return findJList(cont, text, ce, ccs, itemIndex, 0);
    }

    /**
     * Waits JList in container.
     *
     * @return JList instance or null if component was not found.
     */
    public static JList<?> waitJList(Container cont, ComponentChooser chooser, int index) {
        return (JList) waitComponent(cont, new JListFinder(chooser), index);
    }

    /**
     * Waits 0'th JList in container.
     *
     * @return JList instance or null if component was not found.
     */
    public static JList<?> waitJList(Container cont, ComponentChooser chooser) {
        return waitJList(cont, chooser, 0);
    }

    /**
     * Waits JList by item.
     *
     * @param itemIndex Index of item to compare text. If -1, selected item is
     * checked.
     * @return JList instance or null if component was not found.
     * @see ComponentOperator#isCaptionEqual(String, String, boolean, boolean)
     */
    public static JList<?> waitJList(Container cont, String text, boolean ce, boolean ccs, int itemIndex, int index) {
        return waitJList(cont, new JListByItemFinder(text, itemIndex, new DefaultStringComparator(ce, ccs)), index);
    }

    /**
     * Waits JList by item.
     *
     * @param itemIndex Index of item to compare text. If -1, selected item is
     * checked.
     * @return JList instance or null if component was not found.
     * @see ComponentOperator#isCaptionEqual(String, String, boolean, boolean)
     */
    public static JList<?> waitJList(Container cont, String text, boolean ce, boolean ccs, int itemIndex) {
        return waitJList(cont, text, ce, ccs, itemIndex, 0);
    }

    @Override
    public void setOutput(TestOut output) {
        super.setOutput(output.createErrorOutput());
        this.output = output;
    }

    @Override
    public TestOut getOutput() {
        return output;
    }

    @Override
    public void copyEnvironment(Operator anotherOperator) {
        super.copyEnvironment(anotherOperator);
        driver = (MultiSelListDriver) DriverManager.getDriver(
                DriverManager.MULTISELLIST_DRIVER_ID, getClass(), anotherOperator.getProperties());
    }

    /**
     * Gets point to click on itemIndex'th item.
     *
     * @return a Point in component's coordinate system.
     */
    public Point getClickPoint(int itemIndex) {
        Rectangle rect = getCellBounds(itemIndex, itemIndex);
        return (new Point(rect.x + rect.width / 2, rect.y + rect.height / 2));
    }

    /**
     * Ask renderer for component to be displayed.
     *
     * @return Component to be displayed.
     */
    @SuppressWarnings(value = "unchecked")
    public Component getRenderedComponent(int itemIndex, boolean isSelected, boolean cellHasFocus) {
        return (((ListCellRenderer<Object>) getCellRenderer())
                .getListCellRendererComponent(
                        (JList<Object>) getSource(),
                        getModel().getElementAt(itemIndex),
                        itemIndex,
                        isSelected,
                        cellHasFocus));
    }

    /**
     * Ask renderer for component to be displayed. Uses
     * isSelectedIndex(itemIndex) to determine whether item is selected.
     * Supposes item do not have focus.
     *
     * @return Component to be displayed.
     */
    public Component getRenderedComponent(int itemIndex) {
        return getRenderedComponent(itemIndex, isSelectedIndex(itemIndex), false);
    }

    /**
     * Searches for index'th item good from chooser's point of view.
     *
     * @return Item index or -1 if search was insuccessful.
     */
    public int findItemIndex(ListItemChooser chooser, int index) {
        ListModel<?> model = getModel();
        int count = 0;
        for (int i = 0; i < model.getSize(); i++) {
            if (chooser.checkItem(this, i)) {
                if (count == index) {
                    return i;
                } else {
                    count++;
                }
            }
        }
        return -1;
    }

    /**
     * Searches for an item good from chooser's point of view.
     *
     * @return Item index or -1 if serch was insuccessful.
     * @see #findItemIndex(JListOperator.ListItemChooser, int)
     * @see #findItemIndex(String, boolean, boolean)
     */
    public int findItemIndex(ListItemChooser chooser) {
        return findItemIndex(chooser, 0);
    }

    /**
     * Searches for an item good from chooser's point of view.
     *
     * @return Item index or -1 if serch was insuccessful.
     * @see #findItemIndex(JListOperator.ListItemChooser, int)
     * @see #findItemIndex(String, boolean, boolean)
     */
    public int findItemIndex(String item, StringComparator comparator, int index) {
        return findItemIndex(new BySubStringListItemChooser(item, comparator), index);
    }

    /**
     * Searched for index'th item by text.
     *
     * @return Item index or -1 if serch was insuccessful.
     * @see ComponentOperator#isCaptionEqual(String, String, boolean, boolean)
     * @deprecated Use findItemIndex(String, int) or findItemIndex(String,
     * StringComparator, int)
     */
    @Deprecated
    public int findItemIndex(String item, boolean ce, boolean cc, int index) {
        return findItemIndex(item, new DefaultStringComparator(ce, cc), index);
    }

    /**
     * Searched for index'th item by text. Uses StringComparator assigned to
     * this object.
     *
     * @return Item index or -1 if search was insuccessful.
     */
    public int findItemIndex(String item, int index) {
        return findItemIndex(item, getComparator(), index);
    }

    /**
     * Searches for an item good from chooser's point of view.
     *
     * @return Item index or -1 if serch was insuccessful.
     * @see #findItemIndex(JListOperator.ListItemChooser, int)
     * @see #findItemIndex(String, boolean, boolean)
     */
    public int findItemIndex(String item, StringComparator comparator) {
        return findItemIndex(item, comparator, 0);
    }

    /**
     * Searched item by text.
     *
     * @return Item index or -1 if search was insuccessful.
     * @see ComponentOperator#isCaptionEqual(String, String, boolean, boolean)
     * @deprecated Use findItemIndex(String) or findItemIndex(String,
     * StringComparator)
     */
    @Deprecated
    public int findItemIndex(String item, boolean ce, boolean cc) {
        return findItemIndex(item, ce, cc, 0);
    }

    /**
     * Searched for first item by text. Uses StringComparator assigned to this
     * object.
     *
     * @return Item index or -1 if search was insuccessful.
     * @see ComponentOperator#isCaptionEqual(String, String, boolean, boolean)
     */
    public int findItemIndex(String item) {
        return findItemIndex(item, 0);
    }

    /**
     * Searches for index'th item by rendered component.
     *
     * @return Item index or -1 if serch was insuccessful.
     * @see #getRenderedComponent(int, boolean, boolean)
     */
    public int findItemIndex(ComponentChooser chooser, int index) {
        return findItemIndex(new ByRenderedComponentListItemChooser(chooser), index);
    }

    /**
     * Searches for an item by rendered component.
     *
     * @return Item index or -1 if serch was insuccessful.
     * @see #getRenderedComponent(int, boolean, boolean)
     */
    public int findItemIndex(ComponentChooser chooser) {
        return findItemIndex(chooser, 0);
    }

    /**
     * Clicks on item by item index.
     *
     * @return Click point or null if list does not contains itemIndex'th item.
     * @throws NoSuchItemException
     */
    public Object clickOnItem(final int itemIndex, final int clickCount) {
        output.printLine("Click " + Integer.toString(clickCount) + " times on JList\n    : " + toStringSource());
        output.printGolden("Click " + Integer.toString(clickCount) + " times on JList");
        checkIndex(itemIndex);
        try {
            scrollToItem(itemIndex);
        } catch (TimeoutExpiredException e) {
            output.printStackTrace(e);
        }
        if (((JList) getSource()).getModel().getSize() <= itemIndex) {
            output.printErrLine(
                    "JList " + toStringSource() + " does not contain " + Integer.toString(itemIndex) + "'th item");
            return null;
        }
        if (((JList) getSource()).getAutoscrolls()) {
            ((JList) getSource()).ensureIndexIsVisible(itemIndex);
        }
        return (getQueueTool().invokeSmoothly(new QueueTool.QueueAction<Object>("Path selecting") {
            @Override
            public Object launch() {
                Rectangle rect = getCellBounds(itemIndex, itemIndex);
                if (rect == null) {
                    output.printErrLine(
                            "Impossible to determine click point for " + Integer.toString(itemIndex) + "'th item");
                    return null;
                }
                Point point = new Point(
                        (int) (rect.getX() + rect.getWidth() / 2), (int) (rect.getY() + rect.getHeight() / 2));
                Object result = getModel().getElementAt(itemIndex);
                clickMouse(point.x, point.y, clickCount);
                return result;
            }
        }));
    }

    /**
     * Finds item by item text, and do mouse click on it.
     *
     * @return Click point or null if list does not contains itemIndex'th item.
     * @throws NoSuchItemException
     */
    public Object clickOnItem(final String item, final StringComparator comparator, final int clickCount) {
        scrollToItem(findItemIndex(item, comparator, 0));
        return (getQueueTool().invokeSmoothly(new QueueTool.QueueAction<Object>("Path selecting") {
            @Override
            public Object launch() {
                int index = findItemIndex(item, comparator, 0);
                if (index != -1) {
                    return clickOnItem(index, clickCount);
                } else {
                    throw (new NoSuchItemException(item));
                }
            }
        }));
    }

    /**
     * Finds item by item text, and do mouse click on it.
     *
     * @return Click point or null if list does not contains itemIndex'th item.
     * @throws NoSuchItemException
     * @deprecated Use clickOnItem(String, int) or clickOnItem(String,
     * StringComparator, int)
     */
    @Deprecated
    public Object clickOnItem(String item, boolean ce, boolean cc, int clickCount) {
        return clickOnItem(item, new DefaultStringComparator(ce, cc), clickCount);
    }

    /**
     * Finds item by item text, and do mouse click on it. Uses StringComparator
     * assigned to this object.
     *
     * @return Click point or null if list does not contains itemIndex'th item.
     * @throws NoSuchItemException
     */
    public Object clickOnItem(String item, int clickCount) {
        return clickOnItem(item, getComparator(), clickCount);
    }

    /**
     * Finds item by item text, and do simple mouse click on it. Uses
     * StringComparator assigned to this object.
     *
     * @return Click point or null if list does not contains itemIndex'th item.
     * @throws NoSuchItemException
     */
    public Object clickOnItem(String item, StringComparator comparator) {
        return clickOnItem(item, comparator, 1);
    }

    /**
     * Finds item by item text, and do simple mouse click on it.
     *
     * @return Click point or null if list does not contains itemIndex'th item.
     * @throws NoSuchItemException
     * @deprecated Use clickOnItem(String) or clickOnItem(String,
     * StringComparator)
     */
    @Deprecated
    public Object clickOnItem(String item, boolean ce, boolean cc) {
        return clickOnItem(item, ce, cc, 1);
    }

    /**
     * Finds item by item text, and do simple mouse click on it. Uses
     * StringComparator assigned to this object.
     *
     * @return Click point or null if list does not contains itemIndex'th item.
     * @throws NoSuchItemException
     */
    public Object clickOnItem(String item) {
        return clickOnItem(item, 0);
    }

    /**
     * Scrolls to an item if the list is on a JScrollPane component.
     *
     * @see #scrollToItem(String, boolean, boolean)
     *
     * @throws NoSuchItemException
     */
    public void scrollToItem(int itemIndex) {
        output.printTrace("Scroll JList to " + Integer.toString(itemIndex) + "'th item\n    : " + toStringSource());
        output.printGolden("Scroll JList to " + Integer.toString(itemIndex) + "'th item");
        checkIndex(itemIndex);
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
        Rectangle rect = getCellBounds(itemIndex, itemIndex);
        scroller.scrollToComponentRectangle(
                getSource(), (int) rect.getX(), (int) rect.getY(), (int) rect.getWidth(), (int) rect.getHeight());
    }

    /**
     * Scrolls to an item if the list is on a JScrollPane component.
     *
     * @see #scrollToItem(String, boolean, boolean)
     */
    public void scrollToItem(String item, StringComparator comparator) {
        scrollToItem(findItemIndex(item, comparator));
    }

    /**
     * Scrolls to an item if the list is on a JScrollPane component.
     *
     * @see #scrollToItem(String, boolean, boolean)
     *
     * @deprecated Use scrollToItem(String) or scrollToItem(String,
     * StringComparator)
     */
    @Deprecated
    public void scrollToItem(String item, boolean ce, boolean cc) {
        scrollToItem(findItemIndex(item, ce, cc));
    }

    public void selectItem(int index) {
        checkIndex(index);
        driver.selectItem(this, index);
        if (getVerification()) {
            waitItemSelection(index, true);
        }
    }

    /**
     * Selects an item by text.
     */
    public void selectItem(final String item) {
        scrollToItem(findItemIndex(item));
        getQueueTool().invokeSmoothly(new QueueTool.QueueAction<Void>("Path selecting") {
            @Override
            public Void launch() {
                driver.selectItem(JListOperator.this, findItemIndex(item));
                return null;
            }
        });
    }

    public void selectItems(int[] indices) {
        checkIndices(indices);
        driver.selectItems(this, indices);
        if (getVerification()) {
            waitItemsSelection(indices, true);
        }
    }

    /**
     * Selects items by texts.
     */
    public void selectItem(String[] items) {
        int[] indices = new int[items.length];
        for (int i = 0; i < items.length; i++) {
            indices[i] = findItemIndex(items[i]);
        }
        selectItems(indices);
    }

    public void waitItemsSelection(final int[] itemIndices, final boolean selected) {
        getOutput()
                .printLine("Wait items to be "
                        + (selected ? "" : "un") + "selected in component \n    : "
                        + toStringSource());
        getOutput().printGolden("Wait items to be " + (selected ? "" : "un") + "selected");
        waitState(new ComponentChooser() {
            @Override
            public boolean checkComponent(Component comp) {
                int[] indices = getSelectedIndices();
                for (int i = 0; i < indices.length; i++) {
                    if (indices[i] != itemIndices[i]) {
                        return false;
                    }
                }
                return true;
            }

            @Override
            public String getDescription() {
                return ("Item has been " + (selected ? "" : "un") + "selected");
            }

            @Override
            public String toString() {
                return "JListOperator.waitItemsSelection.ComponentChooser{description = " + getDescription() + '}';
            }
        });
    }

    public void waitItemSelection(final int itemIndex, final boolean selected) {
        waitItemsSelection(new int[] {itemIndex}, selected);
    }

    /**
     * Waits for item. Uses getComparator() comparator.
     *
     * @param itemIndex Index of item to check or -1 to check selected item.
     */
    public void waitItem(String item, int itemIndex) {
        getOutput()
                .printLine("Wait \"" + item + "\" at the " + Integer.toString(itemIndex)
                        + " position in component \n    : "
                        + toStringSource());
        getOutput().printGolden("Wait \"" + item + "\" at the " + Integer.toString(itemIndex) + " position");
        waitState(new JListByItemFinder(item, itemIndex, getComparator()));
    }

    /**
     * Returns information about component.
     */
    @Override
    public Hashtable<String, Object> getDump() {
        Hashtable<String, Object> result = super.getDump();
        String[] items = new String[((JList) getSource()).getModel().getSize()];
        for (int i = 0; i < ((JList) getSource()).getModel().getSize(); i++) {
            items[i] = ((JList) getSource()).getModel().getElementAt(i).toString();
        }
        int[] selectedIndices = ((JList) getSource()).getSelectedIndices();
        String[] selectedItems = new String[selectedIndices.length];
        for (int i = 0; i < selectedIndices.length; i++) {
            selectedItems[i] = items[selectedIndices[i]];
        }
        addToDump(result, ITEM_PREFIX_DPROP, items);
        addToDump(result, SELECTED_ITEM_PREFIX_DPROP, selectedItems);
        return result;
    }

    ////////////////////////////////////////////////////////
    // Mapping                                             //
    public void addListSelectionListener(final ListSelectionListener listSelectionListener) {
        runMapping(new MapVoidAction("addListSelectionListener") {
            @Override
            public void map() {
                ((JList) getSource()).addListSelectionListener(listSelectionListener);
            }
        });
    }

    public void addSelectionInterval(final int i, final int i1) {
        runMapping(new MapVoidAction("addSelectionInterval") {
            @Override
            public void map() {
                ((JList) getSource()).addSelectionInterval(i, i1);
            }
        });
    }

    public void clearSelection() {
        runMapping(new MapVoidAction("clearSelection") {
            @Override
            public void map() {
                ((JList) getSource()).clearSelection();
            }
        });
    }

    public void ensureIndexIsVisible(final int i) {
        runMapping(new MapVoidAction("ensureIndexIsVisible") {
            @Override
            public void map() {
                ((JList) getSource()).ensureIndexIsVisible(i);
            }
        });
    }

    public int getAnchorSelectionIndex() {
        return (runMapping(new MapIntegerAction("getAnchorSelectionIndex") {
            @Override
            public int map() {
                return ((JList) getSource()).getAnchorSelectionIndex();
            }
        }));
    }

    public Rectangle getCellBounds(final int i, final int i1) {
        return (runMapping(new MapAction<Rectangle>("getCellBounds") {
            @Override
            public Rectangle map() {
                return ((JList) getSource()).getCellBounds(i, i1);
            }
        }));
    }

    public ListCellRenderer<?> getCellRenderer() {
        return (runMapping(new MapAction<ListCellRenderer<?>>("getCellRenderer") {
            @Override
            public ListCellRenderer<?> map() {
                return ((JList) getSource()).getCellRenderer();
            }
        }));
    }

    public int getFirstVisibleIndex() {
        return (runMapping(new MapIntegerAction("getFirstVisibleIndex") {
            @Override
            public int map() {
                return ((JList) getSource()).getFirstVisibleIndex();
            }
        }));
    }

    public int getFixedCellHeight() {
        return (runMapping(new MapIntegerAction("getFixedCellHeight") {
            @Override
            public int map() {
                return ((JList) getSource()).getFixedCellHeight();
            }
        }));
    }

    public int getFixedCellWidth() {
        return (runMapping(new MapIntegerAction("getFixedCellWidth") {
            @Override
            public int map() {
                return ((JList) getSource()).getFixedCellWidth();
            }
        }));
    }

    public int getLastVisibleIndex() {
        return (runMapping(new MapIntegerAction("getLastVisibleIndex") {
            @Override
            public int map() {
                return ((JList) getSource()).getLastVisibleIndex();
            }
        }));
    }

    public int getLeadSelectionIndex() {
        return (runMapping(new MapIntegerAction("getLeadSelectionIndex") {
            @Override
            public int map() {
                return ((JList) getSource()).getLeadSelectionIndex();
            }
        }));
    }

    public int getMaxSelectionIndex() {
        return (runMapping(new MapIntegerAction("getMaxSelectionIndex") {
            @Override
            public int map() {
                return ((JList) getSource()).getMaxSelectionIndex();
            }
        }));
    }

    public int getMinSelectionIndex() {
        return (runMapping(new MapIntegerAction("getMinSelectionIndex") {
            @Override
            public int map() {
                return ((JList) getSource()).getMinSelectionIndex();
            }
        }));
    }

    public ListModel<?> getModel() {
        return (runMapping(new MapAction<ListModel<?>>("getModel") {
            @Override
            public ListModel<?> map() {
                return ((JList) getSource()).getModel();
            }
        }));
    }

    public Dimension getPreferredScrollableViewportSize() {
        return (runMapping(new MapAction<Dimension>("getPreferredScrollableViewportSize") {
            @Override
            public Dimension map() {
                return ((JList) getSource()).getPreferredScrollableViewportSize();
            }
        }));
    }

    public Object getPrototypeCellValue() {
        return (runMapping(new MapAction<Object>("getPrototypeCellValue") {
            @Override
            public Object map() {
                return ((JList) getSource()).getPrototypeCellValue();
            }
        }));
    }

    public int getScrollableBlockIncrement(final Rectangle rectangle, final int i, final int i1) {
        return (runMapping(new MapIntegerAction("getScrollableBlockIncrement") {
            @Override
            public int map() {
                return ((JList) getSource()).getScrollableBlockIncrement(rectangle, i, i1);
            }
        }));
    }

    public boolean getScrollableTracksViewportHeight() {
        return (runMapping(new MapBooleanAction("getScrollableTracksViewportHeight") {
            @Override
            public boolean map() {
                return ((JList) getSource()).getScrollableTracksViewportHeight();
            }
        }));
    }

    public boolean getScrollableTracksViewportWidth() {
        return (runMapping(new MapBooleanAction("getScrollableTracksViewportWidth") {
            @Override
            public boolean map() {
                return ((JList) getSource()).getScrollableTracksViewportWidth();
            }
        }));
    }

    public int getScrollableUnitIncrement(final Rectangle rectangle, final int i, final int i1) {
        return (runMapping(new MapIntegerAction("getScrollableUnitIncrement") {
            @Override
            public int map() {
                return ((JList) getSource()).getScrollableUnitIncrement(rectangle, i, i1);
            }
        }));
    }

    public int getSelectedIndex() {
        return (runMapping(new MapIntegerAction("getSelectedIndex") {
            @Override
            public int map() {
                return ((JList) getSource()).getSelectedIndex();
            }
        }));
    }

    public int[] getSelectedIndices() {
        return ((int[]) runMapping(new MapAction<Object>("getSelectedIndices") {
            @Override
            public Object map() {
                return ((JList) getSource()).getSelectedIndices();
            }
        }));
    }

    public Object getSelectedValue() {
        return (runMapping(new MapAction<Object>("getSelectedValue") {
            @Override
            public Object map() {
                return ((JList) getSource()).getSelectedValue();
            }
        }));
    }

    @Deprecated
    public Object[] getSelectedValues() {
        return ((Object[]) runMapping(new MapAction<Object>("getSelectedValues") {
            @Override
            public Object map() {
                return ((JList) getSource()).getSelectedValues();
            }
        }));
    }

    public Color getSelectionBackground() {
        return (runMapping(new MapAction<Color>("getSelectionBackground") {
            @Override
            public Color map() {
                return ((JList) getSource()).getSelectionBackground();
            }
        }));
    }

    public Color getSelectionForeground() {
        return (runMapping(new MapAction<Color>("getSelectionForeground") {
            @Override
            public Color map() {
                return ((JList) getSource()).getSelectionForeground();
            }
        }));
    }

    public int getSelectionMode() {
        return (runMapping(new MapIntegerAction("getSelectionMode") {
            @Override
            public int map() {
                return ((JList) getSource()).getSelectionMode();
            }
        }));
    }

    public ListSelectionModel getSelectionModel() {
        return (runMapping(new MapAction<ListSelectionModel>("getSelectionModel") {
            @Override
            public ListSelectionModel map() {
                return ((JList) getSource()).getSelectionModel();
            }
        }));
    }

    public ListUI getUI() {
        return (runMapping(new MapAction<ListUI>("getUI") {
            @Override
            public ListUI map() {
                return ((JList) getSource()).getUI();
            }
        }));
    }

    public boolean getValueIsAdjusting() {
        return (runMapping(new MapBooleanAction("getValueIsAdjusting") {
            @Override
            public boolean map() {
                return ((JList) getSource()).getValueIsAdjusting();
            }
        }));
    }

    public int getVisibleRowCount() {
        return (runMapping(new MapIntegerAction("getVisibleRowCount") {
            @Override
            public int map() {
                return ((JList) getSource()).getVisibleRowCount();
            }
        }));
    }

    public Point indexToLocation(final int i) {
        return (runMapping(new MapAction<Point>("indexToLocation") {
            @Override
            public Point map() {
                return ((JList) getSource()).indexToLocation(i);
            }
        }));
    }

    public boolean isSelectedIndex(final int i) {
        return (runMapping(new MapBooleanAction("isSelectedIndex") {
            @Override
            public boolean map() {
                return ((JList) getSource()).isSelectedIndex(i);
            }
        }));
    }

    public boolean isSelectionEmpty() {
        return (runMapping(new MapBooleanAction("isSelectionEmpty") {
            @Override
            public boolean map() {
                return ((JList) getSource()).isSelectionEmpty();
            }
        }));
    }

    public int locationToIndex(final Point point) {
        return (runMapping(new MapIntegerAction("locationToIndex") {
            @Override
            public int map() {
                return ((JList) getSource()).locationToIndex(point);
            }
        }));
    }

    public void removeListSelectionListener(final ListSelectionListener listSelectionListener) {
        runMapping(new MapVoidAction("removeListSelectionListener") {
            @Override
            public void map() {
                ((JList) getSource()).removeListSelectionListener(listSelectionListener);
            }
        });
    }

    public void removeSelectionInterval(final int i, final int i1) {
        runMapping(new MapVoidAction("removeSelectionInterval") {
            @Override
            public void map() {
                ((JList) getSource()).removeSelectionInterval(i, i1);
            }
        });
    }

    @SuppressWarnings(value = "unchecked")
    public void setCellRenderer(final ListCellRenderer<?> listCellRenderer) {
        runMapping(new MapVoidAction("setCellRenderer") {
            @Override
            public void map() {
                ((JList) getSource()).setCellRenderer(listCellRenderer);
            }
        });
    }

    public void setFixedCellHeight(final int i) {
        runMapping(new MapVoidAction("setFixedCellHeight") {
            @Override
            public void map() {
                ((JList) getSource()).setFixedCellHeight(i);
            }
        });
    }

    public void setFixedCellWidth(final int i) {
        runMapping(new MapVoidAction("setFixedCellWidth") {
            @Override
            public void map() {
                ((JList) getSource()).setFixedCellWidth(i);
            }
        });
    }

    @SuppressWarnings(value = "unchecked")
    public void setListData(final Vector<?> vector) {
        runMapping(new MapVoidAction("setListData") {
            @Override
            public void map() {
                ((JList) getSource()).setListData(vector);
            }
        });
    }

    @SuppressWarnings(value = "unchecked")
    public void setListData(final Object[] object) {
        runMapping(new MapVoidAction("setListData") {
            @Override
            public void map() {
                ((JList) getSource()).setListData(object);
            }
        });
    }

    @SuppressWarnings(value = "unchecked")
    public void setModel(final ListModel<?> listModel) {
        runMapping(new MapVoidAction("setModel") {
            @Override
            public void map() {
                ((JList) getSource()).setModel(listModel);
            }
        });
    }

    @SuppressWarnings(value = "unchecked")
    public void setPrototypeCellValue(final Object object) {
        runMapping(new MapVoidAction("setPrototypeCellValue") {
            @Override
            public void map() {
                ((JList) getSource()).setPrototypeCellValue(object);
            }
        });
    }

    public void setSelectedIndex(final int i) {
        runMapping(new MapVoidAction("setSelectedIndex") {
            @Override
            public void map() {
                ((JList) getSource()).setSelectedIndex(i);
            }
        });
    }

    public void setSelectedIndices(final int[] i) {
        runMapping(new MapVoidAction("setSelectedIndices") {
            @Override
            public void map() {
                ((JList) getSource()).setSelectedIndices(i);
            }
        });
    }

    public void setSelectedValue(final Object object, final boolean b) {
        runMapping(new MapVoidAction("setSelectedValue") {
            @Override
            public void map() {
                ((JList) getSource()).setSelectedValue(object, b);
            }
        });
    }

    public void setSelectionBackground(final Color color) {
        runMapping(new MapVoidAction("setSelectionBackground") {
            @Override
            public void map() {
                ((JList) getSource()).setSelectionBackground(color);
            }
        });
    }

    public void setSelectionForeground(final Color color) {
        runMapping(new MapVoidAction("setSelectionForeground") {
            @Override
            public void map() {
                ((JList) getSource()).setSelectionForeground(color);
            }
        });
    }

    public void setSelectionInterval(final int i, final int i1) {
        runMapping(new MapVoidAction("setSelectionInterval") {
            @Override
            public void map() {
                ((JList) getSource()).setSelectionInterval(i, i1);
            }
        });
    }

    public void setSelectionMode(final int i) {
        runMapping(new MapVoidAction("setSelectionMode") {
            @Override
            public void map() {
                ((JList) getSource()).setSelectionMode(i);
            }
        });
    }

    public void setSelectionModel(final ListSelectionModel listSelectionModel) {
        runMapping(new MapVoidAction("setSelectionModel") {
            @Override
            public void map() {
                ((JList) getSource()).setSelectionModel(listSelectionModel);
            }
        });
    }

    public void setUI(final ListUI listUI) {
        runMapping(new MapVoidAction("setUI") {
            @Override
            public void map() {
                ((JList) getSource()).setUI(listUI);
            }
        });
    }

    public void setValueIsAdjusting(final boolean b) {
        runMapping(new MapVoidAction("setValueIsAdjusting") {
            @Override
            public void map() {
                ((JList) getSource()).setValueIsAdjusting(b);
            }
        });
    }

    public void setVisibleRowCount(final int i) {
        runMapping(new MapVoidAction("setVisibleRowCount") {
            @Override
            public void map() {
                ((JList) getSource()).setVisibleRowCount(i);
            }
        });
    }

    // End of mapping                                      //
    ////////////////////////////////////////////////////////
    private void checkIndex(int index) {
        if (index < 0 || index >= getModel().getSize()) {
            throw (new NoSuchItemException(index));
        }
    }

    private void checkIndices(int[] indices) {
        for (int indice : indices) {
            checkIndex(indice);
        }
    }

    /**
     * Iterface to choose list item.
     */
    public interface ListItemChooser {

        /**
         * Should be true if item is good.
         *
         * @return true if the item fits the criteria
         */
        public boolean checkItem(JListOperator oper, int index);

        /**
         * Item description.
         *
         * @return a description.
         */
        public String getDescription();
    }

    /**
     * Can be thrown during item selecting if list does not have item requested.
     */
    public class NoSuchItemException extends JemmyInputException {

        private static final long serialVersionUID = 42L;

        public NoSuchItemException(String item) {
            super("No such item as \"" + item + "\"", getSource());
        }

        public NoSuchItemException(int index) {
            super("List does not contain " + index + "'th item", getSource());
        }
    }

    private static class BySubStringListItemChooser implements ListItemChooser {

        String subString;
        StringComparator comparator;

        public BySubStringListItemChooser(String subString, StringComparator comparator) {
            this.subString = subString;
            this.comparator = comparator;
        }

        @Override
        public boolean checkItem(JListOperator oper, int index) {
            return (comparator.equals(oper.getModel().getElementAt(index).toString(), subString));
        }

        @Override
        public String getDescription() {
            return "Item containing \"" + subString + "\" string";
        }

        @Override
        public String toString() {
            return "BySubStringListItemChooser{" + "subString=" + subString + ", comparator=" + comparator + '}';
        }
    }

    private static class ByRenderedComponentListItemChooser implements ListItemChooser {

        ComponentChooser chooser;

        public ByRenderedComponentListItemChooser(ComponentChooser chooser) {
            this.chooser = chooser;
        }

        @Override
        public boolean checkItem(JListOperator oper, int index) {
            return chooser.checkComponent(oper.getRenderedComponent(index));
        }

        @Override
        public String getDescription() {
            return chooser.getDescription();
        }

        @Override
        public String toString() {
            return "ByRenderedComponentListItemChooser{" + "chooser=" + chooser + '}';
        }
    }

    /**
     * Allows to find component by an item.
     */
    public static class JListByItemFinder implements ComponentChooser {

        String label;
        int itemIndex;
        StringComparator comparator;

        /**
         * Constructs JListByItemFinder.
         *
         * @param ii item index to check. If equal to -1, selected item is
         * checked.
         */
        public JListByItemFinder(String lb, int ii, StringComparator comparator) {
            label = lb;
            itemIndex = ii;
            this.comparator = comparator;
        }

        /**
         * Constructs JListByItemFinder.
         *
         * @param ii item index to check. If equal to -1, selected item is
         * checked.
         */
        public JListByItemFinder(String lb, int ii) {
            this(lb, ii, Operator.getDefaultStringComparator());
        }

        @Override
        public boolean checkComponent(Component comp) {
            if (comp instanceof JList) {
                if (label == null) {
                    return true;
                }
                if (((JList) comp).getModel().getSize() > itemIndex) {
                    int ii = itemIndex;
                    if (ii == -1) {
                        ii = ((JList) comp).getSelectedIndex();
                        if (ii == -1) {
                            return false;
                        }
                    }
                    return (comparator.equals(
                            ((JList) comp).getModel().getElementAt(ii).toString(), label));
                }
            }
            return false;
        }

        @Override
        public String getDescription() {
            return ("JList with text \"" + label + "\" in " + itemIndex + "'th item");
        }

        @Override
        public String toString() {
            return "JListByItemFinder{" + "label=" + label + ", itemIndex=" + itemIndex + ", comparator=" + comparator
                    + '}';
        }
    }

    /**
     * Checks component type.
     */
    public static class JListFinder extends Finder {

        /**
         * Constructs JListFinder.
         */
        public JListFinder(ComponentChooser sf) {
            super(JList.class, sf);
        }

        /**
         * Constructs JListFinder.
         */
        public JListFinder() {
            super(JList.class);
        }
    }
}
