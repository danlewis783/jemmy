/*
 * Copyright (c) 1997, 2022, Oracle and/or its affiliates. All rights reserved.
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
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.Hashtable;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.MenuElement;
import javax.swing.MenuSelectionManager;
import javax.swing.SingleSelectionModel;
import javax.swing.plaf.MenuBarUI;
import org.netbeans.jemmy.Action;
import org.netbeans.jemmy.ComponentChooser;
import org.netbeans.jemmy.Outputable;
import org.netbeans.jemmy.TestOut;
import org.netbeans.jemmy.Timeoutable;
import org.netbeans.jemmy.Timeouts;
import org.netbeans.jemmy.drivers.DriverManager;
import org.netbeans.jemmy.drivers.MenuDriver;

/**
 * Timeouts used:
 * <ul>
 * <li>JMenuOperator.WaitBeforePopupTimeout - time to sleep before popup expanding</li>
 * <li>JMenuOperator.WaitPopupTimeout - time to wait popup displayed</li>
 * <li>ComponentOperator.WaitComponentTimeout - time to wait button displayed</li>
 * </ul>
 *
 * @see org.netbeans.jemmy.Timeouts
 */
public class JMenuBarOperator extends JComponentOperator implements Outputable, Timeoutable {

    /**
     * Identifier for a "submenu" properties.
     *
     * @see #getDump
     */
    public static final String SUBMENU_PREFIX_DPROP = "Submenu";

    private TestOut output;
    private Timeouts timeouts;
    private MenuDriver driver;

    public JMenuBarOperator(JMenuBar b) {
        super(b);
        driver = DriverManager.getMenuDriver(getClass());
    }

    public JMenuBarOperator(ContainerOperator<?> cont, ComponentChooser chooser, int index) {
        this((JMenuBar) cont.waitSubComponent(new JMenuBarFinder(chooser), index));
        copyEnvironment(cont);
    }

    public JMenuBarOperator(ContainerOperator<?> cont, ComponentChooser chooser) {
        this(cont, chooser, 0);
    }

    /**
     * Waits component in container first. Uses cont's timeout and
     * output for waiting and to init operator.
     */
    public JMenuBarOperator(ContainerOperator<?> cont) {
        this((JMenuBar) waitComponent(cont, new JMenuBarFinder(), 0));
        copyEnvironment(cont);
    }

    /**
     * Searches JMenuBar in frame.
     *
     * @return found JMenuBar
     */
    public static JMenuBar findJMenuBar(JFrame frame) {
        return findJMenuBar((Container) frame);
    }

    /**
     * Searches JMenuBar in dialog.
     *
     * @return found JMenuBar
     */
    public static JMenuBar findJMenuBar(JDialog dialog) {
        return findJMenuBar((Container) dialog);
    }

    /**
     * Searches JMenuBar in container.
     *
     * @return found JMenuBar
     */
    public static JMenuBar waitJMenuBar(Container cont) {
        return (JMenuBar) waitComponent(cont, new JMenuBarFinder());
    }

    /**
     * Waits JMenuBar in frame.
     *
     * @return found JMenuBar
     */
    public static JMenuBar waitJMenuBar(JFrame frame) {
        return waitJMenuBar((Container) frame);
    }

    /**
     * Waits JMenuBar in dialog.
     *
     * @return found JMenuBar
     */
    public static JMenuBar waitJMenuBar(JDialog dialog) {
        return waitJMenuBar((Container) dialog);
    }

    /**
     * Waits JMenuBar in container.
     *
     * @return found JMenuBar
     */
    public static JMenuBar findJMenuBar(Container cont) {
        return (JMenuBar) findComponent(cont, new JMenuBarFinder());
    }

    static {
        // necessary to init timeouts
        JMenuOperator.performInit();
    }

    @Override
    public void setOutput(TestOut out) {
        super.setOutput(out);
        output = out;
    }

    @Override
    public TestOut getOutput() {
        return output;
    }

    @Override
    public void setTimeouts(Timeouts times) {
        super.setTimeouts(times);
        timeouts = times;
    }

    @Override
    public Timeouts getTimeouts() {
        return timeouts;
    }

    @Override
    public void copyEnvironment(Operator anotherOperator) {
        super.copyEnvironment(anotherOperator);
        driver = DriverManager.getMenuDriver(this);
    }

