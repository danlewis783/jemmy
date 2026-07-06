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
import java.awt.Point;
import java.awt.Rectangle;
import javax.swing.JTable;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.plaf.TableHeaderUI;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import org.netbeans.jemmy.ComponentChooser;
import org.netbeans.jemmy.ComponentSearcher;
import org.netbeans.jemmy.Outputable;
import org.netbeans.jemmy.TestOut;
import org.netbeans.jemmy.Timeoutable;
import org.netbeans.jemmy.Timeouts;
import org.netbeans.jemmy.drivers.DriverManager;
import org.netbeans.jemmy.drivers.OrderedListDriver;

/**
 * ComponentOperator.BeforeDragTimeout - time to sleep before column moving
 * <p>
 * ComponentOperator.AfterDragTimeout - time to sleep after column moving
 * <p>
 * ComponentOperator.WaitComponentTimeout - time to wait component displayed
 */
public class JTableHeaderOperator extends JComponentOperator implements Outputable, Timeoutable {

    private @SuppressWarnings("NullAway.Init") TestOut output;
    private @SuppressWarnings("NullAway.Init") Timeouts timeouts;

    private OrderedListDriver driver;

    public JTableHeaderOperator(JTableHeader b) {
        super(b);
        driver = DriverManager.getOrderedListDriver(getClass());
    }

    public JTableHeaderOperator(ContainerOperator<?> cont, ComponentChooser chooser, int index) {
        this((JTableHeader) cont.waitSubComponent(new JTableHeaderFinder(chooser), index));
        copyEnvironment(cont);
    }

    public JTableHeaderOperator(ContainerOperator<?> cont, ComponentChooser chooser) {
        this(cont, chooser, 0);
    }

    public JTableHeaderOperator(ContainerOperator<?> cont, int index) {
        this((JTableHeader) waitComponent(
                cont, new JTableHeaderFinder(ComponentSearcher.getTrueChooser("Any JTableHeader")), index));
        copyEnvironment(cont);
    }

    public JTableHeaderOperator(ContainerOperator<?> cont) {
        this(cont, 0);
    }

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

    public void selectColumn(int columnIndex) {
        driver.selectItem(this, columnIndex);
    }

    /**
     * Selects some columns.
     */
    public void selectColumns(int[] columnIndices) {
        driver.selectItems(this, columnIndices);
    }

    /**
     * Moves a column to a different location.
     */
    public void moveColumn(int moveColumn, int moveTo) {
        driver.moveItem(this, moveColumn, moveTo);
    }

    /**
     * Return a point to click on column header.
     *
     * @return the point to click.
     */
    public Point getPointToClick(int columnIndex) {
        Rectangle rect = getHeaderRect(columnIndex);
        return (new Point(rect.x + rect.width / 2, rect.y + rect.height / 2));
    }

    @Override
    public void copyEnvironment(Operator anotherOperator) {
        super.copyEnvironment(anotherOperator);
        driver = (OrderedListDriver) DriverManager.getDriver(
                DriverManager.ORDEREDLIST_DRIVER_ID, getClass(), anotherOperator.getProperties());
    }

    ////////////////////////////////////////////////////////
    // Mapping                                             //
    public void setTable(final JTable jTable) {
        runMapping(new MapVoidAction("setTable") {
            @Override
            public void map() {
                ((JTableHeader) getSource()).setTable(jTable);
            }
        });
    }

    public JTable getTable() {
        return (runMapping(new MapAction<JTable>("getTable") {
            @Override
            public JTable map() {
                return ((JTableHeader) getSource()).getTable();
            }
        }));
    }

    public void setReorderingAllowed(final boolean b) {
        runMapping(new MapVoidAction("setReorderingAllowed") {
            @Override
            public void map() {
                ((JTableHeader) getSource()).setReorderingAllowed(b);
            }
        });
    }

    public boolean getReorderingAllowed() {
        return (runMapping(new MapBooleanAction("getReorderingAllowed") {
            @Override
            public boolean map() {
                return ((JTableHeader) getSource()).getReorderingAllowed();
            }
        }));
    }

    public void setResizingAllowed(final boolean b) {
        runMapping(new MapVoidAction("setResizingAllowed") {
            @Override
            public void map() {
                ((JTableHeader) getSource()).setResizingAllowed(b);
            }
        });
    }

    public boolean getResizingAllowed() {
        return (runMapping(new MapBooleanAction("getResizingAllowed") {
            @Override
            public boolean map() {
                return ((JTableHeader) getSource()).getResizingAllowed();
            }
        }));
    }

    public TableColumn getDraggedColumn() {
        return (runMapping(new MapAction<TableColumn>("getDraggedColumn") {
            @Override
            public TableColumn map() {
                return ((JTableHeader) getSource()).getDraggedColumn();
            }
        }));
    }

    public int getDraggedDistance() {
        return (runMapping(new MapIntegerAction("getDraggedDistance") {
            @Override
            public int map() {
                return ((JTableHeader) getSource()).getDraggedDistance();
            }
        }));
    }

    public TableColumn getResizingColumn() {
        return (runMapping(new MapAction<TableColumn>("getResizingColumn") {
            @Override
            public TableColumn map() {
                return ((JTableHeader) getSource()).getResizingColumn();
            }
        }));
    }

    public void setUpdateTableInRealTime(final boolean b) {
        runMapping(new MapVoidAction("setUpdateTableInRealTime") {
            @Override
            public void map() {
                ((JTableHeader) getSource()).setUpdateTableInRealTime(b);
            }
        });
    }

