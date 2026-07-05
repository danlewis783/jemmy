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
import java.util.EventObject;
import java.util.Hashtable;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableModelEvent;
import javax.swing.plaf.TableUI;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import org.netbeans.jemmy.ComponentChooser;
import org.netbeans.jemmy.ComponentSearcher;
import org.netbeans.jemmy.JemmyException;
import org.netbeans.jemmy.Outputable;
import org.netbeans.jemmy.QueueTool;
import org.netbeans.jemmy.TestOut;
import org.netbeans.jemmy.Timeoutable;
import org.netbeans.jemmy.Timeouts;
import org.netbeans.jemmy.Waiter;
import org.netbeans.jemmy.drivers.DriverManager;
import org.netbeans.jemmy.drivers.TableDriver;
import org.netbeans.jemmy.util.EmptyVisualizer;

/**
 * Timeouts used:
 * <ul>
 * <li>JTableOperator.WaitEditingTimeout - time to wait cell editing</li>
 * <li>ComponentOperator.WaitComponentTimeout - time to wait component displayed</li>
 * <li>ComponentOperator.WaitStateTimeout - time to wait for cell contents</li>
 * <li>JTextComponentOperator.ChangeCaretPositionTimeout - maximum time to chenge caret position</li>
 * <li>JTextComponentOperator.TypeTextTimeout - maximum time to type text</li>
 * <li>JScrollBarOperator.WholeScrollTimeout - time for the whole scrolling</li>
 * </ul>
 *
 * @see Timeouts
 *
 */
public class JTableOperator extends JComponentOperator implements Outputable, Timeoutable {

    /**
     * Identifier for a "cell" property.
     *
     * @see #getDump
     */
    public static final String CELL_PREFIX_DPROP = "Cell";

    /**
     * Identifier for a "column" property.
     *
     * @see #getDump
     */
    public static final String COLUMN_PREFIX_DPROP = "Column";

    /**
     * Identifier for a "selected column" property.
     *
     * @see #getDump
     */
    public static final String SELECTED_COLUMN_PREFIX_DPROP = "SelectedColumn";

    /**
     * Identifier for a "selected row" property.
     *
     * @see #getDump
     */
    public static final String SELECTED_ROW_PREFIX_DPROP = "SelectedRow";

    /**
     * Identifier for a "column count" property.
     *
     * @see #getDump
     */
    public static final String COLUMN_COUNT_DPROP = "Column count";

    /**
     * Identifier for a "row count" property.
     *
     * @see #getDump
     */
    public static final String ROW_COUNT_DPROP = "Row count";

    private static final long WAIT_EDITING_TIMEOUT = 60000;

    private TestOut output;
    private Timeouts timeouts;

    TableDriver driver;

    public JTableOperator(JTable b) {
        super(b);
        driver = DriverManager.getTableDriver(getClass());
    }

    public JTableOperator(ContainerOperator<?> cont, ComponentChooser chooser, int index) {
        this((JTable) cont.waitSubComponent(new JTableFinder(chooser), index));
        copyEnvironment(cont);
    }

    public JTableOperator(ContainerOperator<?> cont, ComponentChooser chooser) {
        this(cont, chooser, 0);
    }

    /**
     * Waits by cell text first. Uses cont's timeout and output for
     * waiting and to init operator.
     *
     * @see ComponentOperator#isCaptionEqual(String, String, boolean, boolean)
     */
    public JTableOperator(ContainerOperator<?> cont, String text, int row, int column, int index) {
        this((JTable) waitComponent(cont, new JTableByCellFinder(text, row, column, cont.getComparator()), index));
        copyEnvironment(cont);
    }

    /**
     * Waits by cell text first. Uses cont's timeout and output for
     * waiting and to init operator.
     *
     * @see ComponentOperator#isCaptionEqual(String, String, boolean, boolean)
     */
    public JTableOperator(ContainerOperator<?> cont, String text, int row, int column) {
        this(cont, text, row, column, 0);
    }

    /**
     * Waits by text in selected cell first. Uses cont's timeout
     * and output for waiting and to init operator.
     *
     * @see ComponentOperator#isCaptionEqual(String, String, boolean, boolean)
     */
    public JTableOperator(ContainerOperator<?> cont, String text, int index) {
        this(cont, text, -1, -1, index);
    }

    /**
     * Waits component in container first. Uses cont's timeout and
     * output for waiting and to init operator.
     *
     * @see ComponentOperator#isCaptionEqual(String, String, boolean, boolean)
     */
    public JTableOperator(ContainerOperator<?> cont, String text) {
        this(cont, text, 0);
    }

    /**
     * Waits component in container first. Uses cont's timeout and
     * output for waiting and to init operator.
     */
    public JTableOperator(ContainerOperator<?> cont, int index) {
        this((JTable) waitComponent(cont, new JTableFinder(), index));
        copyEnvironment(cont);
    }

    /**
     * Waits component in container first. Uses cont's timeout and
     * output for waiting and to init operator.
     */
    public JTableOperator(ContainerOperator<?> cont) {
        this(cont, 0);
    }

    ////////////////////////////////////////////////////////
    // Static finds                                        //
    ////////////////////////////////////////////////////////
    /**
     * Searches JTable in container.
     *
     * @return JTable instance or null if component was not found.
     */
    public static JTable findJTable(Container cont, ComponentChooser chooser, int index) {
        return (JTable) findComponent(cont, new JTableFinder(chooser), index);
    }

    /**
     * Searches 0'th JTable in container.
     *
     * @return JTable instance or null if component was not found.
     */
    public static JTable findJTable(Container cont, ComponentChooser chooser) {
        return findJTable(cont, chooser, 0);
    }

    /**
     * Searches JTable by cell.
     *
     * @param row Index of row to compare text. If -1, selected row is checked.
     * @param column Index of column to compare text. If -1, selected column is
     * checked.
     * @return JTable instance or null if component was not found.
     * @see ComponentOperator#isCaptionEqual(String, String, boolean, boolean)
     */
    public static JTable findJTable(
            Container cont, String text, boolean ce, boolean ccs, int row, int column, int index) {
        return findJTable(cont, new JTableByCellFinder(text, row, column, new DefaultStringComparator(ce, ccs)), index);
    }

    /**
     * Searches JTable by cell.
     *
     * @param row Index of row to compare text. If -1, selected row is checked.
     * @param column Index of column to compare text. If -1, selected column is
     * checked.
     * @return JTable instance or null if component was not found.
     * @see ComponentOperator#isCaptionEqual(String, String, boolean, boolean)
     */
    public static JTable findJTable(Container cont, String text, boolean ce, boolean ccs, int row, int column) {
        return findJTable(cont, text, ce, ccs, row, column, 0);
    }

    /**
     * Waits JTable in container.
     *
     * @return JTable instance or null if component was not found.
     */
    public static JTable waitJTable(Container cont, ComponentChooser chooser, int index) {
        return (JTable) waitComponent(cont, new JTableFinder(chooser), index);
    }

    /**
     * Waits 0'th JTable in container.
     *
     * @return JTable instance or null if component was not found.
     */
    public static JTable waitJTable(Container cont, ComponentChooser chooser) {
        return waitJTable(cont, chooser, 0);
    }