    /**
     * Pushes menu.
     *
     * @return Last pushed JMenuItem.
     */
    public JMenuItem pushMenu(final ComponentChooser[] choosers) {
        makeComponentVisible();
        return ((JMenuItem) produceTimeRestricted(
                new Action<Object, Void>() {
                    @Override
                    public Object launch(Void obj) {
                        // TDB 1.5 menu workaround
                        getQueueTool().waitEmpty();
                        Object result = driver.pushMenu(JMenuBarOperator.this, JMenuOperator.converChoosers(choosers));
                        getQueueTool().waitEmpty();
                        return result;
                    }

                    @Override
                    public String getDescription() {
                        return JMenuOperator.createDescription(choosers);
                    }

                    @Override
                    public String toString() {
                        return "JMenuBarOperator.pushMenu.Action{description = " + getDescription() + '}';
                    }
                },
                "JMenuOperator.PushMenuTimeout"));
    }

    /**
     * Executes {@code pushMenu(choosers)} in a separate thread.
     *
     * @see #pushMenu(ComponentChooser[])
     */
    public void pushMenuNoBlock(final ComponentChooser[] choosers) {
        makeComponentVisible();
        produceNoBlocking(new NoBlockingAction<Object, Void>("Menu pushing") {
            @Override
            public Object doAction(Void param) {
                // TDB 1.5 menu workaround
                getQueueTool().waitEmpty();
                Object result = driver.pushMenu(JMenuBarOperator.this, JMenuOperator.converChoosers(choosers));
                getQueueTool().waitEmpty();
                return result;
            }
        });
    }

    /**
     * Pushes menu.
     *
     * @return Last pushed JMenuItem.
     */
    public JMenuItem pushMenu(String[] names, StringComparator comparator) {
        return pushMenu(JMenuItemOperator.createChoosers(names, comparator));
    }

    /**
     * Pushes menu.
     *
     * @see ComponentOperator#isCaptionEqual(String, String, boolean, boolean)
     * @return Last pushed JMenuItem.
     * @deprecated Use pushMenu(String[]) or pushMenu(String[],
     * StringComparator)
     */
    @Deprecated
    public JMenuItem pushMenu(String[] names, boolean ce, boolean ccs) {
        return pushMenu(names, new DefaultStringComparator(ce, ccs));
    }

    /**
     * Executes {@code pushMenu(names, ce, ccs)} in a separate thread.
     */
    public void pushMenuNoBlock(String[] names, StringComparator comparator) {
        pushMenuNoBlock(JMenuItemOperator.createChoosers(names, comparator));
    }

    /**
     * Executes {@code pushMenu(names, ce, ccs)} in a separate thread.
     *
     * @see #pushMenu(String[], boolean,boolean)
     * @deprecated Use pushMenuNoBlock(String[]) or pushMenuNoBlock(String[],
     * StringComparator)
     */
    @Deprecated
    public void pushMenuNoBlock(String[] names, boolean ce, boolean ccs) {
        pushMenuNoBlock(names, new DefaultStringComparator(ce, ccs));
    }

    /**
     * Pushes menu.
     *
     * @return Last pushed JMenuItem.
     */
    public JMenuItem pushMenu(String[] names) {
        return pushMenu(names, getComparator());
    }

    /**
     * Executes {@code pushMenu(names)} in a separate thread.
     *
     * @see #pushMenu(String[])
     */
    public void pushMenuNoBlock(String[] names) {
        pushMenuNoBlock(names, getComparator());
    }

    /**
     * Pushes menu.
     *
     * @return Last pushed JMenuItem.
     */
    public JMenuItem pushMenu(String path, String delim, StringComparator comparator) {
        return pushMenu(parseString(path, delim), comparator);
    }

    /**
     * Pushes menu. Uses PathParser assigned to this operator.
     *
     * @return Last pushed JMenuItem.
     */
    public JMenuItem pushMenu(String path, StringComparator comparator) {
        return pushMenu(parseString(path), comparator);
    }

    /**
     * Pushes menu.
     *
     * @see ComponentOperator#isCaptionEqual(String, String, boolean, boolean)
     * @return Last pushed JMenuItem.
     * @deprecated Use pushMenu(String, String) or pushMenu(String, String,
     * StringComparator)
     */
    @Deprecated
    public JMenuItem pushMenu(String path, String delim, boolean ce, boolean ccs) {
        return pushMenu(parseString(path, delim), ce, ccs);
    }

    /**
     * Executes {@code pushMenu(names, delim, comparator)} in a separate
     * thread.
     */
    public void pushMenuNoBlock(String path, String delim, StringComparator comparator) {
        pushMenuNoBlock(parseString(path, delim), comparator);
    }

