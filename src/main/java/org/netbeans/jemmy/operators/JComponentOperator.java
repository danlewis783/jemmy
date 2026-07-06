/*
 * Copyright (c) 1997, 2018, Oracle and/or its affiliates. All rights reserved.
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
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.beans.VetoableChangeListener;
import java.util.Hashtable;
import java.util.Objects;
import javax.accessibility.AccessibleContext;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.JRootPane;
import javax.swing.JToolTip;
import javax.swing.KeyStroke;
import javax.swing.border.Border;
import javax.swing.event.AncestorListener;
import org.jspecify.annotations.Nullable;
import org.netbeans.jemmy.ComponentChooser;
import org.netbeans.jemmy.ComponentSearcher;
import org.netbeans.jemmy.Outputable;
import org.netbeans.jemmy.TestOut;
import org.netbeans.jemmy.Timeoutable;
import org.netbeans.jemmy.Timeouts;

/**
 * Timeouts used:
 * <ul>
 * <li>JComponentOperator.WaitToolTipTimeout - time to wait tool tip displayed</li>
 * <li>JComponentOperator.ShowToolTipTimeout - time to show tool tip</li>
 * <li>ComponentOperator.WaitComponentTimeout - time to wait component displayed</li>
 * </ul>
 *
 * @see org.netbeans.jemmy.Timeouts
 *
 */
public class JComponentOperator extends ContainerOperator<Container> implements Timeoutable, Outputable {

    /**
     * Identifier for a "tooltip text" property.
     *
     * @see #getDump
     */
    public static final String TOOLTIP_TEXT_DPROP = "Tooltip text";

    public static final String A11Y_DATA = "Accessible data (yes/no)";
    public static final String A11Y_NAME_DPROP = "Accessible name";
    public static final String A11Y_DESCRIPTION_DPROP = "Accessible decription";

    private static final long WAIT_TOOL_TIP_TIMEOUT = 10000;
    private static final long SHOW_TOOL_TIP_TIMEOUT = 0;

    private @SuppressWarnings("NullAway.Init") Timeouts timeouts;
    private @SuppressWarnings("NullAway.Init") TestOut output;

    public JComponentOperator(JComponent b) {
        super(b);
    }

    public JComponentOperator(ContainerOperator<?> cont, ComponentChooser chooser, int index) {
        this((JComponent) cont.waitSubComponent(new JComponentFinder(chooser), index));
        copyEnvironment(cont);
    }

    public JComponentOperator(ContainerOperator<?> cont, ComponentChooser chooser) {
        this(cont, chooser, 0);
    }

    /**
     * Waits component in container first. Uses cont's timeout and
     * output for waiting and to init operator.
     */
    public JComponentOperator(ContainerOperator<?> cont, int index) {
        this((JComponent)
                waitComponent(cont, new JComponentFinder(ComponentSearcher.getTrueChooser("Any JComponent")), index));
        copyEnvironment(cont);
    }

    /**
     * Waits component in container first. Uses cont's timeout and
     * output for waiting and to init operator.
     */
    public JComponentOperator(ContainerOperator<?> cont) {
        this(cont, 0);
    }

    /**
     * Searches JComponent in container.
     *
     * @return JComponent instance or null if component was not found.
     */
    public static @Nullable JComponent findJComponent(Container cont, ComponentChooser chooser, int index) {
        return (JComponent) findComponent(cont, new JComponentFinder(chooser), index);
    }

    /**
     * Searches 0'th JComponent in container.
     *
     * @return JComponent instance or null if component was not found.
     */
    public static @Nullable JComponent findJComponent(Container cont, ComponentChooser chooser) {
        return findJComponent(cont, chooser, 0);
    }

    /**
     * Searches JComponent by tooltip text.
     *
     * @return JComponent instance or null if component was not found.
     * @see ComponentOperator#isCaptionEqual(String, String, boolean, boolean)
     */
    public static @Nullable JComponent findJComponent(
            Container cont, String toolTipText, boolean ce, boolean ccs, int index) {
        return findJComponent(
                cont, new JComponentByTipFinder(toolTipText, new DefaultStringComparator(ce, ccs)), index);
    }