    /**
     * Waits JTable by cell.
     *
     * @param row Index of row to compare text. If -1, selected row is checked.
     * @param column Index of column to compare text. If -1, selected column is
     * checked.
     * @return JTable instance or null if component was not found.
     * @see ComponentOperator#isCaptionEqual(String, String, boolean, boolean)
     */
    public static JTable waitJTable(
            Container cont, String text, boolean ce, boolean ccs, int row, int column, int index) {
        return waitJTable(cont, new JTableByCellFinder(text, row, column, new DefaultStringComparator(ce, ccs)), index);
    }

    /**
     * Waits JTable by cell.
     *
     * @param row Index of row to compare text. If -1, selected row is checked.
     * @param column Index of column to compare text. If -1, selected column is
     * checked.
     * @return JTable instance or null if component was not found.
     * @see ComponentOperator#isCaptionEqual(String, String, boolean, boolean)
     */
    public static JTable waitJTable(Container cont, String text, boolean ce, boolean ccs, int row, int column) {
        return waitJTable(cont, text, ce, ccs, row, column, 0);
    }

    static {
        Timeouts.initDefault("JTableOperator.WaitEditingTimeout", WAIT_EDITING_TIMEOUT);
    }

    ////////////////////////////////////////////////////////
    // Environment                                         //
    ////////////////////////////////////////////////////////
    @Override
    public void setTimeouts(Timeouts times) {
        this.timeouts = times;
        super.setTimeouts(timeouts);
    }

    @Override
    public Timeouts getTimeouts() {
        return timeouts;
    }

    @Override
    public void setOutput(TestOut out) {
        output = out;
        super.setOutput(output);
    }

    @Override
    public TestOut getOutput() {
        return output;
    }

    @Override
    public void copyEnvironment(Operator anotherOperator) {
        super.copyEnvironment(anotherOperator);
        driver = (TableDriver)
                DriverManager.getDriver(DriverManager.TABLE_DRIVER_ID, getClass(), anotherOperator.getProperties());
    }

    ////////////////////////////////////////////////////////
    // Find methods                                        //
    ////////////////////////////////////////////////////////
    // text, comparator and index
    /**
     * Searches cell coordinates.
     *
     * @return Point indicating coordinates (x - column, y - row)
     */
    public Point findCell(String text, StringComparator comparator, int index) {
        return (findCell(new BySubStringTableCellChooser(text, comparator), index));
    }

    /**
     * Searches cell coordinates in the specified rows and columns.
     *
     * @return Point indicating coordinates (x - column, y - row)
     */
    public Point findCell(String text, StringComparator comparator, int[] rows, int[] columns, int index) {
        return findCell(new BySubStringTableCellChooser(text, comparator), rows, columns, index);
    }

    /**
     * Searches cell row index.
     *
     * @return a row index.
     */
    public int findCellRow(String text, StringComparator comparator, int index) {
        return findCell(text, comparator, index).y;
    }

    /**
     * Searches cell row index. Searching is performed between cells in one
     * column.
     *
     * @return a row index.
     */
    public int findCellRow(String text, StringComparator comparator, int column, int index) {
        return findCell(text, comparator, null, new int[] {column}, index).y;
    }

    /**
     * Searches cell column visible index.
     *
     * @return a column index.
     */
    public int findCellColumn(String text, StringComparator comparator, int index) {
        return findCell(text, comparator, index).x;
    }

    /**
     * Searches cell column index. Searching is performed between cells in one
     * row.
     *
     * @return a column index.
     */
    public int findCellColumn(String text, StringComparator comparator, int row, int index) {
        return findCell(text, comparator, new int[] {row}, null, index).x;
    }

    // booleans - deprecated
    /**
     * Searches cell row by cell text.
     *
     * @return a row index.
     * @see #findCellRow(String, int)
     * @deprecated Use findCellRow(String, int) or findCellRow(String,
     * StringComparator, int)
     */
    @Deprecated
    public int findCellRow(String text, boolean ce, boolean ccs, int index) {
        return findCell(text, ce, ccs, index).y;
    }

    /**
     * Searches cell column by cell text.
     *
     * @return a column index.
     * @see #findCellColumn(String, int)
     * @deprecated Use findCellColumn(String, int) or findCellColumn(String,
     * StringComparator, int)
     */
    @Deprecated
    public int findCellColumn(String text, boolean ce, boolean ccs, int index) {
        return findCell(text, ce, ccs, index).x;
    }

    /**
     * Searches first cell row by cell text.
     *
     * @return a row index.
     * @see #findCellRow(String)
     * @deprecated Use findCellRow(String) or findCellRow(String,
     * StringComparator)
     */
    @Deprecated
    public int findCellRow(String text, boolean ce, boolean ccs) {
        return findCellRow(text, ce, ccs, 0);
    }

    /**
     * Searches first cell column by cell text.
     *
     * @return a column index.
     * @see #findCellColumn(String)
     * @deprecated Use findCellColumn(String) or findCellColumn(String,
     * StringComparator)
     */
    @Deprecated
    public int findCellColumn(String text, boolean ce, boolean ccs) {
        return findCellColumn(text, ce, ccs, 0);
    }

    // text and comparator only
    /**
     * Searches cell row index.
     *
     * @return a row index.
     */
    public int findCellRow(String text, StringComparator comparator) {
        return findCellRow(text, comparator, 0);
    }

    /**
     * Searches cell column visible index.
     *
     * @return a column index.
     */
    public int findCellColumn(String text, StringComparator comparator) {
        return findCellColumn(text, comparator, 0);
    }

    // text and index
    /**
     * Searches cell row by cell text.
     *
     * @return a row index.
     */
    public int findCellRow(String text, int index) {
        return findCell(text, index).y;
    }

    /**
     * Searches cell row index. Searching is performed between cells in one
     * column.
     *
     * @return a row index.
     */
    public int findCellRow(String text, int column, int index) {
        return findCell(text, null, new int[] {column}, index).y;
    }

    /**
     * Searches cell column by cell text.
     *
     * @return a column index.
     */
    public int findCellColumn(String text, int index) {
        return findCell(text, index).x;
    }

    /**
     * Searches cell column index. Searching is performed between cells in one
     * row.
     *
     * @return a column index.
     */
    public int findCellColumn(String text, int row, int index) {
        return findCell(text, new int[] {row}, null, index).x;
    }

    /**
     * Searches cell coordinates.
     *
     * @return Point indicating coordinates (x - column, y - row)
     */
    public Point findCell(String text, int index) {
        return findCell(text, getComparator(), index);
    }

    /**
     * Searches cell coordinates in the specified rows and columns.
     *
     * @return Point indicating coordinates (x - column, y - row)
     */
    public Point findCell(String text, int[] rows, int[] columns, int index) {
        return findCell(new BySubStringTableCellChooser(text, getComparator()), rows, columns, index);
    }

    // text only
    /**
     * Searches first cell row by cell text.
     *
     * @return a row index.
     */
    public int findCellRow(String text) {
        return findCellRow(text, 0);
    }

    /**
     * Searches first cell column by cell text.
     *
     * @return a column index.
     */
    public int findCellColumn(String text) {
        return findCellColumn(text, 0);
    }

    // component chooser and index
    /**
     * Searches cell row by rendered component.
     *
     * @return a row index.
     */
    public int findCellRow(ComponentChooser chooser, int index) {
        return findCell(chooser, index).y;
    }