    /**
     * Executes {@code pushMenu(names, comparator)} in a separate thread.
     * Uses PathParser assigned to this operator.
     */
    public void pushMenuNoBlock(String path, StringComparator comparator) {
        pushMenuNoBlock(parseString(path), comparator);
    }

    /**
     * Executes {@code pushMenu(path, delim, ce, ccs)} in a separate
     * thread.
     *
     * @see #pushMenu
     * @deprecated Use pushMenuNoBlock(String, String) or
     * pushMenuNoBlock(String, String, StringComparator)
     */
    @Deprecated
    public void pushMenuNoBlock(String path, String delim, boolean ce, boolean ccs) {
        pushMenuNoBlock(parseString(path, delim), ce, ccs);
    }

    /**
     * Pushes menu.
     *
     * @return Last pushed JMenuItem.
     */
    public JMenuItem pushMenu(String path, String delim) {
        return pushMenu(parseString(path, delim));
    }

    /**
     * Pushes menu. Uses PathParser assigned to this operator.
     *
     * @return Last pushed JMenuItem.
     */
    public JMenuItem pushMenu(String path) {
        return pushMenu(parseString(path));
    }

    /**
     * Executes {@code pushMenu(path, delim)} in a separate thread.
     */
    public void pushMenuNoBlock(String path, String delim) {
        pushMenuNoBlock(parseString(path, delim));
    }

    /**
     * Executes {@code pushMenu(path)} in a separate thread.
     */
    public void pushMenuNoBlock(String path) {
        pushMenuNoBlock(parseString(path));
    }

    public JMenuItemOperator[] showMenuItems(ComponentChooser[] choosers) {
        if (choosers == null || choosers.length == 0) {
            return JMenuItemOperator.getMenuItems((MenuElement) getSource(), this);
        } else {
            return JMenuItemOperator.getMenuItems((JMenu) pushMenu(choosers), this);
        }
    }

    /**
     * Shows submenu of menu specified by a {@code path} parameter.
     *
     * @return an array of operators created tor items from the submenu.
     */
    public JMenuItemOperator[] showMenuItems(String[] path, StringComparator comparator) {
        if (path == null || path.length == 0) {
            return JMenuItemOperator.getMenuItems((MenuElement) getSource(), this);
        } else {
            return JMenuItemOperator.getMenuItems((JMenu) pushMenu(path, comparator), this);
        }
    }

    /**
     * Shows submenu of menu specified by a {@code path} parameter. Uses
     * StringComparator assigned to the operator.
     *
     * @return an array of operators created tor items from the submenu.
     */
    public JMenuItemOperator[] showMenuItems(String[] path) {
        return showMenuItems(path, getComparator());
    }

    /**
     * Shows submenu of menu specified by a {@code path} parameter.
     *
     * @return an array of operators created tor items from the submenu.
     */
    public JMenuItemOperator[] showMenuItems(String path, String delim, StringComparator comparator) {
        return showMenuItems(parseString(path, delim), comparator);
    }

    /**
     * Shows submenu of menu specified by a {@code path} parameter. Uses
     * PathParser assigned to this operator.
     *
     * @return an array of operators created tor items from the submenu.
     */
    public JMenuItemOperator[] showMenuItems(String path, StringComparator comparator) {
        return showMenuItems(parseString(path), comparator);
    }

    /**
     * Shows submenu of menu specified by a {@code path} parameter. Uses
     * StringComparator assigned to the operator.
     *
     * @return an array of operators created tor items from the submenu.
     */
    public JMenuItemOperator[] showMenuItems(String path, String delim) {
        return showMenuItems(path, delim, getComparator());
    }

    /**
     * Shows submenu of menu specified by a {@code path} parameter. Uses
     * PathParser assigned to this operator. Uses StringComparator assigned to
     * the operator.
     *
     * @return an array of operators created tor items from the submenu.
     */
    public JMenuItemOperator[] showMenuItems(String path) {
        return showMenuItems(path, getComparator());
    }

    public JMenuItemOperator showMenuItem(ComponentChooser[] choosers) {
        ComponentChooser[] parentPath = getParentPath(choosers);
        JMenu menu;
        ContainerOperator<?> menuCont;
        if (parentPath.length > 0) {
            menu = (JMenu) pushMenu(getParentPath(choosers));
            menuCont = new ContainerOperator<>(menu.getPopupMenu());
            menuCont.copyEnvironment(this);
        } else {
            menuCont = this;
        }
        JMenuItemOperator result = new JMenuItemOperator(menuCont, choosers[choosers.length - 1]);
        result.copyEnvironment(this);
        return result;
    }

