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
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.Hashtable;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.MenuElement;
import javax.swing.MenuSelectionManager;
import javax.swing.SingleSelectionModel;
import javax.swing.event.PopupMenuListener;
import javax.swing.plaf.PopupMenuUI;
import org.jspecify.annotations.Nullable;
import org.netbeans.jemmy.Action;
import org.netbeans.jemmy.ComponentChooser;
import org.netbeans.jemmy.ComponentSearcher;
import org.netbeans.jemmy.JemmyException;
import org.netbeans.jemmy.JemmyProperties;
import org.netbeans.jemmy.Outputable;
import org.netbeans.jemmy.QueueTool;
import org.netbeans.jemmy.TestOut;
import org.netbeans.jemmy.Timeoutable;
import org.netbeans.jemmy.Timeouts;
import org.netbeans.jemmy.WindowWaiter;
import org.netbeans.jemmy.drivers.DriverManager;
import org.netbeans.jemmy.drivers.MenuDriver;

/**
 * Timeouts used:
 * <ul>
 * <li>JMenuOperator.WaitBeforePopupTimeout - time to sleep before popup expanding</li>
 * <li>JMenuOperator.WaitPopupTimeout - time to wait popup displayed</li>
 * <li>ComponentOperator.WaitComponentTimeout - time to wait button displayed</li>
 * <li>WindowWaiter.WaitWindowTimeout - time to wait popup window displayed</li>
 * <li>WindowWaiter.AfterWindowTimeout - time to sleep after popup window has been dispayed</li>
 * </ul>
 *
 * @see org.netbeans.jemmy.Timeouts
 *
 */
public class JPopupMenuOperator extends JComponentOperator implements Outputable, Timeoutable {

    /**
     * Identifier for a "label" property.
     *
     * @see #getDump
     */
    public static final String LABEL_DPROP = "Label";

    private @SuppressWarnings("NullAway.Init") TestOut output;
    private @SuppressWarnings("NullAway.Init") Timeouts timeouts;
    private MenuDriver driver;

    public JPopupMenuOperator(JPopupMenu popup) {
        super(popup);
        driver = DriverManager.getMenuDriver(getClass());
    }

    public JPopupMenuOperator(ContainerOperator<?> cont, ComponentChooser chooser, int index) {
        this((JPopupMenu) cont.waitSubComponent(new JPopupMenuFinder(chooser), index));
        copyEnvironment(cont);
    }

    public JPopupMenuOperator(ContainerOperator<?> cont, ComponentChooser chooser) {
        this(cont, chooser, 0);
    }

    /**
     * Waits component first. Constructor can be used in
     * complicated cases when output or timeouts should differ from default.
     */
    public JPopupMenuOperator(Operator env) {
        this((JPopupMenu) waitComponent(
                WindowOperator.waitWindow(new JPopupWindowFinder(), 0, env.getTimeouts(), env.getOutput()),
                new JPopupMenuFinder(),
                0,
                env.getTimeouts(),
                env.getOutput()));
        copyEnvironment(env);
    }

    /**
     * Waits component in container first. Uses cont's timeout and
     * output for waiting and to init operator.
     */
    public JPopupMenuOperator(ContainerOperator<?> cont) {
        this((JPopupMenu) waitComponent(cont, new JPopupMenuFinder(), 0));
        copyEnvironment(cont);
    }

    /**
     * Waits component first.
     */
    public JPopupMenuOperator() {
        this(Operator.getEnvironmentOperator());
    }

    /**
     * Searches JPopupMenu in container.
     *
     * @return JPopupMenu instance or null if component was not found.
     */
    public static @Nullable JPopupMenu findJPopupMenu(Container cont, ComponentChooser chooser, int index) {
        return (JPopupMenu) findComponent(cont, new JPopupMenuFinder(chooser), index);
    }

    /**
     * Searches JPopupMenu in container.
     *
     * @return JPopupMenu instance or null if component was not found.
     */
    public static @Nullable JPopupMenu findJPopupMenu(Container cont, ComponentChooser chooser) {
        return findJPopupMenu(cont, chooser, 0);
    }

    /**
     * Waits JPopupMenu in container.
     *
     * @return JPopupMenu instance.
     */
    public static JPopupMenu waitJPopupMenu(Container cont, ComponentChooser chooser, int index) {
        return (JPopupMenu) waitComponent(cont, new JPopupMenuFinder(chooser), index);
    }

