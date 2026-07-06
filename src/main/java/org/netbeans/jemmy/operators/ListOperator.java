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
import java.awt.List;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.util.Hashtable;
import org.jspecify.annotations.Nullable;
import org.netbeans.jemmy.ComponentChooser;
import org.netbeans.jemmy.Outputable;
import org.netbeans.jemmy.TestOut;
import org.netbeans.jemmy.drivers.DriverManager;
import org.netbeans.jemmy.drivers.MultiSelListDriver;

/**
 * Timeouts used:
 * <ul>
 * <li>ComponentOperator.WaitComponentTimeout - time to wait component displayed</li>
 * <li>ComponentOperator.WaitComponentEnabledTimeout - time to wait component enabled</li>
 * </ul>
 *
 * @see org.netbeans.jemmy.Timeouts
 *
 */
public class ListOperator extends ComponentOperator implements Outputable {

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

    private @SuppressWarnings("NullAway.Init") TestOut output;
    private MultiSelListDriver driver;

    public ListOperator(List b) {
        super(b);
        driver = DriverManager.getMultiSelListDriver(getClass());
    }

    public ListOperator(ContainerOperator<?> cont, ComponentChooser chooser, int index) {
        this((List) cont.waitSubComponent(new ListFinder(chooser), index));
        copyEnvironment(cont);
    }

    public ListOperator(ContainerOperator<?> cont, ComponentChooser chooser) {
        this(cont, chooser, 0);
    }

    /**
     * Waits item text first. Uses cont's timeout and output for
     * waiting and to init operator.
     */
    public ListOperator(ContainerOperator<?> cont, String text, int itemIndex, int index) {
        this((List) waitComponent(cont, new ListByItemFinder(text, itemIndex, cont.getComparator()), index));
        copyEnvironment(cont);
    }

    /**
     * Waits component by selected item text first. Uses cont's
     * timeout and output for waiting and to init operator.
     *
     * @see ComponentOperator#isCaptionEqual(String, String, boolean, boolean)
     */
    public ListOperator(ContainerOperator<?> cont, String text, int index) {
        this(cont, text, -1, index);
    }

    /**
     * Waits component in container first. Uses cont's timeout and
     * output for waiting and to init operator.
     *
     * @see ComponentOperator#isCaptionEqual(String, String, boolean, boolean)
     */
    public ListOperator(ContainerOperator<?> cont, String text) {
        this(cont, text, 0);
    }

    /**
     * Waits component in container first. Uses cont's timeout and
     * output for waiting and to init operator.
     */
    public ListOperator(ContainerOperator<?> cont, int index) {
        this((List) waitComponent(cont, new ListFinder(), index));
        copyEnvironment(cont);
    }

    /**
     * Waits component in container first. Uses cont's timeout and
     * output for waiting and to init operator.
     */
    public ListOperator(ContainerOperator<?> cont) {
        this(cont, 0);
    }

    /**
     * Searches List in container.
     *
     * @return List instance or null if component was not found.
     */
    public static @Nullable List findList(Container cont, ComponentChooser chooser, int index) {
        return (List) findComponent(cont, new ListFinder(chooser), index);
    }