    /**
     * Expends all menus to show menu item specified by a {@code path}
     * parameter.
     *
     * @return an operator for the last menu item in path.
     */
    public JMenuItemOperator showMenuItem(String[] path, StringComparator comparator) {
        String[] parentPath = getParentPath(path);
        JMenu menu;
        ContainerOperator<?> menuCont;
        if (parentPath.length > 0) {
            menu = (JMenu) pushMenu(getParentPath(path), comparator);
            menuCont = new ContainerOperator<>(menu.getPopupMenu());
            menuCont.copyEnvironment(this);
        } else {
            menuCont = this;
        }
        JMenuItemOperator result;
        result = new JMenuItemOperator(menuCont, path[path.length - 1]);
        result.copyEnvironment(this);
        return result;
    }

    /**
     * Expands all menus to show menu item specified by a {@code path}
     * parameter.
     *
     * @return an operator for the last menu item in path.
     */
    public JMenuItemOperator showMenuItem(String[] path) {
        return showMenuItem(path, getComparator());
    }

    /**
     * Expands all menus to show menu item specified by a {@code path}
     * parameter.
     *
     * @return an operator for the last menu item in path.
     */
    public JMenuItemOperator showMenuItem(String path, String delim, StringComparator comparator) {
        return showMenuItem(parseString(path, delim), comparator);
    }

    /**
     * Expands all menus to show menu item specified by a {@code path}
     * parameter. Uses PathParser assigned to this operator.
     *
     * @return an operator for the last menu item in path.
     */
    public JMenuItemOperator showMenuItem(String path, StringComparator comparator) {
        return showMenuItem(parseString(path), comparator);
    }

    /**
     * Expands all menus to show menu item specified by a {@code path}
     * parameter. Uses StringComparator assigned to the operator.
     *
     * @return an operator for the last menu item in path.
     */
    public JMenuItemOperator showMenuItem(String path, String delim) {
        return showMenuItem(path, delim, getComparator());
    }

    /**
     * Expands all menus to show menu item specified by a {@code path}
     * parameter. Uses PathParser assigned to this operator. Uses
     * StringComparator assigned to the operator.
     *
     * @return an array of operators created tor items from the submenu.
     */
    public JMenuItemOperator showMenuItem(String path) {
        return showMenuItem(path, getComparator());
    }

    /**
     * Closes all expanded submenus.
     */
    public void closeSubmenus() {
        JMenu menu = (JMenu) findSubComponent(new ComponentChooser() {
            @Override
            public boolean checkComponent(Component comp) {
                return (comp instanceof JMenu && ((JMenu) comp).isPopupMenuVisible());
            }

            @Override
            public String getDescription() {
                return "Expanded JMenu";
            }

            @Override
            public String toString() {
                return "JMenuBarOperator.closeSubmenus.ComponentChooser{description = " + getDescription() + '}';
            }
        });
        if (menu != null) {
            JMenuOperator oper = new JMenuOperator(menu);
            oper.copyEnvironment(this);
            oper.push();
        }
    }

    @Override
    public Hashtable<String, Object> getDump() {
        Hashtable<String, Object> result = super.getDump();
        String[] items = new String[((JMenuBar) getSource()).getMenuCount()];
        for (int i = 0; i < ((JMenuBar) getSource()).getMenuCount(); i++) {
            if (((JMenuBar) getSource()).getMenu(i) != null) {
                items[i] = ((JMenuBar) getSource()).getMenu(i).getText();
            } else {
                items[i] = "null";
            }
        }
        addToDump(result, SUBMENU_PREFIX_DPROP, items);
        return result;
    }

    ////////////////////////////////////////////////////////
    // Mapping                                             //
    public JMenu add(final JMenu jMenu) {
        return (runMapping(new MapAction<JMenu>("add") {
            @Override
            public JMenu map() {
                return ((JMenuBar) getSource()).add(jMenu);
            }
        }));
    }

    public int getComponentIndex(final Component component) {
        return (runMapping(new MapIntegerAction("getComponentIndex") {
            @Override
            public int map() {
                return ((JMenuBar) getSource()).getComponentIndex(component);
            }
        }));
    }

    public JMenu getHelpMenu() {
        return (runMapping(new MapAction<JMenu>("getHelpMenu") {
            @Override
            public JMenu map() {
                return ((JMenuBar) getSource()).getHelpMenu();
            }
        }));
    }

    public Insets getMargin() {
        return (runMapping(new MapAction<Insets>("getMargin") {
            @Override
            public Insets map() {
                return ((JMenuBar) getSource()).getMargin();
            }
        }));
    }