    /**
     * Searches JComponent by tooltip text.
     *
     * @return JComponent instance or null if component was not found.
     * @see ComponentOperator#isCaptionEqual(String, String, boolean, boolean)
     */
    public static @Nullable JComponent findJComponent(Container cont, String toolTipText, boolean ce, boolean ccs) {
        return findJComponent(cont, toolTipText, ce, ccs, 0);
    }

    /**
     * Waits JComponent in container.
     *
     * @return JComponent instance or null if component was not found.
     */
    public static JComponent waitJComponent(Container cont, ComponentChooser chooser, final int index) {
        return (JComponent) waitComponent(cont, new JComponentFinder(chooser), index);
    }

    /**
     * Waits 0'th JComponent in container.
     *
     * @return JComponent instance or null if component was not found.
     */
    public static JComponent waitJComponent(Container cont, ComponentChooser chooser) {
        return waitJComponent(cont, chooser, 0);
    }

    /**
     * Waits JComponent by tooltip text.
     *
     * @return JComponent instance or null if component was not found.
     * @see ComponentOperator#isCaptionEqual(String, String, boolean, boolean)
     */
    public static JComponent waitJComponent(Container cont, String toolTipText, boolean ce, boolean ccs, int index) {
        return waitJComponent(
                cont, new JComponentByTipFinder(toolTipText, new DefaultStringComparator(ce, ccs)), index);
    }

    /**
     * Waits JComponent by tooltip text.
     *
     * @return JComponent instance or null if component was not found.
     * @see ComponentOperator#isCaptionEqual(String, String, boolean, boolean)
     */
    public static JComponent waitJComponent(Container cont, String toolTipText, boolean ce, boolean ccs) {
        return waitJComponent(cont, toolTipText, ce, ccs, 0);
    }