    /**
     * Waits JPopupMenu in container.
     *
     * @return JPopupMenu instance.
     */
    public static JPopupMenu waitJPopupMenu(Container cont, ComponentChooser chooser) {
        return waitJPopupMenu(cont, chooser, 0);
    }

    /**
     * Searches for a window which contains JPopupMenu.
     *
     * @return a window containing JPopupMenu.
     */
    public static @Nullable Window findJPopupWindow(ComponentChooser chooser) {
        return WindowWaiter.getWindow(new JPopupWindowFinder(chooser));
    }

    /**
     * Waits for a window which contains JPopupMenu.
     *
     * @return a window containing JPopupMenu.
     */
    public static Window waitJPopupWindow(ComponentChooser chooser) {
        try {
            return (new WindowWaiter()).waitWindow(new JPopupWindowFinder(chooser));
        } catch (InterruptedException e) {
            throw new JemmyException("Interrupted", e);
        }
    }

    /**
     * Waits popup defined by {@code popupChooser} parameter.
     *
     * @return a JPopupMenu fitting the criteria.
     */
    public static JPopupMenuOperator waitJPopupMenu(final ComponentChooser popupChooser) {
        try {
            WindowOperator wind = new WindowOperator(new WindowWaiter().waitWindow(new ComponentChooser() {
                @Override
                public boolean checkComponent(Component comp) {
                    ComponentSearcher searcher = new ComponentSearcher((Container) comp);
                    searcher.setOutput(JemmyProperties.getCurrentOutput().createErrorOutput());
                    return searcher.findComponent(popupChooser) != null;
                }

                @Override
                public String getDescription() {
                    return "Window containing \"" + popupChooser.getDescription() + "\" popup";
                }

                @Override
                public String toString() {
                    return "JPopupMenuOperator.waitJPopupMenu.ComponentChooser{description = " + getDescription() + '}';
                }
            }));
            return new JPopupMenuOperator(wind);
        } catch (InterruptedException e) {
            throw (new JemmyException("Popup waiting has been interrupted", e));
        }
    }

    /**
     * Waits popup containing menu item with {@code menuItemText} text.
     *
     * @param menuItemText a text of a menu item which supposed to be displayed
     * inside the popup.
     * @return a JPopupMenu fitting the criteria.
     */
    public static JPopupMenuOperator waitJPopupMenu(final String menuItemText) {
        return (waitJPopupMenu(new ComponentChooser() {
            @Override
            public boolean checkComponent(Component comp) {
                if (comp instanceof JPopupMenu) {
                    ComponentSearcher searcher = new ComponentSearcher((Container) comp);
                    searcher.setOutput(JemmyProperties.getCurrentOutput().createErrorOutput());
                    return (searcher.findComponent(new JMenuItemOperator.JMenuItemByLabelFinder(
                                    menuItemText, Operator.getDefaultStringComparator()))
                            != null);
                } else {
                    return false;
                }
            }

            @Override
            public String getDescription() {
                return "Popup containing \"" + menuItemText + "\" menu item";
            }

            @Override
            public String toString() {
                return "JPopupMenuOperator.waitJPopupMenu.ComponentChooser{description = " + getDescription() + '}';
            }
        }));
    }

    /**
     * Calls popup by clicking on (x, y) point in component.
     *
     * @param x X coordinate of click point in the component coordinate system.
     * @param y Y coordinate of click point in the component coordinate system.
     * @return an opened JPopupMenu
     */
    public static JPopupMenu callPopup(final ComponentOperator oper, int x, int y, int mouseButton) {
        oper.makeComponentVisible();
        // 1.5 workaround
        if (System.getProperty("java.specification.version").compareTo("1.4") > 0) {
            QueueTool qt = new QueueTool();
            qt.setOutput(oper.getOutput().createErrorOutput());
            qt.waitEmpty(10);
            qt.waitEmpty(10);
            qt.waitEmpty(10);
        }
        // end of 1.5 workaround
        oper.clickForPopup(x, y, mouseButton);
        oper.getTimeouts().sleep("JMenuOperator.WaitBeforePopupTimeout");
        return (waitJPopupMenu(
                waitJPopupWindow(new ComponentChooser() {
                    @Override
                    public boolean checkComponent(Component cmp) {
                        Component invoker = ((JPopupMenu) cmp).getInvoker();
                        return (invoker == oper.getSource()
                                || (invoker instanceof Container
                                        && ((Container) invoker).isAncestorOf(oper.getSource()))
                                || (oper.getSource() instanceof Container
                                        && ((Container) oper.getSource()).isAncestorOf(invoker)));
                    }

                    @Override
                    public String getDescription() {
                        return "Popup menu";
                    }

                    @Override
                    public String toString() {
                        return "JPopupMenuOperator.callPopup.ComponentChooser{description = " + getDescription() + '}';
                    }
                }),
                ComponentSearcher.getTrueChooser("Popup menu")));
    }