    public JMenu getMenu(final int i) {
        return (runMapping(new MapAction<JMenu>("getMenu") {
            @Override
            public JMenu map() {
                return ((JMenuBar) getSource()).getMenu(i);
            }
        }));
    }

    public int getMenuCount() {
        return (runMapping(new MapIntegerAction("getMenuCount") {
            @Override
            public int map() {
                return ((JMenuBar) getSource()).getMenuCount();
            }
        }));
    }

    public SingleSelectionModel getSelectionModel() {
        return (runMapping(new MapAction<SingleSelectionModel>("getSelectionModel") {
            @Override
            public SingleSelectionModel map() {
                return ((JMenuBar) getSource()).getSelectionModel();
            }
        }));
    }

    public MenuElement[] getSubElements() {
        return ((MenuElement[]) runMapping(new MapAction<Object>("getSubElements") {
            @Override
            public Object map() {
                return ((JMenuBar) getSource()).getSubElements();
            }
        }));
    }

    public MenuBarUI getUI() {
        return (runMapping(new MapAction<MenuBarUI>("getUI") {
            @Override
            public MenuBarUI map() {
                return ((JMenuBar) getSource()).getUI();
            }
        }));
    }

    public boolean isBorderPainted() {
        return (runMapping(new MapBooleanAction("isBorderPainted") {
            @Override
            public boolean map() {
                return ((JMenuBar) getSource()).isBorderPainted();
            }
        }));
    }

    public boolean isSelected() {
        return (runMapping(new MapBooleanAction("isSelected") {
            @Override
            public boolean map() {
                return ((JMenuBar) getSource()).isSelected();
            }
        }));
    }

    public void menuSelectionChanged(final boolean b) {
        runMapping(new MapVoidAction("menuSelectionChanged") {
            @Override
            public void map() {
                ((JMenuBar) getSource()).menuSelectionChanged(b);
            }
        });
    }

    public void processKeyEvent(
            final KeyEvent keyEvent, final MenuElement[] menuElement, final MenuSelectionManager menuSelectionManager) {
        runMapping(new MapVoidAction("processKeyEvent") {
            @Override
            public void map() {
                ((JMenuBar) getSource()).processKeyEvent(keyEvent, menuElement, menuSelectionManager);
            }
        });
    }

    public void processMouseEvent(
            final MouseEvent mouseEvent,
            final MenuElement[] menuElement,
            final MenuSelectionManager menuSelectionManager) {
        runMapping(new MapVoidAction("processMouseEvent") {
            @Override
            public void map() {
                ((JMenuBar) getSource()).processMouseEvent(mouseEvent, menuElement, menuSelectionManager);
            }
        });
    }

    public void setBorderPainted(final boolean b) {
        runMapping(new MapVoidAction("setBorderPainted") {
            @Override
            public void map() {
                ((JMenuBar) getSource()).setBorderPainted(b);
            }
        });
    }

    public void setHelpMenu(final JMenu jMenu) {
        runMapping(new MapVoidAction("setHelpMenu") {
            @Override
            public void map() {
                ((JMenuBar) getSource()).setHelpMenu(jMenu);
            }
        });
    }

    public void setMargin(final Insets insets) {
        runMapping(new MapVoidAction("setMargin") {
            @Override
            public void map() {
                ((JMenuBar) getSource()).setMargin(insets);
            }
        });
    }

    public void setSelected(final Component component) {
        runMapping(new MapVoidAction("setSelected") {
            @Override
            public void map() {
                ((JMenuBar) getSource()).setSelected(component);
            }
        });
    }

    public void setSelectionModel(final SingleSelectionModel singleSelectionModel) {
        runMapping(new MapVoidAction("setSelectionModel") {
            @Override
            public void map() {
                ((JMenuBar) getSource()).setSelectionModel(singleSelectionModel);
            }
        });
    }

    public void setUI(final MenuBarUI menuBarUI) {
        runMapping(new MapVoidAction("setUI") {
            @Override
            public void map() {
                ((JMenuBar) getSource()).setUI(menuBarUI);
            }
        });
    }

    // End of mapping                                      //
    ////////////////////////////////////////////////////////
    /**
     * Checks component type.
     */
    public static class JMenuBarFinder extends Finder {

        /**
         * Constructs JMenuBarFinder.
         */
        public JMenuBarFinder(ComponentChooser sf) {
            super(JMenuBar.class, sf);
        }

        /**
         * Constructs JMenuBarFinder.
         */
        public JMenuBarFinder() {
            super(JMenuBar.class);
        }
    }
}