    static {
        Timeouts.initDefault("JComponentOperator.WaitToolTipTimeout", WAIT_TOOL_TIP_TIMEOUT);
        Timeouts.initDefault("JComponentOperator.ShowToolTipTimeout", SHOW_TOOL_TIP_TIMEOUT);
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
    public int getCenterXForClick() {
        Rectangle rect = getVisibleRect();
        return ((int) rect.getX() + (int) rect.getWidth() / 2);
    }

    @Override
    public int getCenterYForClick() {
        Rectangle rect = getVisibleRect();
        return ((int) rect.getY() + (int) rect.getHeight() / 2);
    }

    /**
     * Showes tool tip.
     *
     * @return JToolTip component.
     */
    public JToolTip showToolTip() {
        enterMouse();
        moveMouse(getCenterXForClick(), getCenterYForClick());
        return waitToolTip();
    }

    public JToolTip waitToolTip() {
        return JToolTipOperator.waitJToolTip(this);
    }

    /**
     * Looks for a first window-like container.
     *
     * @return either WindowOperator of JInternalFrameOperator
     */
    public ContainerOperator<?> getWindowContainerOperator() {
        Component resultComp;
        if (getSource() instanceof Window) {
            resultComp = getSource();
        } else {
            resultComp = getContainer(new ComponentChooser() {
                @Override
                public boolean checkComponent(Component comp) {
                    return (comp instanceof Window || comp instanceof JInternalFrame);
                }

                @Override
                public String getDescription() {
                    return "";
                }
            });
        }
        ContainerOperator<?> result;
        if (resultComp instanceof Window) {
            result = new WindowOperator((Window) resultComp);
        } else {
            result = new ContainerOperator<>((Container) Objects.requireNonNull(resultComp, "container not found"));
        }
        result.copyEnvironment(this);
        return result;
    }

    @Override
    public Hashtable<String, Object> getDump() {
        Hashtable<String, Object> result = super.getDump();
        if (getToolTipText() != null) {
            result.put(TOOLTIP_TEXT_DPROP, getToolTipText());
        }
        // System.out.println("Dump a11y = " + System.getProperty("jemmy.dump.a11y"));
        if (System.getProperty("jemmy.dump.a11y") != null
                && System.getProperty("jemmy.dump.a11y").equals("on")) {
            AccessibleContext a11y = getSource().getAccessibleContext();
            if (a11y != null) {
                result.put(A11Y_DATA, "yes");
                String accName = (a11y.getAccessibleName() == null) ? "null" : a11y.getAccessibleName();
                String accDesc = (a11y.getAccessibleDescription() == null) ? "null" : a11y.getAccessibleDescription();
                result.put(A11Y_NAME_DPROP, accName);
                result.put(A11Y_DESCRIPTION_DPROP, accDesc);
            } else {
                result.put(A11Y_DATA, "no");
            }
        }
        return result;
    }

    ////////////////////////////////////////////////////////
    // Mapping                                             //
    public void addAncestorListener(final AncestorListener ancestorListener) {
        runMapping(new MapVoidAction("addAncestorListener") {
            @Override
            public void map() {
                ((JComponent) getSource()).addAncestorListener(ancestorListener);
            }
        });
    }

    public void addVetoableChangeListener(final VetoableChangeListener vetoableChangeListener) {
        runMapping(new MapVoidAction("addVetoableChangeListener") {
            @Override
            public void map() {
                ((JComponent) getSource()).addVetoableChangeListener(vetoableChangeListener);
            }
        });
    }

    public void computeVisibleRect(final Rectangle rectangle) {
        runMapping(new MapVoidAction("computeVisibleRect") {
            @Override
            public void map() {
                ((JComponent) getSource()).computeVisibleRect(rectangle);
            }
        });
    }

    public JToolTip createToolTip() {
        return (runMapping(new MapAction<JToolTip>("createToolTip") {
            @Override
            public JToolTip map() {
                return ((JComponent) getSource()).createToolTip();
            }
        }));
    }

    public void firePropertyChange(final String string, final byte b, final byte b1) {
        runMapping(new MapVoidAction("firePropertyChange") {
            @Override
            public void map() {
                getSource().firePropertyChange(string, b, b1);
            }
        });
    }

    public void firePropertyChange(final String string, final char c, final char c1) {
        runMapping(new MapVoidAction("firePropertyChange") {
            @Override
            public void map() {
                getSource().firePropertyChange(string, c, c1);
            }
        });
    }

    public void firePropertyChange(final String string, final double d, final double d1) {
        runMapping(new MapVoidAction("firePropertyChange") {
            @Override
            public void map() {
                getSource().firePropertyChange(string, d, d1);
            }
        });
    }

    public void firePropertyChange(final String string, final float f, final float f1) {
        runMapping(new MapVoidAction("firePropertyChange") {
            @Override
            public void map() {
                getSource().firePropertyChange(string, f, f1);
            }
        });
    }

    public void firePropertyChange(final String string, final int i, final int i1) {
        runMapping(new MapVoidAction("firePropertyChange") {
            @Override
            public void map() {
                ((JComponent) getSource()).firePropertyChange(string, i, i1);
            }
        });
    }

    public void firePropertyChange(final String string, final long l, final long l1) {
        runMapping(new MapVoidAction("firePropertyChange") {
            @Override
            public void map() {
                getSource().firePropertyChange(string, l, l1);
            }
        });
    }

    public void firePropertyChange(final String string, final short s, final short s1) {
        runMapping(new MapVoidAction("firePropertyChange") {
            @Override
            public void map() {
                getSource().firePropertyChange(string, s, s1);
            }
        });
    }

    public void firePropertyChange(final String string, final boolean b, final boolean b1) {
        runMapping(new MapVoidAction("firePropertyChange") {
            @Override
            public void map() {
                ((JComponent) getSource()).firePropertyChange(string, b, b1);
            }
        });
    }

    public AccessibleContext getAccessibleContext() {
        return (runMapping(new MapAction<AccessibleContext>("getAccessibleContext") {
            @Override
            public AccessibleContext map() {
                return getSource().getAccessibleContext();
            }
        }));
    }

    public ActionListener getActionForKeyStroke(final KeyStroke keyStroke) {
        return (runMapping(new MapAction<ActionListener>("getActionForKeyStroke") {
            @Override
            public ActionListener map() {
                return ((JComponent) getSource()).getActionForKeyStroke(keyStroke);
            }
        }));
    }

    public boolean getAutoscrolls() {
        return (runMapping(new MapBooleanAction("getAutoscrolls") {
            @Override
            public boolean map() {
                return ((JComponent) getSource()).getAutoscrolls();
            }
        }));
    }

    public Border getBorder() {
        return (runMapping(new MapAction<Border>("getBorder") {
            @Override
            public Border map() {
                return ((JComponent) getSource()).getBorder();
            }
        }));
    }

    public Object getClientProperty(final Object object) {
        return (runMapping(new MapAction<Object>("getClientProperty") {
            @Override
            public Object map() {
                return ((JComponent) getSource()).getClientProperty(object);
            }
        }));
    }

    public int getConditionForKeyStroke(final KeyStroke keyStroke) {
        return (runMapping(new MapIntegerAction("getConditionForKeyStroke") {
            @Override
            public int map() {
                return ((JComponent) getSource()).getConditionForKeyStroke(keyStroke);
            }
        }));
    }

    public int getDebugGraphicsOptions() {
        return (runMapping(new MapIntegerAction("getDebugGraphicsOptions") {
            @Override
            public int map() {
                return ((JComponent) getSource()).getDebugGraphicsOptions();
            }
        }));
    }

    public Insets getInsets(final Insets insets) {
        return (runMapping(new MapAction<Insets>("getInsets") {
            @Override
            public Insets map() {
                return ((JComponent) getSource()).getInsets(insets);
            }
        }));
    }

    @Deprecated
    public Component getNextFocusableComponent() {
        return (runMapping(new MapAction<Component>("getNextFocusableComponent") {
            @Override
            public Component map() {
                return ((JComponent) getSource()).getNextFocusableComponent();
            }
        }));
    }

    public KeyStroke[] getRegisteredKeyStrokes() {
        return ((KeyStroke[]) runMapping(new MapAction<Object>("getRegisteredKeyStrokes") {
            @Override
            public Object map() {
                return ((JComponent) getSource()).getRegisteredKeyStrokes();
            }
        }));
    }

    public JRootPane getRootPane() {
        return (runMapping(new MapAction<JRootPane>("getRootPane") {
            @Override
            public JRootPane map() {
                return ((JComponent) getSource()).getRootPane();
            }
        }));
    }

    public Point getToolTipLocation(final MouseEvent mouseEvent) {
        return (runMapping(new MapAction<Point>("getToolTipLocation") {
            @Override
            public Point map() {
                return ((JComponent) getSource()).getToolTipLocation(mouseEvent);
            }
        }));
    }

    public String getToolTipText() {
        return (runMapping(new MapAction<String>("getToolTipText") {
            @Override
            public String map() {
                return ((JComponent) getSource()).getToolTipText();
            }
        }));
    }

    public String getToolTipText(final MouseEvent mouseEvent) {
        return (runMapping(new MapAction<String>("getToolTipText") {
            @Override
            public String map() {
                return ((JComponent) getSource()).getToolTipText(mouseEvent);
            }
        }));
    }

    public Container getTopLevelAncestor() {
        return (runMapping(new MapAction<Container>("getTopLevelAncestor") {
            @Override
            public Container map() {
                return ((JComponent) getSource()).getTopLevelAncestor();
            }
        }));
    }

    public String getUIClassID() {
        return (runMapping(new MapAction<String>("getUIClassID") {
            @Override
            public String map() {
                return ((JComponent) getSource()).getUIClassID();
            }
        }));
    }

    public Rectangle getVisibleRect() {
        return (runMapping(new MapAction<Rectangle>("getVisibleRect") {
            @Override
            public Rectangle map() {
                return ((JComponent) getSource()).getVisibleRect();
            }
        }));
    }

    public void grabFocus() {
        runMapping(new MapVoidAction("grabFocus") {
            @Override
            public void map() {
                ((JComponent) getSource()).grabFocus();
            }
        });
    }

    public boolean isFocusCycleRoot() {
        return (runMapping(new MapBooleanAction("isFocusCycleRoot") {
            @Override
            public boolean map() {
                return ((JComponent) getSource()).isFocusCycleRoot();
            }
        }));
    }

    @Deprecated
    public boolean isManagingFocus() {
        return (runMapping(new MapBooleanAction("isManagingFocus") {
            @Override
            public boolean map() {
                return ((JComponent) getSource()).isManagingFocus();
            }
        }));
    }

    public boolean isOptimizedDrawingEnabled() {
        return (runMapping(new MapBooleanAction("isOptimizedDrawingEnabled") {
            @Override
            public boolean map() {
                return ((JComponent) getSource()).isOptimizedDrawingEnabled();
            }
        }));
    }

    public boolean isPaintingTile() {
        return (runMapping(new MapBooleanAction("isPaintingTile") {
            @Override
            public boolean map() {
                return ((JComponent) getSource()).isPaintingTile();
            }
        }));
    }

    public boolean isRequestFocusEnabled() {
        return (runMapping(new MapBooleanAction("isRequestFocusEnabled") {
            @Override
            public boolean map() {
                return ((JComponent) getSource()).isRequestFocusEnabled();
            }
        }));
    }

    public boolean isValidateRoot() {
        return (runMapping(new MapBooleanAction("isValidateRoot") {
            @Override
            public boolean map() {
                return ((JComponent) getSource()).isValidateRoot();
            }
        }));
    }

    public void paintImmediately(final int i, final int i1, final int i2, final int i3) {
        runMapping(new MapVoidAction("paintImmediately") {
            @Override
            public void map() {
                ((JComponent) getSource()).paintImmediately(i, i1, i2, i3);
            }
        });
    }

    public void paintImmediately(final Rectangle rectangle) {
        runMapping(new MapVoidAction("paintImmediately") {
            @Override
            public void map() {
                ((JComponent) getSource()).paintImmediately(rectangle);
            }
        });
    }

    public void putClientProperty(final Object object, final Object object1) {
        runMapping(new MapVoidAction("putClientProperty") {
            @Override
            public void map() {
                ((JComponent) getSource()).putClientProperty(object, object1);
            }
        });
    }

    public void registerKeyboardAction(
            final ActionListener actionListener, final String string, final KeyStroke keyStroke, final int i) {
        runMapping(new MapVoidAction("registerKeyboardAction") {
            @Override
            public void map() {
                ((JComponent) getSource()).registerKeyboardAction(actionListener, string, keyStroke, i);
            }
        });
    }

    public void registerKeyboardAction(final ActionListener actionListener, final KeyStroke keyStroke, final int i) {
        runMapping(new MapVoidAction("registerKeyboardAction") {
            @Override
            public void map() {
                ((JComponent) getSource()).registerKeyboardAction(actionListener, keyStroke, i);
            }
        });
    }

    public void removeAncestorListener(final AncestorListener ancestorListener) {
        runMapping(new MapVoidAction("removeAncestorListener") {
            @Override
            public void map() {
                ((JComponent) getSource()).removeAncestorListener(ancestorListener);
            }
        });
    }

    public void removeVetoableChangeListener(final VetoableChangeListener vetoableChangeListener) {
        runMapping(new MapVoidAction("removeVetoableChangeListener") {
            @Override
            public void map() {
                ((JComponent) getSource()).removeVetoableChangeListener(vetoableChangeListener);
            }
        });
    }

    public void repaint(final Rectangle rectangle) {
        runMapping(new MapVoidAction("repaint") {
            @Override
            public void map() {
                ((JComponent) getSource()).repaint(rectangle);
            }
        });
    }

    @Deprecated
    public boolean requestDefaultFocus() {
        return (runMapping(new MapBooleanAction("requestDefaultFocus") {
            @Override
            public boolean map() {
                return ((JComponent) getSource()).requestDefaultFocus();
            }
        }));
    }

    public void resetKeyboardActions() {
        runMapping(new MapVoidAction("resetKeyboardActions") {
            @Override
            public void map() {
                ((JComponent) getSource()).resetKeyboardActions();
            }
        });
    }

    public void revalidate() {
        runMapping(new MapVoidAction("revalidate") {
            @Override
            public void map() {
                getSource().revalidate();
            }
        });
    }

    public void scrollRectToVisible(final Rectangle rectangle) {
        runMapping(new MapVoidAction("scrollRectToVisible") {
            @Override
            public void map() {
                ((JComponent) getSource()).scrollRectToVisible(rectangle);
            }
        });
    }

    public void setAlignmentX(final float f) {
        runMapping(new MapVoidAction("setAlignmentX") {
            @Override
            public void map() {
                ((JComponent) getSource()).setAlignmentX(f);
            }
        });
    }

    public void setAlignmentY(final float f) {
        runMapping(new MapVoidAction("setAlignmentY") {
            @Override
            public void map() {
                ((JComponent) getSource()).setAlignmentY(f);
            }
        });
    }

    public void setAutoscrolls(final boolean b) {
        runMapping(new MapVoidAction("setAutoscrolls") {
            @Override
            public void map() {
                ((JComponent) getSource()).setAutoscrolls(b);
            }
        });
    }

    public void setBorder(final Border border) {
        runMapping(new MapVoidAction("setBorder") {
            @Override
            public void map() {
                ((JComponent) getSource()).setBorder(border);
            }
        });
    }

    public void setDebugGraphicsOptions(final int i) {
        runMapping(new MapVoidAction("setDebugGraphicsOptions") {
            @Override
            public void map() {
                ((JComponent) getSource()).setDebugGraphicsOptions(i);
            }
        });
    }

    public void setDoubleBuffered(final boolean b) {
        runMapping(new MapVoidAction("setDoubleBuffered") {
            @Override
            public void map() {
                ((JComponent) getSource()).setDoubleBuffered(b);
            }
        });
    }

    public void setMaximumSize(final Dimension dimension) {
        runMapping(new MapVoidAction("setMaximumSize") {
            @Override
            public void map() {
                getSource().setMaximumSize(dimension);
            }
        });
    }

    public void setMinimumSize(final Dimension dimension) {
        runMapping(new MapVoidAction("setMinimumSize") {
            @Override
            public void map() {
                getSource().setMinimumSize(dimension);
            }
        });
    }

    @Deprecated
    public void setNextFocusableComponent(final Component component) {
        runMapping(new MapVoidAction("setNextFocusableComponent") {
            @Override
            public void map() {
                ((JComponent) getSource()).setNextFocusableComponent(component);
            }
        });
    }

    public void setOpaque(final boolean b) {
        runMapping(new MapVoidAction("setOpaque") {
            @Override
            public void map() {
                ((JComponent) getSource()).setOpaque(b);
            }
        });
    }

    public void setPreferredSize(final Dimension dimension) {
        runMapping(new MapVoidAction("setPreferredSize") {
            @Override
            public void map() {
                getSource().setPreferredSize(dimension);
            }
        });
    }

    public void setRequestFocusEnabled(final boolean b) {
        runMapping(new MapVoidAction("setRequestFocusEnabled") {
            @Override
            public void map() {
                ((JComponent) getSource()).setRequestFocusEnabled(b);
            }
        });
    }

    public void setToolTipText(final String string) {
        runMapping(new MapVoidAction("setToolTipText") {
            @Override
            public void map() {
                ((JComponent) getSource()).setToolTipText(string);
            }
        });
    }

    public void unregisterKeyboardAction(final KeyStroke keyStroke) {
        runMapping(new MapVoidAction("unregisterKeyboardAction") {
            @Override
            public void map() {
                ((JComponent) getSource()).unregisterKeyboardAction(keyStroke);
            }
        });
    }

    public void updateUI() {
        runMapping(new MapVoidAction("updateUI") {
            @Override
            public void map() {
                ((JComponent) getSource()).updateUI();
            }
        });
    }

    // End of mapping                                      //
    ////////////////////////////////////////////////////////
    /**
     * Allows to find component by tooltip.
     */
    public static class JComponentByTipFinder implements ComponentChooser {

        String label;

        StringComparator comparator;

        /**
         * Constructs JComponentByTipFinder.
         */
        public JComponentByTipFinder(String lb, StringComparator comparator) {
            label = lb;
            this.comparator = comparator;
        }

        /**
         * Constructs JComponentByTipFinder.
         */
        public JComponentByTipFinder(String lb) {
            this(lb, Operator.getDefaultStringComparator());
        }

        @Override
        public boolean checkComponent(Component comp) {
            if (comp instanceof JComponent) {
                if (((JComponent) comp).getToolTipText() != null) {
                    return (comparator.equals(((JComponent) comp).getToolTipText(), label));
                }
            }
            return false;
        }

        @Override
        public String getDescription() {
            return "JComponent with tool tip \"" + label + "\"";
        }

        @Override
        public String toString() {
            return "JComponentByTipFinder{" + "label=" + label + ", comparator=" + comparator + '}';
        }
    }

    /**
     * Checks component type.
     */
    public static class JComponentFinder extends Finder {

        /**
         * Constructs JComponentFinder.
         */
        public JComponentFinder(ComponentChooser sf) {
            super(JComponent.class, sf);
        }

        /**
         * Constructs JComponentFinder.
         */
        public JComponentFinder() {
            super(JComponent.class);
        }
    }
}