    /**
     * Calls popup by clicking on (x, y) point in component.
     *
     * @param x X coordinate of click point in the component coordinate system.
     * @param y Y coordinate of click point in the component coordinate system.
     * @return an opened JPopupMenu
     * @see ComponentOperator#getPopupMouseButton()
     */
    public static JPopupMenu callPopup(ComponentOperator oper, int x, int y) {
        return callPopup(oper, x, y, getPopupMouseButton());
    }

    /**
     * Calls popup by clicking on (x, y) point in component.
     *
     * @param x X coordinate of click point in the component coordinate system.
     * @param y Y coordinate of click point in the component coordinate system.
     * @return an opened JPopupMenu
     */
    public static JPopupMenu callPopup(Component comp, int x, int y, int mouseButton) {
        return callPopup(new ComponentOperator(comp), x, y, mouseButton);
    }

    /**
     * Calls popup by clicking on (x, y) point in component.
     *
     * @param x X coordinate of click point in the component coordinate system.
     * @param y Y coordinate of click point in the component coordinate system.
     * @return an opened JPopupMenu
     * @see ComponentOperator#getPopupMouseButton()
     */
    public static JPopupMenu callPopup(Component comp, int x, int y) {
        return callPopup(comp, x, y, getPopupMouseButton());
    }

    /**
     * Calls popup by clicking component center.
     *
     * @return an opened JPopupMenu
     */
    public static @Nullable JPopupMenu callPopup(Component comp, int mouseButton) {
        ComponentOperator co = new ComponentOperator(comp);
        co.makeComponentVisible();
        co.clickForPopup(mouseButton);
        return (findJPopupMenu(
                waitJPopupWindow(ComponentSearcher.getTrueChooser("Popup menu window")),
                ComponentSearcher.getTrueChooser("Popup menu")));
    }