    /**
     * Searches cell row index. Searching is performed between cells in one
     * column.
     *
     * @return a row index.
     */
    public int findCellRow(ComponentChooser chooser, int column, int index) {
        return findCell(chooser, null, new int[] {column}, index).y;
    }

    /**
     * Searches cell column by rendered component.
     *
     * @return a column index.
     */
    public int findCellColumn(ComponentChooser chooser, int index) {
        return findCell(chooser, index).x;
    }

    /**
     * Searches cell column index. Searching is performed between cells in one
     * row.
     *
     * @return a column index.
     */
    public int findCellColumn(ComponentChooser chooser, int row, int index) {
        return findCell(chooser, new int[] {row}, null, index).x;
    }

    /**
     * Searches cell coordinates.
     *
     * @return Point indicating coordinates (x - column, y - row)
     */
    public Point findCell(ComponentChooser chooser, int index) {
        return findCell(new ByRenderedComponentTableCellChooser(chooser), index);
    }

    /**
     * Searches cell coordinates.
     *
     * @return Point indicating coordinates (x - column, y - row)
     */
    public Point findCell(ComponentChooser chooser, int[] rows, int[] columns, int index) {
        return findCell(new ByRenderedComponentTableCellChooser(chooser), rows, columns, index);
    }

    // component chooser only
    /**
     * Searches cell row by rendered component.
     *
     * @return a row index.
     */
    public int findCellRow(ComponentChooser chooser) {
        return findCellRow(chooser, 0);
    }

    /**
     * Searches cell column by rendered component.
     *
     * @return a column index.
     */
    public int findCellColumn(ComponentChooser chooser) {
        return findCellColumn(chooser, 0);
    }

    /**
     * Searches cell coordinates.
     *
     * @return Point indicating coordinates (x - column, y - row)
     */
    public Point findCell(ComponentChooser chooser) {
        return findCell(chooser, 0);
    }

    // cell chooser and index
    /**
     * Searches cell row by TableCellChooser.
     *
     * @return a row index.
     */
    public int findCellRow(TableCellChooser chooser, int index) {
        return findCell(chooser, index).y;
    }

    /**
     * Searches cell row index. Searching is performed between cells in one
     * column.
     *
     * @return a row index.
     */
    public int findCellRow(TableCellChooser chooser, int column, int index) {
        return findCell(chooser, null, new int[] {column}, index).y;
    }

    /**
     * Searches cell column by TableCellChooser.
     *
     * @return a column index.
     */
    public int findCellColumn(TableCellChooser chooser, int index) {
        return findCell(chooser, index).x;
    }

    /**
     * Searches cell column index. Searching is performed between cells in one
     * row.
     *
     * @return a column index.
     */
    public int findCellColumn(TableCellChooser chooser, int row, int index) {
        return findCell(chooser, new int[] {row}, null, index).x;
    }

    /**
     * Searches cell coordinates.
     *
     * @return Point indicating coordinates (x - column, y - row)
     */
    public Point findCell(TableCellChooser chooser, int index) {
        return findCell(chooser, null, null, index);
    }

    /**
     * Searches cell coordinates in the specified rows and columns.
     *
     * @return Point indicating coordinates (x - column, y - row)
     */
    public Point findCell(TableCellChooser chooser, int[] rows, int[] columns, int index) {
        TableModel model = getModel();
        int[] realRows;
        if (rows != null) {
            realRows = rows;
        } else {
            realRows = new int[model.getRowCount()];
            for (int i = 0; i < model.getRowCount(); i++) {
                realRows[i] = i;
            }
        }
        int[] realColumns;
        if (columns != null) {
            realColumns = columns;
        } else {
            realColumns = new int[model.getColumnCount()];
            for (int i = 0; i < model.getColumnCount(); i++) {
                realColumns[i] = i;
            }
        }
        int count = 0;
        for (int realRow : realRows) {
            for (int realColumn : realColumns) {
                if (chooser.checkCell(this, realRow, realColumn)) {
                    if (count == index) {
                        return new Point(realColumn, realRow);
                    } else {
                        count++;
                    }
                }
            }
        }
        return new Point(-1, -1);
    }

    // cell chooser only
    /**
     * Searches cell row by TableCellChooser.
     *
     * @return a row index.
     */
    public int findCellRow(TableCellChooser chooser) {
        return findCellRow(chooser, 0);
    }

    /**
     * Searches cell column by TableCellChooser.
     *
     * @return a column index.
     */
    public int findCellColumn(TableCellChooser chooser) {
        return findCellColumn(chooser, 0);
    }

    /**
     * Searches cell coordinates.
     *
     * @return Point indicating coordinates (x - column, y - row)
     */
    public Point findCell(TableCellChooser chooser) {
        return findCell(chooser, 0);
    }

    ////////////////////////////////////////////////////////
    // Actions                                             //
    ////////////////////////////////////////////////////////
    /**
     * Does mouse click on the cell.
     */
    public void clickOnCell(
            final int row, final int column, final int clickCount, final int button, final int modifiers) {
        output.printLine("Click on (" + Integer.toString(row) + ", " + Integer.toString(column) + ") cell");
        output.printGolden("Click on cell");
        makeComponentVisible();
        scrollToCell(row, column);
        getQueueTool().invokeSmoothly(new QueueTool.QueueAction<Void>("Path selecting") {
            @Override
            public Void launch() {
                Point point = getPointToClick(row, column);
                clickMouse(point.x, point.y, clickCount, button, modifiers);
                return null;
            }
        });
    }

    /**
     * Does mouse click on the cell with no modifiers.
     */
    public void clickOnCell(int row, int column, int clickCount, int button) {
        clickOnCell(row, column, clickCount, button, 0);
    }

    /**
     * Does mouse click on the cell by default mouse button with no modifiers.
     */
    public void clickOnCell(int row, int column, int clickCount) {
        clickOnCell(row, column, clickCount, getDefaultMouseButton());
    }

    /**
     * Does single mouse click on the cell.
     */
    public void clickOnCell(int row, int column) {
        clickOnCell(row, column, 1);
    }

    /**
     * Double clicks on cell to turns it to the editing mode.
     */
    public void clickForEdit(int row, int column) {
        clickOnCell(row, column, 2);
    }

    /**
     * Changes text of the cell pointed by row and column indexes.
     *
     * @deprecated Use changeCellObject(int, int, Object) instead.
     * @see #changeCellObject(int, int, Object)
     */
    @Deprecated
    public void changeCellText(int row, int column, String newText) {
        changeCellObject(row, column, newText);
    }

    /**
     * Changes value of the cell pointed by row and column indexes. uses editor
     * defined by setCellEditor method.
     */
    public void changeCellObject(int row, int column, Object newValue) {
        driver.editCell(this, row, column, newValue);
    }

    /**
     * Scrolls to a cell if the table lies on a JScrollPane component.
     */
    public void scrollToCell(int row, int column) {
        output.printTrace("Scroll JTable to (" + Integer.toString(row)
                + "," + Integer.toString(column) + ") cell\n    : "
                + toStringSource());
        output.printGolden("Scroll JTable to (" + Integer.toString(row) + "," + Integer.toString(column) + ")");
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
        Rectangle rect = getCellRect(row, column, false);
        scroller.scrollToComponentRectangle(
                getSource(), (int) rect.getX(), (int) rect.getY(), (int) rect.getWidth(), (int) rect.getHeight());
    }

    public void selectCell(int row, int column) {
        driver.selectCell(this, row, column);
    }