    public boolean getUpdateTableInRealTime() {
        return (runMapping(new MapBooleanAction("getUpdateTableInRealTime") {
            @Override
            public boolean map() {
                return ((JTableHeader) getSource()).getUpdateTableInRealTime();
            }
        }));
    }

    public void setDefaultRenderer(final TableCellRenderer tableCellRenderer) {
        runMapping(new MapVoidAction("setDefaultRenderer") {
            @Override
            public void map() {
                ((JTableHeader) getSource()).setDefaultRenderer(tableCellRenderer);
            }
        });
    }

    public TableCellRenderer getDefaultRenderer() {
        return (runMapping(new MapAction<TableCellRenderer>("getDefaultRenderer") {
            @Override
            public TableCellRenderer map() {
                return ((JTableHeader) getSource()).getDefaultRenderer();
            }
        }));
    }

    public int columnAtPoint(final Point point) {
        return (runMapping(new MapIntegerAction("columnAtPoint") {
            @Override
            public int map() {
                return ((JTableHeader) getSource()).columnAtPoint(point);
            }
        }));
    }

    public Rectangle getHeaderRect(final int i) {
        return (runMapping(new MapAction<Rectangle>("getHeaderRect") {
            @Override
            public Rectangle map() {
                return ((JTableHeader) getSource()).getHeaderRect(i);
            }
        }));
    }

    public TableHeaderUI getUI() {
        return (runMapping(new MapAction<TableHeaderUI>("getUI") {
            @Override
            public TableHeaderUI map() {
                return ((JTableHeader) getSource()).getUI();
            }
        }));
    }

    public void setUI(final TableHeaderUI tableHeaderUI) {
        runMapping(new MapVoidAction("setUI") {
            @Override
            public void map() {
                ((JTableHeader) getSource()).setUI(tableHeaderUI);
            }
        });
    }

    public void setColumnModel(final TableColumnModel tableColumnModel) {
        runMapping(new MapVoidAction("setColumnModel") {
            @Override
            public void map() {
                ((JTableHeader) getSource()).setColumnModel(tableColumnModel);
            }
        });
    }

    public TableColumnModel getColumnModel() {
        return (runMapping(new MapAction<TableColumnModel>("getColumnModel") {
            @Override
            public TableColumnModel map() {
                return ((JTableHeader) getSource()).getColumnModel();
            }
        }));
    }

    public void columnAdded(final TableColumnModelEvent tableColumnModelEvent) {
        runMapping(new MapVoidAction("columnAdded") {
            @Override
            public void map() {
                ((JTableHeader) getSource()).columnAdded(tableColumnModelEvent);
            }
        });
    }

    public void columnRemoved(final TableColumnModelEvent tableColumnModelEvent) {
        runMapping(new MapVoidAction("columnRemoved") {
            @Override
            public void map() {
                ((JTableHeader) getSource()).columnRemoved(tableColumnModelEvent);
            }
        });
    }

    public void columnMoved(final TableColumnModelEvent tableColumnModelEvent) {
        runMapping(new MapVoidAction("columnMoved") {
            @Override
            public void map() {
                ((JTableHeader) getSource()).columnMoved(tableColumnModelEvent);
            }
        });
    }

    public void columnMarginChanged(final ChangeEvent changeEvent) {
        runMapping(new MapVoidAction("columnMarginChanged") {
            @Override
            public void map() {
                ((JTableHeader) getSource()).columnMarginChanged(changeEvent);
            }
        });
    }

    public void columnSelectionChanged(final ListSelectionEvent listSelectionEvent) {
        runMapping(new MapVoidAction("columnSelectionChanged") {
            @Override
            public void map() {
                ((JTableHeader) getSource()).columnSelectionChanged(listSelectionEvent);
            }
        });
    }

    public void resizeAndRepaint() {
        runMapping(new MapVoidAction("resizeAndRepaint") {
            @Override
            public void map() {
                ((JTableHeader) getSource()).resizeAndRepaint();
            }
        });
    }

    public void setDraggedColumn(final TableColumn tableColumn) {
        runMapping(new MapVoidAction("setDraggedColumn") {
            @Override
            public void map() {
                ((JTableHeader) getSource()).setDraggedColumn(tableColumn);
            }
        });
    }

    public void setDraggedDistance(final int i) {
        runMapping(new MapVoidAction("setDraggedDistance") {
            @Override
            public void map() {
                ((JTableHeader) getSource()).setDraggedDistance(i);
            }
        });
    }

    public void setResizingColumn(final TableColumn tableColumn) {
        runMapping(new MapVoidAction("setResizingColumn") {
            @Override
            public void map() {
                ((JTableHeader) getSource()).setResizingColumn(tableColumn);
            }
        });
    }

    // End of mapping                                      //
    ////////////////////////////////////////////////////////
    /**
     * Checks component type.
     */
    public static class JTableHeaderFinder implements ComponentChooser {

        ComponentChooser subFinder;

        /**
         * Constructs JTableHeaderFinder.
         */
        public JTableHeaderFinder(ComponentChooser sf) {
            subFinder = sf;
        }

        @Override
        public boolean checkComponent(Component comp) {
            if (comp instanceof JTableHeader) {
                return subFinder.checkComponent(comp);
            }
            return false;
        }

        @Override
        public String getDescription() {
            return subFinder.getDescription();
        }

        @Override
        public String toString() {
            return "JTableHeaderFinder{" + "subFinder=" + subFinder + '}';
        }
    }
}