    /**
     * Calls popup by clicking component center.
     *
     * @return an opened JPopupMenu
     * @see ComponentOperator#getPopupMouseButton()
     */
    public static @Nullable JPopupMenu callPopup(Component comp) {
        return callPopup(comp, getPopupMouseButton());
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
        return ((JMenuItem) produceTimeRestricted(
                new Action<Object, Void>() {
                    @Override
                    public @Nullable Object launch(Void obj) {
                        // TDB 1.5 menu workaround
                        getQueueTool().waitEmpty();
                        Object result =
                                driver.pushMenu(JPopupMenuOperator.this, JMenuOperator.converChoosers(choosers));
                        getQueueTool().waitEmpty();
                        return result;
                    }

                    @Override
                    public String getDescription() {
                        return JMenuOperator.createDescription(choosers);
                    }

                    @Override
                    public String toString() {
                        return "JPopupMenuOperator.pushMenu.ComponentChooser{description = " + getDescription() + '}';
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
        produceNoBlocking(new NoBlockingAction<Object, Void>("Menu pushing") {
            @Override
            public @Nullable Object doAction(Void param) {
                // TDB 1.5 menu workaround
                getQueueTool().waitEmpty();
                Object result = driver.pushMenu(JPopupMenuOperator.this, JMenuOperator.converChoosers(choosers));
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
     * @see ComponentOperator#isCaptionEqual(String, String, boolean, boolean)
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
        JMenuItemOperator result = new JMenuItemOperator(menuCont, path[path.length - 1]);
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

    @Override
    public Hashtable<String, Object> getDump() {
        Hashtable<String, Object> result = super.getDump();
        if (((JPopupMenu) getSource()).getLabel() != null) {
            result.put(LABEL_DPROP, ((JPopupMenu) getSource()).getLabel());
        }
        return result;
    }

    ////////////////////////////////////////////////////////
    // Mapping                                             //
    public JMenuItem add(final String string) {
        return (runMapping(new MapAction<JMenuItem>("add") {
            @Override
            public JMenuItem map() {
                return ((JPopupMenu) getSource()).add(string);
            }
        }));
    }

    public JMenuItem add(final javax.swing.Action action) {
        return (runMapping(new MapAction<JMenuItem>("add") {
            @Override
            public JMenuItem map() {
                return ((JPopupMenu) getSource()).add(action);
            }
        }));
    }

    public JMenuItem add(final JMenuItem jMenuItem) {
        return (runMapping(new MapAction<JMenuItem>("add") {
            @Override
            public JMenuItem map() {
                return ((JPopupMenu) getSource()).add(jMenuItem);
            }
        }));
    }

    public void addPopupMenuListener(final PopupMenuListener popupMenuListener) {
        runMapping(new MapVoidAction("addPopupMenuListener") {
            @Override
            public void map() {
                ((JPopupMenu) getSource()).addPopupMenuListener(popupMenuListener);
            }
        });
    }

    public void addSeparator() {
        runMapping(new MapVoidAction("addSeparator") {
            @Override
            public void map() {
                ((JPopupMenu) getSource()).addSeparator();
            }
        });
    }

    public int getComponentIndex(final Component component) {
        return (runMapping(new MapIntegerAction("getComponentIndex") {
            @Override
            public int map() {
                return ((JPopupMenu) getSource()).getComponentIndex(component);
            }
        }));
    }

    public Component getInvoker() {
        return (runMapping(new MapAction<Component>("getInvoker") {
            @Override
            public Component map() {
                return ((JPopupMenu) getSource()).getInvoker();
            }
        }));
    }

    public String getLabel() {
        return (runMapping(new MapAction<String>("getLabel") {
            @Override
            public String map() {
                return ((JPopupMenu) getSource()).getLabel();
            }
        }));
    }

    public Insets getMargin() {
        return (runMapping(new MapAction<Insets>("getMargin") {
            @Override
            public Insets map() {
                return ((JPopupMenu) getSource()).getMargin();
            }
        }));
    }

    public SingleSelectionModel getSelectionModel() {
        return (runMapping(new MapAction<SingleSelectionModel>("getSelectionModel") {
            @Override
            public SingleSelectionModel map() {
                return ((JPopupMenu) getSource()).getSelectionModel();
            }
        }));
    }

    public MenuElement[] getSubElements() {
        return ((MenuElement[]) runMapping(new MapAction<Object>("getSubElements") {
            @Override
            public Object map() {
                return ((JPopupMenu) getSource()).getSubElements();
            }
        }));
    }

    public PopupMenuUI getUI() {
        return (runMapping(new MapAction<PopupMenuUI>("getUI") {
            @Override
            public PopupMenuUI map() {
                return ((JPopupMenu) getSource()).getUI();
            }
        }));
    }

    public void insert(final Component component, final int i) {
        runMapping(new MapVoidAction("insert") {
            @Override
            public void map() {
                ((JPopupMenu) getSource()).insert(component, i);
            }
        });
    }

    public void insert(final javax.swing.Action action, final int i) {
        runMapping(new MapVoidAction("insert") {
            @Override
            public void map() {
                ((JPopupMenu) getSource()).insert(action, i);
            }
        });
    }

    public boolean isBorderPainted() {
        return (runMapping(new MapBooleanAction("isBorderPainted") {
            @Override
            public boolean map() {
                return ((JPopupMenu) getSource()).isBorderPainted();
            }
        }));
    }

    public boolean isLightWeightPopupEnabled() {
        return (runMapping(new MapBooleanAction("isLightWeightPopupEnabled") {
            @Override
            public boolean map() {
                return ((JPopupMenu) getSource()).isLightWeightPopupEnabled();
            }
        }));
    }

    public void menuSelectionChanged(final boolean b) {
        runMapping(new MapVoidAction("menuSelectionChanged") {
            @Override
            public void map() {
                ((JPopupMenu) getSource()).menuSelectionChanged(b);
            }
        });
    }

    public void pack() {
        runMapping(new MapVoidAction("pack") {
            @Override
            public void map() {
                ((JPopupMenu) getSource()).pack();
            }
        });
    }

    public void processKeyEvent(
            final KeyEvent keyEvent, final MenuElement[] menuElement, final MenuSelectionManager menuSelectionManager) {
        runMapping(new MapVoidAction("processKeyEvent") {
            @Override
            public void map() {
                ((JPopupMenu) getSource()).processKeyEvent(keyEvent, menuElement, menuSelectionManager);
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
                ((JPopupMenu) getSource()).processMouseEvent(mouseEvent, menuElement, menuSelectionManager);
            }
        });
    }

    public void removePopupMenuListener(final PopupMenuListener popupMenuListener) {
        runMapping(new MapVoidAction("removePopupMenuListener") {
            @Override
            public void map() {
                ((JPopupMenu) getSource()).removePopupMenuListener(popupMenuListener);
            }
        });
    }

    public void setBorderPainted(final boolean b) {
        runMapping(new MapVoidAction("setBorderPainted") {
            @Override
            public void map() {
                ((JPopupMenu) getSource()).setBorderPainted(b);
            }
        });
    }

    public void setInvoker(final Component component) {
        runMapping(new MapVoidAction("setInvoker") {
            @Override
            public void map() {
                ((JPopupMenu) getSource()).setInvoker(component);
            }
        });
    }

    public void setLabel(final String string) {
        runMapping(new MapVoidAction("setLabel") {
            @Override
            public void map() {
                ((JPopupMenu) getSource()).setLabel(string);
            }
        });
    }

    public void setLightWeightPopupEnabled(final boolean b) {
        runMapping(new MapVoidAction("setLightWeightPopupEnabled") {
            @Override
            public void map() {
                ((JPopupMenu) getSource()).setLightWeightPopupEnabled(b);
            }
        });
    }

    public void setPopupSize(final int i, final int i1) {
        runMapping(new MapVoidAction("setPopupSize") {
            @Override
            public void map() {
                ((JPopupMenu) getSource()).setPopupSize(i, i1);
            }
        });
    }

    public void setPopupSize(final Dimension dimension) {
        runMapping(new MapVoidAction("setPopupSize") {
            @Override
            public void map() {
                ((JPopupMenu) getSource()).setPopupSize(dimension);
            }
        });
    }

    public void setSelected(final Component component) {
        runMapping(new MapVoidAction("setSelected") {
            @Override
            public void map() {
                ((JPopupMenu) getSource()).setSelected(component);
            }
        });
    }

    public void setSelectionModel(final SingleSelectionModel singleSelectionModel) {
        runMapping(new MapVoidAction("setSelectionModel") {
            @Override
            public void map() {
                ((JPopupMenu) getSource()).setSelectionModel(singleSelectionModel);
            }
        });
    }

    public void setUI(final PopupMenuUI popupMenuUI) {
        runMapping(new MapVoidAction("setUI") {
            @Override
            public void map() {
                ((JPopupMenu) getSource()).setUI(popupMenuUI);
            }
        });
    }

    public void show(final Component component, final int i, final int i1) {
        runMapping(new MapVoidAction("show") {
            @Override
            public void map() {
                ((JPopupMenu) getSource()).show(component, i, i1);
            }
        });
    }

    // End of mapping                                      //
    ////////////////////////////////////////////////////////
    /**
     * Checks component type.
     */
    public static class JPopupMenuFinder extends Finder {

        /**
         * Constructs JPopupMenuFinder.
         */
        public JPopupMenuFinder(ComponentChooser sf) {
            super(JPopupMenu.class, sf);
        }

        /**
         * Constructs JPopupMenuFinder.
         */
        public JPopupMenuFinder() {
            super(JPopupMenu.class);
        }

        @Override
        public boolean checkComponent(Component comp) {
            return (comp.isShowing() && super.checkComponent(comp));
        }
    }

    /**
     * Allwos to find a window containing JPopupMenu.
     */
    public static class JPopupWindowFinder implements ComponentChooser {

        ComponentChooser subFinder;
        ComponentChooser ppFinder;

        /**
         * Constructs JPopupWindowFinder.
         */
        public JPopupWindowFinder(ComponentChooser sf) {
            subFinder = new JPopupMenuFinder(sf);
            ppFinder = new ComponentChooser() {
                @Override
                public boolean checkComponent(Component comp) {
                    return (comp.isShowing() && comp.isVisible() && subFinder.checkComponent(comp));
                }

                @Override
                public String getDescription() {
                    return subFinder.getDescription();
                }

                @Override
                public String toString() {
                    return "JPopupMenuOperator.JPopupWindowFinder.ComponentChooser{description = " + getDescription()
                            + '}';
                }
            };
        }

        /**
         * Constructs JPopupWindowFinder.
         */
        public JPopupWindowFinder() {
            this(ComponentSearcher.getTrueChooser("Any JPopupWindow"));
        }

        @Override
        public boolean checkComponent(Component comp) {
            if (comp.isShowing() && comp instanceof Window) {
                ComponentSearcher cs = new ComponentSearcher((Container) comp);
                cs.setOutput(JemmyProperties.getCurrentOutput().createErrorOutput());
                return (cs.findComponent(ppFinder) != null);
            }
            return false;
        }

        @Override
        public String getDescription() {
            return subFinder.getDescription();
        }

        @Override
        public String toString() {
            return "JPopupWindowFinder{" + "subFinder=" + subFinder + ", ppFinder=" + ppFinder + '}';
        }
    }
}