    /**
     * Searches 0'th List in container.
     *
     * @return List instance or null if component was not found.
     */
    public static @Nullable List findList(Container cont, ComponentChooser chooser) {
        return findList(cont, chooser, 0);
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

    private int findItemIndex(String item, StringComparator comparator, int index) {
        int count = 0;
        for (int i = 0; i < getItemCount(); i++) {
            if (comparator.equals(getItem(i), item)) {
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
     * Searches an item index.
     *
     * @return an index.
     */
    public int findItemIndex(String item, int index) {
        return findItemIndex(item, getComparator(), index);
    }

    /**
     * Searches an item index.
     *
     * @return an index.
     */
    public int findItemIndex(String item) {
        return findItemIndex(item, 0);
    }

    private void selectItem(String item, StringComparator comparator, int index) {
        selectItem(findItemIndex(item, comparator, index));
    }

    public void selectItem(String item, int index) {
        selectItem(item, getComparator(), index);
    }

    public void selectItem(String item) {
        selectItem(item, 0);
    }

    public void selectItem(int index) {
        output.printLine("Select " + Integer.toString(index) + "`th item in list\n    : " + toStringSource());
        output.printGolden("Select " + Integer.toString(index) + "`th item in list");
        driver.selectItem(this, index);
        if (getVerification()) {
            waitItemSelection(index, true);
        }
    }

    /**
     * Selects some items.
     */
    public void selectItems(int from, int to) {
        output.printLine("Select items from " + Integer.toString(from)
                + "`th to " + Integer.toString(from) + "'th in list\n    : "
                + toStringSource());
        output.printGolden("Select items from " + Integer.toString(from) + "`th to " + Integer.toString(from) + "'th");
        driver.selectItems(this, new int[] {from, to});
        if (getVerification()) {
            waitItemsSelection(from, to, true);
        }
    }

    public void waitItemsSelection(final int from, final int to, final boolean selected) {
        getOutput()
                .printLine("Wait items to be "
                        + (selected ? "" : "un") + "selected in component \n    : "
                        + toStringSource());
        getOutput().printGolden("Wait items to be " + (selected ? "" : "un") + "selected");
        waitState(new ComponentChooser() {
            @Override
            public boolean checkComponent(Component comp) {
                int[] indices = getSelectedIndexes();
                for (int indice : indices) {
                    if (indice < from || indice > to) {
                        return false;
                    }
                }
                return true;
            }

            @Override
            public String getDescription() {
                return ("Items has been " + (selected ? "" : "un") + "selected");
            }

            @Override
            public String toString() {
                return "ListOperator.waitItemsSelection.ComponentChooser{description = " + getDescription() + '}';
            }
        });
    }

    public void waitItemSelection(final int itemIndex, final boolean selected) {
        waitItemsSelection(itemIndex, itemIndex, selected);
    }

    @Override
    public Hashtable<String, Object> getDump() {
        Hashtable<String, Object> result = super.getDump();
        addToDump(result, ITEM_PREFIX_DPROP, ((List) getSource()).getItems());
        addToDump(result, SELECTED_ITEM_PREFIX_DPROP, ((List) getSource()).getSelectedItems());
        return result;
    }

    ////////////////////////////////////////////////////////
    // Mapping                                             //
    public void addActionListener(final ActionListener actionListener) {
        runMapping(new MapVoidAction("addActionListener") {
            @Override
            public void map() {
                ((List) getSource()).addActionListener(actionListener);
            }
        });
    }

    public void addItemListener(final ItemListener itemListener) {
        runMapping(new MapVoidAction("addItemListener") {
            @Override
            public void map() {
                ((List) getSource()).addItemListener(itemListener);
            }
        });
    }

    public void deselect(final int i) {
        runMapping(new MapVoidAction("deselect") {
            @Override
            public void map() {
                ((List) getSource()).deselect(i);
            }
        });
    }

    public String getItem(final int i) {
        return (runMapping(new MapAction<String>("getItem") {
            @Override
            public String map() {
                return ((List) getSource()).getItem(i);
            }
        }));
    }

    public int getItemCount() {
        return (runMapping(new MapIntegerAction("getItemCount") {
            @Override
            public int map() {
                return ((List) getSource()).getItemCount();
            }
        }));
    }

    public String[] getItems() {
        return ((String[]) runMapping(new MapAction<Object>("getItems") {
            @Override
            public Object map() {
                return ((List) getSource()).getItems();
            }
        }));
    }

    public Dimension getMinimumSize(final int i) {
        return (runMapping(new MapAction<Dimension>("getMinimumSize") {
            @Override
            public Dimension map() {
                return ((List) getSource()).getMinimumSize(i);
            }
        }));
    }

    public Dimension getPreferredSize(final int i) {
        return (runMapping(new MapAction<Dimension>("getPreferredSize") {
            @Override
            public Dimension map() {
                return ((List) getSource()).getPreferredSize(i);
            }
        }));
    }

    public int getRows() {
        return (runMapping(new MapIntegerAction("getRows") {
            @Override
            public int map() {
                return ((List) getSource()).getRows();
            }
        }));
    }

    public int getSelectedIndex() {
        return (runMapping(new MapIntegerAction("getSelectedIndex") {
            @Override
            public int map() {
                return ((List) getSource()).getSelectedIndex();
            }
        }));
    }

    public int[] getSelectedIndexes() {
        return ((int[]) runMapping(new MapAction<Object>("getSelectedIndexes") {
            @Override
            public Object map() {
                return ((List) getSource()).getSelectedIndexes();
            }
        }));
    }

    public String getSelectedItem() {
        return (runMapping(new MapAction<String>("getSelectedItem") {
            @Override
            public String map() {
                return ((List) getSource()).getSelectedItem();
            }
        }));
    }

    public String[] getSelectedItems() {
        return ((String[]) runMapping(new MapAction<Object>("getSelectedItems") {
            @Override
            public Object map() {
                return ((List) getSource()).getSelectedItems();
            }
        }));
    }

    public Object[] getSelectedObjects() {
        return ((Object[]) runMapping(new MapAction<Object>("getSelectedObjects") {
            @Override
            public Object map() {
                return ((List) getSource()).getSelectedObjects();
            }
        }));
    }

    public int getVisibleIndex() {
        return (runMapping(new MapIntegerAction("getVisibleIndex") {
            @Override
            public int map() {
                return ((List) getSource()).getVisibleIndex();
            }
        }));
    }

    public boolean isIndexSelected(final int i) {
        return (runMapping(new MapBooleanAction("isIndexSelected") {
            @Override
            public boolean map() {
                return ((List) getSource()).isIndexSelected(i);
            }
        }));
    }

    public boolean isMultipleMode() {
        return (runMapping(new MapBooleanAction("isMultipleMode") {
            @Override
            public boolean map() {
                return ((List) getSource()).isMultipleMode();
            }
        }));
    }

    public void makeVisible(final int i) {
        runMapping(new MapVoidAction("makeVisible") {
            @Override
            public void map() {
                ((List) getSource()).makeVisible(i);
            }
        });
    }

    public void remove(final int i) {
        runMapping(new MapVoidAction("remove") {
            @Override
            public void map() {
                ((List) getSource()).remove(i);
            }
        });
    }

    public void remove(final String string) {
        runMapping(new MapVoidAction("remove") {
            @Override
            public void map() {
                ((List) getSource()).remove(string);
            }
        });
    }

    public void removeActionListener(final ActionListener actionListener) {
        runMapping(new MapVoidAction("removeActionListener") {
            @Override
            public void map() {
                ((List) getSource()).removeActionListener(actionListener);
            }
        });
    }

    public void removeAll() {
        runMapping(new MapVoidAction("removeAll") {
            @Override
            public void map() {
                ((List) getSource()).removeAll();
            }
        });
    }

    public void removeItemListener(final ItemListener itemListener) {
        runMapping(new MapVoidAction("removeItemListener") {
            @Override
            public void map() {
                ((List) getSource()).removeItemListener(itemListener);
            }
        });
    }

    public void replaceItem(final String string, final int i) {
        runMapping(new MapVoidAction("replaceItem") {
            @Override
            public void map() {
                ((List) getSource()).replaceItem(string, i);
            }
        });
    }

    public void select(final int i) {
        runMapping(new MapVoidAction("select") {
            @Override
            public void map() {
                ((List) getSource()).select(i);
            }
        });
    }

    public void setMultipleMode(final boolean b) {
        runMapping(new MapVoidAction("setMultipleMode") {
            @Override
            public void map() {
                ((List) getSource()).setMultipleMode(b);
            }
        });
    }

    // End of mapping                                      //
    ////////////////////////////////////////////////////////
    /**
     * Allows to find component by item text.
     */
    public static class ListByItemFinder implements ComponentChooser {

        String label;
        int itemIndex;
        StringComparator comparator;

        /**
         * Constructs ListByItemFinder.
         *
         * @param ii item index to check. If equal to -1, selected item is
         * checked.
         */
        public ListByItemFinder(String lb, int ii, StringComparator comparator) {
            label = lb;
            itemIndex = ii;
            this.comparator = comparator;
        }

        /**
         * Constructs ListByItemFinder.
         *
         * @param ii item index to check. If equal to -1, selected item is
         * checked.
         */
        public ListByItemFinder(String lb, int ii) {
            this(lb, ii, Operator.getDefaultStringComparator());
        }

        @Override
        public boolean checkComponent(Component comp) {
            if (comp instanceof List) {
                if (label == null) {
                    return true;
                }
                if (((List) comp).getItemCount() > itemIndex) {
                    int ii = itemIndex;
                    if (ii == -1) {
                        ii = ((List) comp).getSelectedIndex();
                        if (ii == -1) {
                            return false;
                        }
                    }
                    return (comparator.equals(((List) comp).getItem(ii), label));
                }
            }
            return false;
        }

        @Override
        public String getDescription() {
            return ("List with text \"" + label + "\" in " + itemIndex + "'th item");
        }

        @Override
        public String toString() {
            return "ListByItemFinder{" + "label=" + label + ", itemIndex=" + itemIndex + ", comparator=" + comparator
                    + '}';
        }
    }

    /**
     * Checks component type.
     */
    public static class ListFinder extends Finder {

        public ListFinder(ComponentChooser sf) {
            super(List.class, sf);
        }

        public ListFinder() {
            super(List.class);
        }
    }
}