    /**
     * Searches a column by name.
     *
     * @return a column index
     */
    public int findColumn(String name, StringComparator comparator) {
        int columnCount = getColumnCount();
        for (int i = 0; i < columnCount; i++) {
            if (comparator.equals(getColumnName(i), name)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Searches a column by name.
     *
     * @return a column index
     */
    public int findColumn(String name) {
        return findColumn(name, getComparator());
    }

    /**
     * Calls popup menu on specified cell.
     *
     * @return the menu
     */
    public JPopupMenu callPopupOnCell(int row, int column) {
        output.printLine("Call popup on (" + row + ", " + column + ") cell");
        output.printGolden("Call popup on cell");
        makeComponentVisible();
        Point point = getPointToClick(row, column);
        return (JPopupMenuOperator.callPopup(
                getSource(), (int) point.getX(), (int) point.getY(), getPopupMouseButton()));
    }

    ////////////////////////////////////////////////////////
    // Gets                                                //
    ////////////////////////////////////////////////////////
    /**
     * Ask renderer for component to be displayed.
     *
     * @return Component to be displayed.
     */
    public Component getRenderedComponent(int row, int column, boolean isSelected, boolean cellHasFocus) {
        return (getCellRenderer(row, column)
                .getTableCellRendererComponent(
                        (JTable) getSource(), getValueAt(row, column), isSelected, cellHasFocus, row, column));
    }

    /**
     * Ask renderer for component to be displayed. Uses
     * isCellSelected(itemIndex) to determine whether cell is selected. Supposes
     * item do not have focus.
     *
     * @return Component to be displayed.
     */
    public Component getRenderedComponent(int row, int column) {
        return (getRenderedComponent(row, column, isCellSelected(row, column), false));
    }

    /**
     * Returns a point at the center of the cell rectangle.
     *
     * @return a Point in component's coordinate system.
     */
    public Point getPointToClick(int row, int column) {
        Rectangle rect = getCellRect(row, column, false);
        return (new Point((int) (rect.getX() + rect.getWidth() / 2), (int) (rect.getY() + rect.getHeight() / 2)));
    }

    /**
     * Creates an operator for a teble header assigned to this table.
     *
     * @return an JTableHeaderOperator operator
     */
    public JTableHeaderOperator getHeaderOperator() {
        return new JTableHeaderOperator(getTableHeader());
    }

    /**
     * Waits for an editor.
     *
     * @return a component displayed over the cell and fitting the criteria
     * specified by {@code chooser}
     */
    public Component waitCellComponent(ComponentChooser chooser, int row, int column) {
        CellComponentWaiter waiter = new CellComponentWaiter(chooser, row, column);
        waiter.setOutput(getOutput());
        waiter.setTimeoutsToCloneOf(getTimeouts(), "JTableOperator.WaitEditingTimeout");
        try {
            return waiter.waitAction(null);
        } catch (InterruptedException e) {
            throw (new JemmyException("Waiting has been interrupted", e));
        }
    }

    /**
     * Waits for certain cell contents.
     *
     * @param cellText Text comparing to cell text by
     * {@code getComparator()} comparator.
     * @param row cell row index. If -1, selected one is checked.
     * @param column cell column visible index. If -1, selected one is checked.
     */
    public void waitCell(String cellText, int row, int column) {
        getOutput()
                .printLine("Wait \"" + cellText + "\" text at ("
                        + Integer.toString(row) + ","
                        + Integer.toString(column) + ")"
                        + " position in component \n    : "
                        + toStringSource());
        getOutput()
                .printGolden("Wait  \"" + cellText + "\" text at ("
                        + Integer.toString(row) + ","
                        + Integer.toString(column) + ")"
                        + " position");
        waitState(new JTableByCellFinder(cellText, row, column, getComparator()));
    }

    /**
     * Returns information about component.
     */
    @Override
    public Hashtable<String, Object> getDump() {
        Hashtable<String, Object> result = super.getDump();
        TableModel model = ((JTable) getSource()).getModel();
        int colCount = model.getColumnCount();
        int rowCount = model.getRowCount();
        String[][] items = new String[rowCount][colCount];
        for (int i = 0; i < rowCount; i++) {
            for (int j = 0; j < colCount; j++) {
                if (model.getValueAt(i, j) != null) {
                    items[i][j] = model.getValueAt(i, j).toString();
                } else {
                    items[i][j] = "null";
                }
            }
        }
        addToDump(result, CELL_PREFIX_DPROP, items);
        String[] columns = new String[colCount];
        for (int j = 0; j < colCount; j++) {
            columns[j] = ((JTable) getSource()).getColumnName(j);
        }
        addToDump(result, COLUMN_PREFIX_DPROP, columns);
        int[] selColNums = ((JTable) getSource()).getSelectedColumns();
        String[] selColumns = new String[selColNums.length];
        for (int j = 0; j < selColNums.length; j++) {
            selColumns[j] = Integer.toString(selColNums[j]);
        }
        addToDump(result, SELECTED_COLUMN_PREFIX_DPROP, selColumns);
        int[] selRowNums = ((JTable) getSource()).getSelectedRows();
        String[] selRows = new String[selRowNums.length];
        for (int i = 0; i < selRowNums.length; i++) {
            selRows[i] = Integer.toString(selRowNums[i]);
        }
        addToDump(result, SELECTED_ROW_PREFIX_DPROP, selRows);
        result.put(COLUMN_COUNT_DPROP, Integer.toString(colCount));
        result.put(ROW_COUNT_DPROP, Integer.toString(rowCount));
        return result;
    }

    ////////////////////////////////////////////////////////
    // Mapping                                             //
    public void addColumn(final TableColumn tableColumn) {
        runMapping(new MapVoidAction("addColumn") {
            @Override
            public void map() {
                ((JTable) getSource()).addColumn(tableColumn);
            }
        });
    }

    public void addColumnSelectionInterval(final int i, final int i1) {
        runMapping(new MapVoidAction("addColumnSelectionInterval") {
            @Override
            public void map() {
                ((JTable) getSource()).addColumnSelectionInterval(i, i1);
            }
        });
    }

    public void addRowSelectionInterval(final int i, final int i1) {
        runMapping(new MapVoidAction("addRowSelectionInterval") {
            @Override
            public void map() {
                ((JTable) getSource()).addRowSelectionInterval(i, i1);
            }
        });
    }

    public void changeSelection(int rowIndex, int columnIndex, boolean toggle, boolean extend) {
        runMapping(new MapVoidAction("clearSelection") {
            @Override
            public void map() {
                ((JTable) getSource()).changeSelection(rowIndex, columnIndex, toggle, extend);
            }
        });
    }

    public void clearSelection() {
        runMapping(new MapVoidAction("clearSelection") {
            @Override
            public void map() {
                ((JTable) getSource()).clearSelection();
            }
        });
    }

    public void columnAdded(final TableColumnModelEvent tableColumnModelEvent) {
        runMapping(new MapVoidAction("columnAdded") {
            @Override
            public void map() {
                ((JTable) getSource()).columnAdded(tableColumnModelEvent);
            }
        });
    }

    public int columnAtPoint(final Point point) {
        return (runMapping(new MapIntegerAction("columnAtPoint") {
            @Override
            public int map() {
                return ((JTable) getSource()).columnAtPoint(point);
            }
        }));
    }

    public void columnMarginChanged(final ChangeEvent changeEvent) {
        runMapping(new MapVoidAction("columnMarginChanged") {
            @Override
            public void map() {
                ((JTable) getSource()).columnMarginChanged(changeEvent);
            }
        });
    }

    public void columnMoved(final TableColumnModelEvent tableColumnModelEvent) {
        runMapping(new MapVoidAction("columnMoved") {
            @Override
            public void map() {
                ((JTable) getSource()).columnMoved(tableColumnModelEvent);
            }
        });
    }

    public void columnRemoved(final TableColumnModelEvent tableColumnModelEvent) {
        runMapping(new MapVoidAction("columnRemoved") {
            @Override
            public void map() {
                ((JTable) getSource()).columnRemoved(tableColumnModelEvent);
            }
        });
    }

    public void columnSelectionChanged(final ListSelectionEvent listSelectionEvent) {
        runMapping(new MapVoidAction("columnSelectionChanged") {
            @Override
            public void map() {
                ((JTable) getSource()).columnSelectionChanged(listSelectionEvent);
            }
        });
    }

    public int convertColumnIndexToModel(final int i) {
        return (runMapping(new MapIntegerAction("convertColumnIndexToModel") {
            @Override
            public int map() {
                return ((JTable) getSource()).convertColumnIndexToModel(i);
            }
        }));
    }

    public int convertColumnIndexToView(final int i) {
        return (runMapping(new MapIntegerAction("convertColumnIndexToView") {
            @Override
            public int map() {
                return ((JTable) getSource()).convertColumnIndexToView(i);
            }
        }));
    }

    public void createDefaultColumnsFromModel() {
        runMapping(new MapVoidAction("createDefaultColumnsFromModel") {
            @Override
            public void map() {
                ((JTable) getSource()).createDefaultColumnsFromModel();
            }
        });
    }

    public boolean editCellAt(final int i, final int i1) {
        return (runMapping(new MapBooleanAction("editCellAt") {
            @Override
            public boolean map() {
                return ((JTable) getSource()).editCellAt(i, i1);
            }
        }));
    }

    public boolean editCellAt(final int i, final int i1, final EventObject eventObject) {
        return (runMapping(new MapBooleanAction("editCellAt") {
            @Override
            public boolean map() {
                return ((JTable) getSource()).editCellAt(i, i1, eventObject);
            }
        }));
    }

    public void editingCanceled(final ChangeEvent changeEvent) {
        runMapping(new MapVoidAction("editingCanceled") {
            @Override
            public void map() {
                ((JTable) getSource()).editingCanceled(changeEvent);
            }
        });
    }

    public void editingStopped(final ChangeEvent changeEvent) {
        runMapping(new MapVoidAction("editingStopped") {
            @Override
            public void map() {
                ((JTable) getSource()).editingStopped(changeEvent);
            }
        });
    }

    public boolean getAutoCreateColumnsFromModel() {
        return (runMapping(new MapBooleanAction("getAutoCreateColumnsFromModel") {
            @Override
            public boolean map() {
                return ((JTable) getSource()).getAutoCreateColumnsFromModel();
            }
        }));
    }

    public int getAutoResizeMode() {
        return (runMapping(new MapIntegerAction("getAutoResizeMode") {
            @Override
            public int map() {
                return ((JTable) getSource()).getAutoResizeMode();
            }
        }));
    }

    public TableCellEditor getCellEditor() {
        return (runMapping(new MapAction<TableCellEditor>("getCellEditor") {
            @Override
            public TableCellEditor map() {
                return ((JTable) getSource()).getCellEditor();
            }
        }));
    }

    public TableCellEditor getCellEditor(final int i, final int i1) {
        return (runMapping(new MapAction<TableCellEditor>("getCellEditor") {
            @Override
            public TableCellEditor map() {
                return ((JTable) getSource()).getCellEditor(i, i1);
            }
        }));
    }

    public Rectangle getCellRect(final int i, final int i1, final boolean b) {
        return (runMapping(new MapAction<Rectangle>("getCellRect") {
            @Override
            public Rectangle map() {
                return ((JTable) getSource()).getCellRect(i, i1, b);
            }
        }));
    }

    public TableCellRenderer getCellRenderer(final int i, final int i1) {
        return (runMapping(new MapAction<TableCellRenderer>("getCellRenderer") {
            @Override
            public TableCellRenderer map() {
                return ((JTable) getSource()).getCellRenderer(i, i1);
            }
        }));
    }

    public boolean getCellSelectionEnabled() {
        return (runMapping(new MapBooleanAction("getCellSelectionEnabled") {
            @Override
            public boolean map() {
                return ((JTable) getSource()).getCellSelectionEnabled();
            }
        }));
    }

    public TableColumn getColumn(final Object object) {
        return (runMapping(new MapAction<TableColumn>("getColumn") {
            @Override
            public TableColumn map() {
                return ((JTable) getSource()).getColumn(object);
            }
        }));
    }

    public Class<?> getColumnClass(final int i) {
        return (runMapping(new MapAction<Class<?>>("getColumnClass") {
            @Override
            public Class<?> map() {
                return ((JTable) getSource()).getColumnClass(i);
            }
        }));
    }

    public int getColumnCount() {
        return (runMapping(new MapIntegerAction("getColumnCount") {
            @Override
            public int map() {
                return ((JTable) getSource()).getColumnCount();
            }
        }));
    }

    public TableColumnModel getColumnModel() {
        return (runMapping(new MapAction<TableColumnModel>("getColumnModel") {
            @Override
            public TableColumnModel map() {
                return ((JTable) getSource()).getColumnModel();
            }
        }));
    }

    public String getColumnName(final int i) {
        return (runMapping(new MapAction<String>("getColumnName") {
            @Override
            public String map() {
                return ((JTable) getSource()).getColumnName(i);
            }
        }));
    }

    public boolean getColumnSelectionAllowed() {
        return (runMapping(new MapBooleanAction("getColumnSelectionAllowed") {
            @Override
            public boolean map() {
                return ((JTable) getSource()).getColumnSelectionAllowed();
            }
        }));
    }

    public TableCellEditor getDefaultEditor(final Class<?> clss) {
        return (runMapping(new MapAction<TableCellEditor>("getDefaultEditor") {
            @Override
            public TableCellEditor map() {
                return ((JTable) getSource()).getDefaultEditor(clss);
            }
        }));
    }

    public TableCellRenderer getDefaultRenderer(final Class<?> clss) {
        return (runMapping(new MapAction<TableCellRenderer>("getDefaultRenderer") {
            @Override
            public TableCellRenderer map() {
                return ((JTable) getSource()).getDefaultRenderer(clss);
            }
        }));
    }

    public int getEditingColumn() {
        return (runMapping(new MapIntegerAction("getEditingColumn") {
            @Override
            public int map() {
                return ((JTable) getSource()).getEditingColumn();
            }
        }));
    }

    public int getEditingRow() {
        return (runMapping(new MapIntegerAction("getEditingRow") {
            @Override
            public int map() {
                return ((JTable) getSource()).getEditingRow();
            }
        }));
    }

    public Component getEditorComponent() {
        return (runMapping(new MapAction<Component>("getEditorComponent") {
            @Override
            public Component map() {
                return ((JTable) getSource()).getEditorComponent();
            }
        }));
    }

    public Color getGridColor() {
        return (runMapping(new MapAction<Color>("getGridColor") {
            @Override
            public Color map() {
                return ((JTable) getSource()).getGridColor();
            }
        }));
    }

    public Dimension getIntercellSpacing() {
        return (runMapping(new MapAction<Dimension>("getIntercellSpacing") {
            @Override
            public Dimension map() {
                return ((JTable) getSource()).getIntercellSpacing();
            }
        }));
    }

    public TableModel getModel() {
        return (runMapping(new MapAction<TableModel>("getModel") {
            @Override
            public TableModel map() {
                return ((JTable) getSource()).getModel();
            }
        }));
    }

    public Dimension getPreferredScrollableViewportSize() {
        return (runMapping(new MapAction<Dimension>("getPreferredScrollableViewportSize") {
            @Override
            public Dimension map() {
                return ((JTable) getSource()).getPreferredScrollableViewportSize();
            }
        }));
    }

    public int getRowCount() {
        return (runMapping(new MapIntegerAction("getRowCount") {
            @Override
            public int map() {
                return ((JTable) getSource()).getRowCount();
            }
        }));
    }

    public int getRowHeight() {
        return (runMapping(new MapIntegerAction("getRowHeight") {
            @Override
            public int map() {
                return ((JTable) getSource()).getRowHeight();
            }
        }));
    }

    public int getRowMargin() {
        return (runMapping(new MapIntegerAction("getRowMargin") {
            @Override
            public int map() {
                return ((JTable) getSource()).getRowMargin();
            }
        }));
    }

    public boolean getRowSelectionAllowed() {
        return (runMapping(new MapBooleanAction("getRowSelectionAllowed") {
            @Override
            public boolean map() {
                return ((JTable) getSource()).getRowSelectionAllowed();
            }
        }));
    }

    public int getScrollableBlockIncrement(final Rectangle rectangle, final int i, final int i1) {
        return (runMapping(new MapIntegerAction("getScrollableBlockIncrement") {
            @Override
            public int map() {
                return ((JTable) getSource()).getScrollableBlockIncrement(rectangle, i, i1);
            }
        }));
    }

    public boolean getScrollableTracksViewportHeight() {
        return (runMapping(new MapBooleanAction("getScrollableTracksViewportHeight") {
            @Override
            public boolean map() {
                return ((JTable) getSource()).getScrollableTracksViewportHeight();
            }
        }));
    }

    public boolean getScrollableTracksViewportWidth() {
        return (runMapping(new MapBooleanAction("getScrollableTracksViewportWidth") {
            @Override
            public boolean map() {
                return ((JTable) getSource()).getScrollableTracksViewportWidth();
            }
        }));
    }

    public int getScrollableUnitIncrement(final Rectangle rectangle, final int i, final int i1) {
        return (runMapping(new MapIntegerAction("getScrollableUnitIncrement") {
            @Override
            public int map() {
                return ((JTable) getSource()).getScrollableUnitIncrement(rectangle, i, i1);
            }
        }));
    }

    public int getSelectedColumn() {
        return (runMapping(new MapIntegerAction("getSelectedColumn") {
            @Override
            public int map() {
                return ((JTable) getSource()).getSelectedColumn();
            }
        }));
    }

    public int getSelectedColumnCount() {
        return (runMapping(new MapIntegerAction("getSelectedColumnCount") {
            @Override
            public int map() {
                return ((JTable) getSource()).getSelectedColumnCount();
            }
        }));
    }

    public int[] getSelectedColumns() {
        return ((int[]) runMapping(new MapAction<Object>("getSelectedColumns") {
            @Override
            public Object map() {
                return ((JTable) getSource()).getSelectedColumns();
            }
        }));
    }

    public int getSelectedRow() {
        return (runMapping(new MapIntegerAction("getSelectedRow") {
            @Override
            public int map() {
                return ((JTable) getSource()).getSelectedRow();
            }
        }));
    }

    public int getSelectedRowCount() {
        return (runMapping(new MapIntegerAction("getSelectedRowCount") {
            @Override
            public int map() {
                return ((JTable) getSource()).getSelectedRowCount();
            }
        }));
    }

    public int[] getSelectedRows() {
        return ((int[]) runMapping(new MapAction<Object>("getSelectedRows") {
            @Override
            public Object map() {
                return ((JTable) getSource()).getSelectedRows();
            }
        }));
    }

    public Color getSelectionBackground() {
        return (runMapping(new MapAction<Color>("getSelectionBackground") {
            @Override
            public Color map() {
                return ((JTable) getSource()).getSelectionBackground();
            }
        }));
    }

    public Color getSelectionForeground() {
        return (runMapping(new MapAction<Color>("getSelectionForeground") {
            @Override
            public Color map() {
                return ((JTable) getSource()).getSelectionForeground();
            }
        }));
    }

    public ListSelectionModel getSelectionModel() {
        return (runMapping(new MapAction<ListSelectionModel>("getSelectionModel") {
            @Override
            public ListSelectionModel map() {
                return ((JTable) getSource()).getSelectionModel();
            }
        }));
    }

    public boolean getShowHorizontalLines() {
        return (runMapping(new MapBooleanAction("getShowHorizontalLines") {
            @Override
            public boolean map() {
                return ((JTable) getSource()).getShowHorizontalLines();
            }
        }));
    }

    public boolean getShowVerticalLines() {
        return (runMapping(new MapBooleanAction("getShowVerticalLines") {
            @Override
            public boolean map() {
                return ((JTable) getSource()).getShowVerticalLines();
            }
        }));
    }

    public JTableHeader getTableHeader() {
        return (runMapping(new MapAction<JTableHeader>("getTableHeader") {
            @Override
            public JTableHeader map() {
                return ((JTable) getSource()).getTableHeader();
            }
        }));
    }

    public TableUI getUI() {
        return (runMapping(new MapAction<TableUI>("getUI") {
            @Override
            public TableUI map() {
                return ((JTable) getSource()).getUI();
            }
        }));
    }

    public Object getValueAt(final int i, final int i1) {
        return (runMapping(new MapAction<Object>("getValueAt") {
            @Override
            public Object map() {
                return ((JTable) getSource()).getValueAt(i, i1);
            }
        }));
    }

    public boolean isCellEditable(final int i, final int i1) {
        return (runMapping(new MapBooleanAction("isCellEditable") {
            @Override
            public boolean map() {
                return ((JTable) getSource()).isCellEditable(i, i1);
            }
        }));
    }

    public boolean isCellSelected(final int i, final int i1) {
        return (runMapping(new MapBooleanAction("isCellSelected") {
            @Override
            public boolean map() {
                return ((JTable) getSource()).isCellSelected(i, i1);
            }
        }));
    }

    public boolean isColumnSelected(final int i) {
        return (runMapping(new MapBooleanAction("isColumnSelected") {
            @Override
            public boolean map() {
                return ((JTable) getSource()).isColumnSelected(i);
            }
        }));
    }

    public boolean isEditing() {
        return (runMapping(new MapBooleanAction("isEditing") {
            @Override
            public boolean map() {
                return ((JTable) getSource()).isEditing();
            }
        }));
    }

    public boolean isRowSelected(final int i) {
        return (runMapping(new MapBooleanAction("isRowSelected") {
            @Override
            public boolean map() {
                return ((JTable) getSource()).isRowSelected(i);
            }
        }));
    }

    public void moveColumn(final int i, final int i1) {
        runMapping(new MapVoidAction("moveColumn") {
            @Override
            public void map() {
                ((JTable) getSource()).moveColumn(i, i1);
            }
        });
    }

    public Component prepareEditor(final TableCellEditor tableCellEditor, final int i, final int i1) {
        return (runMapping(new MapAction<Component>("prepareEditor") {
            @Override
            public Component map() {
                return ((JTable) getSource()).prepareEditor(tableCellEditor, i, i1);
            }
        }));
    }

    public Component prepareRenderer(final TableCellRenderer tableCellRenderer, final int i, final int i1) {
        return (runMapping(new MapAction<Component>("prepareRenderer") {
            @Override
            public Component map() {
                return ((JTable) getSource()).prepareRenderer(tableCellRenderer, i, i1);
            }
        }));
    }

    public void removeColumn(final TableColumn tableColumn) {
        runMapping(new MapVoidAction("removeColumn") {
            @Override
            public void map() {
                ((JTable) getSource()).removeColumn(tableColumn);
            }
        });
    }

    public void removeColumnSelectionInterval(final int i, final int i1) {
        runMapping(new MapVoidAction("removeColumnSelectionInterval") {
            @Override
            public void map() {
                ((JTable) getSource()).removeColumnSelectionInterval(i, i1);
            }
        });
    }

    public void removeEditor() {
        runMapping(new MapVoidAction("removeEditor") {
            @Override
            public void map() {
                ((JTable) getSource()).removeEditor();
            }
        });
    }

    public void removeRowSelectionInterval(final int i, final int i1) {
        runMapping(new MapVoidAction("removeRowSelectionInterval") {
            @Override
            public void map() {
                ((JTable) getSource()).removeRowSelectionInterval(i, i1);
            }
        });
    }

    public int rowAtPoint(final Point point) {
        return (runMapping(new MapIntegerAction("rowAtPoint") {
            @Override
            public int map() {
                return ((JTable) getSource()).rowAtPoint(point);
            }
        }));
    }

    public void selectAll() {
        runMapping(new MapVoidAction("selectAll") {
            @Override
            public void map() {
                ((JTable) getSource()).selectAll();
            }
        });
    }

    public void setAutoCreateColumnsFromModel(final boolean b) {
        runMapping(new MapVoidAction("setAutoCreateColumnsFromModel") {
            @Override
            public void map() {
                ((JTable) getSource()).setAutoCreateColumnsFromModel(b);
            }
        });
    }

    public void setAutoResizeMode(final int i) {
        runMapping(new MapVoidAction("setAutoResizeMode") {
            @Override
            public void map() {
                ((JTable) getSource()).setAutoResizeMode(i);
            }
        });
    }

    public void setCellEditor(final TableCellEditor tableCellEditor) {
        runMapping(new MapVoidAction("setCellEditor") {
            @Override
            public void map() {
                ((JTable) getSource()).setCellEditor(tableCellEditor);
            }
        });
    }

    public void setCellSelectionEnabled(final boolean b) {
        runMapping(new MapVoidAction("setCellSelectionEnabled") {
            @Override
            public void map() {
                ((JTable) getSource()).setCellSelectionEnabled(b);
            }
        });
    }

    public void setColumnModel(final TableColumnModel tableColumnModel) {
        runMapping(new MapVoidAction("setColumnModel") {
            @Override
            public void map() {
                ((JTable) getSource()).setColumnModel(tableColumnModel);
            }
        });
    }

    public void setColumnSelectionAllowed(final boolean b) {
        runMapping(new MapVoidAction("setColumnSelectionAllowed") {
            @Override
            public void map() {
                ((JTable) getSource()).setColumnSelectionAllowed(b);
            }
        });
    }

    public void setColumnSelectionInterval(final int i, final int i1) {
        runMapping(new MapVoidAction("setColumnSelectionInterval") {
            @Override
            public void map() {
                ((JTable) getSource()).setColumnSelectionInterval(i, i1);
            }
        });
    }

    public void setDefaultEditor(final Class<?> clss, final TableCellEditor tableCellEditor) {
        runMapping(new MapVoidAction("setDefaultEditor") {
            @Override
            public void map() {
                ((JTable) getSource()).setDefaultEditor(clss, tableCellEditor);
            }
        });
    }

    public void setDefaultRenderer(final Class<?> clss, final TableCellRenderer tableCellRenderer) {
        runMapping(new MapVoidAction("setDefaultRenderer") {
            @Override
            public void map() {
                ((JTable) getSource()).setDefaultRenderer(clss, tableCellRenderer);
            }
        });
    }

    public void setEditingColumn(final int i) {
        runMapping(new MapVoidAction("setEditingColumn") {
            @Override
            public void map() {
                ((JTable) getSource()).setEditingColumn(i);
            }
        });
    }

    public void setEditingRow(final int i) {
        runMapping(new MapVoidAction("setEditingRow") {
            @Override
            public void map() {
                ((JTable) getSource()).setEditingRow(i);
            }
        });
    }

    public void setGridColor(final Color color) {
        runMapping(new MapVoidAction("setGridColor") {
            @Override
            public void map() {
                ((JTable) getSource()).setGridColor(color);
            }
        });
    }

    public void setIntercellSpacing(final Dimension dimension) {
        runMapping(new MapVoidAction("setIntercellSpacing") {
            @Override
            public void map() {
                ((JTable) getSource()).setIntercellSpacing(dimension);
            }
        });
    }

    public void setModel(final TableModel tableModel) {
        runMapping(new MapVoidAction("setModel") {
            @Override
            public void map() {
                ((JTable) getSource()).setModel(tableModel);
            }
        });
    }

    public void setPreferredScrollableViewportSize(final Dimension dimension) {
        runMapping(new MapVoidAction("setPreferredScrollableViewportSize") {
            @Override
            public void map() {
                ((JTable) getSource()).setPreferredScrollableViewportSize(dimension);
            }
        });
    }

    public void setRowHeight(final int i) {
        runMapping(new MapVoidAction("setRowHeight") {
            @Override
            public void map() {
                ((JTable) getSource()).setRowHeight(i);
            }
        });
    }

    public void setRowMargin(final int i) {
        runMapping(new MapVoidAction("setRowMargin") {
            @Override
            public void map() {
                ((JTable) getSource()).setRowMargin(i);
            }
        });
    }

    public void setRowSelectionAllowed(final boolean b) {
        runMapping(new MapVoidAction("setRowSelectionAllowed") {
            @Override
            public void map() {
                ((JTable) getSource()).setRowSelectionAllowed(b);
            }
        });
    }

    public void setRowSelectionInterval(final int i, final int i1) {
        runMapping(new MapVoidAction("setRowSelectionInterval") {
            @Override
            public void map() {
                ((JTable) getSource()).setRowSelectionInterval(i, i1);
            }
        });
    }

    public void setSelectionBackground(final Color color) {
        runMapping(new MapVoidAction("setSelectionBackground") {
            @Override
            public void map() {
                ((JTable) getSource()).setSelectionBackground(color);
            }
        });
    }

    public void setSelectionForeground(final Color color) {
        runMapping(new MapVoidAction("setSelectionForeground") {
            @Override
            public void map() {
                ((JTable) getSource()).setSelectionForeground(color);
            }
        });
    }

    public void setSelectionMode(final int i) {
        runMapping(new MapVoidAction("setSelectionMode") {
            @Override
            public void map() {
                ((JTable) getSource()).setSelectionMode(i);
            }
        });
    }

    public void setSelectionModel(final ListSelectionModel listSelectionModel) {
        runMapping(new MapVoidAction("setSelectionModel") {
            @Override
            public void map() {
                ((JTable) getSource()).setSelectionModel(listSelectionModel);
            }
        });
    }

    public void setShowGrid(final boolean b) {
        runMapping(new MapVoidAction("setShowGrid") {
            @Override
            public void map() {
                ((JTable) getSource()).setShowGrid(b);
            }
        });
    }

    public void setShowHorizontalLines(final boolean b) {
        runMapping(new MapVoidAction("setShowHorizontalLines") {
            @Override
            public void map() {
                ((JTable) getSource()).setShowHorizontalLines(b);
            }
        });
    }

    public void setShowVerticalLines(final boolean b) {
        runMapping(new MapVoidAction("setShowVerticalLines") {
            @Override
            public void map() {
                ((JTable) getSource()).setShowVerticalLines(b);
            }
        });
    }

    public void setTableHeader(final JTableHeader jTableHeader) {
        runMapping(new MapVoidAction("setTableHeader") {
            @Override
            public void map() {
                ((JTable) getSource()).setTableHeader(jTableHeader);
            }
        });
    }

    public void setUI(final TableUI tableUI) {
        runMapping(new MapVoidAction("setUI") {
            @Override
            public void map() {
                ((JTable) getSource()).setUI(tableUI);
            }
        });
    }

    public void setValueAt(final Object object, final int i, final int i1) {
        runMapping(new MapVoidAction("setValueAt") {
            @Override
            public void map() {
                ((JTable) getSource()).setValueAt(object, i, i1);
            }
        });
    }

    public void tableChanged(final TableModelEvent tableModelEvent) {
        runMapping(new MapVoidAction("tableChanged") {
            @Override
            public void map() {
                ((JTable) getSource()).tableChanged(tableModelEvent);
            }
        });
    }

    public void valueChanged(final ListSelectionEvent listSelectionEvent) {
        runMapping(new MapVoidAction("valueChanged") {
            @Override
            public void map() {
                ((JTable) getSource()).valueChanged(listSelectionEvent);
            }
        });
    }

    // End of mapping                                      //
    ////////////////////////////////////////////////////////
    private Point findCell(String text, boolean ce, boolean ccs, int index) {
        return findCell(text, new DefaultStringComparator(ce, ccs), index);
    }

    /**
     * Iterface to choose table cell.
     */
    public interface TableCellChooser {

        /**
         * Should be true if item is good.
         *
         * @return true if cell fits the criteria
         */
        public boolean checkCell(JTableOperator oper, int row, int column);

        /**
         * Item description.
         *
         * @return the description.
         */
        public String getDescription();
    }

    private static class BySubStringTableCellChooser implements TableCellChooser {

        String subString;
        StringComparator comparator;

        public BySubStringTableCellChooser(String subString, StringComparator comparator) {
            this.subString = subString;
            this.comparator = comparator;
        }

        @Override
        public boolean checkCell(JTableOperator oper, int row, int column) {
            Object value = ((JTable) oper.getSource()).getModel().getValueAt(row, column);
            return (comparator.equals((value != null) ? value.toString() : null, subString));
        }

        @Override
        public String getDescription() {
            return "Cell containing \"" + subString + "\" string";
        }

        @Override
        public String toString() {
            return "BySubStringTableCellChooser{" + "subString=" + subString + ", comparator=" + comparator + '}';
        }
    }

    private static class ByRenderedComponentTableCellChooser implements TableCellChooser {

        ComponentChooser chooser;

        public ByRenderedComponentTableCellChooser(ComponentChooser chooser) {
            this.chooser = chooser;
        }

        @Override
        public boolean checkCell(JTableOperator oper, int row, int column) {
            return chooser.checkComponent(oper.getRenderedComponent(row, column));
        }

        @Override
        public String getDescription() {
            return chooser.getDescription();
        }

        @Override
        public String toString() {
            return "ByRenderedComponentTableCellChooser{" + "chooser=" + chooser + '}';
        }
    }

    /**
     * Allows to find component by cell text.
     */
    public static class JTableByCellFinder implements ComponentChooser {

        String label;
        int row;
        int column;
        StringComparator comparator;

        /**
         * Constructs JTableByCellFinder.
         *
         * @param r a row index to look in. If equal to -1, selected row is
         * checked.
         * @param c a column index to look in. If equal to -1, selected column
         * is checked.
         */
        public JTableByCellFinder(String lb, int r, int c, StringComparator comparator) {
            label = lb;
            row = r;
            column = c;
            this.comparator = comparator;
        }

        /**
         * Constructs JTableByCellFinder.
         *
         * @param r a row index to look in. If equal to -1, selected row is
         * checked.
         * @param c a column index to look in. If equal to -1, selected column
         * is checked.
         */
        public JTableByCellFinder(String lb, int r, int c) {
            this(lb, r, c, Operator.getDefaultStringComparator());
        }

        @Override
        public boolean checkComponent(Component comp) {
            if (comp instanceof JTable) {
                if (label == null) {
                    return true;
                }
                if (((JTable) comp).getRowCount() > row && ((JTable) comp).getColumnCount() > column) {
                    int r = row;
                    if (r == -1) {
                        int[] rows = ((JTable) comp).getSelectedRows();
                        if (rows.length != 0) {
                            r = rows[0];
                        } else {
                            return false;
                        }
                    }
                    int c = column;
                    if (c == -1) {
                        int[] columns = ((JTable) comp).getSelectedColumns();
                        if (columns.length != 0) {
                            c = columns[0];
                        } else {
                            return false;
                        }
                    }
                    Object value = ((JTable) comp).getValueAt(r, c);
                    if (value == null) {
                        return false;
                    }
                    return (comparator.equals(value.toString(), label));
                }
            }
            return false;
        }

        @Override
        public String getDescription() {
            return ("JTable with text \"" + label + "\" in (" + row + ", " + column + ") cell");
        }

        @Override
        public String toString() {
            return "JTableByCellFinder{" + "label=" + label + ", row=" + row + ", column=" + column + ", comparator="
                    + comparator + '}';
        }
    }

    /**
     * Checks component type.
     */
    public static class JTableFinder extends Finder {

        /**
         * Constructs JTableFinder.
         */
        public JTableFinder(ComponentChooser sf) {
            super(JTable.class, sf);
        }

        /**
         * Constructs JTableFinder.
         */
        public JTableFinder() {
            super(JTable.class);
        }
    }

    private class CellComponentWaiter extends Waiter<Component, Void> {

        private ComponentChooser chooser;
        private int row, column;

        public CellComponentWaiter(ComponentChooser chooser, int row, int column) {
            this.chooser = chooser;
            this.row = row;
            this.column = column;
        }

        @Override
        public Component actionProduced(Void obj) {
            Point pnt = getPointToClick(row, column);
            Component comp = getComponentAt(pnt.x, pnt.y);
            if (comp != null && chooser.checkComponent(comp)) {
                return comp;
            } else {
                return null;
            }
        }

        @Override
        public String getDescription() {
            return chooser.getDescription();
        }

        @Override
        public String toString() {
            return "CellComponentWaiter{" + "chooser=" + chooser + ", row=" + row + ", column=" + column + '}';
        }
    }
}
